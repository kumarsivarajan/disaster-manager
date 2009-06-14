package controller;

import framework.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;
import model.actions.*;
import model.Procedure;

public class ActionManagerController extends Controller
{
	public ActionManagerController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}

	private void addAction(int procedureID) throws ServletException, SQLException
	{
		Procedure proc = Procedure.getProcedureByID(procedureID);

		Action.ActionType type;

		try
		{
			type = Action.actionTypeFromInt((int)getParameterLong("type"));
		}
		catch (IllegalArgumentException e)
		{
			this.message("Nieprawidłowy parametr");
			return;
		}

		Action action = Action.createAction(proc, type);

		tpl.setVar("action", action);
		tpl.display("actionManager-form.ftl");
	}

	private void editAction(int id) throws ServletException, SQLException
	{
		Action action = Action.getActionByID(id);

		if (request.getMethod().equalsIgnoreCase("POST"))
		{
			action.setLabel(getParameterString("label"));

			int maxTime = (int)getParameterLong("maxtime");
			if (maxTime > 0)
				action.setMaxTime(maxTime);

			action.save(true);
			redirect("/procedureManagement/edit/" +
					action.getProcedure().getID()
					+ "/", RedirectType.REDIR_SEEOTHER);
			return;
		}

		tpl.setVar("action", action);
		tpl.display("actionManager-form.ftl");
	}
	
	private void deleteAction(int id) throws ServletException, SQLException
	{
		Action action = Action.getActionByID(id);
		String url = "/procedureManagement/edit/" +
				action.getProcedure().getID()
				+ "/";
		Action.delete(action);
		redirect(url, RedirectType.REDIR_SEEOTHER);
	}
	
	public void doAction(String[] params) throws ServletException, SQLException
	{
		if (params.length != 1)
			throw new ServletException("Zła ilośc parametrów");
		params = params[0].split("/");
		
		String akcja = params[0];
		if (akcja.equals("add"))
			addAction(Integer.parseInt(params[1]));
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
