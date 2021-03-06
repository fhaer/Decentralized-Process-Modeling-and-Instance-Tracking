package de.fha.dpmi.view.e4.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import de.fha.dpmi.view.e4.rcp.parts.VersioningGraphSelectorController;
/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix H�rer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * File open menu entry
 */
public class OpenHandler {

	@Execute
	public void execute(Shell shell) {
		// FileDialog dialog = new FileDialog(shell);
		// dialog.open();
		VersioningGraphSelectorController.getInstance().btAddVersionGraphClicked();
	}
}
