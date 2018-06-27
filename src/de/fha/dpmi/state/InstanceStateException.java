package de.fha.dpmi.state;

import de.fha.dpmi.util.DpmiException;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Exception during instance state handling
 */
public class InstanceStateException extends DpmiException {

	private static final long serialVersionUID = -3580625916609045268L;

	public InstanceStateException() {
	}

	public InstanceStateException(String arg0) {
		super(arg0);
	}

	public InstanceStateException(Throwable arg0) {
		super(arg0);
	}

	public InstanceStateException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
