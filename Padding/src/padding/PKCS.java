package padding;

public class PKCS {
	public static void main(String[] args){
		StringBuilder s = new StringBuilder("YELLOW SUBMARINE");
		int bytes=4;
		String s1=String.format("0x%02X", bytes);
		/* @reference https://www.securecoding.cert.org/confluence/display/java/
		 * EXP52-J.+Use+braces+for+the+body+of+an+if%2C+for%2C+or+while+statement
		 * A for loop with one statement inside is enclosed within braces */
		for(int i=0;i<bytes;i++)
		{
			s.append(s1);
		}
			System.out.println(s);
	}

}
