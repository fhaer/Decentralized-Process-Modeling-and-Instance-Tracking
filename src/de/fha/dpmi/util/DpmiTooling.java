package de.fha.dpmi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import de.fha.dpmi.view.e4.rcp.parts.VersioningController;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Tooling class for cross-cutting tool functions, e.g. logging
 */
public interface DpmiTooling {

	static final Logger LOGGER = Logger.getLogger("DPMI Default");

	default void log(String msg) {
		LOGGER.info(msg);
		showStatusInUi(msg);
	}

	@Deprecated
	default void log(String msg, int x, int y) {
		// for compatibility with legacy code -- to be removed
		LOGGER.info(msg);
		showStatusInUi(msg);
	}

	default void showStatusInUi(String msg) {
		VersioningController vc = VersioningController.getInstance();
		if (vc != null) {
			String logString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")) + " " + msg;
			vc.showStatus(logString);
		}
	}

}
