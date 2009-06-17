package controller;

import framework.Controller;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ProcedureExecutionController extends Controller
{
	public ProcedureExecutionController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}

	private void listAction() throws ServletException
	{
		message("TODO");
	}

	public void doAction(String[] params) throws ServletException, SQLException
	{
		if (params.length != 1)
			throw new ServletException("Zła ilośc parametrów");
		params = params[0].split("/");

		String akcja = params[0];
		if (akcja.equals("list"))
			listAction();
		else
			throw new ServletException("TODO: komunikaty 404: " + akcja);
	}
}
