package girish.security.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BreakFixedNonceCTRStatistically {

	static String hexLookUp = "0123456789abcdef",
			base64LookUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";;

	public static void main(String[] args) {
		String[] strings = new String[60];
		int i = 0;
		try {
			File file = new File("20.txt");
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				strings[i] = base64ToHex(scanner.nextLine());
				i++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		byte[][] ciphers = new byte[60][];
		for (i = 0; i < 60; ++i) {
			String s = strings[i];
			String asciiPlainText = computeHexToASCII(s);
			String asciiNonce = "enirambus wolley";
			String asciiKey = "yellow submarine";
			ciphers[i] = encryptAESCTR(asciiNonce, asciiPlainText, asciiKey);
		}

		int minLength = ciphers[0].length;
		for (i = 0; i < 60; ++i) {
			if (ciphers[i].length < minLength) {
				minLength = ciphers[i].length;
			}
		}
		
		for (i = 0; i < 60; ++i) {
			ciphers[i] = Arrays.copyOf(ciphers[i], minLength);
		}
		List<String> afterFindingKey = new ArrayList<>();
		byte[][] transposedBlocks = transposeBlocks(ciphers);
		for (i = 0; i < transposedBlocks.length; ++i) {
			String st = computeSingleByteXOR(transposedBlocks[i]);
			afterFindingKey.add(st);
		}

		int index = 0;
		while (true) {
			for (String singleByteCipher : afterFindingKey) {
				try {
					System.out.print(singleByteCipher.charAt(index));
				} catch (Exception e) {
					return;
				}
			}
			index++;
		}
	}

	private static byte[][] transposeBlocks(byte[][] blocks) {
		byte[][] transposedBlocks = new byte[blocks[0].length][60];
		for (int i = 0; i < blocks[0].length; i++) {
			for (int j = 0; j < blocks.length; ++j) {
				transposedBlocks[i][j] = blocks[j][i];
			}
		}
		return transposedBlocks;
	}

	public static String computeSingleByteXOR(byte[] byteCipherText) {
		Map<String, Double> finalScores = new LinkedHashMap<String, Double>();

		for (int i = 0x20; i <= 0x7e; ++i) {
			String key = formRepeatingCharacterString(i, byteCipherText.length);
			byte[] bytePlainText = computeXOR(byteCipherText, key.getBytes());
			String output = computeHexToASCII(new String(bytePlainText));
			finalScores.put(String.valueOf((char) i), computeScore(output));
		}

		// Iterate through the HashMap and display the keyCharacter, the
		// corresponding plainText and its score.
		Iterator<?> it = finalScores.entrySet().iterator();
		double maxScore = 0;
		String keyCharacter = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			maxScore = (maxScore > (double) pair.getValue() ? maxScore : (double) pair.getValue());
			keyCharacter = (maxScore > (double) pair.getValue() ? keyCharacter : (String) pair.getKey());
		}
		//System.out.println(maxScore + " " + keyCharacter);
		//System.out.print(keyCharacter);
		String key = formRepeatingCharacterString(keyCharacter, byteCipherText.length);
		byte[] bytePlainText = computeXOR(byteCipherText, key.getBytes());
		String originalPlainText = computeHexToASCII(new String(bytePlainText));
		it.remove();
		return originalPlainText;
	}

	static String formRepeatingCharacterString(String keyCharacter, int num) {
		String key = "";
		for (int j = 0; j < num; ++j) {
			key = key + Integer.toHexString((int) keyCharacter.charAt(0));
		}
		return key;
	}

	static String formRepeatingCharacterString(int i, int num) {
		String key = "";
		for (int j = 0; j < num/2; ++j) {
			key = key + Integer.toHexString(i);
		}
		return key;
	}

	static byte[] computeXOR(byte[] first, byte[] second) {
		byte[] resultBytes = new byte[first.length];
		for (int i = 0; i < first.length; ++i) {
			resultBytes[i] = (byte) (first[i] ^ second[i]);
		}
		return resultBytes;
	}

	static String computeXOR(String first, String second) {
		String result = "";
		StringBuilder stringBuilder = new StringBuilder(result);

		// I'm going character by character here. ^ does the XOR.
		for (int i = 0; i < first.length(); ++i) {
			int cc = Integer.parseInt(String.valueOf(first.charAt(i)), 16)
					^ Integer.parseInt(String.valueOf(second.charAt(i)), 16);
			stringBuilder.append(String.valueOf(Integer.toHexString(cc)));
			result = stringBuilder.toString();
		}
		return result;
	}

	static String computeHexToASCII(String hexPlainText) {
		StringBuilder output = new StringBuilder();
		for (int k = 0; k < hexPlainText.length(); k += 2) {
			try {
				String str = hexPlainText.substring(k, k + 2);
				output.append((char) Integer.parseInt(str, 16));
			} catch (Exception e) {
			}
		}
		return output.toString();
	}

	/*
	 * ETAOIN SHRDLU - The most frequent English alphabet list. I'm giving E a
	 * score of 13, decreasing up to 1 for U. I'm not scoring any other
	 * alphabets. (And for this case, it turns out you don't have to!)
	 * 
	 * This function returns the final score of the input string, after scoring
	 * each character in it, based on this scheme.
	 */
	static double computeScore(String s) {
		double score = 0;
		for (int i = 0; i < s.length(); ++i) {
			if (s.charAt(i) == 'e' || s.charAt(i) == 'E') {
				score += 13;
			} else if (s.charAt(i) == 't' || s.charAt(i) == 'T') {
				score += 12;
			} else if (s.charAt(i) == 'a' || s.charAt(i) == 'A') {
				score += 11;
			} else if (s.charAt(i) == 'o' || s.charAt(i) == 'O') {
				score += 10;
			} else if (s.charAt(i) == 'i' || s.charAt(i) == 'I') {
				score += 9;
			} else if (s.charAt(i) == 'n' || s.charAt(i) == 'N') {
				score += 8;
			} else if (s.charAt(i) == ' ') {
				score += 7;
			} else if (s.charAt(i) == 's' || s.charAt(i) == 'S') {
				score += 6;
			} else if (s.charAt(i) == 'h' || s.charAt(i) == 'H') {
				score += 5;
			} else if (s.charAt(i) == 'r' || s.charAt(i) == 'R') {
				score += 4;
			} else if (s.charAt(i) == 'd' || s.charAt(i) == 'D') {
				score += 3;
			} else if (s.charAt(i) == 'l' || s.charAt(i) == 'L') {
				score += 2;
			} else if (s.charAt(i) == 'u' || s.charAt(i) == 'U') {
				score += 1;
			}
		}
		return score;
	}

	public static String hexToAscii(String hex) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i + 1 < hex.length(); ++i) {
			String s = "" + hex.charAt(i);
			i++;
			s = s + hex.charAt(i);
			buffer.append((char) Integer.parseInt(s, 16));
		}
		return buffer.toString();
	}

	private static String base64ToHex(String base64) {
		String hex = "";
		for (int i = 0; i < base64.length(); ++i) {
			String firstDigit = String.format("%06d",
					Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i++)))));
			hex = convert(hex, firstDigit.substring(0, 4));

			String secondDigit = "";
			try {
				secondDigit = String.format("%06d",
						Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i)))));
			} catch (StringIndexOutOfBoundsException e) {
				secondDigit = "000000";
			}

			hex = convert(hex, firstDigit.substring(4, 6) + secondDigit.substring(0, 2));

			hex = convert(hex, secondDigit.substring(2, 6));
		}
		return hex;
	}

	static String convert(String hex, String bits) {
		StringBuilder stringBuilder = new StringBuilder(hex);
		stringBuilder.append(String.valueOf(hexLookUp.charAt(Integer.parseInt(bits, 2))));
		return stringBuilder.toString();
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
