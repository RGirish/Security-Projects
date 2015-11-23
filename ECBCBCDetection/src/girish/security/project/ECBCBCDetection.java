package girish.security.project;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class ECBCBCDetection {

	public static void main(String[] args) {
		encryptionOracle("yellow submarine", generateRandomAESKey());
		encryptionOracle("girish raman0202", generateRandomAESKey());
	}

	public static String generateRandomAESKey() {
		String keyCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		SecureRandom rand = new SecureRandom();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 16; ++i) {
			builder.append(keyCharacters.charAt(rand.nextInt(keyCharacters.length())));
		}
		return builder.toString();
	}

	public static void encryptionOracle(String plainText, String key) {
		/*
		 * MET00-J. Validate method arguments
		 * 
		 * @reference
		 * https://securecoding.cert.org/confluence/display/java/MET00-J.+
		 * Validate+method+arguments
		 */
		if (plainText == null) {
			System.out.println("Plain Text cannot be NULL.");
			return;
		}
		if (key == null) {
			System.out.println("Key cannot be NULL.");
			return;
		}
		if (key.length() != 16) {
			System.out.println("Key should be 16 Bytes (hex characters) long.");
			return;
		}

		SecureRandom rand = new SecureRandom();
		int bit = rand.nextInt(2);
		if (bit == 0) {
			System.out.println(encryptAESECB(asciiToHex(plainText), asciiToHex(key)));
		} else {
			// CBC
			System.out.println(encryptAESECB(asciiToHex(plainText), asciiToHex(key)));
		}
	}

	/**
	 * This function computes the Hex equivalent of an ASCII String.
	 */
	public static String asciiToHex(String ascii) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < ascii.length(); ++i) {
			buffer.append(Integer.toHexString((int) ascii.charAt(i)));
		}
		return buffer.toString();
	}

	/**
	 * This function encrypts the given plain text in AES-ECB mode and returns
	 * the cipher text.
	 * 
	 * @param hexPlainText
	 *            The plain text in hex format
	 * @param hexKey
	 *            The random key in hex format
	 * @return AES-ECB encryption of the plain text.
	 */
	private static String encryptAESECB(String hexPlainText, String hexKey) {
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(hexKey), "AES");
		Cipher cipher;
		byte[] result = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			result = cipher.doFinal(DatatypeConverter.parseHexBinary(hexPlainText));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException " + e.getMessage());
			return null;
		} catch (NoSuchPaddingException e) {
			System.out.println("NoSuchPaddingException " + e.getMessage());
			return null;
		} catch (InvalidKeyException e) {
			System.out.println("InvalidKeyException " + e.getMessage());
			return null;
		} catch (IllegalBlockSizeException e) {
			System.out.println("IllegalBlockSizeException " + e.getMessage());
			return null;
		} catch (BadPaddingException e) {
			System.out.println("BadPaddingException " + e.getMessage());
			return null;
		}
		return new String(result);
	}
}