package application;

public class CaesarCipher {

	private String alphabetLowercase;
	private String alphabetUppercase;
	private String number;
	private String symbol;

	public CaesarCipher() {
		alphabetLowercase = "abcdefghijklmnopqrstuvwxyz";
		alphabetUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		number = "1234567890";
		symbol = "~!@#$%^&*()_+{}|:<>?`-=[];',./";
	}

	public String encrypt(String plainText, int key) {
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
				int newPosition = (position + key) % symbol.length();
				char cipherCharacter = symbol.charAt(newPosition);
				cipherText += cipherCharacter;

			} else {
				cipherText += plainCharacter;
			}
		}
		return cipherText;
	}

	public String decrypt(String cipherText, int key) {
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
				int newPosition = (position - key + symbol.length()) % symbol.length();
				char plainCharacter = symbol.charAt(newPosition);
				plainText += plainCharacter;

			} else {
				plainText += cipherCharacter;
			}
		}

		return plainText;
	}
}
