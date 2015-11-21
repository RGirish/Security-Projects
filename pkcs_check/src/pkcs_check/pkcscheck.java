package pkcs_check;

public class pkcscheck {
	public static void main(String[] args){
		String pt = "ICE ICE BABY0404040404";
		String s=null;
		for(int i=0;i<pt.length();i++)
		{
			if(pt.charAt(i)=='0')
			{
				s=pt.substring(i);
				break;
			}
				
		}
		System.out.println(s);
		char c= s.charAt(1);
		System.out.println("\n"+c);
		System.out.println("\n"+s.length()/2);
		if(s.length()/2 == Character.getNumericValue(c))
		{
			int i=0;
			for(i=1;i<(s.length());i=i+2)
			{
				System.out.println("\n"+s.charAt(i));
				if(s.charAt(i)==c)
					continue;
				else
				{
					System.out.println("\nWrong padding 2 - Padding value is different");
					break;
				}
			}
			if(i>s.length())
				System.out.println("\nCorrect padding");
		}
		else
			System.out.println("\nWrong padding 1 - padding length is different");

			
		
	}

}
