
package model.actions;

import model.Procedure;
import model.ProcedureExecution;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import tools.StringTools;
import framework.Servlet;
import java.util.Vector;

public class ActionSMS extends Action
{
	//TODO: do konfiga, "final", zdublowany konfig z SMSMessage
	protected String[] recipients = new String[0];
	protected String message = "";

	protected final static String from = 
			Servlet.config.getProperty("action.SMS.from") ; // max 11 znaków tylko w wersji Premium serwisu

	protected final static String requestUrl =
			Servlet.config.getProperty("action.SMS.requestUrl");
	protected final static String key =
			Servlet.config.getProperty("action.SMS.key");

	protected final static String send =
			Servlet.config.getProperty("action.SMS.send");
	protected final static String unicode =
			Servlet.config.getProperty("action.SMS.unicode");


	//TODO ususwanie polskich znakow z tekstu SMSa


	public ActionSMS(Procedure procedure) 
	{
		super(procedure);
	}


	public void setRecipients(String recip)
	{
		if (recip == null)
			throw new NullPointerException();
		recip = recip.replace('\n', ' ');
		recip = recip.replace(',', ' ').trim();
		String[] recipientsRAW = recip.split(" ");
		Vector<String> recipientsV = new Vector<String>();
		for (String recipient : recipientsRAW)
		{
			if (recipient.equals(""))
			continue;
			try
			{
				recipientsV.add(recipient);
			}
			catch (NumberFormatException e)
			{
			}
		}
		this.recipients = recipientsV.toArray(new String[0]);
	}

	public String getRecipients()
	{
		return StringTools.join(", ", recipients);
	}

	public void setMessage(String message)
	{
		if (message == null)
			throw new NullPointerException();
		this.message = message.trim();
	}

	public String getMessage()
	{
		return message;
	}

	public ActionType getType()
	{
		return ActionType.ACTION_SMS;
	}
	
	public String getArguments()
	{
		return getRecipients() + "\n" + getMessage();
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		String[] args = (arguments + "\n\n").split("\n", 3);
		setRecipients(args[0].trim());
		setMessage(args[1].trim());
	}


	public void doAction(ProcedureExecution procExec) throws ActionException
	{

		if (message == null)
			throw new ActionException("Błąd wysyłania SMS. Brak treści wiadomości.");
 

		for (String recipient : recipients)
		{
			try
			{
				URL url = new URL(String.format(requestUrl, key, message, recipient, from,
						send, unicode).replaceAll(" ", "%20"));
				url.getContent();
			}
			catch (MalformedURLException ex)
			{
				throw new ActionException("MalformedURLException: "
						+ ex.getMessage());
			}
			catch (IOException ex)
			{
				throw new ActionException("IOException: "
						+ ex.getMessage());
			}

		}


	}
}
