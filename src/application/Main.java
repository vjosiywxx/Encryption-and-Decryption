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
	String colorString;
	static final String masterKey = "NbWntnB1";
	static final String masterKeyAes = "NbWntnB1NbWntnB1";
	VBox hb;
	private CaesarCipher CaesarCipher = new CaesarCipher();
	private DES des;// 在用户选择的des算法中加密输入的text
	private DES secretKeyDES; // 加密des中生成的key
	private AES aes; // 在用户选择的aes算法中加密输入的text
	private AES secretKeyAES; // 加密aes中生成的key
	private CCsql ccsql;
	private DESsql dessql;
	private AESsql aessql;
	private Loginsql loginsql;
	private Connection connection;
	private Statement statement;
	SecretKey keyDES;
	SecretKey keyAES;

	@Override
	public void start(Stage primaryStage) throws NoSuchAlgorithmException {

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

			choosePage(primaryStage);
//			} else {
//				loginStatus.setText("Something goes wrong, please try again!");
//			}
		});

		loginVBox.getChildren().addAll(usernameLabel, usernameField, pswLabel, passwordField, loginButton, loginStatus);
		Scene loginScene = new Scene(loginVBox, 500, 600);
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}

	public void choosePage(Stage primaryStage) {

		if (!isLoggedIn) {
			return;
		}

		primaryStage.setTitle("Selection Page");
		VBox chooseBox = new VBox();
		chooseBox.setAlignment(Pos.CENTER);
		chooseBox.setSpacing(10);
		chooseBox.setPadding(new Insets(20));
		Button selectEncButton = new Button("Encryption");
		selectEncButton.setOnAction(ev -> {
			enPage(primaryStage);
		});
		Button selectDecButton = new Button("Decryption");
		selectDecButton.setOnAction(ev -> {
			dePage(primaryStage);
		});
		chooseBox.getChildren().addAll(selectEncButton, selectDecButton);
		Scene chooseScene = new Scene(chooseBox, 500, 600);
		primaryStage.setScene(chooseScene);
		primaryStage.show();
	}

	private void enPage(Stage primaryStage) {

		primaryStage.setTitle("Encryption Page");
		hb = new VBox();

		MenuButton menuButton = new MenuButton("Choose Algorithm");
		MenuItem ccMenuItem = new MenuItem("CaesarCiper");
		MenuItem aesMenuItem = new MenuItem("AES");
		MenuItem desMenuItem = new MenuItem("DES");
		menuButton.getItems().addAll(ccMenuItem, aesMenuItem, desMenuItem);

		Label textLabel = new Label("Enter text:");
		TextField textField = new TextField();

		Label keyLabel = new Label("Key (for Caesar Cipher ONLY) :");
		TextField keyField = new TextField();

		Label entextLabel = new Label("Encrypted text:");
		TextField entextField = new TextField();
		entextField.setEditable(false);

		Label enKeyLabel = new Label(" Encrypted key (For AES and DES) :");
		Label enReminderKeyLabel = new Label(
				" PLEASE NOTE: This key is the only credential to retrieve\n the original text, please keep it safe. ");
		TextField enKeyTextField = new TextField();
		enKeyTextField.setEditable(false);
		Button encryptButton = new Button("Encrypt");

		ColorPicker cpicker = new ColorPicker();
		Button saveButton = new Button("Save Theme Color");
		Button loadButton = new Button("Load last saved Theme Color");
		Button backToSPageButton = new Button("Go back");

		backToSPageButton.setOnAction(e -> {
			choosePage(primaryStage);
		});

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
				entextField.setText(encryptedText);

				ccsql.insertCCData(key, encryptedText);

			});

		});

		// DES
		desMenuItem.setOnAction(e -> {
			menuButton.setText("DES");
			encryptButton.setOnAction(ev -> {
				String textInput = textField.getText();
				try {
					byte[] encryptedText = des.encrypt(textInput);
					String enText = Base64.getEncoder().encodeToString(encryptedText);
					entextField.setText(enText);// text已加密

					String KeyString = Base64.getEncoder().encodeToString(des.getSecretkey().getEncoded());
//					System.out.println("originalkey:" + KeyString);//未加密的generated的key
					byte[] enKey = secretKeyDES.encrypt(KeyString);// 用secretkeydes的方法把原始的key加密了
					String encryptedKeyString = Base64.getEncoder().encodeToString(enKey);
					enKeyTextField.setText(encryptedKeyString);// key已加密

					dessql.insertDESData(encryptedKeyString, enText);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

		});

		// AES
		aesMenuItem.setOnAction(e -> {
			menuButton.setText("AES");
			encryptButton.setOnAction(ev -> {
				String textInput = textField.getText();
//				DESEncrypt(masterKey, textInput);
				try {
					byte[] encryptedText = aes.encrypt(textInput);// 进行加密
					String enText = Base64.getEncoder().encodeToString(encryptedText);
					entextField.setText(enText);// text已加密

					String KeyString = Base64.getEncoder().encodeToString(aes.getSecretkey().getEncoded());
//					System.out.println(KeyString);

					byte[] enKey = secretKeyAES.encrypt(KeyString);// secretkeyaes的方法把原始的key加密了
					String encryptedKeyString = Base64.getEncoder().encodeToString(enKey);
					enKeyTextField.setText(encryptedKeyString);

					AESsql aessql = new AESsql();
					aessql.insertAESData(encryptedKeyString, enText);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

		});

		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(20, 20, 0, 20));
		hb.setSpacing(10); // Add spacing between nodes
		hb.getChildren().addAll(cpicker, saveButton, loadButton, menuButton, textLabel, textField, keyLabel, keyField,
				encryptButton, entextLabel, entextField, enKeyLabel, enKeyTextField, enReminderKeyLabel,
				backToSPageButton);
		Scene eScene = new Scene(hb, 500, 600);
		primaryStage.setScene(eScene);
		primaryStage.show();
	}

	private void dePage(Stage primaryStage) {

		primaryStage.setTitle("Decryption Page");
		VBox dBox = new VBox();

		MenuButton menuButton = new MenuButton("Choose Algorithm");
		MenuItem ccMenuItem = new MenuItem("CaesarCiper");
		MenuItem aesMenuItem = new MenuItem("AES");
		MenuItem desMenuItem = new MenuItem("DES");
		menuButton.getItems().addAll(ccMenuItem, aesMenuItem, desMenuItem);

		Label keyLabel = new Label("Key:");
		TextField keyField = new TextField();
		Label ccentextLabel = new Label("Encrypted Text (Only for Caesar Cipher):");
		TextField ccentextField = new TextField();
		Label resultLabel = new Label("Result decrpted - text:");
		TextField decTextField = new TextField();
		decTextField.setEditable(false);
		Button decryptButton = new Button("Decrypt");
		Button backToSPageButton = new Button("Go back");
		backToSPageButton.setOnAction(e -> {
			choosePage(primaryStage);
		});

		ccMenuItem.setOnAction(e -> {
			menuButton.setText("CaesarCiper");
			decryptButton.setOnAction(ev -> {

				String encryptedText = ccentextField.getText();
				int key = Integer.parseInt(keyField.getText());

				String decryptedText = CaesarCipher.decrypt(encryptedText, key);
				decTextField.setText(decryptedText);

			});
		});

		// DES
		desMenuItem.setOnAction(e -> {
			menuButton.setText("DES");
			decryptButton.setOnAction(ev -> {

				try {
					String enKey = keyField.getText();
					Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
					String sql = "SELECT * FROM desEncryptedData WHERE desEncryptedKey = ?";
					PreparedStatement statement = connection.prepareStatement(sql);
					statement.setString(1, enKey);
					ResultSet resultSet = statement.executeQuery();

					if (resultSet.next()) {
						String enKeyStringDB = resultSet.getString("desEncryptedKey");
						String enTextDB = resultSet.getString("desEncryptedText");

						byte[] enKeyBytesDB = Base64.getDecoder().decode(enKeyStringDB);
						String decryptedkeyString = secretKeyDES.decrypt(enKeyBytesDB);

						byte[] enTextBytesDB = Base64.getDecoder().decode(enTextDB);
						String decryptedTextString = des.decrypt(enTextBytesDB);

						decTextField.setText(decryptedTextString);
					}
					resultSet.close();
					statement.close();
					connection.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			});
		});

		// AES
		aesMenuItem.setOnAction(e -> {
			menuButton.setText("AES");

			decryptButton.setOnAction(ev -> {

				try {
					String enKey = keyField.getText();
					Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
					String sql = "SELECT * FROM aesEncryptedData WHERE aesEncryptedKey = ?";
					PreparedStatement statement = connection.prepareStatement(sql);
					statement.setString(1, enKey);
					ResultSet resultSet = statement.executeQuery();

					if (resultSet.next()) {
						String enKeyStringDB = resultSet.getString("aesEncryptedKey");
						String enTextDB = resultSet.getString("aesEncryptedText");

						byte[] enKeyBytesDB = Base64.getDecoder().decode(enKeyStringDB);
						String decryptedkeyString = secretKeyAES.decrypt(enKeyBytesDB);

						byte[] enTextBytesDB = Base64.getDecoder().decode(enTextDB);
						String decryptedTextString = aes.decrypt(enTextBytesDB);

						decTextField.setText(decryptedTextString);
					}
					resultSet.close();
					statement.close();
					connection.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			});
		});

		dBox.setAlignment(Pos.CENTER);
		dBox.setPadding(new Insets(20, 20, 0, 20));
		dBox.setSpacing(10); // Add spacing between nodes
		dBox.getChildren().addAll(menuButton, keyLabel, keyField, ccentextLabel, ccentextField, decryptButton,
				resultLabel, decTextField, backToSPageButton);
		Scene dScene = new Scene(dBox, 500, 600);
		primaryStage.setScene(dScene);
		primaryStage.show();
	}

	public void saveSetting(String colorString, String colorName) {
		if (connection != null) {
			System.out.println("Connecting...");
		} else {
			System.out.println("Connection is null");
			return;
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

//	public String DESEncrypt(String masterKey, String textInput) {
//
//		String result = null;// 如果没有加密的文本的话 （说明并没有开始加密）
//		try {
//			DES des = new DES();
//			if (masterKey.length() != 8) {
//				throw new IllegalArgumentException("DES密钥长度必须是8个字节");
//			}
////			byte[] keyBytes = Base64.getDecoder().decode(masterKey);// 将mk变成byte形式
//			byte[] keyBytes = masterKey.getBytes("UTF-8");
//
//			SecretKey desMasterKey = new SecretKeySpec(keyBytes, "DES");//
//			des.setSecretkey(desMasterKey);// 得到给des加密方法用的mk
//			System.out.println("The plain text: " + textInput);
//			byte[] enText = des.encrypt(textInput);
//			result = Base64.getEncoder().encodeToString(enText);// 加密文本并将文本转换成string形式
//			System.out.println(result);
//			System.out.println(enText);
//
//		} catch (Exception e) {
//			System.out.println("Error in DES: " + e);
//			e.printStackTrace();
//		}
//		return result;
//	}

	private void setDESMasterKey() {
		byte[] masterKeyBytes = "NbWntnB1".getBytes();
		keyDES = new SecretKeySpec(masterKeyBytes, "DES");
		secretKeyDES.setSecretkey(keyDES);
	}

	private void setAESMasterKey() {
		byte[] masterKeyBytes = "NbWntnB1NbWntnB1".getBytes();
		keyAES = new SecretKeySpec(masterKeyBytes, "AES");
		secretKeyAES.setSecretkey(keyAES);
	}

	static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/security";
	static final String USERNAME = "root";
	static final String PASSWORD = "";

	public static void main(String[] args) {
		launch(args);

	}

	public Main() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			secretKeyDES = new DES();
			des = new DES();
			aes = new AES();
			secretKeyAES = new AES();
			ccsql = new CCsql();
			dessql = new DESsql();
			aessql = new AESsql();
			loginsql = new Loginsql();
			setDESMasterKey();
			setAESMasterKey();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
