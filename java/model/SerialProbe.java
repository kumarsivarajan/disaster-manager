package model;

//uprawnienia: chmod 0666 /dev/ttyUSB*

import java.io.*;
import java.util.*;
import javax.comm.*;

// http://java.sun.com/products/javacomm/reference/api/index.html
public class SerialProbe implements SerialPortEventListener
{
	private final int readTimeout = 5000;

	private OutputStream output;
	private InputStream input;

	private String serialBuffer = "";
	private final Queue<String> cmdBuffer = new LinkedList<String>();
	private final byte[] byteBuffer = new byte[1024];

	private static SerialProbe instance;
	private final SerialPort port;
	private boolean disconnected = false;

	private DisconnectThread disconnectThread;

	private class DisconnectThread extends Thread
	{
		final static int defaultTimeout = 10000;
		final static int timeResolution = 100;
		public int timeLeft = defaultTimeout;
		private SerialProbe probe;

		public DisconnectThread(SerialProbe probe)
		{
			this.probe = probe;
			this.setDaemon(true);
		}

		@Override public void run()
		{
			while (timeLeft > 0)
			{
				try
				{
					Thread.sleep(timeResolution);
				}
				catch (InterruptedException e)
				{
					break;
				}

				timeLeft -= timeResolution;
			}

			probe.disconnect();
		}

		public void reset()
		{
			timeLeft = defaultTimeout;
		}
	}

	private SerialProbe()
	{
		synchronized (SerialProbe.class)
		{
			if (instance != null)
				throw new AssertionError("Już istnieje instancja klasy");

			disconnectThread = new DisconnectThread(this);

			Enumeration ports;
			try
			{
				ports = CommPortIdentifier.getPortIdentifiers();
			}
			catch (Throwable e)
			{
				throw new SerialCommunicationException(e);
			}
			
			SerialPort currentPort = null;
			boolean portFound = false;

			String checked = "";

			while (ports.hasMoreElements())
			{
				CommPortIdentifier portId = (CommPortIdentifier)ports.nextElement();

				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL &&
					portId.getName().startsWith("/dev/ttyUSB"))
				{
					checked += portId.getName() + ", ";
					
					try
					{
						currentPort = (SerialPort)portId.open("disaster-manager", readTimeout);
					}
					catch (Throwable e) //PortInUseException
					{
						continue;
					}

					try
					{
						currentPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
						currentPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
						currentPort.addEventListener(this);
						currentPort.notifyOnDataAvailable(true);

						input = currentPort.getInputStream();
						output = currentPort.getOutputStream();
					}
					catch (TooManyListenersException e)
					{
						currentPort.close();
						throw new SerialCommunicationException(e);
					}
					catch (Throwable e)
					{
						currentPort.close();
						continue;
					}

					if (!doQuery("HELLO DM-HW-PROBE").equals("WELCOME DM-MASTER"))
					{
						currentPort.close();
						continue;
					}
					
					portFound = true;
					break;
				}
			}

			if (!portFound)
			{
				if (!checked.equals(""))
					checked = checked.substring(0, checked.length() - 2);
				throw new SerialCommunicationException("Nie znaleziono odpowiedniego portu. Sprawdzone: [" + checked + "]. Ustawiłeś uprawnienia?");
			}

			port = currentPort;
			instance = this;
			disconnectThread.start();
		}
	}

	protected synchronized String doQuery(String query)
	{
		if (disconnected)
			throw new SerialCommunicationException("Port jest zamknięty");
		if (disconnectThread == null)
			throw new NullPointerException("Nie ma wątku rozłączania");
		disconnectThread.reset();
		try
		{
			int timeout = readTimeout;
			output.write((query + "\n").getBytes());
			while (cmdBuffer.isEmpty())
			{
				disconnectThread.reset();
				try
				{
					Thread.sleep(50);
				}
				catch (InterruptedException e)
				{
					disconnect();
					throw new SerialCommunicationException(e);
				}
				timeout -= 50;
				if (timeout < 0)
				{
					disconnect();
					throw new SerialCommunicationException("Czas przekroczony");
				}
				if (disconnected)
					throw new SerialCommunicationException("Port nagle zamknięty");
			}

			String cmd = cmdBuffer.poll();
			if (cmd == null)
				throw new NullPointerException("Kolejka nie jest pusta");

			disconnectThread.reset();
			
			return cmd;
		}
		catch (IOException e)
		{
			throw new SerialCommunicationException("Nie można czytać z portu");
		}
	}

	public void serialEvent(SerialPortEvent event)
	{
		try
		{
			int r = input.read(byteBuffer);
			serialBuffer += new String(byteBuffer, 0, r);
			int npos;
			if ((npos = serialBuffer.indexOf("\n")) >= 0)
			{
				String cmd = serialBuffer.substring(0, npos);
				serialBuffer = serialBuffer.substring(npos + 1);
				cmdBuffer.add(cmd);
			}
		}
		catch (IOException e)
		{
			serialBuffer = "";
		}
	}

	public static boolean isConnected()
	{
		if (instance == null)
			return false;
		SerialProbe p = instance;
		if (p.disconnected)
		{
			p.disconnect();
			return false;
		}
		return true;
	}

	protected synchronized static void connect()
	{
		if (isConnected())
			return;
		new SerialProbe();
		if (!isConnected())
			throw new NullPointerException();
	}

	protected synchronized void disconnect()
	{
		if (!disconnected && port != null)
			port.close();
		disconnected = true;
		synchronized (SerialProbe.class)
		{
			if (this == instance)
				instance = null;
		}
	}

	protected synchronized static SerialProbe getConnection()
	{
		if (isConnected())
			return instance;
		connect();
		if (instance == null)
			throw new NullPointerException();
		return instance;
	}

	public static void setPort(int port, boolean on)
	{
		if (port < 0 || port > 7)
			throw new IllegalArgumentException("Niepoprawny numer portu");
		SerialProbe probe = getConnection();
		if (!probe.doQuery("SETPORT " + port + " " + (on?"1":"0")).
				equals("AS YOU WISH, MASTER"))
				throw new SerialCommunicationException("Nieprawidłowa odpowiedź z czujnika");		
	}

	public static boolean getPort(int port)
	{
		if (port < 0 || port > 7)
			throw new IllegalArgumentException("Niepoprawny numer portu");
		SerialProbe probe = getConnection();
		String r = probe.doQuery("GETPORT " + port);
		if (!r.startsWith("PORT " + port + " IS ") || !r.substring(11).equals(", MASTER"))
			throw new SerialCommunicationException("Nieprawidłowa odpowiedź z czujnika");
		char state = r.charAt(10);
		if (state == '1')
			return true;
		else if (state == '0')
			return false;
		else
			throw new SerialCommunicationException("Nieprawidłowa odpowiedź z czujnika");
	}
}
