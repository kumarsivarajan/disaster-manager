package model.actions;

import framework.*;
import java.sql.SQLException;
import java.util.*;
import model.Procedure;
import tools.Pair;

public abstract class Action
{
	protected Integer id;
	protected Procedure procedure;
	protected String label;
	protected Action nextAction;
	protected Integer maxTime;
	protected boolean added = false;

	protected static enum ActionType { ACTION_MESSAGE }

	private static HashMap<Integer, Action> actionCache =
			new HashMap<Integer, Action>();

	private static HashMap<Integer, Action[]> procedureActionCache =
			new HashMap<Integer, Action[]>();

	public static Vector<Pair<Integer, String>> getActionTypes()
	{
		Vector<Pair<Integer, String>> out =
				new Vector<Pair<Integer, String>>();
		for (int i = 1; i <= ActionType.values().length; i++)
			out.add(new Pair<Integer, String>(i, actionTypeToName(actionTypeFromInt(i))));
		return out;
	}

	public Action(Procedure procedure)
	{
		if (procedure == null)
			throw new NullPointerException();
		this.procedure = procedure;
	}

	public int getID()
	{
		if (id == null)
			throw new NullPointerException("Nie ustawiono ID");
		return id;
	}

	public static Action getActionFromSQL(SQLRow row) throws SQLException
	{
		if (row.get("id") == null ||
			row.get("procedure") == null ||
			row.get("type") == null ||
			row.get("arguments") == null ||
			row.get("added") == null)
			throw new NullPointerException("Z bazy odebrano NULL");

		int id = (Integer)row.get("id");

		if (id <= 0)
			throw new IllegalArgumentException("Niepoprawny ID");

		if (actionCache.containsKey(id))
			return actionCache.get(id);

		Procedure procedure =
				Procedure.getProcedureByID((Integer)row.get("procedure"));

		Action action;
		switch (actionTypeFromInt((Integer)row.get("type")))
		{
			case ACTION_MESSAGE:
				action = new ActionMessage(procedure);
				break;
			default:
				throw new AssertionError("Pominięto case");
		}

		action.id = id;
		action.label = (String)row.get("label");
		action.nextAction = new LazyAction(procedure, (Integer)row.get("next_action"));
		action.maxTime = (Integer)row.get("maxtime");
		action.added = (Boolean)row.get("added");
		action.setArguments((String)row.get("arguments"));

		actionCache.put(id, action);

		return action;
	}

	protected static Action[] getActionsFromSQL(SQLRows rows) throws SQLException
	{
		Action[] out = new Action[rows.size()];
		int i = 0;
		for (SQLRow actRAW : rows)
			out[i++] = getActionFromSQL(actRAW);
		return out;
	}

	public static Action[] getActionsByProcedure(Procedure procedure) throws SQLException
	{
		if (procedure == null)
			throw new NullPointerException();

		if (procedureActionCache.containsKey(procedure.getID()))
			return procedureActionCache.get(procedure.getID());

		Action[] actions = getActionsFromSQL(DBEngine.getAllRows(
				"SELECT * FROM `procedure_action` WHERE added"));

		if (actions.length == 0)
		{
			procedureActionCache.put(procedure.getID(), new Action[0]);
			return new Action[0];
		}

		/**
		 * w tej chwili akcje nie są posortowane, należy ustawić je w kolejności
		 * wyznaczanej przez next_action
		 *
		 */

		HashMap<Integer, Action> actionsMap = new HashMap<Integer, Action>();
		Vector<Integer> rootActions = new Vector<Integer>();

		for (int i = 0; i < actions.length; i++)
		{
			actionsMap.put(actions[i].getID(), actions[i]);
			rootActions.add(actions[i].getID());
		}

		for (int i = 0; i < actions.length; i++)
		{
			int id = actions[i].getID();
			Action a = actionsMap.get(id);
			if (a.nextAction == null)
				continue;
			int nextAction = a.nextAction.getID();
			if (!actionsMap.containsKey(nextAction))
				throw new AssertionError("Niespójność bazy danych: nie istnieje następca akcji " + id);
			a.nextAction = actionsMap.get(nextAction);
			rootActions.remove(nextAction);
		}

		if (rootActions.size() != 1)
			throw new AssertionError("Niespójność bazy danych: zła ilość akcji - korzeni");

		Action[] out = new Action[actions.length];
		Action curr = actionsMap.get(rootActions.firstElement());
		for (int i = 0; i < out.length; i++)
		{
			out[i] = curr;
			curr = curr.getNextAction();
		}
		if (curr != null)
			throw new AssertionError("Nie posortowano wszystkich akcji");

		procedureActionCache.put(procedure.getID(), out);

		return out;
	}

	public static Action getActionByID(int id) throws SQLException
	{
		if (!actionCache.containsKey(id))
			return getActionFromSQL(DBEngine.getRow(
					"SELECT * FROM `procedure_action` WHERE id = " + id));
		return actionCache.get(id);
	}

	public static int actionTypeToInt(ActionType type)
	{
		switch (type)
		{
			case ACTION_MESSAGE:
				return 1;
			default:
				throw new IllegalArgumentException("Nie obsłużono wszystkich typów");
		}
	}

	public static String actionTypeToName(ActionType type)
	{
		switch (type)
		{
			case ACTION_MESSAGE:
				return "Wiadomość dla operatora";
			default:
				throw new IllegalArgumentException("Nie obsłużono wszystkich typów");
		}
	}

	public static ActionType actionTypeFromInt(int id)
	{
		switch (id)
		{
			case 1:
				return ActionType.ACTION_MESSAGE;
			default:
				throw new IllegalArgumentException("Nieprawidłowy id typu");
		}
	}

	/**
	 * Zapisuje zmiany w obiekcie do bazy. W przypadku nie zapisania, po restarcie
	 * serwera może zaistnieć ryzyko powstania niespójności w bazie danych.
	 *
	 * @param commit Tylko dla nowych akcji - czy oznaczyć ją jako dodaną
	 * @throws java.sql.SQLException
	 */
	public void save(boolean commit) throws SQLException
	{
		if (id == null && added)
			throw new AssertionError("Nie można dodawać nowych, dodanych akcji");

		if (commit)
			added = true;

		if (id == null)
		{
			id = DBEngine.insert("procedure", new SQLRow() {{
				put("procedure", procedure.getID());
				put("label", (label == null)?null:getLabel());
				put("next_action", (nextAction == null)?null:getNextAction().getID());
				put("type", actionTypeToInt(getType()));
				put("arguments", getArguments());
				put("maxtime", getMaxTime());
				put("added", added);
				}}, true);

			//procedureCache.put(id, this);
		}
		else
		{
			//TODO: update w bazie danych
		}
	}

	public void setLabel(String label)
	{
		if (label == null)
			throw new NullPointerException();
		if (label.trim().equals(""))
			throw new IllegalArgumentException();
		this.label = label.trim();
	}

	public String getLabel()
	{
		return label;
	}

	public void setMaxTime(int maxTime)
	{
		if (maxTime <= 0)
			throw new IllegalArgumentException("maxTime powinno być większe od zera");
		this.maxTime = maxTime;
	}

	public int getMaxTime()
	{
		return maxTime;
	}

	public Action getNextAction()
	{
		return nextAction;
	}

	protected abstract ActionType getType();

	protected abstract String getArguments();
	
	protected abstract void setArguments(String arguments);

	public abstract void doAction();
}
