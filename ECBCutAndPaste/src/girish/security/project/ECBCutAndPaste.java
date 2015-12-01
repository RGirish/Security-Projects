package girish.security.project;

import java.io.ObjectOutputStream;
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

public class ECBCutAndPaste {

	public static void main(String[] args) {
		String pkcsPaddedHexPlainText = PKCSPadTheHexString(asciiToHex(userProfileFor("girish.raman@asu.edu")), 16);
		String KEY = generateRandomAESKey();
		byte[] ctBytes = encryptAESECB(pkcsPaddedHexPlainText, asciiToHex(KEY));

		pkcsPaddedHexPlainText = PKCSPadTheHexString(asciiToHex(adminProfileFor("girish.raman@asu.edu")), 16);
		byte[] ctBytesAdmin = encryptAESECB(pkcsPaddedHexPlainText, asciiToHex(KEY));
		for (int i = 32; i < 48; ++i) {
			ctBytes[i] = ctBytesAdmin[i];
		}

		String PT = decryptAESECB(ctBytes, asciiToHex(KEY));
		int pad = (int) PT.charAt(PT.length() - 1);
		System.out.println(kvParseRoutine(PT.substring(0, PT.length() - pad)));
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
	 * 
	 * @param hexString
	 *            The input String to be padded
	 * @param blockSize
	 *            The size of each block
	 * @return The input string padded with characters according to PKCS #7
	 */
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
			/*
			 * NUM00-J. Detect or prevent integer overflow
			 * 
			 * @reference
			 * https://securecoding.cert.org/confluence/display/java/NUM00-J.+
			 * Detect+or+prevent+integer+overflow
			 */
			try {
				int diff = (blockSize * 2 - (s.length() % 32)) / 2;
				for (int i = 0; i < diff; i++) {
					s.append("0");
					s.append(Integer.toHexString((diff)));
				}
			} catch (ArithmeticException e) {
				System.out.println("ArithmeticException thrown by safeArithmetic function - " + e.getMessage());
				return null;
			}
		}
		return s.toString();
	}

	/**
	 * This function randomly generates a 16 Byte key to be used with AES
	 * 
	 * @return The randomly generated key
	 */
	public static String generateRandomAESKey() {
		String keyCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		SecureRandom rand = new SecureRandom();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 16; ++i) {
			builder.append(keyCharacters.charAt(rand.nextInt(keyCharacters.length())));
		}
		return builder.toString();
	}

	/**
	 * This function encrypts the given plain text in AES-ECB mode and returns
	 * the cipher text.
	 * 
	 * @param hexPlainText
	 *            The plain text in hex format
	 * @param hexKey
	 *            The random key in hex format
	 * @return AES-ECB encryption of the plain text in bytes.
	 */
	private static byte[] encryptAESECB(String hexPlainText, String hexKey) {
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(hexKey), "AES");
		Cipher cipher;
		byte[] result = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/NOPADDING");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			result = cipher.doFinal(DatatypeConverter.parseHexBinary(hexPlainText));
		}
		/*
		 * ERR01-J. Do not allow exceptions to expose sensitive information
		 * 
		 * @reference
		 * https://www.securecoding.cert.org/confluence/display/java/ERR01-J.+Do
		 * +not+allow+exceptions+to+expose+sensitive+information
		 * 
		 * Instead of giving out specifics about the exact Exception that has
		 * occurred, we're giving out a general error message so that this does
		 * not help the adversary gain any advantage.
		 */
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			System.out.println("An error occured! Please try again.");
		}
		return result;
	}

	/**
	 * This function decrypts the given cipher text in AES-ECB mode and returns
	 * the plain text.
	 * 
	 * @param ctBytes
	 *            The cipher text bytes.
	 * @param hexKey
	 *            The random key in hex format
	 * @return AES-ECB decryption of the cipher.
	 */
	private static String decryptAESECB(byte[] ctBytes, String hexKey) {
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(hexKey), "AES");
		Cipher cipher;
		byte[] result = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/NOPADDING");
			cipher.init(Cipher.DECRYPT_MODE, key);
			result = cipher.doFinal(ctBytes);
		} catch (Exception e) {
			System.out.println("Exception!" + e.getMessage());
		}
		return new String(result);
	}

	private static String userProfileFor(String email) {
		email = email.replaceAll("&", "AND");
		email = email.replaceAll("=", "EQ");
		return "email=" + email + "&uid=10&role=user";
	}

	private static String adminProfileFor(String email) {
		email = email.replaceAll("&", "AND");
		email = email.replaceAll("=", "EQ");
		return "email=" + email + "&uid=10&role=admin";
	}

	private static String kvParseRoutine(String s) {
		StringBuilder builder = new StringBuilder();
		builder.append("{\n\t");
		for (int i = 0; i < s.length(); ++i) {
			if (s.charAt(i) != '=') {
				builder.append(s.charAt(i));
				continue;
			} else {
				builder.append(": '");
				i++;
				for (; i < s.length(); ++i) {
					if (s.charAt(i) != '&') {
						builder.append(s.charAt(i));
						if (i + 1 == s.length()) {
							builder.append("'\n}");
						}
						continue;
					} else {
						builder.append("',\n\t");
						break;
					}
				}
			}
		}
		return builder.toString();
	}

	/*
	 * Cloning is disabled for security reasons. (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public final Object clone() throws java.lang.CloneNotSupportedException {
		throw new java.lang.CloneNotSupportedException();
	}

	/*
	 * Object Serialization is disabled for security reasons.
	 */
	private final void writeObject(ObjectOutputStream out) throws java.io.IOException {
		throw new java.io.IOException("Object cannot be serialized");
	}
}