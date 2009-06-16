package framework;

import java.net.*;
import java.io.IOException;

class SMSException extends Exception {
	SMSException(String msg)
	{
		super(msg);
	}

	}

public class SMSEngine
{
	private static String requestUrl = "http://sms.wadja.com/partners/sms/default.aspx" +
			"?key=%s&msg=%s&to=%s&from=%s&send=%d&unicode=%s/";
	private static String key = "21FA03E3F080";
	private String msg;
	private String to;
	private static String from = "DisManager";
	private static int send = 1;
	private static String unicode = "no";

	private URL url;

	public void setMessage(String message)
	{
		msg = message;
	}
	public void setPhone(String number)
	{
		to = number;
	}
	public void sendSMS() throws SMSException, IOException
	{
		if (to == null)
			throw new SMSException("Błąd wysyłania SMS. Brak adresata.");
		if (msg == null)
			throw new SMSException("Błąd wysyłania SMS. Brak treści wiadomości.");
	
		url = new URL(String.format(requestUrl, key, msg, to, from,
						send, unicode).replaceAll(" ", "%20"));
		
		url.getContent();

	}



}
