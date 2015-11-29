package girish.security.project;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class CBCKEYisIV {

	public static void main(String[] args) {
		String asciiKey = generateRandomAESKey();
		byte[] iv = asciiKey.getBytes();

		String processedString = "1234567890123456yellow submarineuserdatauserdata";
		byte[] cipherByes = padAndEncryptTheString(processedString, asciiToHex(asciiKey), iv);

		for (int i = 0; i < 16; ++i) {
			cipherByes[32 + i] = cipherByes[i];
			cipherByes[16 + i] = 0x00;
		}

		byte[] messageBytes = findIfCipherIsAllAscii(cipherByes, asciiToHex(asciiKey), iv);
		String message = new String(messageBytes);

		if (message.equals("yes")) {
			System.out.println("Message received!");
		} else {
			System.out.println("Hey alice, Bob here. What is this that you sent - " + message);
			byte[] extractedKeyBytes = new byte[16];
			for (int i = 0; i < 16; ++i) {
				extractedKeyBytes[i] = (byte) (messageBytes[i] ^ messageBytes[i+32]);
			}
			System.out.println("Attacker: Found the key - " + new String(extractedKeyBytes));
		}
	}

	private static byte[] findIfCipherIsAllAscii(byte[] cipherByes, String hexKey, byte[] iv) {
		byte[] plainTextBytes = decryptAESCBC(cipherByes, hexKey, iv);
		for (byte b : plainTextBytes) {
			if (b < 0x20 || b > 0x7e) {
				return plainTextBytes;
			}
		}
		return "yes".getBytes();
	}

	private static byte[] padAndEncryptTheString(String string, String hexKey, byte[] iv) {
		String paddedString = PKCSPadTheHexString(asciiToHex(string), 16);
		return encryptAESCBC(paddedString, hexKey, iv);
	}

	private static byte[] encryptAESCBC(String hexPlainText, String hexKey, byte[] iv) {
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(hexKey), "AES");
		Cipher cipher;
		byte[] result = null;
		try {
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
			result = cipher.doFinal(DatatypeConverter.parseHexBinary(PKCSPadTheHexString(hexPlainText, 16)));
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
		}
		return result;
	}

	private static byte[] decryptAESCBC(byte[] cipherBytes, String hexKey, byte[] iv) {
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(hexKey), "AES");
		Cipher cipher;
		byte[] result = null;
		try {

			cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
			result = cipher.doFinal(cipherBytes);
		} catch (Exception e) {
			System.out.println("Exception in decryption! " + e.getMessage());
		}
		return result;
	}

	private static String prependAndAppend(String s) {
		s = s.replaceAll(";", "SC");
		s = s.replaceAll("=", "EQ");
		String finalString = "comment1=cooking%20MCs;userdata=";
		finalString = finalString + s;
		finalString = finalString + ";comment2=%20like%20a%20pound%20of%20bacon";
		return finalString;
	}

	public static String asciiToHex(String ascii) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < ascii.length(); ++i) {
			buffer.append(Integer.toHexString((int) ascii.charAt(i)));
		}
		return buffer.toString();
	}

	private static String PKCSPadTheHexString(String hexString, int blockSize) {
		StringBuilder s = new StringBuilder(hexString);
		if (s.length() % 32 != 0) {
			int diff = (((blockSize * 2) - (s.length() % 32)) / 2);
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

}