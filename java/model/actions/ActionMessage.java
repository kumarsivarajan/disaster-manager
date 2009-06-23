package model.actions;

import model.*;
import framework.*;
import java.sql.SQLException;

public class ActionMessage extends Action
{
	protected String user = "";
	protected String message = "";

	public ActionMessage(Procedure procedure)
	{
		super(procedure);
	}

	public void setMessage(String message)
	{
		if (message == null)
			throw new NullPointerException();
		this.message = message.trim();
	}

	public String getMessage()
	{
		return message;
	}

	public ActionType getType()
	{
		return ActionType.ACTION_MESSAGE;
	}

	public String getArguments()
	{
		return getMessage();
	}

	public void setUser(String user)
	{
		if (user == null)
			throw new NullPointerException();
		this.user = user;
	}

	public String getUser()
	{
		return user;
	}


	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		String[] args = arguments.split("\n", 2);
		//TODO
		setMessage(args[1].trim());
	}

	public void doAction(ProcedureExecution procExec) throws ActionException
	{
		try {
			DBEngine.insert("message", new SQLRow() {{
				put("userID", user);
				put("message", message);
				put("read", 0);
			}}, false);
		}
		catch (java.sql.SQLException e)
		{
			throw new ActionException("SQLException: " + e.getMessage());
		}
	}

	

}
