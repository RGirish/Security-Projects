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
		encryptionOracle("Girish", generateRandomAESKey());
	}

	private static String PKCSPadTheHexString(String hexString, int blockSize) {
		StringBuilder s = new StringBuilder(hexString);
		/*
		 * EXP52-J. Use braces for the body of an if, for, or while statement
		 * 
		 * @reference https://www.securecoding.cert.org/confluence/display/java/
		 * EXP52-J.+Use+braces+for+the+body+of+an+if%2C+for%2C+or+while+
		 * statement A for loop with one statement inside is enclosed within
		 * braces
		 */
		if (s.length() % 32 == 0) {
			// No Padding
		} else {
			int diff = ((blockSize * 2) - (s.length() % 32)) / 2;
			for (int i = 0; i < diff; i++) {
				s.append("0");
				s.append(Integer.toHexString((diff)));
			}
		}
		return s.toString();
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
		System.out.println("Hex Plain Text : " + asciiToHex(plainText));
		System.out.println("Hex Key is : " + asciiToHex(key));
		plainText = appendRandomPrefixAndSuffixToPlainText(asciiToHex(plainText));
		System.out.println("PT after random padding is : " + plainText);
		System.out.println("PT after PKCS padding is : " + PKCSPadTheHexString(plainText, 16));
//		System.exit(0);
		SecureRandom rand = new SecureRandom();
		int bit = rand.nextInt(2);
		if (bit == 0) {
			System.out.println("***Encrypting in ECB Mode***");
			System.out.println(encryptAESECB(PKCSPadTheHexString(plainText, 16), asciiToHex(key)));
		} else {
			System.out.println("***Encrypting in CBC Mode***");
			System.out.println(encryptAESCBC(PKCSPadTheHexString(plainText, 16), asciiToHex(key)));
		}

	}

	/**
	 * This function prefixes and suffixes 5-10 bytes to the plain text string.
	 * 
	 * @param plainText
	 *            The plain text string in hex format
	 * @return The plain text string with 5-10 bytes before and after
	 */
	private static String appendRandomPrefixAndSuffixToPlainText(String hexPlainText) {
		SecureRandom rand = new SecureRandom();
		int N = rand.nextInt(6) + 5;
		StringBuilder builder = new StringBuilder();
		String hexCharacters = "0123456789abcdef";
		for (int i = 0; i < (N * 2); ++i) {
			builder.append(hexCharacters.charAt(rand.nextInt(hexCharacters.length())));
		}
		builder.append(hexPlainText);
		N = rand.nextInt(6) + 5;
		for (int i = 0; i < (N * 2); ++i) {
			builder.append(hexCharacters.charAt(rand.nextInt(hexCharacters.length())));
		}
		return builder.toString();
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

	/**
	 * This function encrypts the given plain text in AES-CBC mode and returns
	 * the cipher text.
	 * 
	 * @param hexPlainText
	 *            The plain text in hex format
	 * @param hexKey
	 *            The random key in hex format
	 * @return AES-CBC encryption of the plain text.
	 */
	private static String encryptAESCBC(String hexPlainText, String hexKey) {
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(hexKey), "AES");
		Cipher cipher;
		byte[] result = null;
		try {
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
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