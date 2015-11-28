package girish.security.project;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class CBCBitFlipping {

	public static void main(String[] args) {
		String asciiKey = generateRandomAESKey();
		byte[] iv = generateRandomAESKey().getBytes();

		String processedString = prependAndAppend("_-_;admin=true");
		byte[] cipherByes = padAndEncryptTheString(processedString, asciiToHex(asciiKey), iv);

		byte[] secondCipherBlock = new byte[16];
		for (int i = 0; i < 16; ++i) {
			secondCipherBlock[i] = cipherByes[16 + i];
		}

		for (int i = 0; i < 16; ++i) {
			if (i == 3) {
				secondCipherBlock[i] = (byte) (secondCipherBlock[i] ^ 0x53 ^ 0x20);
			} else if (i == 4) {
				secondCipherBlock[i] = (byte) (secondCipherBlock[i] ^ 0x43 ^ 0x20);
			} else if (i == 5) {
				secondCipherBlock[i] = (byte) (secondCipherBlock[i] ^ 0x61 ^ 0x3b);
			} else if (i == 6) {
				secondCipherBlock[i] = (byte) (secondCipherBlock[i] ^ 0x61 ^ 0x64);
			} else if (i == 7) {
				secondCipherBlock[i] = (byte) (secondCipherBlock[i] ^ 0x64 ^ 0x6d);
			} else if (i == 8) {
				secondCipherBlock[i] = (byte) (secondCipherBlock[i] ^ 0x6d ^ 0x69);
			} else if (i == 9) {
				secondCipherBlock[i] = (byte) (secondCipherBlock[i] ^ 0x6e ^ 0x69);
			} else if (i == 10) {
				secondCipherBlock[i] = (byte) (secondCipherBlock[i] ^ 0x45 ^ 0x6e);
			} else if (i == 11) {
				secondCipherBlock[i] = (byte) (secondCipherBlock[i] ^ 0x3d ^ 0x51);
			}
		}

		for (int i = 0; i < 16; ++i) {
			cipherByes[16 + i] = secondCipherBlock[i];
		}

		boolean contains = findAdminIsTrueInCipher(cipherByes, asciiToHex(asciiKey), iv);
		if (contains) {
			System.out.println("Yes");
		} else {
			System.out.println("No");
		}
	}

	private static boolean findAdminIsTrueInCipher(byte[] cipherByes, String hexKey, byte[] iv) {
		byte[] plainTextBytes = decryptAESCBC(cipherByes, hexKey, iv);
		String s = new String(plainTextBytes);
		System.out.println(s);
		if (s.contains(";admin=true;")) {
			return true;
		} else {
			return false;
		}
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
