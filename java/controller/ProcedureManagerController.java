package controller;

import framework.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;
import model.Procedure;

public class ProcedureManagerController extends Controller
{
	public ProcedureManagerController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}

	private void listAction() throws ServletException, SQLException
	{
		Procedure[] procedures = Procedure.getAllProcedures(false);
		tpl.setVar("procedures", procedures);
		tpl.display("procedureManager-list.ftl");
	}

	private void addAction() throws ServletException, SQLException
	{
		Procedure proc = new Procedure();
		proc.save(false);

		tpl.setVar("procedure", proc);
		tpl.display("procedureManager-form.ftl");
		return;
	}

	private void editAction(int id) throws ServletException, SQLException
	{
		Procedure proc = Procedure.getProcedureByID(id);

		if (request.getMethod().equalsIgnoreCase("POST"))
		{ //zapisywanie zmian w opisie albo nazwy procedury
			proc.setName(getParameterString("name"));
			proc.setDescription(getParameterString("description"));
			proc.setActive(getParameterBoolean("active"));

			proc.save(true);
		}

		tpl.setVar("procedure", proc);
		tpl.display("procedureManager-form.ftl");
	}

	private void deleteAction(int id) throws ServletException, SQLException
	{
		Procedure proc = Procedure.getProcedureByID(id);
		Procedure.delete(proc);

		redirect("/procedureManagement/list/", RedirectType.REDIR_SEEOTHER);
	}

	public void doAction(String[] params) throws ServletException, SQLException
	{
		if (params.length != 1)
			throw new ServletException("Zła ilośc parametrów");
		params = params[0].split("/");
		
		String akcja = params[0];
		if (akcja.equals("list"))
			listAction();
		else if (akcja.equals("add"))
			addAction();
		else if (akcja.equals("edit"))
		{
			if (params.length != 2)
				throw new ServletException("Zła ilośc parametrów (2st)");
			editAction(Integer.parseInt(params[1]));
		}
		else if (akcja.equals("delete"))
		{
			if (params.length != 2)
				throw new ServletException("Zła ilośc parametrów (2st)");
			deleteAction(Integer.parseInt(params[1]));
		}
		else
			throw new ServletException("TODO: komunikaty 404: " + akcja);
	}
}
