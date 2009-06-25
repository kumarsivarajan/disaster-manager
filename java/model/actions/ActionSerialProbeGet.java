package model.actions;

import model.*;

public class ActionSerialProbeGet extends Action
{
	private int port = 0;
	private boolean on = true;
	public final int readInterval = 250;

	public ActionSerialProbeGet(Procedure procedure)
	{
		super(procedure);
	}

	public void setPort(int port)
	{
		if (port < 0 || port > 7)
			throw new IllegalArgumentException("Niepoprawny numer portu");
		this.port = port;
	}

	public void setOn(boolean on)
	{
		this.on = on;
	}

	public int getPort()
	{
		return port;
	}

	public boolean getOn()
	{
		return on;
	}

	public ActionType getType()
	{
		return ActionType.ACTION_SERIALPROBE_GET;
	}

	public String getArguments()
	{
		return port + "," + (on?"1":"0");
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		String[] args = arguments.split(",");
		setPort(Integer.parseInt("0" + args[0].trim()));
		if (args.length < 2)
			on = false;
		else
			on = (args[1].trim().charAt(0) == '1');
	}

	public void doAction(ProcedureExecution procExec) throws ActionException
	{
		try
		{
			while (SerialProbe.getPort(port) != on)
			{
				try
				{
					Thread.sleep(readInterval);
				}
				catch (InterruptedException e)
				{
					if (procExec.isShuttingDown())
						break;
				}
			}
		}
		catch (SerialCommunicationException e)
		{
			throw new ActionException(e);
		}
	}
}
