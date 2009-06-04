package framework;

import freemarker.template.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.servlet.*;

//http://freemarker.org/docs/dgui.html
public class TplEngine
{
	private static Configuration conf;
	final private static HashMap<String, Template> templates = new HashMap<String, Template>();
	
	final private Map<String, Object> vars = new HashMap<String, Object>();
	final private Map<String, Object> decoratorVars = new HashMap<String, Object>();
	final private ServletOutputStream output;
	final private String decoratorTemplate;
	
	final private Charset outputCharset = Charset.forName("UTF-8");
	
	public TplEngine(String decoratorTemplate, ServletOutputStream writer) throws ServletException
	{
		if (conf == null)
		{
			conf = new Configuration();
			
			try
			{
				conf.setDirectoryForTemplateLoading(
						new File(Servlet.rootPath + "WEB-INF/classes/view"));
			}
			catch (IOException e)
			{
				throw new ServletException("Nie mogę otworzyć katalogu z szablonami: " +
						e.getMessage());
			}
			conf.setObjectWrapper(new DefaultObjectWrapper());
			
			conf.setDefaultEncoding("UTF-8");
			conf.setEncoding(Locale.getDefault(), "UTF-8");
		}
		
		this.decoratorTemplate = decoratorTemplate; // może być null
		this.output = writer; // może być null
	}
	
	public void setVar(String var, Object value)
	{
		if (var == null)
			throw new NullPointerException();
		vars.put(var, value);
	}
	
	public void setDecoratorVar(String var, Object value)
	{
		if (var == null)
			throw new NullPointerException();
		decoratorVars.put(var, value);
	}
	
	private static Template load(String template) throws ServletException
	{
		if (template == null)
			throw new NullPointerException();
		
		if (templates.containsKey(template))
			return templates.get(template);
		
		Template tpl = null;
		
		try
		{
			tpl = conf.getTemplate(template);
		}
		catch (IOException e)
		{
			tpl = null;
			//throw new ServletException(e.getMessage());
		}
		
		if (tpl == null)
			throw new ServletException("Plik template nie istnieje, lub jest niepoprawny (" + template + ")");
		
		templates.put(template, tpl);
		
		return tpl;
	}
	
	protected String parseOne(Map vars, String template) throws ServletException
	{
		if (template == null)
			throw new NullPointerException();
		Template tpl = load(template);
		StringWriter tplOut = new StringWriter();
		
		try
		{
			tpl.process(vars, tplOut);
		}
		catch (TemplateException e)
		{
			ServletException e2 = new ServletException("Błąd w szablonie: " + e.getMessage());
			e2.setStackTrace(e.getStackTrace());
			throw e2;
		}
		catch (IOException e)
		{
			throw new ServletException("Problem dostępu do szablonu: " + e.getMessage());
		}
		
		tplOut.flush();
		
		return tplOut.toString();
	}
	
	public String parse(String template) throws ServletException
	{
		if (template == null)
			throw new NullPointerException();
		
		String contents = parseOne(vars, template);
		
		if (decoratorTemplate != null)
		{
			setDecoratorVar("contents", contents);
			contents = parseOne(decoratorVars, decoratorTemplate);
		}
		
		return contents;
	}
	
	public void display(String template) throws ServletException
	{
		if (output == null)
			throw new NullPointerException("Nie ustawiono obiektu piszącego");
		if (template == null)
			throw new NullPointerException();
	
		try
		{
			output.write(parse(template).getBytes(outputCharset));
			output.flush();
		}
		catch (IOException e)
		{
			throw new ServletException(e);
		}
	}
	
	public void writeDirectly(byte[] contents) throws ServletException
	{
		try
		{
			this.output.write(contents);
		}
		catch (IOException e)
		{
			throw new ServletException(e);
		}
	}

	public void writeDirectly(String contents) throws ServletException
	{
		writeDirectly(contents.getBytes(outputCharset));
	}
}
