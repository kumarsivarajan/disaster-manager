package model;

import framework.*;
import java.sql.*;

public class OperatorMessage
{
	private Integer id;
	final private String message;
	private boolean read = false;
	private Timestamp date;

	public void setID(Integer i)
	{
		id = i;
	}

	public void setDate(Timestamp t)
	{
		date = t;
	}

	public OperatorMessage(String message)
	{
		if (message == null)
			throw new NullPointerException();
		this.message = message;
	}

	protected static OperatorMessage getMessageFromSQL(SQLRow row)
	{
		if (row.get("id") == null ||
			row.get("message") == null ||
			row.get("read") == null ||
			row.get("date") == null)
			throw new NullPointerException("Z bazy odebrano NULL");

		int id = (Integer)row.get("id");

		if (id <= 0)
			throw new IllegalArgumentException("Niepoprawny ID");

		//TODO: cache

		OperatorMessage m = new OperatorMessage((String)row.get("message"));
		m.id = (Integer)row.get("id");
		m.read = (Boolean)row.get("read");
		m.date = (Timestamp)row.get("date");

		return m;
	}

	protected static OperatorMessage[] getMessagesFromSQL(SQLRows rows)
	{
		OperatorMessage[] out = new OperatorMessage[rows.size()];
		int i = 0;
		for (SQLRow msgRAW : rows)
			out[i++] = getMessageFromSQL(msgRAW);
		return out;
	}

	public static OperatorMessage[] getUnreadMessages() throws SQLException
	{
		return getMessagesFromSQL(DBEngine.getAllRows(
				"SELECT * FROM `message` WHERE not `read` " +
				" ORDER BY `date` DESC"));
	}

	public static OperatorMessage[] getAllMessages() throws SQLException
	{
		return getMessagesFromSQL(DBEngine.getAllRows(
				"SELECT * FROM `message` ORDER BY `date` DESC"));
	}

	public static OperatorMessage getMessageByID(int id) throws SQLException
	{
		return getMessageFromSQL(DBEngine.getRow(
					"SELECT * FROM `message` WHERE id = " + id));
	}

	public void save() throws SQLException
	{
		if (id != null)
			throw new AssertionError("Próba ponownego zapisania wiadomości");

		id = DBEngine.insert("message", new SQLRow() {{
			put("message", message);
			put("read", read);
			}}, true);
	}

	public void setRead() throws SQLException
	{
		read = true;
		if (id != null)
			DBEngine.updateByID("message", new SQLRow() {{
				put("read", read);
				}}, id);
	}

	public boolean read()
	{
		return read;
	}

	public int getID()
	{
		if (id == null)
			throw new NullPointerException("ID nie dostępny");
		return id;
	}

	public String getMessage()
	{
		return message;
	}

	public Timestamp getDate()
	{
		if (date == null)
			throw new NullPointerException("Nie zaimplementowano");
		return date;
	}
}
