package model;

import java.sql.SQLException;
import model.actions.Action;
import model.actions.ActionException;

public class ProcedureExecution
{
	private class ProcedureExecutionThread extends Thread
	{
		@Override public void run()
		{
			try
			{
				execution();
			}
			catch (SQLException e)
			{
				report.setErrorMessage("SQLException: " + e.getMessage());
			}

			report.saveErrorMessage();
			shuttedDown = true;
		}
	}

	protected final Procedure procedure;
	private final ProcedureExecutionThread thread;

	private boolean shuttingDown = false;
	private boolean shuttedDown = false;
	private ExecutionReport report;

	public ProcedureExecution(Procedure procedure) throws SQLException
	{
		if (procedure == null)
			throw new NullPointerException();
		
		this.procedure = procedure;
		this.thread = new ProcedureExecutionThread();
		this.report = ExecutionReport.makeNewReport(this);
	}

	public Procedure getProcedure()
	{
		if (procedure.isDeleted())
			return null;
		return procedure;
	}

	protected void execution() throws SQLException
	{
		Action currentAction = getProcedure().getFirstAction();
		if (currentAction == null)
		{
			report.setErrorMessage("Brak akcji");
			return;
		}

		while (currentAction != null)
		{
			long start = System.currentTimeMillis() / 1000;
			try
			{
				currentAction.doAction(this);
			}
			catch (ActionException e)
			{
				report.setErrorMessage("ActionException: " + e.getMessage());
				shuttingDown = true;
			}
			long time = (System.currentTimeMillis() / 1000) - start;
			report.logActionExecution(currentAction, time);
			if (shuttingDown)
				break;
			currentAction = currentAction.getNextAction();
		}
	}

	public void start()
	{
		if (shuttingDown || shuttedDown || thread.isAlive())
			throw new AssertionError("Próba ponownego wystartowania procedury");
		thread.start();
	}

	public void shutdown()
	{
		synchronized (this)
		{
			if (shuttingDown)
				return;
			shuttingDown = true;
			report.setErrorMessage("Zatrzymano");
		}
		thread.interrupt();
	}

	public boolean isShuttingDown()
	{
		return shuttingDown;
	}

	public boolean isShuttedDown()
	{
		return shuttedDown;
	}
}
