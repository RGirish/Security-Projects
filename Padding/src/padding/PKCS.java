package padding;

public class PKCS {
	public static void main(String[] args) {
		StringBuilder s = new StringBuilder("shankar");
		int blockSize = 16;
		/*
		 * @reference https://www.securecoding.cert.org/confluence/display/java/
		 * EXP52-J.+Use+braces+for+the+body+of+an+if%2C+for%2C+or+while+
		 * statement A for loop with one statement inside is enclosed within
		 * braces
		 */
		int diff = (blockSize - s.length());
		for (int i = 0; i < diff; i++) {
			s.append(String.valueOf(diff));
		}
		System.out.println(s);
	}

}