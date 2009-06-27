package model.actions;

import framework.Servlet;
import model.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.filter.*;

public class ActionXmppReceive extends Action
{
	protected String address = "";
	protected String message = "OK";

	protected long timeout = 10000;

	protected String login =
				Servlet.config.getProperty("action.XmppReceive.login");
	protected String password =
				Servlet.config.getProperty("action.XmppReceive.password");
	protected String host =
				Servlet.config.getProperty("action.XmppReceive.host");
	

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
		return address + "\n" + message;
	}

	protected void setArguments(String arguments)
	{
		if (arguments == null)
			throw new NullPointerException();
		String[] args = (arguments + "\n").split("\n", 2);
		address = args[0];
		message = args[1];
	}

	public void setAddress(String ad)
	{
		address = ad;
	}

	public void setMessage(String mes)
	{
		message = mes;
	}

	public String getAddress()
	{
		return address;
	}

	public String getMessage()
	{
		return message;
	}

	protected void acknowledge(boolean b)
	{
		//TODO przekazywanie informacji o odebraniu lub nie
	}

	public void doAction(ProcedureExecution procExec) throws ActionException
	{
		
		/* konfiguraca i utworzenie połaczenia */
		ConnectionConfiguration conf = new ConnectionConfiguration(host);
		conf.setSASLAuthenticationEnabled(false);
		XMPPConnection conn = new XMPPConnection(conf);

		
		/* filtr wiadomości */
		PacketFilter filter = new AndFilter(
								new PacketTypeFilter(Message.class),
								new FromContainsFilter(address));

		try
		{
			while (!conn.isConnected())
				conn.connect();
		}
		catch (XMPPException e)
		{
			throw new ActionException("(Odbieranie wiadomości) Nie można połaczyć z serwerem: "
					+ e.getMessage());
		}

		
		try
		{
			while (!conn.isAuthenticated())
				conn.login(login, password, "web");
		}
		catch (XMPPException e)
		{
			throw new ActionException("Nie można zalogować na serwerze: "
					+ e.getMessage());
		}

		/* conn musi być connected!!! */
		PacketCollector collector = conn.createPacketCollector(filter);
		
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			if (procExec.isShuttingDown())
				return;
		}
		
		while (true) // czekamy do skutku
		{
			Packet pack = collector.nextResult();
			Message mes;
			if (pack instanceof Message)
			{
				mes = (Message)pack;
				String body = mes.getBody().trim();
				if (body.startsWith(message.trim()))
				{
					acknowledge(true);
					conn.disconnect();
					return;
				}
			}
			else
			{
				throw new ActionException("Shouldn't  be");
			}
			
		}
//
//
//		/* sprawdzenie wiadomości oczekuijących na serwerze */
//		/* wiadomość serwera jabber.wp.pl "feature not impleented(!?) */
//		/*
//		OfflineMessageManager manager = new OfflineMessageManager(conn);
//		Iterator<Message> messages;
//		try
//		{
//			messages = manager.getMessages();
//			while (messages.hasNext())
//			{
//				Message mes = messages.next();
//				if (mes.getFrom().equals(address))
//				{
//					String body = mes.getBody().trim();
//					if (body.startsWith(message))
//					{
//						acknowledge(true);
//						Vector<String> nodes = new Vector<String>();
//						nodes.add(mes.getPacketID());
//						try
//						{
//							manager.deleteMessages(nodes);
//						}
//						catch (XMPPException e)
//						{
//							throw new ActionException("Nie można usunąć wiadomości z serwera: "
//									+ e.getMessage());
//						}
//						conn.disconnect();
//						return;
//					}
//				}
//			}
//		}
//		catch (XMPPException e)
//		{
//			throw new ActionException("Nie można pobrać wiadomości: "
//						+ e.getMessage());
//		}
//		*/
//
//
//		/*
//		while (true)
//		{
//			Packet pack = null;
///			OfflineMessageRequest request = new OfflineMessageRequest();
//			OfflineMessageRequest.Item it = new OfflineMessageRequest.Item("");
//			it.setAction("view");
//			it.setJid(from);
//			request.addItem(it);
//			conn.sendPacket(request);
//
//			System.out.println("Jak już tu jesteśmy");
//
//			if (0 == timeout)
//			{
//				pack = collector.nextResult();
//			}
//			else
//			{
//				pack = collector.nextResult(timeout);
//			}
//			if (pack == null)
//				throw new ActionException("Nie odebrano pakietu");
//			Message mes;
//			if (pack instanceof Message)
//			{
//				mes = (Message)pack;
//			}
//			else
///			{
//				throw new ActionException("Niewłaściwy typ pakietu");
//			}
	//		if (!mes.getFrom().equals(from))
	//		{
	//			throw new ActionException("Niewłaściwy nadawca");
	//		}
	//		if (mes.getType() == Message.Type.normal)
	//		{
	//			String body = mes.getBody().trim();
	//			if (body.startsWith("OK"));
	//				acknowledge();
	//			break;
	//		}
	//	}
	//	*/
	//	/*
	//	XmppSession session = new XmppSession(host, port,
	//					XmppSession.COMPONENT_CONNECT_NAMESPACE);
	//	Xmpp2DomTransformer transformer = new Xmpp2DomTransformer(session);
	//	DomPacketSender sender = new DomPacketSender(transformer);
	//	try
	//	{
	//		session.open();
	//	}
	//	catch (IOException e)
	//	{
	//		throw new ActionException("Nie można połączyć się z serwerem: "
	//					+ e.getMessage());
	//	}
	//	String sessionID = session.getAttribute("id");
	//	HandshakeRequest handshake = new HandshakeRequest(sessionID, "secret");
	//	try
	//	{
	//		org.dom4j.Element el = sender.query(handshake);
	//	}
	//	catch (IOException e)
	//	{
	//		throw new ActionException("Nie można nawiązać połączenia z serwerem: "
	//					+ e.getMessage());
	//	}
	//	  */
	}
}

