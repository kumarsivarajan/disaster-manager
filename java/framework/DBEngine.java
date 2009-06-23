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

class SQLQueryException extends SQLException
{
	protected String query;

	public SQLQueryException(SQLException source, String query)
	{
		super(source.getMessage() + "\nQuery: " + ((query == null)?"null":query));
		this.query = query;
	}
}

public class DBEngine
{
	final Connection conn;
	
	final static String DBuser = Servlet.config.getProperty("db.user");
	final static String DBpass = Servlet.config.getProperty("db.password");
	final static String DBname = Servlet.config.getProperty("db.name");
	final static String DBhost = Servlet.config.getProperty("db.host");

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
				"jdbc:mysql://" + DBhost + "/" + DBname +
				"?characterEncoding=UTF-8", DBuser, DBpass);
	}

	protected static DBEngine getConnection() throws SQLException
	{
		if (currConnection == null)
			currConnection = new DBEngine();
		return currConnection;
	}

	public int doUpdateQueryObj(String query) throws SQLException
	{
		Statement stmt = null;
		ResultSet rs = null;
		SQLException e = null;

		try
		{
			stmt = conn.createStatement();
			return stmt.executeUpdate(query);
		}
		catch (SQLException ex)
		{
			e = new SQLQueryException(ex, query);
		}
		finally
		{
			cleanUpStatement(stmt, rs);
		}

		if (e != null)
			throw e;

		throw new AssertionError("Wykonanie nie powinno tutaj dojść");
	}

	public static int doUpdateQuery(String query) throws SQLException
	{
		return DBEngine.getConnection().doUpdateQueryObj(query);
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
			e = new SQLQueryException(ex, query);
		}
		finally
		{
			cleanUpStatement(stmt, rs);
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
				setStatementValue(stmt, i + 1, sqlValues[i]);

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
			e = new SQLQueryException(ex, query);
		}
		finally
		{
			cleanUpStatement(stmt, generatedKeys);
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

	/**
	 * Ustawia obiekt parametr zapytania
	 *
	 * @param stmt Zapytanie
	 * @param paramIndex Pozycja parametru, indeksowane od 1
	 * @param val Wartość
	 * @throws java.sql.SQLException
	 */
	protected void setStatementValue(PreparedStatement stmt, int paramIndex, Object val)
			throws SQLException
	{
		if (val == null)
			stmt.setNull(paramIndex, java.sql.Types.NULL);
		else if (val instanceof String)
			stmt.setString(paramIndex, (String)val);
		else if (val instanceof Integer)
			stmt.setInt(paramIndex, (Integer)val);
		else if (val instanceof Long)
			stmt.setLong(paramIndex, (Long)val);
		else if (val instanceof Boolean)
			stmt.setBoolean(paramIndex, (Boolean)val);
		else
			throw new SQLException("Nie obsługiwany typ danych");
		//stmt.setTimestamp(i, (Timestamp)v);
	}

	protected void cleanUpStatement(Statement stmt, ResultSet results)
	{
		if (results != null)
			try
			{
				results.close();
			}
			catch (SQLException ex) { }

		if (stmt != null)
			try
			{
				stmt.close();
			}
			catch (SQLException ex) { }
	}

	protected void updateObj(String table, SQLRow values, String condition) throws SQLException
	{
		if (table == null || values == null)
			throw new NullPointerException();
		if (values.size() == 0)
			throw new IllegalArgumentException("Pusta tablica uaktualnień");
		PreparedStatement stmt = null;
		SQLException e = null;

		String query = "UPDATE `" + table + "` SET ";
		Object[] sqlValues = new Object[values.size()];
		int sqlVPos = 0;
		for (String key : values.keySet())
		{
			if (key == null || key.trim().equals(""))
				throw new SQLException("Zły nagłówek tabeli");
			sqlValues[sqlVPos] = values.get(key);

			query += "`" + key + "` = ?";
			if (sqlVPos < values.size() - 1)
				query += ", ";

			sqlVPos++;
		}

		if (condition != null)
			query += " WHERE " + condition;

		try
		{
			stmt = conn.prepareStatement(query);

			for (int i = 0; i < sqlValues.length; i++)
				setStatementValue(stmt, i + 1, sqlValues[i]);

			stmt.executeUpdate();
		}
		catch (SQLException ex)
		{
			e = new SQLQueryException(ex, query);
		}
		finally
		{
			cleanUpStatement(stmt, null);
		}

		if (e != null)
			throw e;
	}

	protected void updateByIDObj(String table, SQLRow values, int id) throws SQLException
	{
		updateObj(table, values, "id = " + id);
	}

	public static void updateByID(String table, SQLRow values, int id) throws SQLException
	{
		DBEngine.getConnection().updateByIDObj(table, values, id);
	}
}
