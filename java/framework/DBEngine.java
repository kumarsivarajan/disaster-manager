package framework;

import java.sql.*;

/*
 * Instalacja jdbc dla mysql:
 * - dodać pakiet mysql-connector-java
 * - wykonać:
 *   # ln -s /usr/share/java/mysql-connector-java.jar
 *           /usr/share/tomcat6/lib/mysql-connector-java.jar
 * 
 * http://dev.mysql.com/doc/refman/5.0/en/connector-j.html
 * http://www.fluffycat.com/Java/ResultSet/
 */
import java.util.Collection;
import java.util.HashMap;

public class DBEngine
{
	final Connection conn;
	
	final static String DBuser = "disaster_manager"; //TODO: do jakiegoś konfiga
	final static String DBpass = "dmpass"; // j/w
	final static String DBname = "disaster_manager"; // j/w

	private static DBEngine currConnection; //docelowo: tablica połączeń

	protected DBEngine() throws SQLException
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e)
		{
			throw new SQLException("ClassNotFoundException: " + e.getMessage());
		}
		conn = DriverManager.getConnection(
				"jdbc:mysql://localhost/" + DBname, DBuser, DBpass);
	}

	protected static DBEngine getConnection() throws SQLException
	{
		if (currConnection == null)
			currConnection = new DBEngine();
		return currConnection;
	}

	protected SQLRows getAllRowsObj(String query) throws SQLException
	{
		Statement stmt = null;
		ResultSet rs = null;
		SQLException e = null;

		try
		{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			ResultSetMetaData cT = rs.getMetaData();
			int colCount = cT.getColumnCount();
			String[] columns = new String[colCount];
			for (int i = 0; i < colCount; i++)
				columns[i] = cT.getColumnName(i + 1);
			
			SQLRows result = new SQLRows();
			
			while (rs.next())
			{
				SQLRow row = new SQLRow();
				for (int col = 0; col < colCount; col++)
					row.put(columns[col], rs.getObject(col + 1));
				result.add(row);
			}
			
			return result;
		}
		catch (SQLException ex)
		{
			e = ex;
		}
		finally
		{
			if (rs != null)
				try
				{
					rs.close();
				}
				catch (SQLException ex) { }
			
			if (stmt != null)
				try
				{
					stmt.close();
				}
				catch (SQLException ex) { }
		}
		
		if (e != null)
			throw e;
		
		return null;
	}

	public static SQLRows getAllRows(String query) throws SQLException
	{
		return DBEngine.getConnection().getAllRowsObj(query);
	}
	
	protected SQLRow getRowObj(String query) throws SQLException
	{
		SQLRows rows = getAllRows(query);
		if (rows.size() != 1)
			throw new SQLException("Zapytanie zwróciło więcej lub mniej niż jeden wynik: " +
					rows.size());
		
		return rows.firstElement();
	}

	public static SQLRow getRow(String query) throws SQLException
	{
		return DBEngine.getConnection().getRowObj(query);
	}
	
	protected Object getCellObj(String query) throws SQLException
	{
		SQLRow row = getRow(query);
		
		if (row.size() != 1)
			throw new SQLException("Zapytanie zwróciło więcej lub mniej niż jedną kolumnę: " +
					row.size());
		
		Collection<Object> val = row.values();
		for (Object v : val)
			return v;
		
		throw new AssertionError("Nie udało się zwrócić wartości");
	}

	public static Object getCell(String query) throws SQLException
	{
		return DBEngine.getConnection().getCellObj(query);
	}

	protected Integer insertObj(String table, SQLRow values,
			boolean getID) throws SQLException
	{
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		SQLException e = null;

		String query = "INSERT INTO `" + table + "` ";
		String[] sqlHeaders = new String[values.size()];
		Object[] sqlValues = new Object[values.size()];
		int sqlVPos = 0;
		for (String key : values.keySet())
		{
			if (key == null || key.trim().equals(""))
				throw new SQLException("Zły nagłówek tabeli");
			sqlHeaders[sqlVPos] = key;
			sqlValues[sqlVPos] = values.get(key);
			sqlVPos++;
		}
		query += "(";
		if (sqlHeaders.length > 0)
		{
			for (int i = 0; i < sqlHeaders.length - 1; i++)
				query += "`" + sqlHeaders[i] + "`, ";
			query += "`" + sqlHeaders[sqlHeaders.length - 1] + "`";
		}
		query += ") VALUES (";
		if (sqlValues.length > 0)
		{
			for (int i = 0; i < sqlValues.length - 1; i++)
				query += "?, ";
			query += "?";
		}
		query += ")";

		try
		{
			stmt = conn.prepareStatement(query,
					getID?Statement.RETURN_GENERATED_KEYS:Statement.NO_GENERATED_KEYS);

			for (int i = 0; i < sqlValues.length; i++)
			{
				Object v = sqlValues[i];
				if (v == null)
					stmt.setNull(i + 1, java.sql.Types.NULL);
				else if (v instanceof String)
					stmt.setString(i + 1, (String)v);
				else if (v instanceof Integer)
					stmt.setInt(i + 1, (Integer)v);
				else if (v instanceof Long)
					stmt.setLong(i + 1, (Long)v);
				else if (v instanceof Boolean)
					stmt.setBoolean(i + 1, (Boolean)v);
				else
					throw new SQLException("Nie obsługiwany typ danych");
				//stmt.setTimestamp(i, (Timestamp)v);
			}

			stmt.executeUpdate();

			if (getID)
			{
				generatedKeys = stmt.getGeneratedKeys();

				if (!generatedKeys.next())
					throw new SQLException("Nie zwrócono żadnych kluczy");

				int gotID = generatedKeys.getInt("GENERATED_KEY");

				if (generatedKeys.next())
					throw new SQLException("Zwrócono za dużo kluczy");

				if (gotID <= 0)
					throw new SQLException("Nie wygenerowano poprawnego ID");

				return gotID;
			}
			
			return null;
		}
		catch (SQLException ex)
		{
			e = ex;
		}
		finally
		{
			if (generatedKeys != null)
				try
				{
					generatedKeys.close();
				}
				catch (SQLException ex) { }

			if (stmt != null)
				try
				{
					stmt.close();
				}
				catch (SQLException ex) { }
		}

		if (e != null)
			throw e;

		return null;
	}

	public static Integer insert(String table, SQLRow values,
			boolean getID) throws SQLException
	{
		return DBEngine.getConnection().insertObj(table, values, getID);
	}
}
