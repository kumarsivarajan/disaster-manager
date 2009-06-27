package model.actions;

import framework.Servlet;
import model.*;
import java.io.IOException;
import java.net.*;
import tools.StringTools;
import java.util.Vector;

public class ActionSMS extends Action
{
	protected String recipient;
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


	public void setRecipient(String recip)
	{
		if (recip == null)
			throw new NullPointerException();
		if (recip.length() == 9)
			recipient = "48" + recip;
		else
			recipient = recip;
		/*
		recip = recip.replace('\n', ' ');
		recip = recip.replace(',', ' ').trim();
		String[] recipientsRAW = recip.split(" ");
		Vector<String> recipientsV = new Vector<String>();
		for (String recipient : recipientsRAW)
		{
			if (recipient.equals(""))
			continue;
			recipientsV.add(recipient);
		}
		this.recipients = recipientsV.toArray(new String[0]);*/
	}

	public String getRecipient()
	{
		if (recipient == null)
			return "";
		return recipient;
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
		return getRecipient() + "\n" + getMessage();
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		String[] args = (arguments + "\n").split("\n", 2);
		setRecipient(args[0]);
		setMessage(args[1].trim());
	}


	public void doAction(ProcedureExecution procExec) throws ActionException
	{

		if (message == null)
			throw new ActionException("Błąd wysyłania SMS. Brak treści wiadomości.");
		if (recipient == null)
			throw new ActionException("Błąd wysyłania SMS. Brak numeru odbiorcy.");
		if (recipient.length()!= 11)
			throw new ActionException("Błąd wysyłania SMS. Błędny numer odbiorcy: " + recipient.length());
 

			try
			{
				URL url = new URL(String.format(requestUrl, key, message, recipient, from,
						send, unicode).replaceAll(" ", "%20"));
				url.getContent( );
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
