package de.fha.dpmi.view.e4.rcp.parts;

import de.fha.dpmi.blockchain.EthereumGethController;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Geth console dialog
 */
public class GethDialogConsole {

	private TextArea textArea = new TextArea();

	public GethDialogConsole() {
		EthereumGethController ethereumGeth = EthereumGethController.getInstance();
		for (String s : ethereumGeth.getGethOutput()) {
			textArea.appendText(s);
			textArea.appendText(System.lineSeparator());
		}
		ethereumGeth.registerStdOutTextArea(textArea);
	}

	public void show() {
		EthereumGethController ethereumGeth = EthereumGethController.getInstance();
		if (ethereumGeth.isGethRunning()) {
			showConsole();
		} else {
			JavaFXUtil.showFailure("Geth is not running");
		}
	}

	private void showConsole() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Geth");
		alert.setHeaderText("Geth Console");

		alert.getDialogPane().setPrefWidth(1300);
		alert.getDialogPane().setPrefHeight(700);

		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane pane = new GridPane();
		pane.setMaxWidth(Double.MAX_VALUE);
		pane.add(textArea, 0, 1);

		alert.getDialogPane().setContent(pane);

		alert.show();
	}

}
