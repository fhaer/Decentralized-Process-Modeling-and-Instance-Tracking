package de.fha.dpmi.view.e4.rcp.handlers.ethereum;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import de.fha.dpmi.blockchain.EthereumGethController;
import de.fha.dpmi.view.e4.rcp.parts.JavaFXUtil;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix H�rer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Menu entry to start the geth ethereum client
 */
public class EthereumGethStart {

	@Execute
	public void execute(Shell shell) {
		EthereumGethController ethereumGeth = EthereumGethController.getInstance();
		if (!ethereumGeth.isGethRunning()) {
			ethereumGeth.startGeth();
		} else {
			JavaFXUtil.infoBox("Geth is already running");
		}
	}
}
