package de.fha.dpmi.hashing;

/**
 * Exception during hash calculation or hash value conversions
 */
public class HashValueException extends RuntimeException {

	private static final long serialVersionUID = -9004779688882073805L;

	public HashValueException() {
	}

	public HashValueException(String arg0) {
		super(arg0);
	}

	public HashValueException(Throwable arg0) {
		super(arg0);
	}

	public HashValueException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public HashValueException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
