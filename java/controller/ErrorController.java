package controller;

import framework.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ErrorController extends Controller
{
	public ErrorController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}
	
	public void doAction(String[] params) throws ServletException
	{
		int errCode;
		try
		{
			errCode = Integer.parseInt(params[0]);
		}
		catch (NumberFormatException e)
		{
			errCode = 500;
		}
		catch (IndexOutOfBoundsException e)
		{
			errCode = 500;
		}
		
		tpl.setVar("code", errCode);
		tpl.display("error.ftl");
	}
}
