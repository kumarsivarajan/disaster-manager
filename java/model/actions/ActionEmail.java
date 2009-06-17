package model.actions;

import java.util.Vector;
import model.Procedure;
import tools.StringTools;

public class ActionEmail extends Action
{
	protected String[] addresses = new String[0];
	protected String subject = "";
	protected String message = "";

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

	public void doAction()
	{
		//TODO: wykonanie akcji
	}
}
