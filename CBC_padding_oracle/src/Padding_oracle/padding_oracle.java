package Padding_oracle;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class padding_oracle {
	
	public static String random_string() {
		String s1 = "MDAwMDAwTm93IHRoYXQgdGhlIHBhcnR5IGlzIGp1bXBpbmc=";
		String s2 = "MDAwMDAxV2l0aCB0aGUgYmFzcyBraWNrZWQgaW4gYW5kIHRoZSBWZWdhJ3MgYXJlIHB1bXBpbic=";
		String s3 = "MDAwMDAyUXVpY2sgdG8gdGhlIHBvaW50LCB0byB0aGUgcG9pbnQsIG5vIGZha2luZw==";
		String s4 = "MDAwMDAzQ29va2luZyBNQydzIGxpa2UgYSBwb3VuZCBvZiBiYWNvbg==";
		String s5 = "MDAwMDA0QnVybmluZyAnZW0sIGlmIHlvdSBhaW4ndCBxdWljayBhbmQgbmltYmxl";
		String s6 = "MDAwMDA1SSBnbyBjcmF6eSB3aGVuIEkgaGVhciBhIGN5bWJhbA==";
		String s7 = "MDAwMDA2QW5kIGEgaGlnaCBoYXQgd2l0aCBhIHNvdXBlZCB1cCB0ZW1wbw==";
		String s8 = "MDAwMDA3SSdtIG9uIGEgcm9sbCwgaXQncyB0aW1lIHRvIGdvIHNvbG8=";
		String s9 = "MDAwMDA4b2xsaW4nIGluIG15IGZpdmUgcG9pbnQgb2g=";
		String s10 = "MDAwMDA5aXRoIG15IHJhZy10b3AgZG93biBzbyBteSBoYWlyIGNhbiBibG93";
		int x;
		Random r1 = new Random();
		x = r1.nextInt(10) + 1;
		switch (x) {
		case 1:
			return s1;
		case 2:
			return s2;
		case 3:
			return s3;
		case 4:
			return s4;
		case 5:
			return s5;
		case 6:
			return s6;
		case 7:
			return s7;
		case 8:
			return s8;
		case 9:
			return s9;
		case 10:
			return s10;
		default:
			return s1;
		}
	}

	public static String padding(String s) {
		int blockSize = 16;
		System.out.println("\nLength of string: " + s.length());
		int diff = (blockSize - (s.length() % 16));
		if (diff == 16)
			return s;
		System.out.println("\nTo pad: " + diff);
		for (int i = 0; i < diff; i++) {
			s = s + String.valueOf(diff);
		}
		return s;
	}

	public static void main(String[] args)
			throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		String s = random_string();
		System.out.println("Plain text is: " + s);
		byte[] key = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x8, 0x09, 0x0A, 0x0B, 0x0C, 0x0D,
				0x0E, 0x0F };
		byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00, 0x02, 0x01, 0x00, 0x00, 0x00, 0x01, 0x02, 0x00, 0x00,
				0x00, 0x00 };
		String pt = padding(s);
		System.out.println("\nAfter padding:" + pt);
		SecretKeySpec KEY = new SecretKeySpec(key, "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		// Encryption
		cipher.init(Cipher.ENCRYPT_MODE, KEY, ivspec);
		String ct = new String(cipher.doFinal(pt.getBytes()));
		System.out.println("Cipher text : " + ct);
		// decryption
		Cipher cipher1 = Cipher.getInstance("AES/CBC/NoPadding");
		cipher1.init(Cipher.DECRYPT_MODE, KEY, ivspec);
		String decpt = new String(cipher1.doFinal(ct.getBytes()));
		System.out.println("Decrypted text : " + decpt);
		// padding check
		int l = decpt.length();
		char a = decpt.charAt(l - 1);
		System.out.println("Padding done at last byte:" + a);
		int i;
		for (i = l - 2; i > (l - Character.getNumericValue(a)); i--) {
			if (decpt.charAt(i) != a) {
				System.out.println("\nWrong padding");
				break;
			}

		}
		if (i == l - Character.getNumericValue(a)) {
			System.out.println("\nCorrect padding");
		}
	}

}