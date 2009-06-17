package model.actions;

import model.*;
import tools.StringTools;
import java.util.*;

/*
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
*/

public class ActionEmail extends Action
{
	protected String[] addresses = new String[0];
	protected String subject = "";
	protected String message = "";

	protected final static String from = "disaster@manager.pl";
	protected final static String host = "localhost";
	protected final static int port = 25;
	protected final static String login = "dmanager";
	protected final static String password = "disastermanager1";

	public ActionEmail(Procedure procedure)
	{
		super(procedure);
	}

	/**
	 * Ustaw odbiorców emaila
	 *
	 * @param addresses Adresy email odbiorców, oddzielone spacjami albo przecinkami
	 */
	public void setAddresses(String addresses)
	{
		if (addresses == null)
			throw new NullPointerException();
		addresses = addresses.replace('\n', ' ');
		addresses = addresses.replace(',', ' ').trim();
		String[] addressesRAW = addresses.split(" ");
		Vector<String> addressesV = new Vector<String>();
		for (String address : addressesRAW)
		{
			if (address.equals(""))
				continue;
			addressesV.add(address);
		}
		this.addresses = addressesV.toArray(new String[0]);
	}

	/**
	 * Pobiera odbiorców emaila
	 *
	 * @return Adresy email odbiorców, oddzielone przecinkami
	 */
	public String getAddresses()
	{
		return StringTools.join(", ", addresses);
	}

	public void setSubject(String subject)
	{
		if (subject == null)
			throw new NullPointerException();
		subject = subject.replace('\n', ' ');
		this.subject = subject.trim();
	}

	public String getSubject()
	{
		return subject;
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
		return ActionType.ACTION_EMAIL;
	}

	protected String getArguments()
	{
		return getAddresses() + "\n" + getSubject() + "\n" + getMessage();
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		String[] args = (arguments + "\n\n").split("\n", 3);
		setAddresses(args[0].trim());
		setSubject(args[1].trim());
		setMessage(args[2].trim());
	}

	public void doAction(ProcedureExecution procExec)// throws ActionException
	{
		/*
		try
		{
			Properties prop = System.getProperties();
			prop.put("mail.host", host);
			prop.put("mail.from", from);
			prop.put("mail.user", login);
			prop.put("mail.password", password);
			prop.put("mail.port", port);
			Session session = Session.getDefaultInstance(prop);
			MimeMessage letter = new MimeMessage(session);
			letter.setFrom(new InternetAddress(from));
			
			String[] many_addresses = addresses.split(" ");
			letter.addRecipient(RecipientType.TO, new InternetAddress(many_addresses[0]));
		
			letter.setSubject(subject);
				
			letter.setText(message);
		
			for (int i = 1; i < many_addresses.length; i++)
			{
				letter.addRecipient(RecipientType.CC, new InternetAddress(many_addresses[i]));
			}
			/ *
			Transport transport = session.getTransport("smtp");

			transport.connect(host, port, login, password);
			transport.sendMessage(letter, letter.getAllRecipients());
			* /
			Transport.send(letter);
		}	
		catch (MessagingException e)
		{
			throw new ActionException("Messaging: "+e.getMessage());
		}
		*/
	}
}
