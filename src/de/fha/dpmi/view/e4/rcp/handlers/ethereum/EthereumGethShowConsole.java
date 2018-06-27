package de.fha.dpmi.view.e4.rcp.handlers.ethereum;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import de.fha.dpmi.view.e4.rcp.parts.GethDialogConsole;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * menu entry to show the geth ethereum client console
 */
public class EthereumGethShowConsole {

	@Execute
	public void execute(Shell shell) {
		GethDialogConsole gcd = new GethDialogConsole();
		gcd.show();
	}
}
