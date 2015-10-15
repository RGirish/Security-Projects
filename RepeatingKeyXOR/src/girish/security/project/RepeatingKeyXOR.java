package girish.security.project;

public class RepeatingKeyXOR {

	public static void main(String[] args) {
		String plainText = "Burning 'em, if you ain't quick and nimble\n\nI go crazy when I hear a cymbal";
		System.out.println(encrypt(plainText, "ICE"));
	}

	public static String encrypt(String asciiPlainText, String asciiSeed) {
		String hexPlainText = asciiToHex(asciiPlainText);
		String repeatedAsciiKey = generateRepeatedKey(asciiSeed, asciiPlainText.length());
		String repeatedHexKey = asciiToHex(repeatedAsciiKey);
		//System.out.println(asciiPlainText);
		//System.out.println(hexPlainText);
		//System.out.println(repeatedAsciiKey);
		//System.out.println(repeatedHexKey);
		return computeXOR(hexPlainText, repeatedHexKey);
	}

	/*
	 * This function computes character by character XOR for the two input HEX
	 * strings returns an output string in HEX.
	 */
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

	public static String generateRepeatedKey(String key, int length) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i += key.length()) {
			buffer.append(key);
		}
		String repeatedKey = buffer.toString();
		if (buffer.toString().length() > length) {
			repeatedKey = buffer.substring(0, length);
		}
		return repeatedKey;
	}

	public static String asciiToHex(String ascii) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < ascii.length(); ++i) {
			buffer.append(Integer.toHexString((int) ascii.charAt(i)));
		}
		return buffer.toString();
	}

}