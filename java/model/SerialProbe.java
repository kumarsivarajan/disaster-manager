package model;

//uprawnienia: chmod 0666 /dev/ttyUSB*

import java.io.*;
import java.util.*;
import javax.comm.*;

// http://java.sun.com/products/javacomm/reference/api/index.html
public class SerialProbe implements SerialPortEventListener
{
	private OutputStream output;
	private InputStream input;

	private String serialBuffer = "";
	private final Queue<String> cmdBuffer = new LinkedList<String>();
	private final byte[] byteBuffer = new byte[1024];

	private static SerialProbe instance;
	private final SerialPort port; //TODO: a zamykanie jak zrobić? wątkiem?

/* to nie działa tak, jak trzeba (port nie otwiera się ponownie)
	private DisconnectThread disconnectThread;

	private class DisconnectThread extends Thread
	{
		final static int defaultTimeout = 5000;
		final static int timeResolution = 100;
		public int timeLeft = defaultTimeout;
		private SerialProbe probe;

		public DisconnectThread(SerialProbe probe)
		{
			this.probe = probe;
			this.setDaemon(true);
			this.start();
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
					return;
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
*/

	private SerialProbe()
	{
		synchronized (SerialProbe.class)
		{
			if (instance != null)
				throw new AssertionError("Już istnieje instancja klasy");

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
					portId.getName().startsWith("/dev/ttyUSB")) //TODO: do configa
				{
					checked += portId.getName() + ", ";
					
					try
					{
						currentPort = (SerialPort)portId.open("disaster-manager", 1000);
					}
					catch (PortInUseException e)
					{
						continue;
					}
					catch (RuntimeException e)
					{
						continue;
					}

					try
					{
						currentPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
						currentPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
						currentPort.addEventListener(this);
						currentPort.notifyOnDataAvailable(true);

						input = currentPort.getInputStream();
						output = currentPort.getOutputStream();
					}
					catch (UnsupportedCommOperationException ex)
					{
						currentPort.close();
						continue;
					}
					catch (IOException ex)
					{
						currentPort.close();
						continue;
					}
					catch (TooManyListenersException e)
					{
						currentPort.close();
						throw new SerialCommunicationException(e);
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
				throw new SerialCommunicationException("Nie znaleziono odpowiedniego portu. Sprawdzone: [" + checked + "]");

			port = currentPort;
//			disconnectThread = new DisconnectThread(this);
			instance = this;
		}
	}

	public synchronized String doQuery(String query)
	{
		try
		{
			output.write((query + "\n").getBytes());
			while (cmdBuffer.isEmpty())
			{
				try
				{
					Thread.sleep(50);
				}
				catch (InterruptedException e)
				{
					disconnect();
					throw new SerialCommunicationException(e);
				}
			}

			String cmd = cmdBuffer.poll();
			if (cmd == null)
				throw new NullPointerException("Kolejka nie jest pusta");

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
		return (instance != null);
	}

	private synchronized static void connect()
	{
		if (isConnected())
			return;
		new SerialProbe();
		if (!isConnected())
			throw new NullPointerException();
	}

	private synchronized void disconnect()
	{
		if (!isConnected())
			return;
		port.close();
		if (this == instance)
			instance = null;
	}

	public synchronized static SerialProbe getConnection()
	{
		if (isConnected())
			return instance;
		connect();
		if (!isConnected())
			throw new NullPointerException();
		return instance;
	}

}
