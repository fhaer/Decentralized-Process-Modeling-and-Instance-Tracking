package de.fha.dpmi.versioning;

import de.fha.dpmi.util.DpmiTooling;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * exception thrown during version graph handling
 */
public class VersionGraphCredentialException extends VersionGraphException implements DpmiTooling {

	private static final long serialVersionUID = 5375381512264906193L;

	public final static String COMMON_MESSAGE = "Exception while handling the Version Graph";

	public VersionGraphCredentialException() {
		super();
	}

	public VersionGraphCredentialException(String msg) {
		super(msg);
	}

	public VersionGraphCredentialException(Throwable th) {
		super(th);
	}

	public VersionGraphCredentialException(String msg, Throwable th) {
		super(msg, th);
	}

}
