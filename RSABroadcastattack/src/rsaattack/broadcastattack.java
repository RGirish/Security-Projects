package rsaattack;

public class broadcastattack {
	public static double invmod(double ms , double N){
		double i;
		for(i=2;i<=N;i++){
			if((i*ms)%N == 1)
				break;
		}
		return i;
	}
	public static void main(String[] args){
		double c1 = 333;
		double c2 = 31;
		double c3 = 10;
		double N1 = 667;
		double N2 = 51;
		double N3 = 55;
		double ms1 = N2 * N3 ;
		double ms2 = N1 * N3 ;
		double ms3 = N1 * N2 ;
		double N = N1 * N2 * N3;
		double ms1_inv = invmod(ms1 , N1);
		double ms2_inv = invmod(ms2 , N2);
		double ms3_inv = invmod(ms3 , N3);
		double result = ((c1 * ms1 * ms1_inv) + (c2 * ms2 * ms2_inv) + (c3 * ms3 * ms3_inv)) % N;
		System.out.println("Result:"+result);
		//finding cube root
		double cube = (int)(Math.cbrt(result));
		System.out.println("Cube root:"+cube);
		double pt = ((int)(Math.cbrt(result)))%N;
		System.out.println("Plain text:"+pt);
	}
}
