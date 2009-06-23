package model.actions;

import java.sql.SQLException;
import javax.servlet.ServletException;

public class ActionException extends ServletException
{
	public ActionException(String message)
	{
		super(message);
	}

	public ActionException(Throwable cause)
	{
		super(cause.getClass().getName() + ": " + cause.getMessage());
		this.initCause(cause);
	}
}
