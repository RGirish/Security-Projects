/**
 * @author Girish Raman
 * From http://cryptopals.com/
 * 
 * This program does XOR of 2 equal length buffers.
 */

package girish.security.project;

public class FixedXor {
	
	public static void main(String[] a){
		String first =  "1c0111001f010100061a024b53535009181c";
		String second = "686974207468652062756c6c277320657965";
		String result = "";
		StringBuilder stringBuilder = new StringBuilder(result);
		
		//I'm going character by character here. ^ does the XOR.
		for(int i=0 ; i<first.length() ; ++i){
			int cc = Integer.parseInt(String.valueOf(first.charAt(i)),16) ^ Integer.parseInt(String.valueOf(second.charAt(i)),16);
			stringBuilder.append(String.valueOf(Integer.toHexString(cc)));
			result = stringBuilder.toString();
		}
		System.out.println(result);
		
	}
	
}