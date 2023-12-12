package application;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class main extends Application {

	private boolean isEncrypted = false;

	private CaesarCipher CaesarCipher;
	private DES encryptDES;
	private DES SecretKey;

	@Override
	public void start(Stage primaryStage) throws NoSuchAlgorithmException {
		CaesarCipher = new CaesarCipher();
		encryptDES = new DES();
		SecretKey = new DES();

		primaryStage.setTitle("Encryption & Decryption");
		VBox hb = new VBox();

		MenuButton menuButton = new MenuButton("Choose Algorithmn");
		MenuItem ccMenuItem = new MenuItem("CaesarCiper");
		MenuItem aesMenuItem = new MenuItem("AES");
		MenuItem desMenuItem = new MenuItem("DES");
		menuButton.getItems().addAll(ccMenuItem, aesMenuItem, desMenuItem);

		Label textLabel = new Label("Enter text:");
		TextField textField = new TextField();
		Label keyLabel = new Label("Key:");
		TextField keyField = new TextField();
		Label resultLabel = new Label("Result:");
		TextField resultTextField = new TextField();
		resultTextField.setEditable(false);
		Label keyResultLabel = new Label("(DES ONLY) Generated key:");
		TextField keyresultTextField = new TextField();
		keyresultTextField.setEditable(false);
		Button encryptButton = new Button("Encrypt 加密");
		Button decryptButton = new Button("Decrypt 解密");

		ccMenuItem.setOnAction(e -> {
			menuButton.setText("CaesarCiper");
			encryptButton.setOnAction(ev -> {
				String textInput = textField.getText();
				int key = Integer.parseInt(keyField.getText());

				String encryptedText = CaesarCipher.encrypt(textInput, key);
				resultTextField.setText(encryptedText);

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
			encryptButton.setOnAction(ev -> {
				String textInput = textField.getText();

				try {
					byte[] encryptedText = encryptDES.encrypt(textInput);
					resultTextField.setText(Base64.getEncoder().encodeToString(encryptedText));
					keyresultTextField.setText(
							"key:" + Base64.getEncoder().encodeToString(encryptDES.getSecretkey().getEncoded()));

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
						String decryptedText = encryptDES.decrypt(decodedencryptedText);
						resultTextField.setText(decryptedText);
						keyresultTextField.setText(
								"key:" + Base64.getEncoder().encodeToString(encryptDES.getSecretkey().getEncoded()));

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
		hb.getChildren().addAll(menuButton, textLabel, textField, keyLabel, keyField, encryptButton, decryptButton,
				resultLabel, resultTextField, keyResultLabel, keyresultTextField);
		Scene scene = new Scene(hb, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
