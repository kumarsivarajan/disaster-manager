package model.actions;

import model.Procedure;

public class ActionEmail extends Action
{
	protected String addresses = "";
	protected String subject = "";
	protected String message = "";

	public ActionEmail(Procedure procedure)
	{
		super(procedure);
	}

	public void setAddresses(String addresses)
	{
		if (addresses == null)
			throw new NullPointerException();
		addresses = addresses.replace('\n', ' ');
		this.addresses = addresses.trim();
	}

	public String getAddresses()
	{
		return addresses;
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
		return addresses + "\n" + subject + "\n" + message;
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		String[] args = (arguments + "\n\n").split("\n", 3);
		addresses = args[0].trim();
		subject = args[1].trim();
		message = args[2].trim();
	}

	public void doAction()
	{
		//TODO: wykonanie akcji
	}
}
