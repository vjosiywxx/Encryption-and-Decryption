package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Loginsql {
	static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/security";
	static final String USERNAME = "root";
	static final String PASSWORD = ""; // null password

	public void insertloginData(String username, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

			String sql = "INSERT INTO `loginData`(`username`, `password`) VALUES (?,?)";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, password);

				preparedStatement.executeUpdate();

			}

			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

}