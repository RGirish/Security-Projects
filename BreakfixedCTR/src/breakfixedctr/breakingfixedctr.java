package breakfixedctr;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class breakingfixedctr {
	static String hex = "", hexLookUp = "0123456789abcdef",
			base64LookUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	public static String base64tohex(String pt){
		for (int i = 0; i < pt.length(); ++i) {
			// Take first 6 bits (from the first base64 digit) and call
			// convert() with the first 4 bits.
			String firstDigit = String.format("%06d",
					Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(pt.charAt(i++)))));
			convert(firstDigit.substring(0, 4));

			// Take the remaining 2 bits (from the first base64 digit) and the
			// first 2 bits of the second base64 digit and call convert()
			String secondDigit = String.format("%06d",
					Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(pt.charAt(i)))));
			convert(firstDigit.substring(4, 6) + secondDigit.substring(0, 2));

			convert(secondDigit.substring(2, 6));
		}
		System.out.println(hex);
		return hex;
	}
	static void convert(String bits) {
		StringBuilder stringBuilder = new StringBuilder(hex);
		stringBuilder.append(String.valueOf(hexLookUp.charAt(Integer.parseInt(bits, 2))));
		hex = stringBuilder.toString();
	}
	public static String encrypt_aes_ctr(String pt , byte[] key,byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		SecretKeySpec KEY = new SecretKeySpec(key , "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE,KEY,ivspec);
		String ct = new String(cipher.doFinal(pt.getBytes()));
		return ct;
		
	}
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
	String[] plaintext = {"SSBoYXZlIG1ldCB0aGVtIGF0IGNsb3NlIG9mIGRheQ==",
			"Q29taW5nIHdpdGggdml2aWQgZmFjZXM=",
			"RnJvbSBjb3VudGVyIG9yIGRlc2sgYW1vbmcgZ3JleQ==",
			"RWlnaHRlZW50aC1jZW50dXJ5IGhvdXNlcy4=",
			"SSBoYXZlIHBhc3NlZCB3aXRoIGEgbm9kIG9mIHRoZSBoZWFk",
			"T3IgcG9saXRlIG1lYW5pbmdsZXNzIHdvcmRzLA==",
			"T3IgaGF2ZSBsaW5nZXJlZCBhd2hpbGUgYW5kIHNhaWQ=",
			"UG9saXRlIG1lYW5pbmdsZXNzIHdvcmRzLA==",
			"QW5kIHRob3VnaHQgYmVmb3JlIEkgaGFkIGRvbmU=",
			"T2YgYSBtb2NraW5nIHRhbGUgb3IgYSBnaWJl",
			"VG8gcGxlYXNlIGEgY29tcGFuaW9u" ,
			"QXJvdW5kIHRoZSBmaXJlIGF0IHRoZSBjbHViLA==",
			"QmVpbmcgY2VydGFpbiB0aGF0IHRoZXkgYW5kIEk=",
			"QnV0IGxpdmVkIHdoZXJlIG1vdGxleSBpcyB3b3JuOg==",
			"QWxsIGNoYW5nZWQsIGNoYW5nZWQgdXR0ZXJseTo=",
			"QSB0ZXJyaWJsZSBiZWF1dHkgaXMgYm9ybi4=",
			"VGhhdCB3b21hbidzIGRheXMgd2VyZSBzcGVudA==",
			"SW4gaWdub3JhbnQgZ29vZCB3aWxsLA==",
			"SGVyIG5pZ2h0cyBpbiBhcmd1bWVudA==",
			"VW50aWwgaGVyIHZvaWNlIGdyZXcgc2hyaWxsLg==",
			"V2hhdCB2b2ljZSBtb3JlIHN3ZWV0IHRoYW4gaGVycw==",
			"V2hlbiB5b3VuZyBhbmQgYmVhdXRpZnVsLA==",
			"U2hlIHJvZGUgdG8gaGFycmllcnM/",
			"VGhpcyBtYW4gaGFkIGtlcHQgYSBzY2hvb2w=",
			"QW5kIHJvZGUgb3VyIHdpbmdlZCBob3JzZS4=",
			"VGhpcyBvdGhlciBoaXMgaGVscGVyIGFuZCBmcmllbmQ=",
			"V2FzIGNvbWluZyBpbnRvIGhpcyBmb3JjZTs=",
			"SGUgbWlnaHQgaGF2ZSB3b24gZmFtZSBpbiB0aGUgZW5kLA==",
			"U28gc2Vuc2l0aXZlIGhpcyBuYXR1cmUgc2VlbWVkLA==",
			"U28gZGFyaW5nIGFuZCBzd2VldCBoaXMgdGhvdWdodC4=",
			"VGhpcyBvdGhlciBtYW4gSSBoYWQgZHJlYW1lZA==",
			"QSBkcnVua2VuLCB2YWluLWdsb3Jpb3VzIGxvdXQu",
			"SGUgaGFkIGRvbmUgbW9zdCBiaXR0ZXIgd3Jvbmc=",
			"VG8gc29tZSB3aG8gYXJlIG5lYXIgbXkgaGVhcnQs",
			"WWV0IEkgbnVtYmVyIGhpbSBpbiB0aGUgc29uZzs=",
			"SGUsIHRvbywgaGFzIHJlc2lnbmVkIGhpcyBwYXJ0",
			"SW4gdGhlIGNhc3VhbCBjb21lZHk7",
			"SGUsIHRvbywgaGFzIGJlZW4gY2hhbmdlZCBpbiBoaXMgdHVybiw=",
			"VHJhbnNmb3JtZWQgdXR0ZXJseTo=",
			"QSB0ZXJyaWJsZSBiZWF1dHkgaXMgYm9ybi4="};
	String[] hexplaintext={};
	for(int i=0;i<plaintext.length;i++){
		hexplaintext[i] = base64tohex(plaintext[i]);
	}
	byte[] key = new byte[] {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x8,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F};
	byte[] iv = new byte[] {0x00,0x01,0x02,0x03,0x00,0x02,0x01,0x00,0x00,0x00,0x01,0x02,0x00,0x00,0x00,0x00};
	String[] hexciphertext={};
	for(int i=0;i<plaintext.length;i++){
		hexciphertext[i] = encrypt_aes_ctr(hexplaintext[i],key,iv);
	}
	
	}

}
