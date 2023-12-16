package application;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {
	private boolean isLoggedIn = false;
	String colorString;
	static final String masterKey = "NbWntnB1";
	static final String masterKeyAes = "NbWntnB1NbWntnB1";
	VBox vb;
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
		vb = new VBox();

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
		Button saveButton = new Button("Save Theme");
		Button loadButton = new Button("Load last saved Theme");

		Label fileLabel = new Label("File name:");
		TextField fileField = new TextField();
		fileField.setPromptText("e.g: Enkey.txt");
		Button saveKeytoFileButton = new Button("Save Key to File");
		Button backToSPageButton = new Button("Go back");

		HBox colorBox = new HBox();
		colorBox.getChildren().addAll(cpicker, saveButton, loadButton);
		colorBox.setAlignment(Pos.CENTER);
		colorBox.setPadding(new Insets(20, 20, 0, 20));
		colorBox.setSpacing(10);

		HBox fileBox = new HBox();
		fileBox.getChildren().addAll(fileLabel, fileField);
		fileBox.setAlignment(Pos.CENTER);
		fileBox.setPadding(new Insets(20, 20, 0, 20));
		fileBox.setSpacing(10);

		backToSPageButton.setOnAction(e -> {
			choosePage(primaryStage);
		});

		cpicker.setOnAction(e -> {
			Color value = cpicker.getValue();
			colorString = value.getRed() + "," + value.getGreen() + "," + value.getBlue();
			vb.setBackground(new Background(new BackgroundFill(value, null, null)));// Execute a query
		});

		String colorName = "bgColor";
		saveButton.setOnAction(e -> {
			saveSetting(colorString, colorName);
		});

		loadButton.setOnAction(e -> {
			loadSetting(colorName);
		});

		saveKeytoFileButton.setOnAction(e -> {
			String encryptedKey = enKeyTextField.getText();
			String fileName = fileField.getText();

			saveEncryptedKeyToFile(encryptedKey, fileName);
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
					entextField.setText(enText);

					String KeyString = Base64.getEncoder().encodeToString(des.getSecretkey().getEncoded());
					byte[] enKey = secretKeyDES.encrypt(KeyString);//
					String encryptedKeyString = Base64.getEncoder().encodeToString(enKey);
					enKeyTextField.setText(encryptedKeyString);
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
				try {
					byte[] encryptedText = aes.encrypt(textInput);// 进行加密
					String enText = Base64.getEncoder().encodeToString(encryptedText);
					entextField.setText(enText);// text已加密

					String KeyString = Base64.getEncoder().encodeToString(aes.getSecretkey().getEncoded());

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

		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(20, 20, 0, 20));
		vb.setSpacing(10);
		vb.getChildren().addAll(colorBox, menuButton, textLabel, textField, keyLabel, keyField, encryptButton,
				entextLabel, entextField, enKeyLabel, enKeyTextField, enReminderKeyLabel, fileBox, saveKeytoFileButton,
				backToSPageButton);
		Scene eScene = new Scene(vb, 500, 600);
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
		Label resultLabel = new Label("Result - Decrypted Text:");
		TextField decTextField = new TextField();
		decTextField.setEditable(false);
		Button decryptButton = new Button("Decrypt");
		Button backToSPageButton = new Button("Go back");
		backToSPageButton.setOnAction(e -> {
			choosePage(primaryStage);
		});
		Button loadKeyFromFileButton = new Button("Load Key From File");
		loadKeyFromFileButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Encrypted Key File");
			File selectedFile = fileChooser.showOpenDialog(primaryStage);

			if (selectedFile != null) {
				String filePath = selectedFile.getAbsolutePath();
				loadEncryptedKeyFromFile(filePath, keyField);
			}
		});

		// CC
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
					String sql = "SELECT * FROM desEncryptedData WHERE desEncryptedKey = ? ";
					PreparedStatement statement = connection.prepareStatement(sql);
					statement.setString(1, enKey);
					ResultSet resultSet = statement.executeQuery();

					if (resultSet.next()) {
						String enKeyStringDB = resultSet.getString("desEncryptedKey");
						String enTextDB = resultSet.getString("desEncryptedText");

						byte[] enKeyBytesDB = Base64.getDecoder().decode(enKeyStringDB);
						String decryptedKeyString = secretKeyDES.decrypt(enKeyBytesDB);
						byte[] descrptedKey = Base64.getDecoder().decode(decryptedKeyString);
						SecretKey secretKey = new SecretKeySpec(descrptedKey, 0, descrptedKey.length, "DES");
						des.setSecretkey(secretKey);

						byte[] inputenKeyBytes = Base64.getDecoder().decode(enKey);
						String decInputenKeyBytes = secretKeyDES.decrypt(inputenKeyBytes);

						if (decryptedKeyString.equals(decInputenKeyBytes)) {
							byte[] enTextBytesDB = Base64.getDecoder().decode(enTextDB);
							String decryptedTextString = des.decrypt(enTextBytesDB);

							decTextField.setText(decryptedTextString);
						} else {
							decTextField.setText("Invalid Key，please try again!");
						}
					} else {
						decTextField.setText("Key not found!");
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
						String decryptedKeyString = secretKeyAES.decrypt(enKeyBytesDB);
						byte[] descrptedKey = Base64.getDecoder().decode(decryptedKeyString);
						SecretKey secretKey = new SecretKeySpec(descrptedKey, 0, descrptedKey.length, "AES");
						aes.setSecretkey(secretKey);

						byte[] inputenKeyBytes = Base64.getDecoder().decode(enKey);
						String decInputenKeyBytes = secretKeyAES.decrypt(inputenKeyBytes);

						if (decryptedKeyString.equals(decInputenKeyBytes)) {
							byte[] enTextBytesDB = Base64.getDecoder().decode(enTextDB);
							String decryptedTextString = aes.decrypt(enTextBytesDB);

							decTextField.setText(decryptedTextString);
						} else {
							decTextField.setText("Invalid Key，please try again!");
						}
					} else {
						decTextField.setText("Key not found!");
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
		dBox.getChildren().addAll(menuButton, keyLabel, loadKeyFromFileButton, keyField, ccentextLabel, ccentextField,
				decryptButton, resultLabel, decTextField, backToSPageButton);
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
				vb.setBackground(new Background(new BackgroundFill(c, null, null)));

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

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

	public void saveEncryptedKeyToFile(String encryptedKey, String fileName) {
		try {
			byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKey);
			Path path = Paths.get(fileName);
			Files.write(path, encryptedKeyBytes);
			System.out.println("Encrypted key saved to the file" + fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to save encrypted key to file: " + fileName);
		}
	}

	public void loadEncryptedKeyFromFile(String fileName, TextField keyField) {
		try {
			Path path = Paths.get(fileName);
			byte[] encryptedKeybytes = Files.readAllBytes(path);
			String encryptedkey = Base64.getEncoder().encodeToString(encryptedKeybytes);
			keyField.setText(encryptedkey);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to load encrypted key from file: " + fileName);
		}

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
