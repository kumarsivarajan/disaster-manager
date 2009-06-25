package model;

public class SerialCommunicationException extends AssertionError
{
	public SerialCommunicationException(String msg)
	{
		super(msg);
	}

	public SerialCommunicationException(Throwable cause)
	{
		super(cause.getClass().getName() + ": " + cause.getMessage());
		this.initCause(cause);
	}
}
