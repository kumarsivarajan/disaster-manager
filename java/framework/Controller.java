package framework;

import freemarker.template.*; //http://freemarker.org/docs/dgui.html
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

abstract public class Controller
{
	final protected HttpServletRequest request;
	final protected HttpServletResponse response;
	final protected PrintWriter output;
	
	final private Configuration freemarkerConf = new Configuration();
	final private Map<String, Object> freemakerVars = new HashMap<String, Object>();
	final private HashMap<String, Template> freemakerTemplates = new HashMap<String, Template>();
	
	private String contentType = "application/xhtml+xml";
	
	public Controller(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		if (request == null || response == null)
			throw new NullPointerException();
		this.request = request;
		this.response = response;
		
		response.setContentType(contentType);
		response.setCharacterEncoding("UTF-8");
		
		try
		{
			output = response.getWriter();
		}
		catch (IOException e)
		{
			throw new ServletException("Nie mogę pobrać wyjścia dla odpowiedzi: " +
					e.getMessage());
		}

		//konfiguracja biblioteki freemaker
		try
		{	
			freemarkerConf.setDirectoryForTemplateLoading(
					new File(Servlet.rootPath + "WEB-INF/classes/view"));
		}
		catch (IOException e)
		{
			throw new ServletException("Nie mogę otworzyć katalogu z szablonami: " +
					e.getMessage());
		}
		freemarkerConf.setObjectWrapper(new DefaultObjectWrapper());
	}
	
	protected void setContentType(String contentType)
	{
		if (contentType == null)
			throw new NullPointerException();
		this.contentType = contentType;
		response.setContentType(contentType);
	}
	
	protected void setTplVar(String var, Object value)
	{
		if (var == null)
			throw new NullPointerException();
		freemakerVars.put(var, value);
	}
	
	private Template loadTpl(String template) throws ServletException
	{
		if (template == null)
			throw new NullPointerException();
		
		if (freemakerTemplates.containsKey(template))
			return freemakerTemplates.get(template);
		
		Template tpl = null;
		
		try
		{
			tpl = freemarkerConf.getTemplate(template);
		}
		catch (IOException e)
		{
			tpl = null;
		}
		
		if (tpl == null)
			throw new ServletException("Plik template nie istnieje (" + template + ")");
		
		freemakerTemplates.put(template, tpl);
		
		return tpl;
	}
	
	protected String parseTpl(Map vars, String template) throws ServletException
	{
		if (template == null)
			throw new NullPointerException();
		Template tpl = loadTpl(template);
		StringWriter tplOut = new StringWriter();
		
		try
		{
			tpl.process(vars, tplOut);
		}
		catch (TemplateException e)
		{
			throw new ServletException("Błąd w szablonie: " + e.getMessage());
		}
		catch (IOException e)
		{
			throw new ServletException("Problem dostępu do szablonu: " + e.getMessage());
		}
		
		tplOut.flush();
		
		return tplOut.toString();
	}
	
	protected void displayTpl(String template) throws ServletException
	{
		if (template == null)
			throw new NullPointerException();
		
		String contents = parseTpl(freemakerVars, template);
		
		Map<String, Object> mainVars = new HashMap<String, Object>();
		mainVars.put("title", "Disaster Manager"); //TODO
		mainVars.put("contentType", contentType); //TODO
		mainVars.put("contents", contents);
		
		output.print(parseTpl(mainVars, "_main.ftl"));
		output.flush();
	}
	
	/*
	 * pierwszym parametrem w params[] powinna być nazwa akcji
	 */
	abstract public void doAction(String[] params) throws ServletException;
}
