package application;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AES {
	String alphabetLowercase;
	String alphabetUppercase;
	String number;
	String symbol;

	private SecretKey secretkey;

	public AES() throws NoSuchAlgorithmException {
		generateKey();
		alphabetLowercase = "abcdefghijklmnopqrstuvwxyz";
		alphabetUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		number = "1234567890";
		symbol = "~!@#$%^&*()_+{}|:<>?`-=[];',./";
	}

	public void generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		this.setSecretkey(keyGen.generateKey());
	}

	public byte[] encrypt(String strDataToEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
		byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
		byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
		return byteCipherText;

	}

	public String decrypt(byte[] strCipherText) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
		byte[] byteDecryptedText = aesCipher.doFinal(strCipherText);
		return new String(byteDecryptedText);

	}

	public SecretKey getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(SecretKey secretkey) {
		this.secretkey = secretkey;
	}
}