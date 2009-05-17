package framework;

import java.io.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;

abstract public class Controller
{
	final protected HttpServletRequest request;
	final protected HttpServletResponse response;
	final protected ServletOutputStream output;
	final protected TplEngine tpl;
	final protected DBEngine db;
	
	final static private String defaultContentType = "application/xhtml+xml";
	
	public Controller(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		if (request == null || response == null)
			throw new NullPointerException();
		this.request = request;
		this.response = response;
		
		response.setContentType(defaultContentType);
		response.setCharacterEncoding("UTF-8");
		
		try
		{
			output = response.getOutputStream();
		}
		catch (IOException e)
		{
			throw new ServletException("Nie mogę pobrać wyjścia dla odpowiedzi: " +
					e.getMessage());
		}
		
		tpl = new TplEngine("_main.ftl", output);
		tpl.setDecoratorVar("title", "Disaster Manager"); //TODO
		tpl.setDecoratorVar("contentType", defaultContentType); //TODO
		
		try
		{
			db = new DBEngine();
		}
		catch (SQLException e)
		{
			throw new ServletException(
				"Błąd tworzenia połączenia do bazy danych: " + e.getMessage());
		}
	}
	
	protected void setContentType(String contentType)
	{
		if (contentType == null)
			throw new NullPointerException();
		response.setContentType(contentType);
		tpl.setDecoratorVar("contentType", contentType); //TODO
	}
	
	/*
	 * pierwszym parametrem w params[] powinna być nazwa akcji
	 */
	abstract public void doAction(String[] params)
			throws ServletException, SQLException;
}
