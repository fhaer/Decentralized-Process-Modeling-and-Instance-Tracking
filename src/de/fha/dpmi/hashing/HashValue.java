package de.fha.dpmi.hashing;

/**
 * represents a hash value of 32 bit
 */
public class HashValue {

	final int LEN_BYTE = 32;

	private String hashValue;

	public HashValue(byte[] hashValue) throws HashValueException {
		if (hashValue.length > LEN_BYTE)
			throw new HashValueException("Hash value exceeds maximum length");
		String value = byteArrayToHexSting(hashValue);
		this.hashValue = padHashValue(value);
	}

	/**
	 * constructs a hash value from a hexadecimal string
	 *
	 * @param hashValue
	 * @throws HashValueException
	 */
	public HashValue(String hashValue) throws HashValueException {
		if (hashValue.length() > LEN_BYTE * 2)
			throw new HashValueException("Hash value exceeds maximum length");
		this.hashValue = padHashValue(hashValue);
	}

	private String padHashValue(String hashValue) {
		String value = hashValue.toUpperCase();
		if (value.length() < LEN_BYTE * 2)
			value = new String(new char[LEN_BYTE * 2 - value.length()]).replace('\0', '0') + value;
		return value;
	}

	@Override
	public String toString() {
		return hashValue;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HashValue)
			return ((HashValue) obj).hashValue.equalsIgnoreCase(hashValue);
		// if (obj instanceof HashValue)
		// return Arrays.equals(((HashValue) obj).hashValue, hashValue);
		return false;
	}

	/**
	 * returns a string representation of an arbitrary length byte array
	 *
	 * @param hashValue
	 *            array of bytes
	 * @return string with hexadecimal representation
	 * @throws ModelingFileIOException
	 */
	public String byteArrayToHexSting(byte[] hashValue) {
		StringBuffer sb = new StringBuffer();
		for (byte b : hashValue) {
			String val = Integer.toHexString(0xff & b);
			if (val.length() < 2) {
				sb.append('0');
			}
			sb.append(val);
		}
		return sb.toString().toUpperCase();
	}

	public byte[] getByteArray() {
		int len = hashValue.length();
		byte[] hashValueBytes = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			hashValueBytes[i / 2] = (byte) ((Character.digit(hashValue.charAt(i), 16) << 4)
					+ Character.digit(hashValue.charAt(i + 1), 16));
		}
		return hashValueBytes;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public String getDisplayString() {
		return "0x0" + hashValue.replaceFirst("^0+(?!$)", "").toLowerCase();
	}
}
