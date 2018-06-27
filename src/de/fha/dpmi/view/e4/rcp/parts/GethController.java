package de.fha.dpmi.view.e4.rcp.parts;

import de.fha.dpmi.blockchain.EthereumGethController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * UI Controller class for the geth ethereum client
 */
public class GethController extends JavaFXController {

	@FXML
	private TextArea taGeth;

	@FXML
	private Button btStartGeth;

	@FXML
	private Button btStopGeth;

	@FXML
	private Button btIdentity;

	@FXML
	public void initialize() {
	}

	@Override
	protected void create() {
		EthereumGethController ethereumGeth = EthereumGethController.getInstance();
		for (String s : ethereumGeth.getGethOutput()) {
			taGeth.appendText(s);
			taGeth.appendText(System.lineSeparator());
		}
		ethereumGeth.registerStdOutTextArea(taGeth);
	}

	@FXML
	protected synchronized void btStartGethClicked() {
		EthereumGethController ethereumGeth = EthereumGethController.getInstance();
		if (!ethereumGeth.isGethRunning()) {
			ethereumGeth.startGeth();
		} else {
			JavaFXUtil.infoBox("Geth is already running");
		}
	}

	@FXML
	protected synchronized void btStopGethClicked() {
		EthereumGethController ethereumGeth = EthereumGethController.getInstance();
		if (ethereumGeth.isGethRunning()) {
			ethereumGeth.stopGeth();
			if (!ethereumGeth.isGethRunning()) {
				JavaFXUtil.infoBox("Geth stopped");
			}
		} else {
			JavaFXUtil.infoBox("Geth not running");
		}
	}

	@FXML
	protected synchronized void btIdentityClicked() {
		GethDialogStatus gsd = new GethDialogStatus();
		gsd.show();
	}

	@Override
	public Button[] getButtons() {
		Button[] b = { btIdentity, btStartGeth, btStopGeth };
		return b;
	}
}
