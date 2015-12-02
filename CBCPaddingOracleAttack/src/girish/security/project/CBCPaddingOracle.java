package girish.security.project;

import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class CBCPaddingOracle {

	public static void main(String[] args) {
		String hexString = asciiToHex(generateRandomString());
		System.out.println("The hex plain text is \n" + PKCSPadTheHexString(hexString, 16));
		String hexKey = asciiToHex(generateRandomAESKey());
		String IV = generateRandomAESKey();

		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(hexKey), "AES");
		IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
		Cipher cipher;
		byte[] result = null;
		try {
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
			result = cipher.doFinal(DatatypeConverter.parseHexBinary(PKCSPadTheHexString(hexString, 16)));
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
		}

		byte[] resultTemp = Arrays.copyOf(result, result.length);

		int pad = 0;
		for (int i = 0; i < 16; ++i) {
			// Checking what the padding is, by changing bytes of second last
			// block from left to right until checkPadding returns false for a
			// bad padding
			resultTemp[resultTemp.length - 32 + i] = 0x00;
			if (!checkPadding(resultTemp, key, ivspec)) {
				pad = 16 - i;
				System.out.println("The pad byte is " + pad);
				// Pad if the padding given to the string. Now we have to
				// increment the pad bytes.
				switch (pad) {
				case 1:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x02 ^ 0x01);
					}
					break;
				case 2:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x03 ^ 0x02);
					}
					break;
				case 3:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x04 ^ 0x03);
					}
					break;
				case 4:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x04 ^ 0x05);
					}
					break;
				case 5:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x06 ^ 0x05);
					}
					break;
				case 6:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x06 ^ 0x07);
					}
					break;
				case 7:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x08 ^ 0x07);
					}
					break;
				case 8:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x09 ^ 0x08);
					}
					break;
				case 9:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x0a ^ 0x09);
					}
					break;
				case 10:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x0b ^ 0x0a);
					}
					break;
				case 11:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x0b ^ 0x0c);
					}
					break;
				case 12:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x0d ^ 0x0c);
					}
					break;
				case 13:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x0e ^ 0x0d);
					}
					break;
				case 14:
					for (int j = 0; j < pad; ++j) {
						result[result.length - j - 1 - 16] = (byte) (result[result.length - j - 1 - 16] ^ 0x0e ^ 0x0f);
					}
					break;
				}
				break;
			}
		}

		byte[] resultTemp2 = Arrays.copyOf(result, result.length);

		// Try XOR-ing the (pad+1)th byte from the last, of the second last
		// cipher block with all possible bytes and for one of them,
		// checkPadding is going to return true. That is the last byte!
		for (byte b = -128; b < 127; ++b) {
			resultTemp2 = Arrays.copyOf(result, result.length);
			switch(pad){
			case 1:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x02 ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 2:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x03 ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 3:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x04 ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 4:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x05 ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 5:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x06 ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 6:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x07 ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 7:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x08 ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 8:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x09 ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 9:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x0a ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 10:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x0b ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 11:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x0c ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 12:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x0d ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 13:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x0e ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
			case 14:
				resultTemp2[resultTemp2.length - 1 - pad - 16] = (byte) (0x0f ^ b ^ resultTemp2[resultTemp2.length - 1 - pad - 16]);
				break;
				
			}
			if (checkPadding(resultTemp2, key, ivspec)) {
				int i = b & 0xFF;
				System.out.println("The last byte is " + Integer.toHexString(i));
				break;
			}
		}
	}

	private static boolean checkPadding(byte[] result, SecretKey key, IvParameterSpec ivspec) {
		byte[] result2 = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
			result2 = cipher.doFinal(result);
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
		}

		int c = 0;
		byte b = 0x00;
		switch (result2[result2.length - 1]) {
		case 0x01:
			b = result2[result2.length - 1];
			c = 1;
			break;
		case 0x02:
			b = result2[result2.length - 1];
			c = 2;
			break;
		case 0x03:
			b = result2[result2.length - 1];
			c = 3;
			break;
		case 0x04:
			b = result2[result2.length - 1];
			c = 4;
			break;
		case 0x05:
			b = result2[result2.length - 1];
			c = 5;
			break;
		case 0x06:
			b = result2[result2.length - 1];
			c = 6;
			break;
		case 0x07:
			b = result2[result2.length - 1];
			c = 7;
			break;
		case 0x08:
			b = result2[result2.length - 1];
			c = 8;
			break;
		case 0x09:
			b = result2[result2.length - 1];
			c = 9;
			break;
		case 0x0a:
			b = result2[result2.length - 1];
			c = 10;
			break;
		case 0x0b:
			b = result2[result2.length - 1];
			c = 11;
			break;
		case 0x0c:
			b = result2[result2.length - 1];
			c = 12;
			break;
		case 0x0d:
			b = result2[result2.length - 1];
			c = 13;
			break;
		case 0x0e:
			b = result2[result2.length - 1];
			c = 14;
			break;
		case 0x0f:
			b = result2[result2.length - 1];
			c = 15;
			break;
		}

		if (b != 0x00) {
			for (int k = 0; k < c; ++k) {
				if (result2[result2.length - k - 1] != b) {
					return false;
				}
			}
		}
		return true;
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

	private static String generateRandomString() {
		String[] strings = { 
				"yellow submarineyellow submarineyellow submarineyellow submari",
				"yellow submarineyellow submarineyellow submarineyellow submar",
				"yellow submarineyellow submarineyellow submarineyellow subma",
				"yellow submarineyellow submarineyellow submarineyellow subm",
				"yellow submarineyellow submarineyellow submarineyellow sub"};

		SecureRandom rand = new SecureRandom();
		return strings[rand.nextInt(strings.length)];
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