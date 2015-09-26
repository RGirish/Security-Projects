/**
 * @author Girish Raman
 * From http://cryptopals.com/
 * 
 */

package girish.security.project;

public class SingleByteXOR {

	static String computeXOR(String first, String second){
		String result = "";
		StringBuilder stringBuilder = new StringBuilder(result);
		
		//I'm going character by character here. ^ does the XOR.
		for(int i=0 ; i<first.length() ; ++i){
			int cc = Integer.parseInt(String.valueOf(first.charAt(i)),16) ^ Integer.parseInt(String.valueOf(second.charAt(i)),16);
			stringBuilder.append(String.valueOf(Integer.toHexString(cc)));
			result = stringBuilder.toString();
		}
		return result;
	}
	
	public static void main(String[] a){
		String cipher = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
		
		for(int i = 0x30 ; i<=0x39 ; ++i){
			String key = "";
			for(int j=0 ; j<33 ; ++j){
				key = key + i;
			}
			key=key + String.valueOf(Integer.toHexString(i));
			System.out.println(computeXOR(cipher, key));
		}
		
		for(int i = 0x41 ; i<=0x5a ; ++i){
			System.out.println(String.valueOf(0x78 ^ i));
			String key = "";
			for(int j=0 ; j<33 ; ++j){
				key = key + i;
			}
			key=key + String.valueOf(Integer.toHexString(i));
			System.out.println(computeXOR(cipher, key));
		}
		
		for(int i = 0x61 ; i<=0x7a ; ++i){
			String key = "";
			for(int j=0 ; j<34 ; ++j){
				key = key + i;
			}
			System.out.println(computeXOR(cipher, key));
		}
		
		
	}
	
}
