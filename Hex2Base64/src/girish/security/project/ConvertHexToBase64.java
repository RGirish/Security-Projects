/**
 * @author Girish Raman
 * From http://cryptopals.com/
 * 
 * This piece of code converts a Hex encoded String into Base64. 
 * Probably not the best way to do it- I could have very well used a library. 
 * I just wanted to do it this way!
 */
package girish.security.project;

public class ConvertHexToBase64 {

	// At the end of the program, base64 will contain the output.
	// base64LookUp is the lookup table for base64 encoding of a bit sequence.
	// I'm using the index value to reference it.
	static String base64 = "", base64LookUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	public static void main(String[] a) {
		String temp;

		// Input Hex String
		String hex = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";

		for (int i = 0; i < hex.length(); ++i) {
			// Take first 4 bits (from the first hex digit) and the next 2 bits
			// from the next hex digit and call convert()
			String firstSetOfBits = String.format("%04d",
					Integer.parseInt(Integer.toBinaryString(Integer.parseInt(String.valueOf(hex.charAt(i++)), 16))));
			temp = String.format("%04d",
					Integer.parseInt(Integer.toBinaryString(Integer.parseInt(String.valueOf(hex.charAt(i++)), 16))));
			String secondSetOfBits = temp.substring(0, 2);
			convert(firstSetOfBits, secondSetOfBits);

			// Take the remaining 2 bits and 4 bits of the next hex digit and
			// call convert()
			firstSetOfBits = temp.substring(2);
			secondSetOfBits = String.format("%04d",
					Integer.parseInt(Integer.toBinaryString(Integer.parseInt(String.valueOf(hex.charAt(i)), 16))));
			convert(firstSetOfBits, secondSetOfBits);
		}
		System.out.println(base64);
	}

	/*
	 * firstSetOfBits + secondSetOfBits will be a 6 bit sequence which will be
	 * converted to the corresponding base64 value. Each base64 digit is
	 * appended to the string base64.
	 */
	static void convert(String firstSetOfBits, String secondSetOfBits) {
		StringBuilder stringBuilder = new StringBuilder(base64);
		stringBuilder
				.append(String.valueOf(base64LookUp.charAt(Integer.parseInt(firstSetOfBits + secondSetOfBits, 2))));
		base64 = stringBuilder.toString();
	}
}