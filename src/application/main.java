package application;

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

	@Override
	public void start(Stage primaryStage) {

		CaesarCipher = new CaesarCipher();
		primaryStage.setTitle("Encryption & Decryption");
		VBox hb = new VBox();

		MenuButton menuButton = new MenuButton("Menu");
		MenuItem ccMenuItem = new MenuItem("CaesarCiper");
		MenuItem aesMenuItem = new MenuItem("AES");
		MenuItem desMenuItem = new MenuItem("DES");
		menuButton.getItems().addAll(ccMenuItem, aesMenuItem, desMenuItem);

		Label textLabel = new Label("Enter text:");
		TextField textField = new TextField();
		Label keyLabel = new Label("Key:");
		TextField keyField = new TextField();
		Label resultLabel = new Label("Result:");
		Label resultTextLabel = new Label();

		Button encryptButton = new Button("Encrypt 加密");
		encryptButton.setOnAction(e -> {
			String textInput = textField.getText();
			int key = Integer.parseInt(keyField.getText());

			String encryptedText = CaesarCipher.encrypt(textInput, key);
			resultTextLabel.setText(encryptedText);

			isEncrypted = true;// 这个时候已完成加密
		});

		Button decryptButton = new Button("Decrypt 解密");
		decryptButton.setOnAction(e -> {
			if (isEncrypted) {
				String encryptedText = resultTextLabel.getText();
				int key = Integer.parseInt(keyField.getText());

				String decryptedText = CaesarCipher.decrypt(encryptedText, key);
				resultTextLabel.setText(decryptedText);

				isEncrypted = false;
			} else {
				resultTextLabel.setText("Please encrypt text first!");
			}
		});

		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(50, 50, 0, 50));
		hb.setSpacing(10); // Add spacing between nodes
		hb.getChildren().addAll(menuButton, textLabel, textField, keyLabel, keyField, encryptButton, decryptButton,
				resultLabel, resultTextLabel);
		Scene scene = new Scene(hb, 300, 300);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
