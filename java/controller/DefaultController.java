package controller;

import framework.*;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import freemarker.template.*;

public class DefaultController extends Controller
{
	public DefaultController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}
	
	public void doAction(String[] params) throws ServletException, SQLException
	{
        SimpleSequence sites = new SimpleSequence();
        {
            SimpleHash site = new SimpleHash();
            site.put("url", "procedureManagement/list/");
            site.put("caption", "Zarządzanie procedurami");
            sites.add(site);
        }
        tpl.setVar("sites", sites);
		tpl.display("index.ftl");
	}
}
