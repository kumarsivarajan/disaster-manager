package framework;

import controller.*;
import java.sql.SQLException;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import tools.RegexpMatcher;

public class Servlet extends HttpServlet
{
	final private Vector<ControllerMatch> matchers = new Vector<ControllerMatch>();
	final public static String rootPath = "/srv/tomcat6/webapps/ROOT/"; //to mozna automatycznie?
	
	@Override public void init() throws ServletException
	{
		super.init();
		
		matchers.add(new ControllerMatch("Default", "^/(?:index\\.html)?$"));
		matchers.add(new ControllerMatch("Error", "^/error-([0-9]+)\\.html$"));
	}
	
	@Override public void finalize() throws Throwable
	{
		super.finalize();
	}
	
	@Override public void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		doRequest(request, response);
	}
	
	@Override public void doPost(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		doRequest(request, response);
	}
	
	protected void doRequest(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		String requestURI = request.getRequestURI();
		Controller controller = null;
		String[] params = new String[0];
		
		for (ControllerMatch cm : matchers)
		{
			String[] paramsM = cm.matcher.test(requestURI);
			if (paramsM == null)
				continue;
			
			//to można ładniej
			if (cm.controllerName.equals("Default"))
				controller = new DefaultController(request, response);
			else if (cm.controllerName.equals("Error"))
				controller = new ErrorController(request, response);
			else
				continue;
			
			params = paramsM;
		}
		
		if (controller == null)
		{
			controller = new ErrorController(request, response);
			params = new String[1];
			params[0] = "404";
		}
		
		try
		{
			controller.doAction(params);
		}
		catch (SQLException e)
		{
			ServletException se = new ServletException("Błąd bazy danych: " + e.getMessage());
			se.setStackTrace(e.getStackTrace());
			throw se;
		}
		//tu jakieś łapanie innych wyjątków
	}
}

class ControllerMatch
{
	final public String controllerName;
	final public RegexpMatcher matcher;
	
	public ControllerMatch(String controllerName, String regexp)
	{
		this.controllerName = controllerName;
		this.matcher = new RegexpMatcher(regexp);
	}
}
