package de.fha.dpmi.util;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Super class for exceptions related to DPMI
 */
public abstract class DpmiException extends Exception implements DpmiTooling {

	private static final long serialVersionUID = 5475381512264906193L;

	public final static String COMMON_MESSAGE = "Exception";

	public DpmiException() {
		log(COMMON_MESSAGE);
	}

	public DpmiException(String msg) {
		super(msg);
		log(COMMON_MESSAGE + ": " + msg);
	}

	public DpmiException(Throwable th) {
		super(th);
		log(COMMON_MESSAGE + " / Chained Message: " + th.getLocalizedMessage());
	}

	public DpmiException(String msg, Throwable th) {
		super(msg, th);
		log(COMMON_MESSAGE + ": " + msg + " / Chained Message: " + th.getLocalizedMessage());
	}

	public String getCommonMessage() {
		return COMMON_MESSAGE;
	}

}
