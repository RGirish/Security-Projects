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

public class ECBDecryptionEasy {

	static String hex = "", hexLookUp = "0123456789abcdef",
			base64LookUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";;

	public static void main(String[] args) {
		String unknownString = "Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkgaGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBqdXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUgYnkK";
		String HEXKEY = asciiToHex(generateRandomAESKey());
		String unknownHex = base64ToHex(unknownString);

		String CT = encryptAESECB(PKCSPadTheHexString(asciiToHex("AAAAAAAAAAAAAAA") + unknownHex, 16), HEXKEY);

		StringBuilder builder = new StringBuilder();

		for (int j = 0; j < unknownHex.length() - 2; j = j + 2) {
			CT = encryptAESECB(PKCSPadTheHexString(asciiToHex("AAAAAAAAAAAAAAA") + unknownHex.substring(j, j + 2), 16),
					HEXKEY);

			String firstBlock = CT.substring(0, 15);

			for (int i = 32; i < 127; ++i) {
				String CTtemp = encryptAESECB(PKCSPadTheHexString(asciiToHex("AAAAAAAAAAAAAAA" + (char) i), 16),
						HEXKEY);
				String firstBlockTemp = CTtemp.substring(0, 15);
				if (firstBlock.equals(firstBlockTemp)) {
					builder.append((char) i);
					break;
				}
			}
		}

		System.out.println(builder.toString());

	}

	private static String base64ToHex(String base64) {
		for (int i = 0; i < base64.length(); ++i) {
			// Take first 6 bits (from the first base64 digit) and call
			// convert() with the first 4 bits.
			String firstDigit = String.format("%06d",
					Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i++)))));
			convert(firstDigit.substring(0, 4));

			// Take the remaining 2 bits (from the first base64 digit) and the
			// first 2 bits of the second base64 digit and call convert()
			String secondDigit = String.format("%06d",
					Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i)))));
			convert(firstDigit.substring(4, 6) + secondDigit.substring(0, 2));

			convert(secondDigit.substring(2, 6));
		}
		return hex;
	}

	/*
	 * 'bits' will be a 4 bit sequence which will be converted to the
	 * corresponding hex value. Each hex digit is appended to the string 'hex'.
	 */
	static void convert(String bits) {
		StringBuilder stringBuilder = new StringBuilder(hex);
		stringBuilder.append(String.valueOf(hexLookUp.charAt(Integer.parseInt(bits, 2))));
		hex = stringBuilder.toString();
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
				int diff = safeDivide(safeSubtract(safeMultiply(blockSize, 2), (s.length() % 32)), 2);
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

	/*
	 * @reference
	 * https://securecoding.cert.org/confluence/display/java/NUM00-J.+Detect+or+
	 * prevent+integer+overflow
	 * 
	 * The following are functions that perform the basic arithmetic operations
	 * of subtraction, multiplication and division according to the secure
	 * coding standards - by avoiding the Integer Overflow Exception.
	 */

	static final int safeSubtract(int left, int right) throws ArithmeticException {
		if (right > 0 ? left < Integer.MIN_VALUE + right : left > Integer.MAX_VALUE + right) {
			throw new ArithmeticException("Integer overflow");
		}
		return left - right;
	}

	static final int safeMultiply(int left, int right) throws ArithmeticException {
		if (right > 0 ? left > Integer.MAX_VALUE / right || left < Integer.MIN_VALUE / right
				: (right < -1 ? left > Integer.MIN_VALUE / right || left < Integer.MAX_VALUE / right
						: right == -1 && left == Integer.MIN_VALUE)) {
			throw new ArithmeticException("Integer overflow");
		}
		return left * right;
	}

	static final int safeDivide(int left, int right) throws ArithmeticException {
		if ((left == Integer.MIN_VALUE) && (right == -1)) {
			throw new ArithmeticException("Integer overflow");
		}
		/*
		 * NUM02-J. Ensure that division and remainder operations do not result
		 * in divide-by-zero errors
		 * 
		 * @reference
		 * https://securecoding.cert.org/confluence/display/java/NUM02-J.+Ensure
		 * +that+division+and+remainder+operations+do+not+result+in+divide-by-
		 * zero+errors
		 */
		if (right == 0) {
			throw new ArithmeticException("Divide By Zero");
		}
		return left / right;
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
