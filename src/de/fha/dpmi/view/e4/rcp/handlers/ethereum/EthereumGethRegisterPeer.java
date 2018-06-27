package de.fha.dpmi.view.e4.rcp.handlers.ethereum;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import de.fha.dpmi.blockchain.EthereumException;
import de.fha.dpmi.blockchain.EthereumGethController;
import de.fha.dpmi.view.e4.rcp.parts.JavaFXUtil;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * menu entry for registering a new peer for participation in agreement
 * procedures with the ethereum smart contract
 */
public class EthereumGethRegisterPeer {

	@Execute
	public void execute(Shell shell) {
		EthereumGethController ethereumGeth = EthereumGethController.getInstance();
		if (ethereumGeth.isGethRunning() && ethereumGeth.getTransactionController().isReady()) {
			try {
				// ethereumGeth.getTransactionController().getEthereumSignalContract().deployContract();

				String address = ethereumGeth.getTransactionController().getConfiguration().getAddress();
				address = JavaFXUtil.inputBox("DPMI", "Register Address",
						"Enter Ethereum Address to Register for Voting", address);
				if (address.isEmpty())
					return;
				JavaFXUtil.infoBox("Sending Request, please wait ...");
				boolean suc = ethereumGeth.getTransactionController().getEthereumSignalContract().registerPeer(address);
				if (suc)
					JavaFXUtil.infoBox("Registration request sent");
				else
					JavaFXUtil.infoBox("Registration request failed, insufficient privileges");
			} catch (EthereumException e) {
				JavaFXUtil.showInfoException(e);
			}
		} else {
			JavaFXUtil.infoBox("Geth not running or not synchronized");
		}
	}
}
