package model.actions;

import javax.servlet.ServletException;

public class ActionException extends ServletException
{
	public ActionException(String message)
	{
		super(message);
	}
}
