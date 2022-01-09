package in.trident.crdr.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 9 jan 2022
 * 
 * @version 0.0.9c
 *
 */

public class AccessConnectionService {
	private Connection conn;
	
	public AccessConnectionService(String url) throws SQLException {
		conn = DriverManager.getConnection(url,"","shuttle");
	}
	
	public static Connection getConnection(String url) throws SQLException {
		return new AccessConnectionService(url).conn;
	}
}
