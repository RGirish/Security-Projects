
package girish.security.project;

import java.io.File;
import java.io.FileInputStream;

public class BreakRepeatingKeyXOR {

	public static void main(String[] args) {

		File file = new File("6.txt");
		FileInputStream fileInputStream = null;
		for (int l = 2; l <= 40; ++l) {
			try {
				
				fileInputStream = null;
				fileInputStream = new FileInputStream(file);
				
				byte fileContent[] = new byte[l];
				fileContent = new byte[l];
				fileInputStream.read(fileContent);
				String s1 = new String(fileContent);
				
				fileContent = new byte[l];
				fileInputStream.read(fileContent);
				String s2 = new String(fileContent);
				
				System.out.println(computeHammingDistance(s1, s2) / l);
			} catch (Exception e) {
				System.out.println("Exception " + e.toString());
			}
			try {
				if (fileInputStream != null)
					fileInputStream.close();
			} catch (Exception e) {
				System.out.println("Exception " + e.toString());
			}
		}
		
	}

	/*
	 * This function computes the Hamming distance between 2 strings.
	 */
	public static int computeHammingDistance(String s1, String s2) {
		String hexS1 = asciiToHex(s1);
		String hexS2 = asciiToHex(s2);
		String binaryResult = computeXOR(hexS1, hexS2);
		return findOnesInBinaryString(binaryResult);
	}

	/*
	 * This function computes the number of 1s in a binary string.
	 */
	public static int findOnesInBinaryString(String s) {
		char[] string = s.toCharArray();
		int count = 0;
		for (char c : string) {
			if (c == '1')
				count++;
		}
		return count;
	}

	/*
	 * This function computes the ASCII equivalent of a HEX String.
	 */
	public static String asciiToHex(String ascii) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < ascii.length(); ++i) {
			buffer.append(Integer.toHexString((int) ascii.charAt(i)));
		}
		return buffer.toString();
	}

	/*
	 * This function computes character by character XOR for the two input HEX
	 * strings returns an output string in Binary.
	 */
	static String computeXOR(String first, String second) {
		String result = "";
		StringBuilder stringBuilder = new StringBuilder(result);

		// I'm going character by character here. ^ does the XOR.
		for (int i = 0; i < first.length(); ++i) {
			int cc = Integer.parseInt(String.valueOf(first.charAt(i)), 16)
					^ Integer.parseInt(String.valueOf(second.charAt(i)), 16);
			stringBuilder.append(Integer.toBinaryString(cc));
		}
		result = stringBuilder.toString();
		return result;
	}
}
