package girish.security.project;

import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCTRRandomAccessAttack {

	private final static String KEY = generateRandomAESKey();
	private final static String NONCE = generateRandomAESKey();

	public static void main(String[] args) {
		
		String asciiPlainText = "yellow submarineyellow submarineyellow submarineyellow submarineyellow submarine";
		System.out.println("Original Plain Text (Unknown to attacker) - " + asciiPlainText);
		byte[] ctBytes = encryptAESCTR(NONCE, asciiPlainText, KEY);
		byte[] ctBytesCopy = Arrays.copyOf(ctBytes, ctBytes.length);

		// 0 is the first block
		// 1 is the second block and so on...
		int offset = 2;
		System.out.println("Offset given by attacker - " + offset);
		byte[] allZeros = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00 };

		byte[] modifiedCipherBytesBlock = edit(ctBytes, offset, allZeros);

		byte[] originalPlainTextBytes = new byte[16];
		for (int i = 0; i < 16; ++i) {
			originalPlainTextBytes[i] = (byte) (ctBytesCopy[offset * 16 + i] ^ modifiedCipherBytesBlock[i]);
		}
		System.out.println("Attacker now knows that the original plain text in block number " + offset + " was - " + new String(originalPlainTextBytes));
	}

	private static byte[] edit(byte[] ctBytes, int offset, byte[] newPlainTextBytes) {
		byte[] nonceBytes = NONCE.getBytes();
		byte[] ctBytesNew = new byte[16];

		nonceBytes[15] = (byte) (nonceBytes[15] + offset + 1);

		try {
			SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] result = cipher.doFinal(nonceBytes);
			for (int k = 0; k < result.length; ++k) {
				ctBytesNew[k] = (byte) (result[k] ^ newPlainTextBytes[k]);
			}
		} catch (Exception e) {
		}
		return ctBytesNew;
	}

	public static byte[] encryptAESCTR(String nonce, String plainText, String key) {
		byte[] ptBytes = plainText.getBytes();
		byte[] nonceBytes = nonce.getBytes();
		byte[] temp = new byte[16];
		byte[] ctBytes = new byte[plainText.length()];
		int ctByteCount = 0;

		for (int i = 0, j = 0; i < ptBytes.length; ++i) {
			temp[j] = ptBytes[i];
			++j;
			if ((i + 1) % 16 == 0 || i + 1 == ptBytes.length) {
				nonceBytes[15] = (byte) (nonceBytes[15] + 1);
				try {
					SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
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

	public static byte[] decryptAESCTR(String nonce, byte[] ptBytes, String key) {
		byte[] nonceBytes = nonce.getBytes();
		byte[] temp = new byte[16];
		byte[] ctBytes = new byte[ptBytes.length];
		int ctByteCount = 0;

		for (int i = 0, j = 0; i < ptBytes.length; ++i) {
			temp[j] = ptBytes[i];
			++j;
			if ((i + 1) % 16 == 0 || i + 1 == ptBytes.length) {
				nonceBytes[15] = (byte) (nonceBytes[15] + 1);
				try {
					SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
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
	 * Cloning is disabled for security reasons.
	 * (non-Javadoc)
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