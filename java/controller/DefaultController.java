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
		Object o = db.getCell("SELECT null");
		
		if (o == null)
			tpl.setVar("test", "NULL");
		else
			tpl.setVar("test", o.getClass().getName());
		tpl.display("index.ftl");
	}
}
