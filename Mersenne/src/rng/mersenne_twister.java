package rng;

public class mersenne_twister {

	public static void main(String[] args){
		int n = 624;
		int[] mt= new int[624];
		int index = n+1;
		final int lower_mask = 0x7fffffff;
		final int upper_mask = 0x80000000;
		int f = 1812433253 ;
		int w = 64;
		index = n;
		int seed = 1;
		final int a = 0x9908B0DF;
		mt[0] = seed;
		int m=397;
		int u =11;
		int d = 0xFFFFFFFF;
		int c = 0xEFC60000;
		int s=7;
		int t = 15;
		int b = 0xEFC60000;
		for(int i=1;i<n;i++){
			mt[i] = (f*(mt[i-1]^(mt[i-1]>>(w-2)))+i)<<-w>>-w; 
		}
		if(index>=n)
		{
			if(index>n)
			{
				System.out.println("\nError");
			}
			for(int i=0;i<=n-1;i++){
				int x = (mt[i] & upper_mask)+(mt[(i+1)% n]& lower_mask);
				int xa = x>>1;
				if(x % 2 != 0)
					xa = xa ^ a;
				mt[i]=(mt[(i+m)%n]^xa);
			}
			index = 0;
		}
		int y = mt[index];
		y = y ^ ((y>>u)& d);
		y = y ^ ((y<<s)&b);
		y = y ^ ((y<<t)&c);
		y = y ^ (y>>1);
		index = index+1;
		System.out.println(y);
	}

}
