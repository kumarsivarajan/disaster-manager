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

public class DBEngine
{
	final Connection conn;
	
	final static String DBuser = "disaster_manager"; //TODO: do jakiegoś konfiga
	final static String DBpass = "dmpass"; // j/w
	
	public DBEngine() throws SQLException
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e)
		{
			throw new SQLException("cnfe");
		}
		conn = DriverManager.getConnection(
				"jdbc:mysql://localhost/disaster_manager", DBuser, DBpass);
	}

	public String query(String query) throws SQLException
	{
		Statement stmt = null;
		ResultSet rs = null;
		SQLException e = null;

		try
		{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			String result = "result:";
			
			while (rs.next())
				result += "<br />" + rs.getString(1);
		
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
}
