package girish.security.project;

import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCTR {

	public static void main(String[] args) {
		String asciiPlainText = "hey this is a long sentence that I'm writing so that it goes beyond 16 bytes!";
		String asciiNonce = "enirambus wolley";
		String asciiKey = "yellow submarine";

		byte[] ctBytes = encryptAESCTR(asciiNonce, asciiPlainText, asciiKey);
		byte[] ptBytes = decryptAESCTR(asciiNonce, ctBytes, asciiKey);

		System.out.println(new String(ptBytes));
	}

	public static byte[] encryptAESCTR(String nonce, String plainText, String key) {
		byte[] ptBytes = plainText.getBytes();
		byte[] nonceBytes = nonce.getBytes();
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
		int nonceCtrCount = 0;
		int ctByteCount = 0;

		for (int i = 0, j = 0; i < ptBytes.length; ++i) {
			temp[j] = ptBytes[i];
			++j;
			if ((i + 1) % 16 == 0 || i + 1 == ptBytes.length) {
				nonceBytes[15] = (byte) (nonceBytes[15] + nonceCtrCount);
				nonceCtrCount++;
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