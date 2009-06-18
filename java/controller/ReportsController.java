package controller;

import framework.*;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import model.ExecutionReport;

public class ReportsController extends Controller
{
	public ReportsController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}

	private void listAction() throws ServletException, SQLException
	{
		ExecutionReport[] reports = ExecutionReport.getAllReports();
		tpl.setVar("reports", reports);
		tpl.display("reports-list.ftl");
	}

	private void detailsAction(int id) throws ServletException, SQLException
	{
		ExecutionReport report = ExecutionReport.getReportByID(id);

		tpl.setVar("report", report);
		tpl.display("reports-details.ftl");
	}

	public void doAction(String[] params) throws ServletException, SQLException
	{
		if (params.length != 1)
			throw new ServletException("Zła ilośc parametrów");
		params = params[0].split("/");

		String akcja = params[0];
		if (akcja.equals("list"))
			listAction();
		else if (akcja.equals("details"))
		{
			if (params.length != 2)
				throw new ServletException("Zła ilośc parametrów (2st)");
			detailsAction(Integer.parseInt(params[1]));
		}
		else
			throw new ServletException("TODO: komunikaty 404: " + akcja);
	}
}
