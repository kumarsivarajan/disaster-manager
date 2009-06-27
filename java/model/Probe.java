package model;

import framework.*;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Aktualnie czujki mogą być tylko sprzętowe
 */
public class Probe
{
	private Integer id;
	private String name = "";
	private int interval = 30;
	private int port = 0;
	private boolean state = true;
	private Procedure proc;

	private static HashMap<Integer, Probe> probeCache =
			new HashMap<Integer, Probe>();

	private ProbeScanner scanner = new ProbeScanner();
	protected class ProbeScanner extends Thread
	{
		private boolean stop = false;

		public ProbeScanner()
		{
			this.setDaemon(true);
			this.start();
		}

		@Override public void run()
		{
			while (true)
			{
				try
				{
					sleep(interval * 1000);
				}
				catch (InterruptedException e)
				{
					if (stop)
						break;
					else
						continue;
				}

				if (proc == null)
					continue;
				try
				{
					if (SerialProbe.getPort(port) == state)
					{
						try
						{
							proc.execute();
						}
						catch (SQLException e)
						{
						}
					}
				}
				catch (SerialCommunicationException e)
				{
					continue;
				}
			}
		}

		public void stopThread()
		{
			stop = true;
			this.interrupt();
		}
	}

	public Probe()
	{
	}

	public synchronized static void delete(Probe probe) throws SQLException
	{
		if (probe == null)
			throw new NullPointerException();
		probe.scanner.stopThread();
		probeCache.remove(probe.getID());
		DBEngine.doUpdateQuery("DELETE FROM `probe` WHERE id = " +
				probe.getID());
	}

	protected synchronized static Probe getProbeFromSQL(SQLRow row)
			throws SQLException
	{
		if (row.get("id") == null ||
			row.get("name") == null ||
			row.get("interval") == null ||
			row.get("port") == null ||
			row.get("state") == null)
			throw new NullPointerException("Z bazy odebrano NULL");

		int id = (Integer)row.get("id");

		if (id <= 0)
			throw new IllegalArgumentException("Niepoprawny ID");

		if (probeCache.containsKey(id))
			return probeCache.get(id);

		Probe p = new Probe();
		p.id = (Integer)row.get("id");
		p.name = (String)row.get("name");
		p.interval = (Integer)row.get("interval");
		p.port = (Integer)row.get("port");
		p.state = (Boolean)row.get("state");
		if (row.get("procedure") != null)
			p.proc = Procedure.getProcedureByID((Integer)row.get("procedure"));
		p.scanner.interrupt();

		probeCache.put(id, p);

		return p;
	}

	protected static Probe[] getProbesFromSQL(SQLRows rows) throws SQLException
	{
		Probe[] out = new Probe[rows.size()];
		int i = 0;
		for (SQLRow probeRAW : rows)
			out[i++] = getProbeFromSQL(probeRAW);
		return out;
	}

	public static Probe[] getAllProbes() throws SQLException
	{
		return getProbesFromSQL(DBEngine.getAllRows(
				"SELECT * FROM `probe` ORDER BY id ASC"));
	}

	public static Probe getProbeByID(int id) throws SQLException
	{
		if (probeCache.containsKey(id))
			return probeCache.get(id);
		return getProbeFromSQL(DBEngine.getRow(
				"SELECT * FROM `probe` WHERE id = " + id));
	}

	public void save() throws SQLException
	{
		SQLRow data = new SQLRow() {{
			put("name", name);
			put("interval", interval);
			put("port", port);
			put("state", state);
			put("procedure", (proc == null?null:proc.getID()));
			}};

		if (id == null)
		{
			id = DBEngine.insert("probe", data, true);
			probeCache.put(id, this);
		}
		else
			DBEngine.updateByID("probe", data, id);
	}

	public int getID()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		if (name == null)
			throw new NullPointerException();
		this.name = name.trim();
	}

	public int getInterval()
	{
		return interval;
	}

	public void setInterval(int interval)
	{
		if (interval <= 0)
			throw new IllegalArgumentException();
		this.interval = interval;
		scanner.interrupt();
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		if (port < 0 || port > 7)
			throw new IllegalArgumentException();
		this.port = port;
	}

	public boolean getState()
	{
		return state;
	}

	public void setState(boolean state)
	{
		this.state = state;
	}

	public Procedure getProcedure()
	{
		return proc;
	}

	public void setProcedure(Procedure proc)
	{
		if (proc == null)
			throw new NullPointerException();
		this.proc = proc;
	}
}
