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
	private String alphabetLowercase;
	private String alphabetUppercase;
	private String number;
	private String symbol;

	private SecretKey secretkey;

	// 先调用generateKey()的方法生成一个密钥 初始化字符集
	public AES() throws NoSuchAlgorithmException {
		generateKey();
		alphabetLowercase = "abcdefghijklmnopqrstuvwxyz";
		alphabetUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		number = "1234567890";
		symbol = "~!@#$%^&*()_+{}|:<>?`-=[];',./";
	}

	// 这个方法使用 KeyGenerator 来生成一个DES密钥，并将其设置为类中的 secretkey 属性
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