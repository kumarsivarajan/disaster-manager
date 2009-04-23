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
		tpl.setVar("test", db.query("SHOW TABLES"));
		tpl.display("index.ftl");
	}
}
