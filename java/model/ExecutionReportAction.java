package model;

import framework.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import model.actions.Action;

public class ExecutionReportAction
{
	private final ExecutionReport report;
	private Action.ActionType actionType;
	private Integer maxTime;
	private int usedTime;
	private Timestamp date;

	public ExecutionReportAction(ExecutionReport report)
	{
		if (report == null)
			throw new NullPointerException();
		this.report = report;
	}

	protected static ExecutionReportAction getReportActionFromSQL(SQLRow row)
			throws SQLException
	{
		if (row.get("report") == null ||
			row.get("order") == null ||
			row.get("type") == null ||
			row.get("arguments") == null || //TODO: na razie ignorujemy
			row.get("usedtime") == null ||
			row.get("date") == null
			)
			throw new NullPointerException("Z bazy odebrano NULL");

		int order = (Integer)row.get("order");

		if (order <= 0)
			throw new IllegalArgumentException("Niepoprawny order");

		ExecutionReport report =
				ExecutionReport.getReportByID((Integer)row.get("report"));

		ExecutionReportAction rAction = new ExecutionReportAction(report);
		rAction.actionType = Action.actionTypeFromInt((Integer)row.get("type"));
		if (row.get("maxtime") != null)
			rAction.maxTime = (Integer)row.get("maxtime");
		rAction.usedTime = (Integer)row.get("usedtime");
		rAction.date = (Timestamp)row.get("date");

		return rAction;
	}

	protected static ExecutionReportAction[] getReportActionFromSQL(SQLRows rows)
			throws SQLException
	{
		ExecutionReportAction[] out = new ExecutionReportAction[rows.size()];
		int i = 0;
		for (SQLRow reportActionRAW : rows)
			out[i++] = getReportActionFromSQL(reportActionRAW);
		return out;
	}

	public static ExecutionReportAction[] getReportActionsByReport(ExecutionReport report)
			throws SQLException
	{
		return getReportActionFromSQL(DBEngine.getAllRows(
				"SELECT * FROM `report_action` WHERE report = " +
				report.getID() +
				" ORDER BY `order` ASC"));
	}

	public static void makeNewReportAction(final ExecutionReport report,
			final Action action, final long usedTime, final int order) throws SQLException
	{
		if (report == null || action == null)
			throw new NullPointerException();
		DBEngine.insert("report_action", new SQLRow() {{
			put("report", report.getID());
			put("order", order);
			put("type", Action.actionTypeToInt(action.getType()));
			put("arguments", action.getArguments());
			put("maxtime", action.getMaxTime());
			put("usedtime", usedTime);
			}}, false);
	}

	public String getTypeName()
	{
		return Action.actionTypeToName(actionType);
	}

	public Integer getMaxTime()
	{
		return maxTime;
	}

	public int getUsedTime()
	{
		return usedTime;
	}

	public boolean haveTimeoutWarning()
	{
		if (getMaxTime() == null)
			return false;
		return (getUsedTime() > getMaxTime());
	}

	public Timestamp getDate()
	{
		return date;
	}
}
