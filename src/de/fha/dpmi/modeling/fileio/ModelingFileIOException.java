package de.fha.dpmi.modeling.fileio;

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
 * Exception thrown during file input/output of model files
 */
public class ModelingFileIOException extends DpmiException implements DpmiTooling {

	private File file;

	private static final long serialVersionUID = 5375381212264906193L;

	public final static String COMMON_MESSAGE = "Version Graph handling";

	public ModelingFileIOException(String msg, File f) {
		super(msg);
		file = f;
	}

	public ModelingFileIOException(String msg, Path f) {
		super(msg);
		file = f.toFile();
	}

	public ModelingFileIOException(Throwable th, File f) {
		super(th);
		file = f;
	}

	public ModelingFileIOException(Throwable th, Path f) {
		super(th);
		file = f.toFile();
	}

	public ModelingFileIOException(String msg, Throwable th, File f) {
		super(msg, th);
		file = f;
	}

	public ModelingFileIOException(String msg, Throwable th, Path f) {
		super(msg, th);
		file = f.toFile();
	}

	public ModelingFileIOException(String string) {
		super(string);
	}

	public ModelingFileIOException(String string, Throwable th) {
		super(string, th);
	}

	public File getFile() {
		return file;
	}
}
