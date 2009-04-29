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

public class DBEngine
{
	final Connection conn;
	
	final static String DBuser = "disaster_manager"; //TODO: do jakiegoś konfiga
	final static String DBpass = "dmpass"; // j/w
	final static String DBname = "disaster_manager"; // j/w
	
	public DBEngine() throws SQLException
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

	public SQLRows getAllRows(String query) throws SQLException
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
	
	public SQLRow getRow(String query) throws SQLException
	{
		SQLRows rows = getAllRows(query);
		if (rows.size() != 1)
			throw new SQLException("Zapytanie zwróciło więcej lub mniej niż jeden wynik: " +
					rows.size());
		
		return rows.firstElement();
	}
	
	public Object getCell(String query) throws SQLException
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
}
