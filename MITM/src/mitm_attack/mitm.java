package mitm_attack;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class mitm {
	/* @reference https://www.securecoding.cert.org/confluence/display/java/
	 * NUM51-J.+Do+not+assume+that+the+remainder+operator+always+returns+a+
	 * nonnegative+result+for+integral+operands*/
	public static int mod(int a,int b){
		int c=a%b;
		return (c<0)? -c : c;
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException{
		
		int p = 47 , g = 3;
		int x , y , X ,Y , K1 , K2;
		Random r1 = new Random();
		x = r1.nextInt(10000)+1;
		System.out.println("\n x = "+x);
		X = mod((int) ((Math.pow(g, x))),p);
		System.out.println("\nX = "+X);
		y = r1.nextInt(10000)+1;
		System.out.println("\n y = "+y);
		Y = mod(((int) (Math.pow(g, y))),p);
		System.out.println("\n Y = "+Y);
		//M sends p instead of Y
		K1 = mod(((int) (Math.pow(p, x))),p) ;
		System.out.println("\n"+K1);
		//M sends p instead of X
		K2 = mod(((int) (Math.pow(p, y))),p) ;
		System.out.println("\n"+K2);
	}

}
