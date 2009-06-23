package model.actions;

import model.*;
import framework.*;
import java.sql.SQLException;

public class ActionMessage extends Action
{
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

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		setMessage(arguments);
	}

	public void doAction(ProcedureExecution procExec) throws ActionException
	{
		if (message.trim().equals(""))
			return;
		OperatorMessage opmsg = new OperatorMessage(message);
		try
		{
			opmsg.save();
		}
		catch (SQLException e)
		{
			throw new ActionException(e);
		}
	}
}
