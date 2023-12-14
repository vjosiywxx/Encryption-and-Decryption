package application;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//import com.mysql.cj.xdevapi.Statement;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
	private boolean isLoggedIn = false;
	private boolean isEncrypted = false;
	String colorString;

	private CaesarCipher CaesarCipher;
	private DES des;
	private DES SecretKeyDES;
	private AES aes;
	private AES SecretKeyAES;
	private CCsql ccsql;
	private DESsql dessql;
	private AESsql aessql;
	private Loginsql loginsql;
	private Connection connection;
	private Statement statement;
	VBox hb;

	@Override
	public void start(Stage primaryStage) throws NoSuchAlgorithmException {
		CaesarCipher = new CaesarCipher();
		des = new DES();
//		SecretKeyDES = new DES();

		aes = new AES();
		SecretKeyAES = new AES();
		ccsql = new CCsql();
		dessql = new DESsql();
		aessql = new AESsql();
		loginsql = new Loginsql();

		primaryStage.setTitle("Login");
		VBox loginVBox = new VBox();
		loginVBox.setAlignment(Pos.CENTER);
		loginVBox.setSpacing(10);
		loginVBox.setPadding(new Insets(20));

		Label usernameLabel = new Label("Username: ");
		TextField usernameField = new TextField();
		Label pswLabel = new Label("Password: ");
		PasswordField passwordField = new PasswordField();
		Button loginButton = new Button("Login");

		Label loginStatus = new Label();

		loginButton.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();

//			if (username.equals("user") && password.equals("pass")) {
//				loginsql.insertloginData(username, password);
			isLoggedIn = true;
			loginStatus.setText("Successfully logged in!");
			algorithmPage(primaryStage);
//			} else {
//				loginStatus.setText("Something goes wrong, please try again!");
//			}
		});

		loginVBox.getChildren().addAll(usernameLabel, usernameField, pswLabel, passwordField, loginButton, loginStatus);
		Scene loginScene = new Scene(loginVBox, 500, 500);
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}

	private void algorithmPage(Stage primaryStage) {
		if (!isLoggedIn) {
			return;
		}
		primaryStage.setTitle("Encryption & Decryption");
		hb = new VBox();

		MenuButton menuButton = new MenuButton("Choose Algorithmn");
		MenuItem ccMenuItem = new MenuItem("CaesarCiper");
		MenuItem aesMenuItem = new MenuItem("AES");
		MenuItem desMenuItem = new MenuItem("DES");
		menuButton.getItems().addAll(ccMenuItem, aesMenuItem, desMenuItem);

		Label textLabel = new Label("Enter text:");
		TextField textField = new TextField();
		Label keyLabel = new Label("Key (for Caesar Cipher ONLY) :");
		TextField keyField = new TextField();
		Label resultLabel = new Label("Result:");
		TextField resultTextField = new TextField();
		resultTextField.setEditable(false);
		Label keyResultLabel = new Label(" Generated key (NOT for Caesar Cipher) :");
		TextField keyresultTextField = new TextField();
		keyresultTextField.setEditable(false);
		Button encryptButton = new Button("Encrypt");
		Button decryptButton = new Button("Decrypt");
		ColorPicker cpicker = new ColorPicker();
		Button saveButton = new Button("Save Theme Color");
		Button loadButton = new Button("Load last saved Theme Color");

		cpicker.setOnAction(e -> {
			Color value = cpicker.getValue();
			colorString = value.getRed() + "," + value.getGreen() + "," + value.getBlue();
			hb.setBackground(new Background(new BackgroundFill(value, null, null)));// Execute a query
		});

		String colorName = "bgColor";
		saveButton.setOnAction(e -> {
			saveSetting(colorString, colorName);
		});

		loadButton.setOnAction(e -> {
			loadSetting(colorName);
		});

		ccMenuItem.setOnAction(e -> {
			menuButton.setText("CaesarCiper");
			encryptButton.setOnAction(ev -> {
				String textInput = textField.getText();
				int key = Integer.parseInt(keyField.getText());

				String encryptedText = CaesarCipher.encrypt(textInput, key);
				resultTextField.setText(encryptedText);
				ccsql.insertCCData(key, encryptedText);
				isEncrypted = true;// 这个时候已完成加密

			});

			decryptButton.setOnAction(ev -> {
				if (isEncrypted) {
					String encryptedText = resultTextField.getText();
					int key = Integer.parseInt(keyField.getText());

					String decryptedText = CaesarCipher.decrypt(encryptedText, key);
					resultTextField.setText(decryptedText);

					isEncrypted = false;
				} else {
					resultTextField.setText("Please encrypt text first!");
				}
			});
		});

		// DES
		desMenuItem.setOnAction(e -> {
			menuButton.setText("DES");
			setMasterKey();
			encryptButton.setOnAction(ev -> {
				String textInput = textField.getText();// 输入的需要加密的文字
				try {
					byte[] encryptedText = des.encrypt(textInput);// 调用desclass里的加密方法对输入的文字进行加密 byte格式
					String enText = Base64.getEncoder().encodeToString(encryptedText);// 将已加密的文字从byte格式转为string格式
					resultTextField.setText(enText);// 打印出来

					// 加密key：将byte形式的用于加密文本的key转换成string形式
					String KeyString = Base64.getEncoder().encodeToString(des.getSecretkey().getEncoded());
					byte[] enKey = des.encrypt(KeyString);
					String encryptedKeyString = Base64.getEncoder().encodeToString(enKey);

					keyresultTextField.setText(KeyString);

					dessql.insertDESData(encryptedKeyString, enText);
					isEncrypted = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

			decryptButton.setOnAction(ev -> {
				if (isEncrypted) {
					String encryptedText = resultTextField.getText();

					try {
						byte[] decodedEncryptedText = Base64.getDecoder().decode(encryptedText);
						String decryptedText = des.decrypt(decodedEncryptedText);
						resultTextField.setText(decryptedText);
						keyresultTextField.setText(Base64.getEncoder().encodeToString(des.getSecretkey().getEncoded()));

						isEncrypted = false;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					resultTextField.setText("Please encrypt text first!");
				}
			});
		});

		// AES
		aesMenuItem.setOnAction(e -> {
			menuButton.setText("AES");
			encryptButton.setOnAction(ev -> {
				String textInput = textField.getText();

				try {
					byte[] encryptedText = aes.encrypt(textInput);
					String enText = Base64.getEncoder().encodeToString(encryptedText);
					resultTextField.setText(enText);
					String keyString = Base64.getEncoder().encodeToString(des.getSecretkey().getEncoded());
					keyresultTextField.setText(keyString);
//					aessql.intertAESData(keyString, enText);
					isEncrypted = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

			decryptButton.setOnAction(ev -> {
				if (isEncrypted) {
					String encryptedText = resultTextField.getText();

					try {
						byte[] decodedencryptedText = Base64.getDecoder().decode(encryptedText);
						String decryptedText = aes.decrypt(decodedencryptedText);
						resultTextField.setText(decryptedText);
						keyresultTextField.setText(Base64.getEncoder().encodeToString(aes.getSecretkey().getEncoded()));

						isEncrypted = false;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					resultTextField.setText("Please encrypt text first!");
				}
			});
		});

		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(20, 20, 0, 20));
		hb.setSpacing(10); // Add spacing between nodes
		hb.getChildren().addAll(cpicker, saveButton, loadButton, menuButton, textLabel, textField, keyLabel, keyField,
				encryptButton, decryptButton, resultLabel, resultTextField, keyResultLabel, keyresultTextField);
		Scene scene = new Scene(hb, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void saveSetting(String colorString, String colorName) {
		if (connection != null) {
			System.out.println("Connection is not null");
		} else {
			System.out.println("Connection is null");
			return; // 如果连接为空，直接返回，避免后续代码执行
		}

		System.out.println("Creating statement...");
		try {
			String sql = "UPDATE colorData SET color_value = ? WHERE color_name = ?";
			PreparedStatement statement = connection.prepareStatement(sql);

			statement.setString(1, colorString);
			statement.setString(2, colorName);

			int sqlUpdated = statement.executeUpdate();
			if (sqlUpdated > 0) {
				System.out.println("Color updated");
			} else {
				System.out.println("Update failed");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void loadSetting(String colorName) {
		if (connection != null) {
			System.out.println("Connection is not null");
		} else {
			System.out.println("Connection is null");
		}

		System.out.println("Creating statement...");
		try {
			String sql = "SELECT color_value FROM colorData WHERE color_name = ?";
			PreparedStatement statement = connection.prepareStatement(sql);

			statement.setString(1, colorName);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				String strColor = resultSet.getString("color_value");
				String[] colors = strColor.split(",");
				Color c = new Color(Double.parseDouble(colors[0]), Double.parseDouble(colors[1]),
						Double.parseDouble(colors[2]), 1);
				hb.setBackground(new Background(new BackgroundFill(c, null, null)));

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void setMasterKey() {
		byte[] masterKeyBytes = "jyyx1998".getBytes();
		SecretKey secretKey = new SecretKeySpec(masterKeyBytes, "DES");
		des.setSecretkey(secretKey); // 将固定密钥设为 DES 密钥
	}

	static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/security";
	static final String USERNAME = "root";
	static final String PASSWORD = ""; // null password

	public static void main(String[] args) {
		launch(args);

	}

	public Main() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
