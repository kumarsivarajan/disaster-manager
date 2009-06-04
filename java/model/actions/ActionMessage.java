package model.actions;

import model.Procedure;

public class ActionMessage extends Action
{
	protected String message;

	public ActionMessage(Procedure procedure)
	{
		super(procedure);
	}

	protected ActionType getType()
	{
		return ActionType.ACTION_MESSAGE;
	}

	protected String getArguments()
	{
		return message;
	}

	protected void setArguments(String arguments)
	{
		if (message == null)
			throw new NullPointerException();
		message = arguments;
	}

	public void doAction()
	{
		//TODO: wykonanie akcji
	}
}
