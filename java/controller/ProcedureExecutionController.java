package controller;

import framework.Controller;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import model.Procedure;

public class ProcedureExecutionController extends Controller
{
	public ProcedureExecutionController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}

	private void listAction() throws ServletException, SQLException
	{
		Procedure[] procedures = Procedure.getAllProcedures(true);
		tpl.setVar("procedures", procedures);
		tpl.display("procedureExecution-list.ftl");
	}

	private void runAction(int id) throws SQLException
	{
		Procedure proc = Procedure.getProcedureByID(id);

		proc.execute();

		redirect("/procedureExecution/list/", RedirectType.REDIR_SEEOTHER);
	}

	private void shutdownAction(int id) throws SQLException
	{
		Procedure proc = Procedure.getProcedureByID(id);

		proc.stopExecution();

		redirect("/procedureExecution/list/", RedirectType.REDIR_SEEOTHER);
	}

	public void doAction(String[] params) throws ServletException, SQLException
	{
		if (params.length != 1)
			throw new ServletException("Zła ilośc parametrów");
		params = params[0].split("/");

		String akcja = params[0];
		if (akcja.equals("list"))
			listAction();
		else if (akcja.equals("run"))
		{
			if (params.length != 2)
				throw new ServletException("Zła ilośc parametrów (2st)");
			runAction(Integer.parseInt(params[1]));
		}
		else if (akcja.equals("shutdown"))
		{
			if (params.length != 2)
				throw new ServletException("Zła ilośc parametrów (2st)");
			shutdownAction(Integer.parseInt(params[1]));
		}
		else
			throw new ServletException("TODO: komunikaty 404: " + akcja);
	}
}
