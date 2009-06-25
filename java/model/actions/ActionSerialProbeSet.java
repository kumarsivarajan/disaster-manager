package model.actions;

import model.*;

public class ActionSerialProbeSet extends Action
{
	public ActionSerialProbeSet(Procedure procedure)
	{
		super(procedure);
	}

	public ActionType getType()
	{
		return ActionType.ACTION_SERIALPROBE_SET;
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

	public void doAction(ProcedureExecution procExec) throws ActionException
	{
		try
		{
			SerialProbe.setPort(3, true);
		}
		catch (SerialCommunicationException e)
		{
			throw new ActionException(e);
		}
	}
}
