package model.actions;

import model.Procedure;

public class ActionXmppReceive extends Action
{
	public ActionXmppReceive(Procedure procedure)
	{
		super(procedure);
	}

	
	public ActionType getType()
	{
		return ActionType.ACTION_XMPP_RECEIVE;
	}

	protected String getArguments()
	{
		return "";
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
	}

	public void doAction()
	{
		//TODO: wykonanie akcji
		try
		{
			Thread.sleep(10000);
		}
		catch (InterruptedException e)
		{
			return;
		}
	}
}
