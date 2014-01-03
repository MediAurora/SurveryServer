package com.inn.survey.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private Connection conn = null;

	public static final String USER_NAME = "surveyserver";

	public static final String PASSWORD = "surveyserver";

	public static final String DRIVER = "com.mysql.jdbc.Driver";

	public static final String URL = "jdbc:mysql://localhost:3306/";

	public static final String DB_NAME = "surveyserver";

	public Connection getDBConnection() {

		try {
			Class.forName(DRIVER);
			String url = URL + DB_NAME;
			conn = (Connection) DriverManager.getConnection(url, USER_NAME,
					PASSWORD);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
