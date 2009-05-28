package controller;

import framework.*;
import java.sql.SQLException;
import java.util.Vector;
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
		Procedure[] procedures = Procedure.getAllProcedures();
		tpl.setVar("procedures", procedures);
		tpl.display("procedureManager-list.ftl");
	}

	private void addAction() throws ServletException, SQLException
	{
		Procedure proc = new Procedure();
		proc.save();

		tpl.setVar("procedure", proc);
		tpl.display("procedureManager-form.ftl");
		return;
	}

	private void editAction(int id) throws ServletException, SQLException
	{
		Procedure proc = Procedure.getProcedureByID(id);

		if (request.getMethod().equalsIgnoreCase("POST"))
		{
			throw new ServletException("Nie zaimplementowano");
		}

		tpl.setVar("procedure", proc);
		tpl.display("procedureManager-form.ftl");
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
			throw new ServletException("TODO: nie zaimplementowano usuwania");
		else
			throw new ServletException("TODO: komunikaty 404: " + akcja);
	}
}
