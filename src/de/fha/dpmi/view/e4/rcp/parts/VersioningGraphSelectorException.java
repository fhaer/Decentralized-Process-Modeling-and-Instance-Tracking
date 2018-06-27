package de.fha.dpmi.view.e4.rcp.parts;

import de.fha.dpmi.util.DpmiException;
import de.fha.dpmi.util.DpmiTooling;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * exception thrown during adding, removing and selecting version graphs
 */
public class VersioningGraphSelectorException extends DpmiException implements DpmiTooling {

	private static final long serialVersionUID = 5375381212264906193L;

	public final static String COMMON_MESSAGE = "add Version Graph";

	public VersioningGraphSelectorException() {
		super();
	}

	public VersioningGraphSelectorException(String msg) {
		super(msg);
	}

	public VersioningGraphSelectorException(Throwable th) {
		super(th);
	}

	public VersioningGraphSelectorException(String msg, Throwable th) {
		super(msg, th);
	}

}
