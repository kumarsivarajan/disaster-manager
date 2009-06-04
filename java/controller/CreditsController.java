package controller;

import framework.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 *
 * @author Pawel
 */
public class CreditsController extends Controller
{
	public CreditsController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}

	public void doAction(String[] params) throws ServletException
	{
		tpl.display("credits.ftl");
	}
}
