package girish.security.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BreakRepeatingKeyXOR {

	static String hexLookUp = "0123456789abcdef",
			base64LookUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";;

	public static void main(String[] args) {
		convertBase64FileToHex();
		int probableKeyLength = findProbableKeyLength();
		List<String> blocks = breakCipherIntoBlocks(probableKeyLength);
		for (String s : blocks) {
			System.out.println(computeSingleByteXOR(s));
		}
	}

	static String computeSingleByteXOR(String hexCipherText) {
		Map<String, Double> finalScores = new LinkedHashMap<String, Double>();

		// For each character A-Z, XOR the cipher with an equal length repeating
		// character string.
		// Convert the XOR output to ASCII, and send the ASCII string to
		// computeScore to (surprise!) compute its score.
		for (int i = 0x41; i <= 0x5a; ++i) {
			String key = formRepeatingCharacterString(i, hexCipherText.length());
			String hexPlainText = computeXOR(hexCipherText, key);
			String output = computeHexToASCII(hexPlainText);
			finalScores.put(String.valueOf((char) i), computeScore(output.toString()));
		}

		// Do the same for a-z.
		for (int i = 0x61; i <= 0x7a; ++i) {
			String key = formRepeatingCharacterString(i, hexCipherText.length());
			String hexPlainText = computeXOR(hexCipherText, key);
			String output = computeHexToASCII(hexPlainText);
			finalScores.put(String.valueOf((char) i), computeScore(output));
		}

		// Do the same for 0-9.
		for (int i = 0x30; i <= 0x39; ++i) {
			String key = formRepeatingCharacterString(i, hexCipherText.length());
			String hexPlainText = computeXOR(hexCipherText, key);
			String output = computeHexToASCII(hexPlainText);
			finalScores.put(String.valueOf((char) i), computeScore(output));
		}

		// Iterate through the HashMap and display the keyCharacter, the
		// corresponding plainText and its score.
		Iterator<?> it = finalScores.entrySet().iterator();
		double maxScore = 0;
		String keyCharacter = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			maxScore = (maxScore > (double) pair.getValue() ? maxScore : (double) pair.getValue());
			keyCharacter = (maxScore > (double) pair.getValue() ? keyCharacter : (String) pair.getKey());
		}
		String key = formRepeatingCharacterString(keyCharacter, hexCipherText.length());
		String hexPlainText = computeXOR(hexCipherText, key);
		String originalPlainText = computeHexToASCII(hexPlainText);
		it.remove();
		return originalPlainText;
	}

	private static List<String> breakCipherIntoBlocks(int probableKeyLength) {
		File file = new File("6hex.txt");
		FileInputStream fileInputStream = null;
		byte fileContent[] = new byte[1];
		try {
			fileInputStream = new FileInputStream(file);
		} catch (IOException e) {
			System.out.println("IOException " + e.getMessage());
		}

		List<String> blocks = new ArrayList<String>();
		String s = "";
		for (int i = 0; i < probableKeyLength; ++i) {
			try {
				fileInputStream = new FileInputStream(file);
				for (int j = 0; j < i; ++j) {
					fileInputStream.read(fileContent);
					// System.out.println("skiped " + new String(fileContent));
				}
				while (fileInputStream.read(fileContent) >= 0) {
					String ch = new String(fileContent);
					s = s + ch;
					for (int c = 0; c < probableKeyLength - 1; ++c) {
						fileInputStream.read(fileContent);
					}
				}
			} catch (IOException e) {
				System.out.println("IOException " + e.getMessage());
			}
			try {
				fileInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println(s);
			blocks.add(s);
			s = "";
		}
		return blocks;
	}

	private static int findProbableKeyLength() {
		File file = new File("6hex.txt");
		FileInputStream fileInputStream = null;

		int len = 0;
		double min = 0.0d;

		for (int l = 2; l <= 40; ++l) {
			try {
				fileInputStream = new FileInputStream(file);
				byte fileContent[] = new byte[l];
				fileInputStream.read(fileContent);
				String s1 = new String(fileContent);

				fileContent = new byte[l];
				fileInputStream.read(fileContent);
				String s2 = new String(fileContent);
				double val = (double) computeHammingDistance(s1, s2) / (double) l;
				// DecimalFormat df = new DecimalFormat("#.0000");
				if (min == 0.0)
					min = val;
				if (val < min) {
					min = val;
					len = l;
				}
				// System.out.println(l + " " + s1 + " " + s2 + " " +
				// computeHammingDistance(s1, s2) + " " + df.format(val));
			} catch (Exception e) {
				System.out.println("Exception " + e.toString());
			}
			try {
				if (fileInputStream != null)
					fileInputStream.close();
			} catch (Exception e) {
				System.out.println("Exception " + e.toString());
			}
		}
		return len;
	}

	/**
	 * 
	 * @return
	 */
	private static void convertBase64FileToHex() {
		String inFilename = "6.txt";
		Path inPath = new File(inFilename).toPath();
		String outFilename = "6hex.txt";
		Path outPath = new File(outFilename).toPath();
		File outFile = new File(outFilename);

		try {
			outFile.createNewFile();
			/*
			 * if (!isInSecureDir(path)) { System.out.println(
			 * "File not in secure directory"); return; }
			 */
			BasicFileAttributes inAttr = Files.readAttributes(inPath, BasicFileAttributes.class,
					LinkOption.NOFOLLOW_LINKS);
			BasicFileAttributes outAttr = Files.readAttributes(outPath, BasicFileAttributes.class,
					LinkOption.NOFOLLOW_LINKS);

			// Check if the file is a regular file and not a FIFO file or a
			// device file.
			if (!inAttr.isRegularFile() || !outAttr.isRegularFile()) {
				System.out.println("Not a regular file");
				return;
			}
			try {
				FileReader fr = new FileReader(inFilename);
				BufferedReader br = new BufferedReader(fr);
				FileWriter fileWriter = new FileWriter(outFile);
				String s;

				while ((s = br.readLine()) != null && s.length() == 60) {
					s = convertBase64ToHex(s);
					fileWriter.write(s + "\n");
				}
				fileWriter.write(convertBase64ToHex(s.substring(0, s.length() - 2)));
				fileWriter.close();
				br.close();
				fr.close();
			} catch (IOException e) {
			}
		} catch (IOException x) {
			// Handle error
			return;
		}
	}

	public static String convertBase64ToHex(String base64) {
		String hex = "";
		for (int i = 0; i < base64.length(); ++i) {
			// Take first 6 bits (from the first base64 digit) and call
			// convert() with the first 4 bits.
			String firstDigit = String.format("%06d",
					Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i++)))));
			hex = hex + convert(firstDigit.substring(0, 4));

			// Take the remaining 2 bits (from the first base64 digit) and the
			// first 2 bits of the second base64 digit and call convert()
			String secondDigit = String.format("%06d",
					Integer.parseInt(Integer.toBinaryString(base64LookUp.indexOf(base64.charAt(i)))));
			hex = hex + convert(firstDigit.substring(4, 6) + secondDigit.substring(0, 2));

			hex = hex + convert(secondDigit.substring(2, 6));
		}
		return hex;
	}

	/*
	 * 'bits' will be a 4 bit sequence which will be converted to the
	 * corresponding hex value. Each hex digit is appended to the string 'hex'.
	 */
	static String convert(String bits) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.valueOf(hexLookUp.charAt(Integer.parseInt(bits, 2))));
		String hex = stringBuilder.toString();
		return hex;
	}

	/**
	 * This function computes the Hamming distance between 2 strings.
	 */
	public static int computeHammingDistance(String s1, String s2) {
		String hexS1 = asciiToHex(s1);
		String hexS2 = asciiToHex(s2);
		String binaryResult = computeXOR(hexS1, hexS2);
		return findOnesInBinaryString(binaryResult);
	}

	/**
	 * This function computes the number of 1s in a binary string.
	 */
	public static int findOnesInBinaryString(String s) {
		char[] string = s.toCharArray();
		int count = 0;
		for (char c : string) {
			if (c == '1')
				count++;
		}
		return count;
	}

	/**
	 * This function computes the ASCII equivalent of a HEX String.
	 */
	public static String asciiToHex(String ascii) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < ascii.length(); ++i) {
			buffer.append(Integer.toHexString((int) ascii.charAt(i)));
		}
		return buffer.toString();
	}

	/**
	 * This function computes character by character XOR for the two input HEX
	 * strings returns an output string in Binary.
	 */
	static String computeXOR(String first, String second) {
		String result = "";
		StringBuilder stringBuilder = new StringBuilder(result);

		// I'm going character by character here. ^ does the XOR.
		for (int i = 0; i < first.length(); ++i) {
			int cc = Integer.parseInt(String.valueOf(first.charAt(i)), 16)
					^ Integer.parseInt(String.valueOf(second.charAt(i)), 16);
			stringBuilder.append(Integer.toBinaryString(cc));
		}
		result = stringBuilder.toString();
		return result;
	}

	/*
	 * Given a HEX character, this function forms a String by repeating it 30
	 * times.
	 */
	static String formRepeatingCharacterString(int i, int num) {
		String key = "";
		for (int j = 0; j < num; ++j) {
			key = key + Integer.toHexString(i);
		}
		return key;
	}

	static String formRepeatingCharacterString(String keyCharacter, int num) {
		String key = "";
		for (int j = 0; j < num; ++j) {
			key = key + Integer.toHexString((int) keyCharacter.charAt(0));
		}
		return key;
	}

	/*
	 * This function computes the ASCII equivalent of a HEX String.
	 */
	static String computeHexToASCII(String hexPlainText) {
		StringBuilder output = new StringBuilder();
		for (int k = 0; k < hexPlainText.length(); k += 2) {
			String str = hexPlainText.substring(k, k + 2);
			output.append((char) Integer.parseInt(str, 16));
		}
		return output.toString();
	}

	/*
	 * ETAOIN SHRDLU - The most frequent English alphabet list. I'm giving E a
	 * score of 13, decreasing up to 1 for U. I'm not scoring any other
	 * alphabets. (And for this case, it turns out you don't have to!)
	 * 
	 * This function returns the final score of the input string, after scoring
	 * each character in it, based on this scheme.
	 */
	static double computeScore(String s) {
		double score = 0;
		for (int i = 0; i < s.length(); ++i) {
			if (s.charAt(i) == 'e' || s.charAt(i) == 'E') {
				score += 13;
			} else if (s.charAt(i) == 't' || s.charAt(i) == 'T') {
				score += 12;
			} else if (s.charAt(i) == 'a' || s.charAt(i) == 'A') {
				score += 11;
			} else if (s.charAt(i) == 'o' || s.charAt(i) == 'O') {
				score += 10;
			} else if (s.charAt(i) == 'i' || s.charAt(i) == 'I') {
				score += 9;
			} else if (s.charAt(i) == 'n' || s.charAt(i) == 'N') {
				score += 8;
			} else if (s.charAt(i) == ' ') {
				score += 7;
			} else if (s.charAt(i) == 's' || s.charAt(i) == 'S') {
				score += 6;
			} else if (s.charAt(i) == 'h' || s.charAt(i) == 'H') {
				score += 5;
			} else if (s.charAt(i) == 'r' || s.charAt(i) == 'R') {
				score += 4;
			} else if (s.charAt(i) == 'd' || s.charAt(i) == 'D') {
				score += 3;
			} else if (s.charAt(i) == 'l' || s.charAt(i) == 'L') {
				score += 2;
			} else if (s.charAt(i) == 'u' || s.charAt(i) == 'U') {
				score += 1;
			}
		}
		return score;
	}

	/*
	 * The code below has been taken from https://securecoding.cert.org
	 * 
	 * It’s not secure to operate on files that don’t belong to what are known
	 * as secure directories – a secure directory is one in which only the
	 * current user and the system administrator can create/modify/delete files
	 * in it – any shared directory is not secure. The following function is
	 * used to determine if a file belongs to a secure directory. We can only
	 * work on that file if this function returns true, meaning that it belongs
	 * to a secure directory.
	 */

	/**
	 * @reference https://securecoding.cert.org/confluence/display/java/FIO00-J.
	 *            +Do+not+operate+on+files+in+shared+directories Indicates
	 *            whether file lives in a secure directory relative to the
	 *            program's user
	 * @param file
	 *            Path to test
	 * @return true if file's directory is secure.
	 */
	public static boolean isInSecureDir(Path file) {
		return isInSecureDir(file, null);
	}

	/**
	 * @reference https://securecoding.cert.org/confluence/display/java/FIO00-J.
	 *            +Do+not+operate+on+files+in+shared+directories Indicates
	 *            whether file lives in a secure directory relative to the
	 *            program's user
	 * @param file
	 *            Path to test
	 * @param user
	 *            User to test. If null, defaults to current user
	 * @return true if file's directory is secure.
	 */
	public static boolean isInSecureDir(Path file, UserPrincipal user) {
		return isInSecureDir(file, user, 5);
	}

	/**
	 * @reference https://securecoding.cert.org/confluence/display/java/FIO00-J.
	 *            +Do+not+operate+on+files+in+shared+directories Indicates
	 *            whether file lives in a secure directory relative to the
	 *            program's user
	 * @param file
	 *            Path to test
	 * @param user
	 *            User to test. If null, defaults to current user
	 * @param symlinkDepth
	 *            Number of symbolic links allowed
	 * @return true if file's directory is secure.
	 */
	public static boolean isInSecureDir(Path file, UserPrincipal user, int symlinkDepth) {
		if (!file.isAbsolute()) {
			file = file.toAbsolutePath();
		}
		if (symlinkDepth <= 0) {
			System.out.println(" Too many levels of symbolic links");
			return false;
		}

		// Get UserPrincipal for specified user and superuser
		FileSystem fileSystem = Paths.get(file.getRoot().toString()).getFileSystem();
		UserPrincipalLookupService upls = fileSystem.getUserPrincipalLookupService();
		UserPrincipal root = null;
		try {
			root = upls.lookupPrincipalByName("root");
			if (user == null) {
				user = upls.lookupPrincipalByName(System.getProperty("user.name"));
			}
			if (root == null || user == null) {
				System.out.println("User or Root is null");
				return false;
			}
		} catch (IOException x) {
			System.out.println("IOException1 " + x.getMessage());
			return false;
		}

		// If any parent dirs (from root on down) are not secure,
		// dir is not secure
		for (int i = 1; i <= file.getNameCount(); i++) {
			Path partialPath = Paths.get(file.getRoot().toString(), file.subpath(0, i).toString());

			try {
				if (Files.isSymbolicLink(partialPath)) {
					if (!isInSecureDir(Files.readSymbolicLink(partialPath), user, symlinkDepth - 1)) {
						System.out.println("Symbolic link, linked-to dir not secure");
						return false;
					}
				} else {
					UserPrincipal owner = Files.getOwner(partialPath);
					if (!user.equals(owner) && !root.equals(owner)) {
						System.out.println("dir owned by someone else, not secure");
						return false;
					}
					PosixFileAttributes attr = Files.readAttributes(partialPath, PosixFileAttributes.class);
					Set<PosixFilePermission> perms = attr.permissions();
					if (perms.contains(PosixFilePermission.GROUP_WRITE)
							|| perms.contains(PosixFilePermission.OTHERS_WRITE)) {
						System.out.println("Someone else can write files, not secure");
						return false;
					}
				}
			} catch (IOException x) {
				System.out.println("IOException" + x.getMessage());
				return false;
			}
		}

		return true;
	}

}