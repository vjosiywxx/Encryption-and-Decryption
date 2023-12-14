package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CCsql {
	static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/security";
	static final String USERNAME = "root";
	static final String PASSWORD = ""; // null password

	public void insertCCData(int ccKey, String ccEncryptedText) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			String sql = "INSERT INTO `ccEncryptedData`(`ccKey`, `ccEncryptedText`) VALUES (?,?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setInt(1, ccKey);
				preparedStatement.setString(2, ccEncryptedText);

				preparedStatement.executeUpdate();
			}

			connection.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

}