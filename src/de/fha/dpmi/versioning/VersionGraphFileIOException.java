package de.fha.dpmi.versioning;

import java.io.File;
import java.nio.file.Path;

import de.fha.dpmi.util.DpmiException;
import de.fha.dpmi.util.DpmiTooling;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * exception thrown during reading/writing version graphs from/to disk
 */
public class VersionGraphFileIOException extends DpmiException implements DpmiTooling {

	private File file;

	private static final long serialVersionUID = 5375381212264906193L;

	public final static String COMMON_MESSAGE = "Version Graph handling";

	public VersionGraphFileIOException(String msg, File f) {
		super(msg);
		file = f;
	}

	public VersionGraphFileIOException(String msg, Path f) {
		super(msg);
		file = f.toFile();
	}

	public VersionGraphFileIOException(Throwable th, File f) {
		super(th);
		file = f;
	}

	public VersionGraphFileIOException(Throwable th, Path f) {
		super(th);
		file = f.toFile();
	}

	public VersionGraphFileIOException(String msg, Throwable th, File f) {
		super(msg, th);
		file = f;
	}

	public VersionGraphFileIOException(String msg, Throwable th, Path f) {
		super(msg, th);
		file = f.toFile();
	}

	public VersionGraphFileIOException(String string) {
		super(string);
	}

	public VersionGraphFileIOException(String string, Throwable th) {
		super(string, th);
	}

	public File getFile() {
		return file;
	}
}
