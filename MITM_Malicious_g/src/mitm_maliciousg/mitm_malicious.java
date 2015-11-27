package mitm_maliciousg;

import java.util.Random;

public class mitm_malicious {
	/* @reference https://www.securecoding.cert.org/confluence/display/java/
	 * NUM51-J.+Do+not+assume+that+the+remainder+operator+always+returns+a+
	 * nonnegative+result+for+integral+operands*/
	public static int mod(int a,int b){
		int c=a%b;
		return (c<0)? -c : c;
	}
	public static void diffhell(int g ,int g1, int p , int x , int y){
		int X,Y,K1,K2;
		X = mod((int) ((Math.pow(g, x))),p);
		System.out.println("\nX = "+X);
		Y = mod(((int) (Math.pow(g1, y))),p);
		System.out.println("\n Y = "+Y);
		K1 = mod(((int) (Math.pow(Y, x))),p) ;
		System.out.println("\nk1="+K1);
		K2 = mod(((int) (Math.pow(X, y))),p) ;
		System.out.println("\nk2="+K2);
	}
	public static void main(String[] args){
		int p = 47 , g = 3;
		int x , y ;

		Random r1 = new Random();
		x = r1.nextInt(10000)+1;
		System.out.println("\n x = "+x);
		y = r1.nextInt(10000)+1;
		System.out.println("\n y = "+y);
		System.out.println("\nWith original set");		
		// g=3
		int g1=g;
		diffhell(g,g1,p,x,y);
		System.out.println("\nWith g=1");
		g1=1;
		diffhell(g,g1,p,x,y);
		System.out.println("\nWith g=p");
		g1=p;
		diffhell(g,g1,p,x,y);
		System.out.println("\nWith g=p-1");
		g1=p-1;
		diffhell(g,g1,p,x,y);		
	}
}
