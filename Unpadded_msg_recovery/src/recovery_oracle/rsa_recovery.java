package recovery_oracle;

import java.util.Random;

class rsa_recovery {

	public static void main(String[] args){
		double ct = 82 ; 
		double p = 13, q = 7;
		double n = p*q;
		double et = (p-1)*(q-1);
		double e = 5;
		double s;
		Random r1 = new Random();
		s = (double)(r1.nextInt(10000)+1);
		double c1 = 1;
		//c1 = mod ((int)(Math.pow(s , e)) , n);
		int i;
		for(i=1;i<=e;i++)
		{
			c1=(c1*s)%n;
			c1=c1%n;
		}
		double c2;
		c2 = ((c1 * ct)% n);
		System.out.println("\n Produced cipher text : "+c2);
		//decryption
		int d;
		for(i=2;i<=n;i++)
		{
		/* @reference https://www.securecoding.cert.org/confluence/display/java/
		 * EXP53-J.+Use+parentheses+for+precedence+of+operation
		 * Parentheses have been employed for the expression e*i % et == 1*/
			if((e*i)%et == 1){
				break;
			}
		}
		d=i;
		System.out.println("value of d is:"+d);
		double m1=1;
		for(i=1;i<=d;i++)
		{
			m1=(m1*c2)%n;
			m1=m1%n;
		}
		System.out.println("\n Plain text :"+m1);
		//to find inverse of s
		for(i=2;i<=n;i++)
		{
		/* @reference https://www.securecoding.cert.org/confluence/display/java/
		 * EXP53-J.+Use+parentheses+for+precedence+of+operation
		 * Parentheses have been employed for the expression e*i % et == 1*/
			if((s*i)%n == 1){
				break;
			}
		}
		double sinv = i;
		// to find original plain text
		double pt  =  ((sinv * m1) % n);
		System.out.println("\n Original plain text : "+pt);
	}
}
