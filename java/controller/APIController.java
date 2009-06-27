package controller;

import framework.*;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import model.*;

public class APIController extends Controller
{
	public APIController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}

	private void listProceduresAction() throws ServletException, SQLException
	{
		Procedure[] procedures = Procedure.getAllProcedures(true);

		TplEngine xmltpl = new TplEngine(null, output);

		xmltpl.setVar("procedures", procedures);

		setContentType("application/xml");
		xmltpl.display("API-listProcedures.ftl");
	}

	private void runProcedureAction(int id) throws ServletException, SQLException
	{
		Procedure proc;
		boolean success = false;

		try
		{
			proc = Procedure.getProcedureByID(id);
			success = proc.execute();
		}
		catch (SQLException e)
		{
			success = false;
		}

		TplEngine xmltpl = new TplEngine(null, output);

		xmltpl.setVar("success", success);
		xmltpl.setVar("type", "run");

		setContentType("application/xml");
		xmltpl.display("API-procedureExecution.ftl");
	}

	private void shutdownProcedureAction(int id) throws ServletException, SQLException
	{
		Procedure proc;
		boolean success = false;

		try
		{
			proc = Procedure.getProcedureByID(id);
			proc.stopExecution();
			success = true;
		}
		catch (SQLException e)
		{
			success = false;
		}

		TplEngine xmltpl = new TplEngine(null, output);

		xmltpl.setVar("success", success);
		xmltpl.setVar("type", "shutdown");

		setContentType("application/xml");
		xmltpl.display("API-procedureExecution.ftl");
	}

	public void listMessagesAction() throws ServletException, SQLException
	{
		OperatorMessage[] messages = OperatorMessage.getUnreadMessages();
		TplEngine xmltpl = new TplEngine(null, output);

		xmltpl.setVar("messages", messages);
		setContentType("application/xml");
		xmltpl.display("API-listMessages.ftl");
	}

	public void markMessageRead(int id) throws ServletException, SQLException
	{
		OperatorMessage message = OperatorMessage.getMessageByID(id);
		message.setRead();

		TplEngine xmltpl = new TplEngine(null, output);
		xmltpl.setVar("message", message);

		setContentType("application/xml");
		xmltpl.display("API-markMessageRead.ftl");
	}

	public void doAction(String[] params) throws ServletException, SQLException
	{
		if (params.length != 1)
			throw new ServletException("Zła ilośc parametrów");
		params = params[0].split("/");

		String akcja = params[0];
		if (akcja.equals("listProcedures"))
			listProceduresAction();
		else if (akcja.equals("runProcedure"))
		{
			if (params.length != 2)
				throw new ServletException("Zła ilośc parametrów (2st)");
			runProcedureAction(Integer.parseInt(params[1]));
		}
		else if (akcja.equals("shutdownProcedure"))
		{
			if (params.length != 2)
				throw new ServletException("Zła ilośc parametrów (2st)");
			shutdownProcedureAction(Integer.parseInt(params[1]));
		}
		else if (akcja.equals("listMessages"))
			listMessagesAction();
		else if (akcja.equals("markMessageRead"))
		{
			if (params.length != 2)
				throw new ServletException("Zła ilośc parametrów (2st)");
			markMessageRead(Integer.parseInt(params[1]));
		}
		else
			throw new ServletException("TODO: komunikaty 404: [" + akcja + " " + tools.StringTools.join(",", params) + "]");
	}
}
