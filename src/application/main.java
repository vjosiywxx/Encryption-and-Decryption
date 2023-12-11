package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CaesarCipherGui extends Application {

	String alphabetLowercase;
	String alphabetUppercase;
	String number;
	String symbol;
//	private boolean isDecrypted = false;
	private boolean isEncrypted = false;

	@Override
	public void start(Stage primaryStage) {
		alphabetLowercase = "abcdefghijklmnopqrstuvwxyz";
		alphabetUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		number = "1234567890";
		symbol = "~!@#$%^&*()_+{}|:<>?`-=[];',./";

		primaryStage.setTitle("Encryption & Decryption");

		VBox hb = new VBox();
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

			String encryptedText = encrypt(textInput, key);
			resultTextLabel.setText(encryptedText);

			isEncrypted = true;// 这个时候已完成加密
		});

		Button decryptButton = new Button("Decrypt 解密");
		decryptButton.setOnAction(e -> {
			if (isEncrypted) {
				String encryptedText = resultTextLabel.getText();
				int key = Integer.parseInt(keyField.getText());

				String decryptedText = decrypt(encryptedText, key);
				resultTextLabel.setText(decryptedText);

				isEncrypted = false;
			} else {
				resultTextLabel.setText("Please encrypt text first!");
			}
		});

		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(50, 50, 0, 50));
		hb.setSpacing(10); // Add spacing between nodes
		hb.getChildren().addAll(textLabel, textField, keyLabel, keyField, encryptButton, decryptButton, resultLabel,
				resultTextLabel);
		Scene scene = new Scene(hb, 300, 300);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private String encrypt(String plainText, int key) {
		String cipherText = "";

		for (int i = 0; i < plainText.length(); i++) {
			char plainCharacter = plainText.charAt(i);

			if (Character.isUpperCase(plainCharacter)) {
				int position = alphabetUppercase.indexOf(plainCharacter);
				int newPosition = (position + key) % alphabetUppercase.length();
				char cipherCharacter = alphabetUppercase.charAt(newPosition);
				cipherText += cipherCharacter;

			} else if (Character.isLowerCase(plainCharacter)) {
				int position = alphabetLowercase.indexOf(plainCharacter);
				int newPosition = (position + key) % alphabetLowercase.length();
				char cipherCharacter = alphabetLowercase.charAt(newPosition);
				cipherText += cipherCharacter;

			} else if (Character.isDigit(plainCharacter)) {
				int position = number.indexOf(plainCharacter);
				int newPosition = (position + key) % number.length();
				char cipherCharacter = number.charAt(newPosition);
				cipherText += cipherCharacter;

			} else if (symbol.indexOf(plainCharacter) != -1) {
				int position = symbol.indexOf(plainCharacter);
				int newPosition = (position + key) % number.length();
				char cipherCharacter = symbol.charAt(newPosition);
				cipherText += cipherCharacter;

			} else {
				cipherText += plainCharacter;
			}
		}
		return cipherText;
	}

	private String decrypt(String cipherText, int key) {
		String plainText = "";

		for (int i = 0; i < cipherText.length(); i++) {
			char cipherCharacter = cipherText.charAt(i);

			if (Character.isUpperCase(cipherCharacter)) {
				int position = alphabetUppercase.indexOf(cipherCharacter);
				int newPosition = (position - key + alphabetUppercase.length()) % alphabetUppercase.length();
				char plainCharacter = alphabetUppercase.charAt(newPosition);
				plainText += plainCharacter;

			} else if (Character.isLowerCase(cipherCharacter)) {
				int position = alphabetLowercase.indexOf(cipherCharacter);
				int newPosition = (position - key + alphabetLowercase.length()) % alphabetLowercase.length();
				char plainCharacter = alphabetLowercase.charAt(newPosition);
				plainText += plainCharacter;

			} else if (Character.isDigit(cipherCharacter)) {
				int position = number.indexOf(cipherCharacter);
				int newPosition = (position - key + number.length()) % number.length();
				char plainCharacter = number.charAt(newPosition);
				plainText += plainCharacter;

			} else if (symbol.indexOf(cipherCharacter) != -1) {
				int position = symbol.indexOf(cipherCharacter);
				int newPosition = (position - key + number.length()) % number.length();
				char plainCharacter = symbol.charAt(newPosition);
				plainText += plainCharacter;

			} else {
				plainText += cipherCharacter;
			}
		}

		return plainText;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
