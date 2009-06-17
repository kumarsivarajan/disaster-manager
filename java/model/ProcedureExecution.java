package model;

import framework.DBEngine;
import framework.SQLRow;
import java.sql.SQLException;
import model.actions.Action;

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
				setErrorMessage("SQLException: " + e.getMessage());
			}
		}
	}

	protected final Procedure proc;
	private final ProcedureExecutionThread thread;
	private final int id;

	private String errorMessage;
	private boolean shuttingDown = false;

	public ProcedureExecution(Procedure procedure) throws SQLException
	{
		if (procedure == null)
			throw new NullPointerException();
		
		this.proc = procedure;
		this.thread = new ProcedureExecutionThread();

		id = DBEngine.insert("report", new SQLRow() {{
			put("test", false);
			put("procedure", proc.getID());
			put("procedure_name", proc.getName());
			}}, true);
	}

	public int getID()
	{
		return id;
	}

	protected void execution() throws SQLException
	{
		Action currentAction = proc.getFirstAction();
		if (currentAction == null)
		{
			setErrorMessage("Brak akcji");
			return;
		}

		int order = 0;
		while (currentAction != null)
		{
			long start = System.currentTimeMillis() / 1000;
			currentAction.doAction(this);
			long time = (System.currentTimeMillis() / 1000) - start;
			currentAction.logExecution(this, order, time);
			currentAction = currentAction.getNextAction();
		}
	}

	protected synchronized void setErrorMessage(String message)
	{
		if (message == null)
			throw new NullPointerException();
		errorMessage = message;
	}

	protected synchronized void saveErrorMessage() throws SQLException
	{
		if (errorMessage == null)
			return;
		DBEngine.updateByID("report", new SQLRow() {{
			put("error", errorMessage);
			}}, id);
	}

	public void start()
	{
		thread.start();
	}

	public void shutdown()
	{
		shuttingDown = true;
		thread.interrupt();
	}

	public boolean isShuttingDown()
	{
		return shuttingDown;
	}
}
