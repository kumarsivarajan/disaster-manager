package model.actions;

import model.*;
/*import com.realtime.xmpp.*;
import com.realtime.xmpp.dom.*;
import com.realtime.xmpp.util.HandshakeRequest;*/
import org.jivesoftware.smack.*;
import org.jivesoftware.smackx.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.packet.*;
import org.jivesoftware.smack.filter.*;
import java.io.IOException;
import java.util.*;


public class ActionXmppReceive extends Action
{
	protected String from = "crift@jabber.wp.pl";

	protected long timeout = 10000;

	protected String login = "disaster.manager";
	protected String password = "dmpass212";
	protected String host = "jabber.wp.pl";
	protected int port = 5222;

	public ActionXmppReceive(Procedure procedure)
	{
		super(procedure);
	}

	
	public ActionType getType()
	{
		return ActionType.ACTION_XMPP_RECEIVE;
	}

	public String getArguments()
	{
		return from;
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
	}

	protected void acknowledge(boolean b)
	{
		//TODO przekazywanie informacji o odebraniu lub nie
	}

	public void doAction(ProcedureExecution procExec) throws ActionException
	{
		ConnectionConfiguration conf = new ConnectionConfiguration(host);
		conf.setSASLAuthenticationEnabled(false);
		XMPPConnection conn = new XMPPConnection(conf);
		try
		{
			conn.connect();
		}
		catch (XMPPException e)
		{
			throw new ActionException("(Odbieranie wiadomości) Nie można połaczyć z serwerem: "
					+ e.getMessage());
		}
		try
		{
			conn.login(login, password);
		}
		catch (XMPPException e)
		{
			throw new ActionException("Nie można zalogować na serwerze: "
					+ e.getMessage());
		}

		OfflineMessageManager manager = new OfflineMessageManager(conn);
		Iterator<Message> messages;
		while (true)
		{
		try
		{
			 messages = manager.getMessages();
		}
		catch (XMPPException e)
		{
			throw new ActionException("Nie można pobrać wiadomości: "
						+ e.getMessage());
		}
		while (messages.hasNext())
		{
			Message mes = messages.next();
			if (mes.getFrom().equals(from))
			{
				String body = mes.getBody().trim();
				if (body.startsWith("OK"))
				{
					acknowledge(true);
					Vector<String> nodes = new Vector<String>();
					nodes.add(mes.getPacketID());
					try
					{
						manager.deleteMessages(nodes);
					}
					catch (XMPPException e)
					{
						throw new ActionException("Nie można usunąć wiadomości z serwera: "
									+ e.getMessage());
					}
					conn.disconnect();
					return;
				}
			}
		}
		acknowledge(false);
		}




/*
		PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class),
									new FromContainsFilter(from));
		PacketCollector collector = conn.createPacketCollector(filter);

	    try
		{
			conn.connect();
		}
		catch (XMPPException e)
		{
			throw new ActionException("Nie można połączyć z serwerem: "
						+ e.getMessage());
		}
		try
		{
			conn.login(login, password);
		}
		catch (XMPPException e)
		{
			throw new ActionException("Nie można zalogować się na serwerze: "
						+ e.getMessage());
		}

		while (true)
		{
			Packet pack = null;
			OfflineMessageRequest request = new OfflineMessageRequest();
			OfflineMessageRequest.Item it = new OfflineMessageRequest.Item("");
			it.setAction("view");
			it.setJid(from);
			request.addItem(it);
			conn.sendPacket(request);

			System.out.println("Jak już tu jesteśmy");

			if (0 == timeout)
			{
				pack = collector.nextResult();
			}
			else
			{
				pack = collector.nextResult(timeout);
			}
			if (pack == null)
				throw new ActionException("Nie odebrano pakietu");
			Message mes;
			if (pack instanceof Message)
			{
				mes = (Message)pack;
			}
			else
			{
				throw new ActionException("Niewłaściwy typ pakietu");
			}
			if (!mes.getFrom().equals(from))
			{
				throw new ActionException("Niewłaściwy nadawca");
			}
			if (mes.getType() == Message.Type.normal)
			{
				String body = mes.getBody().trim();
				if (body.startsWith("OK"));
					acknowledge();
				break;
			}
		}
		*/
		/*
		XmppSession session = new XmppSession(host, port,
						XmppSession.COMPONENT_CONNECT_NAMESPACE);
		Xmpp2DomTransformer transformer = new Xmpp2DomTransformer(session);
		DomPacketSender sender = new DomPacketSender(transformer);
		try
		{
			session.open();
		}
		catch (IOException e)
		{
			throw new ActionException("Nie można połączyć się z serwerem: "
						+ e.getMessage());
		}
		String sessionID = session.getAttribute("id");
		HandshakeRequest handshake = new HandshakeRequest(sessionID, "secret");
		try
		{
			org.dom4j.Element el = sender.query(handshake);
		}
		catch (IOException e)
		{
			throw new ActionException("Nie można nawiązać połączenia z serwerem: "
						+ e.getMessage());
		}
		  */
	}



}

