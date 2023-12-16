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

public class DES {
	String alphabetLowercase;
	String alphabetUppercase;
	String number;
	String symbol;
	private static DES instance;

	private SecretKey secretkey;

	// 先调用generateKey()的方法生成一个密钥 初始化字符集
	public DES() throws NoSuchAlgorithmException {
		generateKey();
		alphabetLowercase = "abcdefghijklmnopqrstuvwxyz";
		alphabetUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		number = "1234567890";
		symbol = "~!@#$%^&*()_+{}|:<>?`-=[];',./";
	}

	public static DES getInstance() throws NoSuchAlgorithmException {
		if (instance == null) {
			instance = new DES();
		}
		return instance;
	}

	// 这个方法使用 KeyGenerator 来生成一个DES密钥，并将其设置为类中的 secretkey 属性
	public void generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		this.setSecretkey(keyGen.generateKey());
	}

	public byte[] encrypt(String strDataToEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher desCipher = Cipher.getInstance("DES");
		desCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
		byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
		byte[] byteCipherText = desCipher.doFinal(byteDataToEncrypt);
		return byteCipherText;
	}

	public String decrypt(byte[] strCipherText) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher desCipher = Cipher.getInstance("DES");
		desCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
		byte[] byteDecryptedText = desCipher.doFinal(strCipherText);
		return new String(byteDecryptedText);
	}

	public SecretKey getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(SecretKey secretkey) {
		this.secretkey = secretkey;
	}
}
