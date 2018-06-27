package de.fha.dpmi.hashing;

import java.nio.file.Path;

public class FileHashException extends Exception {

	private static final long serialVersionUID = -1792530265466015078L;

	public FileHashException() {
	}

	public FileHashException(String arg0) {
		super(arg0);
	}

	public FileHashException(Throwable arg0) {
		super(arg0);
	}

	public FileHashException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public FileHashException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public FileHashException(String string, Path filePath) {
		super(string);
	}

}
