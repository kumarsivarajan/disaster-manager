package model.actions;

import model.*;
/*import com.realtime.xmpp.*;
import com.realtime.xmpp.dom.*;
import com.realtime.xmpp.util.*;
import java.io.IOException;*/
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import framework.Servlet;

public class ActionXmppSend extends Action {

	protected String address = "";
	protected String message = "";
	protected String subject = "";

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
		address = args[0].trim();
		subject = args[1].trim();
		message = args[2].trim();
	}

	public String getArguments() {
		return address + "\n" + subject + "\n" + message;
	}

	public ActionType getType() {
		return ActionType.ACTION_XMPP_SEND;
	}

	public String getAddress() {
		return address;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public void setAddress(String ad)
	{
		//TODO poprawny format
		address = ad;
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
			connection.connect();
		}
		catch (XMPPException e)
		{
			throw new ActionException("Nie można połączyć z serwerem: "
						+ e.getMessage());
		}
		try
		{
			connection.login(login, password, "web");
		}
		catch (XMPPException e)
		{
			throw new ActionException("Błąd logowania na serwerze: "
						+ e.getMessage());
		}
		Message mes = new Message(address);
		mes.setType(Message.Type.normal);
		mes.setSubject(subject);
		mes.setBody(message);
		connection.sendPacket(mes);
		try {
		Thread.sleep(500);
		}
		catch (InterruptedException e) { }
		finally {connection.disconnect();}
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
