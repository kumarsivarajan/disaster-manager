package controller;

import framework.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;
import model.actions.*;

public class ActionManagerController extends Controller
{
	public ActionManagerController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}

	private void addAction() throws ServletException, SQLException
	{
		//najpierw formularz z typem akcji do wybrania (najlepiej od razu na liście procedury)

		throw new ServletException("Nie zaimplementowano");

		/*Action action = new Action();
		proc.save(false);

		tpl.setVar("procedure", proc);
		tpl.display("procedureManager-form.ftl");
		return;*/
	}
	
/*
	private void editAction(int id) throws ServletException, SQLException
	{
		
	}

	private void deleteAction(int id) throws ServletException, SQLException
	{
		
	}
*/
	
	public void doAction(String[] params) throws ServletException, SQLException
	{
		if (params.length != 1)
			throw new ServletException("Zła ilośc parametrów");
		params = params[0].split("/");
		
		String akcja = params[0];
		if (akcja.equals("add"))
			addAction();
		/*else if (akcja.equals("edit"))
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
		}*/
		else
			throw new ServletException("TODO: komunikaty 404: " + akcja);
	}
}
