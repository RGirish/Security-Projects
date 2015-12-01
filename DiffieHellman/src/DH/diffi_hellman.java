package DH;

import java.io.ObjectOutputStream;
import java.util.Random;

public class diffi_hellman {
	/*
	 * @reference https://www.securecoding.cert.org/confluence/display/java/
	 * NUM51-J.+Do+not+assume+that+the+remainder+operator+always+returns+a+
	 * nonnegative+result+for+integral+operands
	 */
	public static int mod(int a, int b) {
		int c = a % b;
		return (c < 0) ? -c : c;
	}

	public static void main(String[] args) {
		int p = 47, g = 3;
		int x, y, X, Y, K1, K2;
		Random r1 = new Random();
		x = r1.nextInt(10000) + 1;
		System.out.println("\n x = " + x);
		X = mod((int) ((Math.pow(g, x))), p);
		System.out.println("\nX = " + X);
		y = r1.nextInt(10000) + 1;
		System.out.println("\n y = " + y);
		Y = mod(((int) (Math.pow(g, y))), p);
		System.out.println("\n Y = " + Y);
		K1 = mod(((int) (Math.pow(Y, x))), p);
		System.out.println("\n" + K1);
		K2 = mod(((int) (Math.pow(X, y))), p);
		System.out.println("\n" + K2);
		/*
		 * @refernce https://www.securecoding.cert.org/confluence/display/java/
		 * EXP51-J.+Do+not+perform+assignments+in+conditional+expressions K1 and
		 * K2 are not assigned at conditional expressions
		 */
		if (K1 == K2)
			System.out.println("\nKeys are same");
		else
			System.out.println("\nWrong");

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
