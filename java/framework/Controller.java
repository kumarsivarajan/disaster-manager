package framework;

import java.io.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;
import freemarker.template.*;

abstract public class Controller
{
	final protected HttpServletRequest request;
	final protected HttpServletResponse response;
	final protected ServletOutputStream output;
	final protected TplEngine tpl;
	
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
			request.setCharacterEncoding("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ServletException("Brak obsługi kodowania Unicode");
		}
		
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
        SimpleSequence menu = new SimpleSequence();
        {
            SimpleHash button = new SimpleHash();
            button.put("url", "/");
            button.put("caption", "Strona Główna");
            menu.add(button);
        }
        {
            SimpleHash button = new SimpleHash();
            button.put("url", "/procedureManagement/list/");
            button.put("caption", "Procedury");
            menu.add(button);
        }
        {
            SimpleHash button = new SimpleHash();
            button.put("url", "/credits");
            button.put("caption", "O programie");
            menu.add(button);
        }

        tpl.setDecoratorVar("menubuttons", menu);

		
	}
	
	protected void setContentType(String contentType)
	{
		if (contentType == null)
			throw new NullPointerException();
		response.setContentType(contentType);
		tpl.setDecoratorVar("contentType", contentType); //TODO
	}

	protected String getParameterString(String paramName)
	{
		String param = request.getParameter(paramName);
		if (param == null)
			return "";
		return param;
	}

	protected long getParameterLong(String paramName)
	{
		String param = request.getParameter(paramName);
		try
		{
			return Long.parseLong(param);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}

	protected boolean getParameterBoolean(String paramName)
	{
		String param = request.getParameter(paramName);
		if (param == null)
			return false;
		param = param.trim();
		if (param.equals("") || param.equals("0"))
			return false;
		return true;
	}

	protected void message(String message) throws ServletException
	{
		tpl.setVar("message", message);
		tpl.display("_message.ftl");
	}

	/*
	 * pierwszym parametrem w params[] powinna być nazwa akcji
	 */
	abstract public void doAction(String[] params)
			throws ServletException, SQLException;
}
