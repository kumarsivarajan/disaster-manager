package controller;

import framework.*;
import model.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessageController extends Controller
{
	 public MessageController(HttpServletRequest request,
		HttpServletResponse response) throws ServletException
	 {
		 super(request, response);
	 }

	 public void inboxAction() throws ServletException, SQLException
	 {
		 // TODO: który user jest zalogowany
		 //OperatorMessage[] msgs = OperatorMessage.getAllMessages();
		 OperatorMessage[] test = new OperatorMessage[2];
		 OperatorMessage mes1 = new OperatorMessage("czesc");
		 mes1.setID(1);
		 mes1.setDate(new Timestamp(109,6,15,0,0,0,0));
		 test[0] = mes1;
		 OperatorMessage mes2 = new OperatorMessage("na razie");
		 mes2.setID(2);
		 mes2.setRead();
		 mes2.setDate(new Timestamp(109,6,20,0,0,0,0));
		 
		 test[1] = mes2;
		 tpl.setVar("messages", test);
		 tpl.display("message.ftl");

	 }

	 public void doAction(String[] params) throws
			 ServletException, SQLException
	 {
		 if (params.length != 1)
			 throw new ServletException("Zła ilość parametrów");
		 params = params[0].split("/");

		 if (params[0].equals("inbox"))
			inboxAction();

	 }
}
