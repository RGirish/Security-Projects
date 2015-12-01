package girish.security.project;

import java.io.ObjectOutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCTRBitFlipping {

	private final static String KEY = generateRandomAESKey();
	private final static String NONCE = generateRandomAESKey();

	public static void main(String[] args) {

		String processedString = prependAndAppend("_-_;admin=true");
		System.out.println(processedString);
		byte[] cipherByes = encryptAESCTR(processedString);

		byte[] thirdCipherBlock = new byte[16];
		for (int i = 0; i < 16; ++i) {
			thirdCipherBlock[i] = cipherByes[32 + i];
		}

		for (int i = 0; i < 16; ++i) {
			if (i == 3) {
				thirdCipherBlock[i] = (byte) (thirdCipherBlock[i] ^ 0x53 ^ 0x20);
			} else if (i == 4) {
				thirdCipherBlock[i] = (byte) (thirdCipherBlock[i] ^ 0x43 ^ 0x20);
			} else if (i == 5) {
				thirdCipherBlock[i] = (byte) (thirdCipherBlock[i] ^ 0x61 ^ 0x3b);
			} else if (i == 6) {
				thirdCipherBlock[i] = (byte) (thirdCipherBlock[i] ^ 0x61 ^ 0x64);
			} else if (i == 7) {
				thirdCipherBlock[i] = (byte) (thirdCipherBlock[i] ^ 0x64 ^ 0x6d);
			} else if (i == 8) {
				thirdCipherBlock[i] = (byte) (thirdCipherBlock[i] ^ 0x6d ^ 0x69);
			} else if (i == 9) {
				thirdCipherBlock[i] = (byte) (thirdCipherBlock[i] ^ 0x6e ^ 0x69);
			} else if (i == 10) {
				thirdCipherBlock[i] = (byte) (thirdCipherBlock[i] ^ 0x45 ^ 0x6e);
			} else if (i == 11) {
				thirdCipherBlock[i] = (byte) (thirdCipherBlock[i] ^ 0x3d ^ 0x51);
			}
		}

		for (int i = 0; i < 16; ++i) {
			cipherByes[32 + i] = thirdCipherBlock[i];
		}

		boolean contains = findAdminIsTrueInCipher(cipherByes);
		if (contains) {
			System.out.println("Yes");
		} else {
			System.out.println("No");
		}
	}

	private static boolean findAdminIsTrueInCipher(byte[] cipherByes) {
		byte[] plainTextBytes = decryptAESCTR(cipherByes);
		String s = new String(plainTextBytes);
		System.out.println(s);
		if (s.contains(";admin=true;")) {
			return true;
		} else {
			return false;
		}
	}

	public static byte[] encryptAESCTR(String plainText) {
		byte[] ptBytes = plainText.getBytes();
		byte[] nonceBytes = NONCE.getBytes();
		byte[] temp = new byte[16];
		byte[] ctBytes = new byte[plainText.length()];
		int nonceCtrCount = 0;
		int ctByteCount = 0;

		for (int i = 0, j = 0; i < ptBytes.length; ++i) {
			temp[j] = ptBytes[i];
			++j;
			if ((i + 1) % 16 == 0 || i + 1 == ptBytes.length) {
				nonceBytes[15] = (byte) (nonceBytes[15] + nonceCtrCount);
				nonceCtrCount++;
				try {
					SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
					Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
					cipher.init(Cipher.ENCRYPT_MODE, secretKey);
					byte[] result = cipher.doFinal(nonceBytes);
					for (int k = 0; k < result.length; ++k) {
						ctBytes[ctByteCount] = (byte) (result[k] ^ temp[k]);
						temp[k] = 0x00;
						ctByteCount++;
					}
				} catch (Exception e) {
				}
				j = 0;
			}
		}
		return ctBytes;
	}

	public static byte[] decryptAESCTR(byte[] ptBytes) {
		byte[] nonceBytes = NONCE.getBytes();
		byte[] temp = new byte[16];
		byte[] ctBytes = new byte[ptBytes.length];
		int nonceCtrCount = 0;
		int ctByteCount = 0;

		for (int i = 0, j = 0; i < ptBytes.length; ++i) {
			temp[j] = ptBytes[i];
			++j;
			if ((i + 1) % 16 == 0 || i + 1 == ptBytes.length) {
				nonceBytes[15] = (byte) (nonceBytes[15] + nonceCtrCount);
				nonceCtrCount++;
				try {
					SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
					Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
					cipher.init(Cipher.ENCRYPT_MODE, secretKey);
					byte[] result = cipher.doFinal(nonceBytes);
					for (int k = 0; k < result.length; ++k) {
						ctBytes[ctByteCount] = (byte) (result[k] ^ temp[k]);
						temp[k] = 0x00;
						ctByteCount++;
					}
				} catch (Exception e) {
				}
				j = 0;
			}
		}
		return ctBytes;
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

	public static String generateRandomAESKey() {
		String keyCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		SecureRandom rand = new SecureRandom();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 16; ++i) {
			builder.append(keyCharacters.charAt(rand.nextInt(keyCharacters.length())));
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
