package model.actions;

import model.Procedure;

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

	protected String getArguments()
	{
		return getMessage();
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		setMessage(arguments);
	}

	public void doAction()
	{
		//TODO: wykonanie akcji
	}
}
