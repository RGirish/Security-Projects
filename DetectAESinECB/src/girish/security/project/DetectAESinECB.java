package girish.security.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DetectAESinECB {

	private static ArrayList<String> blocks = new ArrayList<>();
	private static int lineNumber = 0, minUniqueBlocks = 0;
	private static String theLine = "";

	public static void main(String[] args) {
		String fileName = "8.txt";
		File file = new File(fileName);
		/*
		 * FIO02-J. Detect and handle file-related errors
		 * 
		 * @reference
		 * https://www.securecoding.cert.org/confluence/display/java/FIO02-J.+
		 * Detect+and+handle+file-related+errors
		 */
		if (file.exists()) {
			if (!file.isDirectory()) {
				// File exists and it is not a directory. Now we can start
				// working with it.

				if (file.canRead()) {
					try {
						FileReader fileReader = new FileReader(file);
						BufferedReader bufferedReader = new BufferedReader(fileReader);

						String line = "";
						int count = 0;
						while ((line = bufferedReader.readLine()) != null) {
							try {
								blocks.clear();
								for (int index = 15; true; index = index + 16) {
									String aBlock = line.substring(index - 15, index);
									if (!blocks.contains(aBlock)) {
										blocks.add(aBlock);
									}
								}
							} catch (StringIndexOutOfBoundsException e) {
							}
							if (minUniqueBlocks == 0 || blocks.size() < minUniqueBlocks) {
								minUniqueBlocks = blocks.size();
								lineNumber = count;
								theLine = line;
							}
							count++;
						}

						bufferedReader.close();
						fileReader.close();

						System.out.println("The line numbered " + lineNumber + " as the least unique blocks("
								+ minUniqueBlocks + "). So it must have been ECB'd (WHP).\n\nThe line is " + theLine);

					} catch (FileNotFoundException e) {
						// Wont come to this as we're already checking if file
						// exists or not
					} catch (IOException e) {
						System.out.println("IO Exception in trying to read File. Message is : " + e.getMessage());
					}
				} else {
					System.out.println("File cannot be read by the applicaiton!");
				}

			} else{
				System.out.println("The given name is that of a Directory, and not a file.");
			}
		} else {
			System.out.println("The file does not exist.");
		}
	}
	
	/*
	 * Cloning is disabled for security reasons.
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public final Object clone() throws java.lang.CloneNotSupportedException {
		throw new java.lang.CloneNotSupportedException();
	}

	/*
	 * Object Serialization is disabled for security reasons.
	 */
	private final void writeObject(ObjectOutputStream out) throws java.io.IOException {
		throw new java.io.IOException("Object cannot be serialized");
	}

}