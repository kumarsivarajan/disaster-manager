package model.actions;

import java.util.Vector;
import model.Procedure;
import tools.StringTools;

public class ActionSMS extends Action
{
	protected Long[] recipients = new Long[0];
	protected String message = "";

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

	protected String getArguments()
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

	public void doAction()
	{
		//TODO: wykonanie akcji
	}
}
