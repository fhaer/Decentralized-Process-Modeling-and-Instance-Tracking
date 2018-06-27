package de.fha.dpmi.view.e4.rcp.handlers.ethereum;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import de.fha.dpmi.view.e4.rcp.parts.GethDialogStatus;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Menu entry to show information about the ethereum blockchain synchronization
 * status and account information
 */
public class EthereumGethShowStatus {

	@Execute
	public void execute(Shell shell) {
		GethDialogStatus gsd = new GethDialogStatus();
		gsd.show();
	}
}
