package model.actions;

import model.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import framework.Servlet;
import java.util.Vector;

public class ActionXmppSend extends Action {

	protected String[] addresses = new String[0];
	protected String message = "";
	protected String subject = "DManager";

	protected String from =
			Servlet.config.getProperty("action.XmppSend.from");
	protected String login =
			Servlet.config.getProperty("action.XmppSend.login");
	protected String password =
			Servlet.config.getProperty("action.XmppSend.password");
	protected String host =
			Servlet.config.getProperty("action.XmppSend.host");
	protected int port = 5222;

	public ActionXmppSend(Procedure procedure) {
		super(procedure);
	}

	public void setArguments(String arguments) {
		if (arguments == null) {
			throw new NullPointerException();
		}
		String[] args = (arguments + "\n\n").split("\n", 3);
		setAddresses(args[0].trim());
		setMessage(args[1].trim());
	}

	public String getArguments()
	{
		return getAddresses() + "\n" + getMessage();
	}

	public ActionType getType() {
		return ActionType.ACTION_XMPP_SEND;
	}

	public String getAddresses() {
		String result = "";
		for (String address : addresses)
		{
			result += address + " ";
		}
		return result.trim();
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public void setAddresses(String ad)
	{
		//TODO poprawny format
		ad = ad.replace('\n', ' ');
		ad = ad.replace(',', ' ');
		String[] addressesRAW = ad.split(" ");
		Vector<String> addressesV = new Vector<String>();
		for (String addressRAW : addressesRAW)
		{
			if (!"".equals(addressRAW))
			{
				addressesV.add(addressRAW);
			}
		}
		addresses = addressesV.toArray(new String[0]);
	}

	public void setSubject(String sub)
	{
		subject = sub;
	}

	public void setMessage(String mes)
	{
		message = mes;
	}


	public void doAction(ProcedureExecution procEx) throws ActionException
	{
		ConnectionConfiguration conf = new ConnectionConfiguration(host);
		conf.setSASLAuthenticationEnabled(false);
		XMPPConnection connection = new XMPPConnection(conf);
		try
		{
			while (!connection.isConnected())
				connection.connect();
		}
		catch (XMPPException e)
		{
			throw new ActionException("Nie można połączyć z serwerem: "
						+ e.getMessage());
		}
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			if (procEx.isShuttingDown())
				return;
		}
		try
		{
			while (!connection.isAuthenticated())
				connection.login(login, password, "web");
		}
		catch (XMPPException e)
		{
			throw new ActionException("Błąd logowania na serwerze: "
						+ e.getMessage());
		}
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			if (procEx.isShuttingDown())
				return;
		}
		for (String address : addresses)
		{
			Message mes = new Message(address);
			mes.setType(Message.Type.normal);
			mes.setSubject(subject);
			mes.setBody(message);
			connection.sendPacket(mes);
		}
		try {
		Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			if (procEx.isShuttingDown())
				return;
		}
		connection.disconnect();
		try{
			Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
			if(procEx.isShuttingDown())
				return;
		} 
		/*XmppSession session = new XmppSession(host, port,
				XmppSession.COMPONENT_ACCEPT_NAMESPACE);
		Xmpp2DomTransformer transformer = new Xmpp2DomTransformer(session);
		DomPacketSender sender = new DomPacketSender(transformer);

		try {
			session.open();

		} catch (IOException e) {
			throw new ActionException("Nie można połączyć z serwerem XMPP: " + e.getMessage());
		}
		String sessionID = session.getAttribute("id");
		HandshakeRequest handshake = new HandshakeRequest(sessionID, "secret");
		try {
			sender.query(handshake);
		} catch (IOException e) {
			throw new ActionException("Nie można nawiązać połączenia z serwerem XMPP: " + e.getMessage());
		}
		XmppWrapper xmessage = DomPacketFactory.createMessage(from, address, subject, message);
		try {
			sender.query(xmessage);
		} catch (IOException e) {
			throw new ActionException("Błąd wysyłania wiadomości XMPP: " + e.getMessage());
		}
		session.close();
		*/
	}
}
