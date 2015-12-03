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
	public static double mod(double a,double b){
		double c=a%b;
		return (c<0)? -c : c;
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException{
		
		double p = 47 , g = 3;
		double x , y , X ,Y , K1=1 , K2=1;
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
		//double a =  (Math.pow(p, x));
		for(int i=1;i<=x;i++){
			K1=K1*p;
			K1=mod(K1,p);
		}
		K1 = mod(K1,p) ;
		//System.out.println("\n"+a);
		System.out.println("\n"+K1);
		//M sends p instead of X
		//K2 = mod(((int) (Math.pow(p, y))),p) ;
		for(int i=1;i<=y;i++){
			K2=K2*p;
			K2=mod(K2,p);
		}
		K2=mod(K2,p);
		System.out.println("\n"+K2);
	}

}
