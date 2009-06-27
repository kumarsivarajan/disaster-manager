package model.actions;

import framework.*;
import java.sql.SQLException;
import java.util.*;
import model.*;
import tools.Pair;

public abstract class Action
{
	protected Integer id;
	protected Procedure procedure;
	protected String label;
	protected Action nextAction;
	protected Integer maxTime;
	protected boolean added = false;

	public static enum ActionType {
		ACTION_MESSAGE, ACTION_EMAIL, ACTION_XMPP_RECEIVE, ACTION_SMS,
		ACTION_SERIALPROBE_SET, ACTION_SERIALPROBE_GET, ACTION_XMPP_SEND}

	
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

	public static Action createAction(Procedure procedure, ActionType type) throws SQLException
	{
		Action a;
		switch (type)
		{
			case ACTION_MESSAGE:
				a = new ActionMessage(procedure);
				break;
			case ACTION_EMAIL:
				a = new ActionEmail(procedure);
				break;
			case ACTION_XMPP_RECEIVE:
				a = new ActionXmppReceive(procedure);
				break;
			case ACTION_SMS:
				a = new ActionSMS(procedure);
				break;
			case ACTION_SERIALPROBE_SET:
				a = new ActionSerialProbeSet(procedure);
				break;
			case ACTION_SERIALPROBE_GET:
				a = new ActionSerialProbeGet(procedure);
				break;
			case ACTION_XMPP_SEND:
				a = new ActionXmppSend(procedure);
				break;
			default:
				throw new AssertionError("Nie rozważono jednej z procedur");
		}
		a.save(false);
		return a;
	}

	public static int actionTypeToInt(ActionType type)
	{
		switch (type)
		{
			case ACTION_MESSAGE:
				return 1;
			case ACTION_EMAIL:
				return 2;
			case ACTION_XMPP_RECEIVE:
				return 3;
			case ACTION_SMS:
				return 4;
			case ACTION_SERIALPROBE_SET:
				return 5;
			case ACTION_SERIALPROBE_GET:
				return 6;
			case ACTION_XMPP_SEND:
				return 7;
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
			case ACTION_EMAIL:
				return "Wiadomość email";
			case ACTION_XMPP_RECEIVE:
				return "Odczytanie wiadomości XMPP";
			case ACTION_XMPP_SEND:
				return "Wysłanie wiadomości XMPP";
			case ACTION_SMS:
				return "Wiadomość SMS";
			case ACTION_SERIALPROBE_SET:
				return "Sterowanie czujnikiem";
			case ACTION_SERIALPROBE_GET:
				return "Odczyt czujnika";
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
			case 2:
				return ActionType.ACTION_EMAIL;
			case 3:
				return ActionType.ACTION_XMPP_RECEIVE;
			case 4:
				return ActionType.ACTION_SMS;
			case 5:
				return ActionType.ACTION_SERIALPROBE_SET;
			case 6:
				return ActionType.ACTION_SERIALPROBE_GET;
			case 7:
				return ActionType.ACTION_XMPP_SEND;
			default:
				throw new IllegalArgumentException("Nieprawidłowy id typu");
		}
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

		Action action = createAction(procedure,
				actionTypeFromInt((Integer)row.get("type")));

		action.id = id;
		action.label = (String)row.get("label");
		if (row.get("next_action") != null)
			action.nextAction =
					new LazyAction(procedure, (Integer)row.get("next_action"));
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

	protected static void clearProcedureActionsCache(Procedure procedure)
	{
		if (procedureActionCache.containsKey(procedure.getID()))
			procedureActionCache.remove(procedure.getID());
	}

	public static Action[] getActionsByProcedure(Procedure procedure) throws SQLException
	{
		if (procedure == null)
			throw new NullPointerException();

		if (procedureActionCache.containsKey(procedure.getID()))
			return procedureActionCache.get(procedure.getID());

		Action[] actions = getActionsFromSQL(DBEngine.getAllRows(
				"SELECT * FROM `procedure_action` WHERE (`procedure` = " +
				procedure.getID() + ") AND added"));

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
			rootActions.remove((Integer)nextAction);
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

		synchronized (Action.class)
		{
			boolean clearCache = false;

			if (commit)
			{
				if (!added)
				{
					Action[] procedureActions = Action.getActionsByProcedure(procedure);
					if (procedureActions.length > 0)
					{
						Action lastAction = procedureActions[procedureActions.length - 1];
						lastAction.nextAction = this;
						lastAction.save(true);
					}

					clearCache = true;
				}
				added = true;
			}

			SQLRow data = new SQLRow() {{
				put("procedure", procedure.getID());
				put("label", (label == null)?null:getLabel());
				put("next_action", (nextAction == null)?null:getNextAction().getID());
				put("type", actionTypeToInt(getType()));
				put("arguments", getArguments());
				put("maxtime", getMaxTime());
				put("added", added);
				}};

			if (id == null)
			{
				id = DBEngine.insert("procedure_action", data, true);
				actionCache.put(id, this);
			}
			else
				DBEngine.updateByID("procedure_action", data, id);

			if (clearCache) //to MUSI być po update - zapytanie leci po "added"
				clearProcedureActionsCache(procedure);
		}
	}

	public static synchronized void delete(Action action) throws SQLException
	{
		Action previous = action.getPreviousAction();
		if (previous != null)
		{
			previous.nextAction = action.nextAction;
			previous.save(true);
		}

		DBEngine.doUpdateQuery("DELETE FROM `procedure_action` WHERE `id` = " +
				action.getID());
		actionCache.remove(action.getID());
		clearProcedureActionsCache(action.getProcedure());

		action.id = null;
		action.procedure = null;
		action.label = null;
		action.nextAction = null;
		action.maxTime = null;
		action.added = false;
	}

	public static synchronized void deleteByProcedure(Procedure procedure)
			throws SQLException
	{
		DBEngine.doUpdateQuery("DELETE FROM `procedure_action` WHERE `procedure` = " +
				procedure.getID());
		clearProcedureActionsCache(procedure);

		Action[] actions = procedure.getActions();
		for (int i = 0; i < actions.length; i++)
		{
			actionCache.remove(actions[i].getID());
			actions[i].id = null;
			actions[i].procedure = null;
			actions[i].label = null;
			actions[i].nextAction = null;
			actions[i].maxTime = null;
			actions[i].added = false;
		}
	}

	public void setLabel(String label)
	{
		if (label == null || label.trim().equals(""))
			this.label = null;
		else
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

	public Integer getMaxTime()
	{
		return maxTime;
	}

	protected Action getPreviousAction() throws SQLException
	{
		if (!added)
			return null;
		Procedure p = this.getProcedure();
		Action[] actions = p.getActions();
		for (int i = 0; i < actions.length; i++)
		{
			if (actions[i] == this)
			{
				if (i == 0)
					return null;
				else
					return actions[i - 1];
			}
			if (actions[i].getID() == this.getID())
				throw new AssertionError("Błąd cache");
		}
		throw new AssertionError("Nie mogę znaleźć poprzednika");
	}

	public Action getNextAction()
	{
		return nextAction;
	}

	public String getTypeName()
	{
		return actionTypeToName(getType());
	}

	public Procedure getProcedure()
	{
		if (procedure == null)
			throw new NullPointerException();
		return procedure;
	}

	public boolean isAdded()
	{
		return added;
	}

	public abstract ActionType getType();

	public abstract String getArguments();// to powinno być raczej przez serializację
	
	protected abstract void setArguments(String arguments); // j/w

	public abstract void doAction(ProcedureExecution procExec) throws ActionException;
}
