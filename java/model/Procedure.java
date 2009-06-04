package model;

import framework.*;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.ServletException;
import model.actions.Action;

public class Procedure
{
	protected Integer id;
	protected String name;
	protected String description;
	protected boolean active = true;
	protected boolean added = false;

	protected static HashMap<Integer, Procedure> procedureCache =
			new HashMap<Integer, Procedure>();

	public Procedure()
	{
	}

	/**
	 * Zapisuje zmiany w obiekcie do bazy. W przypadku nie zapisania, po restarcie
	 * serwera może zaistnieć ryzyko powstania niespójności w bazie danych.
	 *
	 * @param commit Tylko dla nowych procedur - czy oznaczyć ją jako dodaną
	 * @throws java.sql.SQLException
	 */
	public void save(boolean commit) throws SQLException
	{
		if (id == null && added)
			throw new AssertionError("Nie można dodawać nowych, dodanych procedur");

		if (commit)
			added = true;
		
		if (id == null)
		{
			id = DBEngine.insert("procedure", new SQLRow() {{
				put("name", (name == null)?"":name);
				put("description", (description == null)?"":description);
				put("active", active);
				put("added", added);
				}}, true);

			procedureCache.put(id, this);
		}
		else
		{
			//TODO: zapis do bazy
		}
	}

	public static void delete(Procedure proc) throws SQLException
	{
		//TODO: tutaj usuwanie akcji i innych powiązanych

		DBEngine.doUpdateQuery("DELETE FROM `procedure` WHERE id = " + proc.getID());

		procedureCache.remove(proc.getID());
		
		proc.id = null;
		proc.name = proc.description = null;
		proc.active = false;
		proc.added = false;
	}

	protected static Procedure getProcedureFromSQL(SQLRow row)
	{
		if (row.get("id") == null ||
			row.get("name") == null ||
			row.get("description") == null ||
			row.get("active") == null ||
			row.get("added") == null)
			throw new NullPointerException("Z bazy odebrano NULL");

		int id = (Integer)row.get("id");

		if (id <= 0)
			throw new IllegalArgumentException("Niepoprawny ID");

		if (procedureCache.containsKey(id))
			return procedureCache.get(id);

		Procedure p = new Procedure();
		p.id = (Integer)row.get("id");
		p.name = (String)row.get("name");
		p.description = (String)row.get("description");
		p.active = (Boolean)row.get("active");
		p.added = (Boolean)row.get("added");

		procedureCache.put(id, p);
		return p;
	}

	protected static Procedure[] getProceduresFromSQL(SQLRows rows)
	{
		Procedure[] out = new Procedure[rows.size()];
		int i = 0;
		for (SQLRow procRAW : rows)
			out[i++] = getProcedureFromSQL(procRAW);
		return out;
	}

	public static Procedure[] getAllProcedures() throws SQLException
	{
		return getProceduresFromSQL(DBEngine.getAllRows(
				"SELECT * FROM `procedure` WHERE added ORDER BY active DESC, id ASC"));
	}

	public static Procedure getProcedureByID(int id) throws SQLException
	{
		if (!procedureCache.containsKey(id))
		{
			//Procedure p =
			return getProcedureFromSQL(DBEngine.getRow(
					"SELECT * FROM `procedure` WHERE id = " + id));
			//procedureCache.put(id, p);
			//return p;
		}
			
		return procedureCache.get(id);
	}

	public int getID()
	{
		if (id == null)
			throw new NullPointerException("Nie ustawiono jeszcze ID");
		return id;
	}

	public String getName()
	{
		if (name == null)
			throw new NullPointerException("Nie ustawiono jeszcze nazwy");
		return name;
	}

	public void setName(String name)
	{
		if (name == null)
			throw new NullPointerException();
		if (name.trim().equals(""))
			throw new IllegalArgumentException();
		this.name = name.trim();
	}

	public String getDescription()
	{
		if (description == null)
			throw new NullPointerException("Nie ustawiono jeszcze opisu");
		return description;
	}

	public void setDescription(String description)
	{
		if (description == null)
			throw new NullPointerException();
		this.description = description.trim();
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	/**
	 * Nie dodane procedury mogą być po restarcie systemu usuwane z bazy
	 *
	 * @return Czy procedura została zaakceptowana (dodana do systemu)
	 */
	public boolean isAdded()
	{
		return added;
	}

	public Action[] getActions() throws SQLException
	{
		return Action.getActionsByProcedure(this);
	}
}
