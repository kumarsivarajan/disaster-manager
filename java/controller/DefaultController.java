package controller;

import framework.*;
import java.net.*;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import model.SerialCommunicationException;
import model.SerialProbe;

public class DefaultController extends Controller
{
	public DefaultController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	{
		super(request, response);
	}
	
	public void doAction(String[] params) throws ServletException, SQLException
	{
		tpl.display("index.ftl");
	}
}
