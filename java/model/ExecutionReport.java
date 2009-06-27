package model;

import framework.*;
import java.sql.*;
import java.util.HashMap;
import model.actions.Action;

public class ExecutionReport
{
	private final int id;
	private String errorMessage;
	private int order = 0; //przydatne tylko podczas wykonywania procedury
	private final ProcedureExecution execution; //może być null
	private Procedure procedure; //może być null
	private String procedureName;
	private Timestamp date;

	private static HashMap<Integer, ProcedureExecution> executions =
			new HashMap<Integer, ProcedureExecution>();

	private ExecutionReport(ProcedureExecution execution, int id)
	{
		this.id = id;
		if (execution == null)
			execution = executions.get(id);
		else
			executions.put(id, execution);
		
		if (execution != null)
		{
			this.procedure = execution.getProcedure();
			if (this.procedure != null)
				this.procedureName = procedure.getName();
		}
		this.execution = execution;
	}

	public int getID()
	{
		return id;
	}

	protected static ExecutionReport getReportFromSQL(SQLRow row)
	{
		if (row.get("id") == null ||
			row.get("test") == null || //TODO
			row.get("procedure") == null ||
			row.get("procedure_name") == null ||
			row.get("date") == null
			)
			throw new NullPointerException("Z bazy odebrano NULL");

		int id = (Integer)row.get("id");

		if (id <= 0)
			throw new IllegalArgumentException("Niepoprawny ID");

		//TODO: cache? jeżeli tak, to nie zapamiętywać raportów isFinished()

		ExecutionReport report = new ExecutionReport(null, id);

		try
		{
			Procedure procedure =
					Procedure.getProcedureByID((Integer)row.get("procedure"));
			report.procedure = procedure;
		}
		catch (SQLException e)
		{
		}

		report.procedureName = (String)row.get("procedure_name");
		report.date = (Timestamp)row.get("date");
		if (row.get("error") != null)
			report.errorMessage = (String)row.get("error");
		
		return report;
	}

	protected static ExecutionReport[] getReportsFromSQL(SQLRows rows)
	{
		ExecutionReport[] out = new ExecutionReport[rows.size()];
		int i = 0;
		for (SQLRow reportRAW : rows)
			out[i++] = getReportFromSQL(reportRAW);
		return out;
	}

	public static ExecutionReport[] getAllReports() throws SQLException
	{
		return getReportsFromSQL(DBEngine.getAllRows(
				"SELECT * FROM `report` ORDER BY date ASC"));
	}

	public static ExecutionReport getReportByID(int id) throws SQLException
	{
		return getReportFromSQL(DBEngine.getRow(
				"SELECT * FROM `report` WHERE id = " + id));
	}

	public static ExecutionReport makeNewReport(final ProcedureExecution execution)
			throws SQLException
	{
		if (execution == null) //tutaj nie może być null
			throw new NullPointerException();
		int id = DBEngine.insert("report", new SQLRow() {{
			put("test", false); //TODO
			put("procedure", execution.getProcedure().getID());
			put("procedure_name", execution.getProcedure().getName());
			}}, true);
		ExecutionReport report = new ExecutionReport(execution, id);
		return report;
	}

	public boolean isFinished()
	{
		if (execution == null)
			return true;
		return (execution.isShuttedDown());
	}

	public Procedure getProcedure()
	{
		return procedure;
	}

	public String getProcedureName()
	{
		return procedureName;
	}

	public Timestamp getDate()
	{
		if (date == null)
			throw new NullPointerException();
		return date;
	}

	public synchronized void setErrorMessage(String message)
	{
		if (message == null)
			throw new NullPointerException();
		errorMessage = message;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public synchronized void saveErrorMessage()
	{
		if (errorMessage == null)
			return;
		try
		{
			DBEngine.updateByID("report", new SQLRow() {{
				put("error", errorMessage);
				}}, id);
		}
		catch (SQLException e)
		{
		}
	}


	/**
	 * Pobiera przebieg wykonania raportowanej procedury.
	 *
	 * UWAGA: nie zaimplementowano cacheowania, więc BARDZO obciąża bazę danych
	 *  (przez pobieranie danych z tabeli `report`)
	 *
	 * @return Przebieg wykonania raportowanej procedury
	 * @throws java.sql.SQLException
	 */
	public ExecutionReportAction[] getLog() throws SQLException
	{
		return ExecutionReportAction.getReportActionsByReport(this);
	}

	public void logActionExecution(final Action action,
			final long usedTime) throws SQLException
	{
		if (action == null)
			throw new NullPointerException();
		ExecutionReportAction.makeNewReportAction(this, action, usedTime, ++order);
	}
}
