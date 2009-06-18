
package model.actions;

import model.*;
import java.util.Vector;
import tools.StringTools;

public class ActionSMS extends Action
{
	//TODO: do konfiga, "final", zdublowany konfig z SMSMessage
	protected Long[] recipients = new Long[0];
	protected String message = "";
	
    protected final static String from = "DisManager"; // max 11 znaków

	protected final static String requestUrl = "http://sms.wadja.com/partners/sms/default.aspx" +
			"?key=%s&msg=%s&to=%s&from=%s&send=%d&unicode=%s/";
	protected final static String key = "21FA03E3F080";

	protected final static int send = 1;
	protected final static String unicode = "no";


	public ActionSMS(Procedure procedure) 
	{
		super(procedure);
	}


	public void setRecipients(String recipients)
	{
		if (recipients == null)
			throw new NullPointerException();
		recipients = recipients.replace('\n', ' ');
		recipients = recipients.replace(',', ' ').trim();
		String[] recipientsRAW = recipients.split(" ");
		Vector<Long> recipientsV = new Vector<Long>();
		for (String recipient : recipientsRAW)
		{
			if (recipient.equals(""))
			continue;
			try
			{
				recipientsV.add(Long.parseLong(recipient));
			}
			catch (NumberFormatException e)
			{
			}
		}
		this.recipients = recipientsV.toArray(new Long[0]);
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


	public void doAction(ProcedureExecution procExec)// throws ActionException
	{
/*
		if (number == null)
			throw new ActionException("Błąd wysyłania SMS. Brak adresata.");
		if (message == null)
			throw new ActionException("Błąd wysyłania SMS. Brak treści wiadomości.");
 

		try
		{
			URL url = new URL(String.format(requestUrl, key, message, number, from,
						send, unicode).replaceAll(" ", "%20"));
			url.getContent();
		}
		catch (IOException ex)
		{
			throw new ActionException(ex.getMessage());
		}
*/

	}
}
