package girish.security.project;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BreakFixedNonceCTR {

	static String hex = "", hexLookUp = "0123456789abcdef",
			base64LookUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";;

	public static void main(String[] args) {

		String[] strings = { "SSBoYXZlIG1ldCB0aGVtIGF0IGNsb3NlIG9mIGRheQ", "Q29taW5nIHdpdGggdml2aWQgZmFjZXM",
				"RnJvbSBjb3VudGVyIG9yIGRlc2sgYW1vbmcgZ3JleQ", "RWlnaHRlZW50aC1jZW50dXJ5IGhvdXNlcy4",
				"SSBoYXZlIHBhc3NlZCB3aXRoIGEgbm9kIG9mIHRoZSBoZWFk", "T3IgcG9saXRlIG1lYW5pbmdsZXNzIHdvcmRzLA",
				"T3IgaGF2ZSBsaW5nZXJlZCBhd2hpbGUgYW5kIHNhaWQ", "UG9saXRlIG1lYW5pbmdsZXNzIHdvcmRzLA",
				"QW5kIHRob3VnaHQgYmVmb3JlIEkgaGFkIGRvbmU", "T2YgYSBtb2NraW5nIHRhbGUgb3IgYSBnaWJl",
				"VG8gcGxlYXNlIGEgY29tcGFuaW9u", "QXJvdW5kIHRoZSBmaXJlIGF0IHRoZSBjbHViLA",
				"QmVpbmcgY2VydGFpbiB0aGF0IHRoZXkgYW5kIEk", "QnV0IGxpdmVkIHdoZXJlIG1vdGxleSBpcyB3b3JuOg",
				"QWxsIGNoYW5nZWQsIGNoYW5nZWQgdXR0ZXJseTo", "QSB0ZXJyaWJsZSBiZWF1dHkgaXMgYm9ybi4",
				"VGhhdCB3b21hbidzIGRheXMgd2VyZSBzcGVudA", "SW4gaWdub3JhbnQgZ29vZCB3aWxsLA",
				"SGVyIG5pZ2h0cyBpbiBhcmd1bWVudA", "VW50aWwgaGVyIHZvaWNlIGdyZXcgc2hyaWxsLg",
				"V2hhdCB2b2ljZSBtb3JlIHN3ZWV0IHRoYW4gaGVycw", "V2hlbiB5b3VuZyBhbmQgYmVhdXRpZnVsLA",
				"U2hlIHJvZGUgdG8gaGFycmllcnM/", "VGhpcyBtYW4gaGFkIGtlcHQgYSBzY2hvb2w",
				"QW5kIHJvZGUgb3VyIHdpbmdlZCBob3JzZS4", "VGhpcyBvdGhlciBoaXMgaGVscGVyIGFuZCBmcmllbmQ",
				"V2FzIGNvbWluZyBpbnRvIGhpcyBmb3JjZTs", "SGUgbWlnaHQgaGF2ZSB3b24gZmFtZSBpbiB0aGUgZW5kLA",
				"U28gc2Vuc2l0aXZlIGhpcyBuYXR1cmUgc2VlbWVkLA", "U28gZGFyaW5nIGFuZCBzd2VldCBoaXMgdGhvdWdodC4",
				"VGhpcyBvdGhlciBtYW4gSSBoYWQgZHJlYW1lZA", "QSBkcnVua2VuLCB2YWluLWdsb3Jpb3VzIGxvdXQu",
				"SGUgaGFkIGRvbmUgbW9zdCBiaXR0ZXIgd3Jvbmc", "VG8gc29tZSB3aG8gYXJlIG5lYXIgbXkgaGVhcnQs",
				"WWV0IEkgbnVtYmVyIGhpbSBpbiB0aGUgc29uZzs", "SGUsIHRvbywgaGFzIHJlc2lnbmVkIGhpcyBwYXJ0",
				"SW4gdGhlIGNhc3VhbCBjb21lZHk7", "SGUsIHRvbywgaGFzIGJlZW4gY2hhbmdlZCBpbiBoaXMgdHVybiw",
				"VHJhbnNmb3JtZWQgdXR0ZXJseTo", "QSB0ZXJyaWJsZSBiZWF1dHkgaXMgYm9ybi4" };

		byte[][] ciphers = new byte[40][];
		for (int i = 0; i < 40; ++i) {
			String s = strings[i];
			String asciiPlainText = hexToAscii(base64ToHex(s));
			String asciiNonce = "enirambus wolley";
			String asciiKey = "yellow submarine";
			ciphers[i] = encryptAESCTR(asciiNonce, asciiPlainText, asciiKey);
			
			System.out.println(new String(decryptAESCTR(asciiNonce, ciphers[i], asciiKey)));
		}

		int[] bytefrequencies = new int[256];

		for (byte[] cipher : ciphers) {
			for (int i=0;i<16;++i) {
				bytefrequencies[(int)(cipher[i]) + 128] = bytefrequencies[(int)(cipher[i]) + 128] + 1;
				System.out.print(cipher[i] + " ");
			}
			System.out.println("\n");
		}
		
		System.out.println("************\n\n");
		for (int i = 0; i < 256; ++i) {
			System.out.println(i-128 + " " + bytefrequencies[i]);
		}

	}

	public static String hexToAscii(String hex) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i + 1 < hex.length(); ++i) {
			String s = "" + hex.charAt(i);
			i++;
			s = s + hex.charAt(i);
			buffer.append((char) Integer.parseInt(s, 16));
		}
		return buffer.toString();
	}

	private static String base64ToHex(String base64) {
		String hex = "";
		for (int i = 0; i < base64.length(); ++i) {
			String firstDigit = String.format("%06d",
					Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i++)))));
			hex = convert(hex, firstDigit.substring(0, 4));

			String secondDigit = "";
			try {
				secondDigit = String.format("%06d",
						Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i)))));
			} catch (StringIndexOutOfBoundsException e) {
				secondDigit = "000000";
			}

			hex = convert(hex, firstDigit.substring(4, 6) + secondDigit.substring(0, 2));

			hex = convert(hex, secondDigit.substring(2, 6));
		}
		return hex;
	}

	static String convert(String hex, String bits) {
		StringBuilder stringBuilder = new StringBuilder(hex);
		stringBuilder.append(String.valueOf(hexLookUp.charAt(Integer.parseInt(bits, 2))));
		return stringBuilder.toString();
	}

	public static byte[] encryptAESCTR(String nonce, String plainText, String key) {
		byte[] ptBytes = plainText.getBytes();
		byte[] nonceBytes = nonce.getBytes();
		byte[] temp = new byte[16];
		byte[] ctBytes = new byte[plainText.length()];
		int nonceCtrCount = 0;
		int ctByteCount = 0;

		for (int i = 0, j = 0; i < ptBytes.length; ++i) {
			temp[j] = ptBytes[i];
			++j;
			if ((i + 1) % 16 == 0 || i + 1 == ptBytes.length) {
				nonceBytes[15] = (byte) (nonceBytes[15] + nonceCtrCount);
				nonceCtrCount++;
				try {
					SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
					Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
					cipher.init(Cipher.ENCRYPT_MODE, secretKey);
					byte[] result = cipher.doFinal(nonceBytes);
					for (int k = 0; k < result.length; ++k) {
						ctBytes[ctByteCount] = (byte) (result[k] ^ temp[k]);
						temp[k] = 0x00;
						ctByteCount++;
					}
				} catch (Exception e) {
				}
				j = 0;
			}
		}
		return ctBytes;
	}

	public static byte[] decryptAESCTR(String nonce, byte[] ptBytes, String key) {
		byte[] nonceBytes = nonce.getBytes();
		byte[] temp = new byte[16];
		byte[] ctBytes = new byte[ptBytes.length];
		int nonceCtrCount = 0;
		int ctByteCount = 0;

		for (int i = 0, j = 0; i < ptBytes.length; ++i) {
			temp[j] = ptBytes[i];
			++j;
			if ((i + 1) % 16 == 0 || i + 1 == ptBytes.length) {
				nonceBytes[15] = (byte) (nonceBytes[15] + nonceCtrCount);
				nonceCtrCount++;
				try {
					SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
					Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
					cipher.init(Cipher.ENCRYPT_MODE, secretKey);
					byte[] result = cipher.doFinal(nonceBytes);
					for (int k = 0; k < result.length; ++k) {
						ctBytes[ctByteCount] = (byte) (result[k] ^ temp[k]);
						temp[k] = 0x00;
						ctByteCount++;
					}
				} catch (Exception e) {
				}
				j = 0;
			}
		}
		return ctBytes;
	}

}
