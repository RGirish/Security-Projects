package girish.security.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Set;

public class BreakRepeatingKeyXOR {

	public static void main(String[] args) {

		convertBase64FileToHex();

		File file = new File("6.txt");
		FileInputStream fileInputStream = null;
		for (int l = 2; l <= 40; ++l) {
			try {
				fileInputStream = new FileInputStream(file);
				byte fileContent[] = new byte[l];
				fileInputStream.read(fileContent);
				String s1 = new String(fileContent);

				fileContent = new byte[l];
				fileInputStream.read(fileContent);
				String s2 = new String(fileContent);

				System.out.println(Double.valueOf(computeHammingDistance(s1, s2) / l));
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

	}

	private static void convertBase64FileToHex() {

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
			// Too many levels of symbolic links
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
				return false;
			}
		} catch (IOException x) {
			return false;
		}

		// If any parent dirs (from root on down) are not secure,
		// dir is not secure
		for (int i = 1; i <= file.getNameCount(); i++) {
			Path partialPath = Paths.get(file.getRoot().toString(), file.subpath(0, i).toString());

			try {
				if (Files.isSymbolicLink(partialPath)) {
					if (!isInSecureDir(Files.readSymbolicLink(partialPath), user, symlinkDepth - 1)) {
						// Symbolic link, linked-to dir not secure
						return false;
					}
				} else {
					UserPrincipal owner = Files.getOwner(partialPath);
					if (!user.equals(owner) && !root.equals(owner)) {
						// dir owned by someone else, not secure
						return false;
					}
					PosixFileAttributes attr = Files.readAttributes(partialPath, PosixFileAttributes.class);
					Set<PosixFilePermission> perms = attr.permissions();
					if (perms.contains(PosixFilePermission.GROUP_WRITE)
							|| perms.contains(PosixFilePermission.OTHERS_WRITE)) {
						// Someone else can write files, not secure
						return false;
					}
				}
			} catch (IOException x) {
				return false;
			}
		}

		return true;
	}

}