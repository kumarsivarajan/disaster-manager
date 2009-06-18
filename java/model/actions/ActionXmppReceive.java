package model.actions;

import model.*;

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

	public String getArguments()
	{
		return "";
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
	}

	public void doAction(ProcedureExecution procExec)
	{
		//TODO: wykonanie akcji
		try
		{
			Thread.sleep(10000);
		}
		catch (InterruptedException e)
		{
			if (procExec.isShuttingDown())
				return;
		}
	}
}
