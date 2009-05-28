package model;

import framework.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import javax.servlet.ServletException;

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

	public void save() throws ServletException, SQLException
	{
		if (id == null)
		{
			if (added)
				throw new ServletException("Nie można dodawać nowych, dodanych procedur");

			id = DBEngine.insert("procedure", new SQLRow() {{
				put("name", (name == null)?"":name);
				put("description", (description == null)?"":description);
				put("active", active);
				put("added", added);
				}}, true);

			procedureCache.put(id, this);
		}
		else
			throw new ServletException("Nie zaimplementowano");
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

	public static Procedure getProcedureByID(int id) throws ServletException, SQLException
	{
		if (!procedureCache.containsKey(id))
		{
			Procedure p = getProcedureFromSQL(DBEngine.getRow(
					"SELECT * FROM `procedure` WHERE id = " + id));
			procedureCache.put(id, p);
			return p;
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
		this.name = name;
	}

	public String getDescription()
	{
		if (description == null)
			throw new NullPointerException("Nie ustawiono jeszcze opisu");
		return description;
	}

	public boolean isActive()
	{
		return active;
	}
}
