package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DESsql {
	static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/security";
	static final String USERNAME = "root";
	static final String PASSWORD = ""; // null password

	public void insertDESData(String desEncryptedKey, String desEncryptedText) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

			String sql = "INSERT INTO `desEncryptedData`(`desEncryptedKey`, `desEncryptedText`) VALUES (?,?)";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, desEncryptedKey);
				preparedStatement.setString(2, desEncryptedText);

				preparedStatement.executeUpdate();

			}

			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
