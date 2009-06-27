package controller;

import framework.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;
import model.*;

public class ProbeManagerController extends Controller
{
	public ProbeManagerController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}

	private void listAction() throws ServletException, SQLException
	{
		Probe[] probes = Probe.getAllProbes();
		tpl.setVar("probes", probes);
		tpl.display("probeManager-list.ftl");
	}

	private void addAction() throws ServletException, SQLException
	{
		Probe probe = new Probe();
		probe.save();

		tpl.setVar("procedures", Procedure.getAllProcedures(true));
		tpl.setVar("probe", probe);
		tpl.display("probeManager-form.ftl");
		return;
	}

	private void editAction(int id) throws ServletException, SQLException
	{
		Probe probe = Probe.getProbeByID(id);

		if (request.getMethod().equalsIgnoreCase("POST"))
		{
			if (getParameterLong("proc") == 0)
			{
				message("Nie podałeś procedury");
				return;
			}

			probe.setName(getParameterString("name"));
			probe.setInterval((int)getParameterLong("interval"));
			probe.setPort((int)getParameterLong("port"));
			probe.setState(getParameterBoolean("state"));
			probe.setProcedure(Procedure.getProcedureByID(
					(int)getParameterLong("proc")
					));

			probe.save();
			redirect("/probeManagement/list/", RedirectType.REDIR_SEEOTHER);
			return;
		}

		tpl.setVar("procedures", Procedure.getAllProcedures(true));
		tpl.setVar("probe", probe);
		tpl.display("probeManager-form.ftl");
	}

	private void deleteAction(int id) throws ServletException, SQLException
	{
		Probe probe = Probe.getProbeByID(id);
		Probe.delete(probe);

		redirect("/probeManagement/list/", RedirectType.REDIR_SEEOTHER);
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
