package de.fha.dpmi.hashing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * calculates and verifies hash values for multiple files
 */
public class FileHash {

	/**
	 * calculate a SHA-256 hash values for multiple files by calculating
	 * individual hash values, concatenation of hash values, and calculation of
	 * a final hash value out of the concatenation
	 *
	 * @param files
	 *            list of file paths relative to baseDirectory
	 * @param baseDirectory
	 *            base directory for given files
	 * @return 32 byte hash value
	 * @throws FileHashException
	 * @throws HashValueException
	 */
	public static HashValue calculcateSha256(List<String> files, Path baseDirectory) throws FileHashException, HashValueException {

		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			throw new FileHashException("Unable to create file hash values: SHA-256 algorithm missing", e1);
		}

		Collections.sort(files);

		int hashLengthBytes = 32;
		byte[] hashValues = new byte[hashLengthBytes * files.size()];
		int i = 0;
		for (String file : files) {
			Path filePath = baseDirectory.resolve(file);
			byte[] hashValue = calculateSha256(messageDigest, filePath);
			System.arraycopy(hashValue, 0, hashValues, hashLengthBytes * i, hashLengthBytes);
			i++;
		}
		return new HashValue(messageDigest.digest(hashValues));
	}

	/**
	 * calculate a SHA-256 hash values for multiple files using
	 * calculateSha256(...) and compares the resulting value to a given hash
	 * value
	 *
	 * @param files
	 *            list of file paths relative to baseDirectory
	 * @param baseDirectory
	 *            base directory for given files
	 * @param hashValue
	 *            hash value used for the comparison
	 * @return true if calculated hash value matches given value, false
	 *         otherwise
	 * @throws FileHashException
	 * @throws FileHashException
	 * @throws HashValueException
	 */
	public static boolean verifyHashValue(List<String> files, Path baseDirectory, byte[] hashValue)
			throws FileHashException, FileHashException, HashValueException {
		byte[] calculatedValue = calculcateSha256(files, baseDirectory).getByteArray();
		return Arrays.equals(calculatedValue, hashValue);
	}

	/**
	 *
	 * calculate a SHA-256 hash value for the given file
	 *
	 * @param file
	 * @throws FileHashException
	 * @throws FileHashException
	 * @throws de.fha.dpmi.hashing.FileHashException
	 */
	public static byte[] calculateSha256(Path filePath) throws FileHashException, de.fha.dpmi.hashing.FileHashException {

		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			throw new FileHashException("Unable to create file hash values: SHA-256 algorithm missing", e1);
		}
		return calculateSha256(messageDigest, filePath);
	}

	private static byte[] calculateSha256(MessageDigest messageDigest, Path filePath) throws FileHashException {
		if (!Files.isReadable(filePath)) {
			throw new FileHashException("Unable to read file for hashing", filePath);
		}
		try {
			byte[] fileBytes = Files.readAllBytes(filePath);
			byte[] fileHashValue = messageDigest.digest(fileBytes);
			return fileHashValue;
		} catch (IOException e) {
			throw new FileHashException("IO error while read file for hashing", filePath);
		}
	}

	/**
	 * returns a string representation of a 32 byte hash value
	 *
	 * @param hashValue
	 *            array of 32 bytes
	 * @return string with hexadecimal representation
	 * @throws FileHashException
	 */
	public static String hashValueToHexSting(byte[] hashValue) throws FileHashException {
		if (hashValue.length != 32)
			throw new FileHashException("Hash value length is not 256 bit");
		return byteArrayToHexSting(hashValue);
	}

	/**
	 * returns a string representation of an arbitrary length byte array
	 *
	 * @param hashValue
	 *            array of bytes
	 * @return string with hexadecimal representation
	 * @throws FileHashException
	 */
	public static String byteArrayToHexSting(byte[] hashValue) {
		StringBuffer sb = new StringBuffer();
		for (byte b : hashValue) {
			String val = Integer.toHexString(0xff & b);
			if (val.length() < 2) {
				sb.append('0');
			}
			sb.append(val);
		}
		return sb.toString();
	}
}
