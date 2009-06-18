package controller;

import framework.*;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class DefaultController extends Controller
{
	public DefaultController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}
	
	public void doAction(String[] params) throws ServletException, SQLException
	{
		String s = "";
		for (Object a : Servlet.config.keySet())
			s += "[" + a + "] = [" + Servlet.config.getProperty((String)a) + "], ";
		message("keys: " + s);
		return;
		//tpl.display("index.ftl");
	}
}
