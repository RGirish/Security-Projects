package girish.security.project;

public class Base64ToHex {

	// At the end of the program, 'hex' will contain the output.
	// hexLookUp is the lookup table for hex encoding of a bit sequence.
	// I'm using the index value to reference it.
	static String hex = "", hexLookUp = "0123456789abcdef",
			base64LookUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";;

	public static void main(String[] a) {

		// Input Base64 String
		String base64 = "Q29taW5nIHdpdGggdml2aWQgZmFjZXM";
		for (int i = 0; i < base64.length(); ++i) {
			// Take first 6 bits (from the first base64 digit) and call
			// convert() with the first 4 bits.
			String firstDigit = String.format("%06d",
					Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i++)))));
			convert(firstDigit.substring(0, 4));

			// Take the remaining 2 bits (from the first base64 digit) and the
			// first 2 bits of the second base64 digit and call convert()
			String secondDigit = "";
			try {
				secondDigit = String.format("%06d",
						Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i)))));
			} catch (StringIndexOutOfBoundsException e) {
				secondDigit = "000000";
			}

			convert(firstDigit.substring(4, 6) + secondDigit.substring(0, 2));

			convert(secondDigit.substring(2, 6));
		}
		System.out.println(hex);
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
}