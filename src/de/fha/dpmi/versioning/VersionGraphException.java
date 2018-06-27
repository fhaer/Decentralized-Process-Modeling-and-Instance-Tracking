package de.fha.dpmi.versioning;

import de.fha.dpmi.util.DpmiException;
import de.fha.dpmi.util.DpmiTooling;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Exception thrown during version control operations
 */
public class VersionGraphException extends DpmiException implements DpmiTooling {

	private static final long serialVersionUID = 5375381512264906193L;

	public final static String COMMON_MESSAGE = "Exception while handling the Version Graph";

	public VersionGraphException() {
		super();
	}

	public VersionGraphException(String msg) {
		super(msg);
	}

	public VersionGraphException(Throwable th) {
		super(th);
	}

	public VersionGraphException(String msg, Throwable th) {
		super(msg, th);
	}

}
