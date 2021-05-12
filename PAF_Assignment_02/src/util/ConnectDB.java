package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {
	public static Connection connect() {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");

			// Provide the correct details: DBServer/DBName, username, password
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/gb_ordermanagement", "root", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
}
