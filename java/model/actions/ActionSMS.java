
package model.actions;

import java.io.IOException;
import model.Procedure;
import java.net.*;

public class ActionSMS extends Action
{
	protected String number;
	protected String message;
    protected static String from = "DisManager"; // max 11 znaków

	protected static String requestUrl = "http://sms.wadja.com/partners/sms/default.aspx" +
			"?key=%s&msg=%s&to=%s&from=%s&send=%d&unicode=%s/";
	protected static String key = "21FA03E3F080";

	protected static int send = 1;
	protected static String unicode = "no";


	public ActionSMS(Procedure procedure) 
	{
		super(procedure);
	}

	public ActionType getType() { return ActionType.ACTION_SMS; }
	
	public String getArguments()
	{
		return number + "\n" + message;
	}

	public void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		String[] args = (arguments + "\n\n").split("\n",2);
		number = args[0].trim();
	    message = args[1].trim();
	}

	public void doAction() throws ActionException
	{
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


	}
}
