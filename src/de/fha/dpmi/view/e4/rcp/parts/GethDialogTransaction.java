package de.fha.dpmi.view.e4.rcp.parts;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import de.fha.dpmi.blockchain.EthereumConfiguration;
import de.fha.dpmi.blockchain.EthereumGethController;
import de.fha.dpmi.blockchain.TransactionResultAbsAsync;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Displays information on an outgoing ethereum transaction
 */
public class GethDialogTransaction {

	/**
	 * dialog can be closed after this timeout if transaction does not go
	 * through
	 */
	public static final int TIMEOUT_MS = 600000;

	private static final Pattern HEX_STRING_PATTERN = Pattern.compile("0x[0-9A-Fa-f]+");
	private static final int ETHEREUM_TRANSACTION_HASH_LENGTH = 66;
	private static final int ETHEREUM_ADDRESS_LENGTH = 42;

	private Alert alertDialog;
	private TransactionResultAbsAsync transactionResult;
	private EthereumConfiguration ethereumConfiguration;

	public GethDialogTransaction(String headerText, TransactionResultAbsAsync transactionResult,
			EthereumConfiguration ethereumConfiguration) {
		this.transactionResult = transactionResult;
		this.ethereumConfiguration = ethereumConfiguration;

		alertDialog = new Alert(AlertType.INFORMATION);
		alertDialog.setTitle("Ethereum Transaction");
		alertDialog.setHeaderText(headerText);

		GridPane lvPane = new GridPane();
		int width = 800;
		lvPane.setPrefSize(width, 450);
		lvPane.setVgap(10);
		ListView<String> lv = new ListView<>();
		lv.setItems(transactionResult.getTransactionLogList());
		lv.setPrefSize(width, 500);
		lv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		JavaFXUtil.listViewEnableClipboard(lv);
		listViewEnableHashLinks(lv, ethereumConfiguration);
		lvPane.add(lv, 0, 0);
		alertDialog.getDialogPane().setContent(lvPane);
		setClosable(false);
		Task<Void> sleeper = new Task<Void>() {
			protected Void call() throws Exception {
				Thread.sleep(TIMEOUT_MS);
				setClosable(true);
				// Platform.runLater( () -> {
				// list.add("Timeout reached");
				// });
				return null;
			}
		};
		Thread t = new Thread(sleeper);
		t.start();
		alertDialog.getDialogPane().setUserData(t);
		alertDialog.setOnHiding(r -> {
			EthereumGethController.getInstance().getTransactionController().unscribeTransactionConfirmation();
		});
	}

	public void show(String... message) {
		alertDialog.show();
		for (String m : message)
			transactionResult.logString(m);
		transactionResult.logString("Sending Ethereum transaction to " + transactionResult.getToAddress());
		transactionResult.logString("Waiting up to 10 minutes for inclusion in block ...");
	}

	private static void listViewEnableHashLinks(ListView<String> lv, EthereumConfiguration ethereumConfiguration) {
		lv.setOnMouseClicked(e -> {
			String item = lv.getSelectionModel().getSelectedItem();
			Matcher m = HEX_STRING_PATTERN.matcher(item);
			try {
				boolean found = m.find();
				if (found && m.end() - m.start() == ETHEREUM_ADDRESS_LENGTH) {
					java.awt.Desktop.getDesktop()
							.browse(java.net.URI.create(ethereumConfiguration.getBlockExplorerAddress() + m.group()));
				}
				if (found && m.end() - m.start() == ETHEREUM_TRANSACTION_HASH_LENGTH) {
					java.awt.Desktop.getDesktop().browse(
							java.net.URI.create(ethereumConfiguration.getBlockExplorerTransaction() + m.group()));
				}
			} catch (IOException e1) {
			}
		});
	}

	public void logSuccess(TransactionReceipt receipt) {
		transactionResult.logString("Transaction sent successfully");
		setClosable(true);
		String txHash = receipt.getTransactionHash();
		transactionResult.logString("Transaction hash: " + txHash);
		transactionResult.logString("Transaction included in block number " + receipt.getBlockNumber());
		transactionResult.logString("Gas used: " + receipt.getGasUsed());
		transactionResult.logString("Gas limit: " + ethereumConfiguration.getGasLimit());
		LongProperty nConfirmations = EthereumGethController.getInstance().getTransactionController()
				.getTransactionConfirmationsProperty(txHash);
		transactionResult.logProperty("Number of confirmations: ", nConfirmations);

	}

	public void logFailure(TransactionReceipt receipt) {
		transactionResult.logString("Failed to send transaction");
		setClosable(true);
		transactionResult.logString("Transaction hash: " + receipt.getTransactionHash());
		transactionResult.logString("Gas used: " + receipt.getGasUsed());
		transactionResult.logString("Gas limit: " + ethereumConfiguration.getGasLimit());
	}

	public void setClosable(boolean closable) {
		Platform.runLater(() -> {
			try {
				for (Node n : alertDialog.getDialogPane().getChildren()) {
					if (n instanceof ButtonBar) {
						n.setDisable(!closable);
					}
				}
				if (closable) {
					alertDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> {
					});
				} else {
					alertDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> e.consume());
				}
			} catch (Exception e) {
				// dialog already closed
			}
		});
	}

}
