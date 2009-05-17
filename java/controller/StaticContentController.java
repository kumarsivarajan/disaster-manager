package controller;

import framework.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import model.StaticContent;

public class StaticContentController extends Controller
{
	public StaticContentController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}
	
	public void doAction(String[] params) throws ServletException
	{
		String resourceName = params[0];
		
		StaticContent resource = StaticContent.getContent(resourceName);
		
		if (resource == null)
			throw new ServletException("TODO: komunikaty 404");
		
		this.setContentType(resource.mimeType);
		
		tpl.writeDirectly(resource.contents);
	}
}
