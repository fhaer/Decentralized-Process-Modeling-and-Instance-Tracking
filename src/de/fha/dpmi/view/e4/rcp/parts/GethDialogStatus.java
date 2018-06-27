package de.fha.dpmi.view.e4.rcp.parts;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import de.fha.dpmi.blockchain.EthereumConfiguration;
import de.fha.dpmi.blockchain.EthereumException;
import de.fha.dpmi.blockchain.EthereumGethController;
import de.fha.dpmi.blockchain.EthereumTransactionController;
import de.fha.dpmi.blockchain.TransactionResultPeer;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Displays ethereum blockchain synchronization status of the geth client and
 * account information
 */
public class GethDialogStatus {

	public void show() {
		EthereumGethController ethereumGeth = EthereumGethController.getInstance();
		if (!ethereumGeth.isGethRunning()) {
			JavaFXUtil.showFailure("Geth is not running");
			return;
		}
		EthereumTransactionController etc = ethereumGeth.getTransactionController();
		try {
			String syncStatus = etc.isReady() + "";
			String syncError = etc.getSynchronizationHasError() + "";
			EthereumConfiguration config = etc.getConfiguration();
			String useTestnet = config.USE_TESTNET + "";
			String gethHost = EthereumConfiguration.GETH_RPC_HTTP_IP;
			String gethPort = EthereumConfiguration.GETH_RPC_HTTP_PORT + "";
			String syncMode = EthereumConfiguration.GETH_SYNCMODE + "";
			String gasLimit = config.getGasLimit().toString();
			String gasPrice = config.getGasPrice().toString();
			if (etc.isReady()) {
				String address = etc.getAddress();
				BigDecimal balance;
				String blockNr = etc.getBlockNumber() + "";
				String blockHash = etc.getBlockHash();
				String blockTimestamp = etc.getBlockTimestamp() + "";
				String isReg = getIsReg(address) + "";
				balance = etc.getBalance();
				Map<String, String> propertyMap = new HashMap<>();
				propertyMap.put("Peer Count", etc.getPeerCount());
				propertyMap.put("Synchronization Completed", syncStatus);
				propertyMap.put("Synchronization Has Error", syncError);
				propertyMap.put("Block Number", blockNr);
				propertyMap.put("Block Hash", blockHash);
				propertyMap.put("Block Timestamp", blockTimestamp);
				propertyMap.put("Address / Identity", address);
				propertyMap.put("Address is Registered", isReg);
				propertyMap.put("Balance [ETH]", balance.toString());

				propertyMap.put("Use Testnet", useTestnet);
				propertyMap.put("Geth Host", gethHost);
				propertyMap.put("Geth Port", gethPort);
				propertyMap.put("Geth Syncmode", syncMode);
				propertyMap.put("Gas Limit", gasLimit);
				propertyMap.put("Gas Price [Wei]", gasPrice);

				JavaFXUtil.propertyView("Status", "Ethereum blockchain synchronization status and account information:",
						propertyMap);
			} else {
				String address = etc.getAddress();
				Map<String, String> propertyMap = new HashMap<>();
				propertyMap.put("Peer Count", etc.getPeerCount());
				propertyMap.put("Synchronization Completed", syncStatus);
				propertyMap.put("Synchronization Has Error", syncError);
				propertyMap.put("Address / Identity", address);

				propertyMap.put("Use Testnet", useTestnet);
				propertyMap.put("Geth Host", gethHost);
				propertyMap.put("Geth Port", gethPort);
				propertyMap.put("Geth Syncmode", syncMode);
				propertyMap.put("Gas Limit", gasLimit);
				propertyMap.put("Gas Price [Wei]", gasPrice);

				JavaFXUtil.infoBox("The synchronization with the ethereum blockchain is ongoing, therefore, only "
						+ "limited information is available at this time.");
				JavaFXUtil.propertyView("Status", "Ethereum blockchain synchronization status and account information:",
						propertyMap);
			}
		} catch (EthereumException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	private boolean getIsReg(String address) {
		try {
			TransactionResultPeer t = EthereumGethController.getInstance().getTransactionController()
					.getEthereumSignalContract().getRegisteredPeer(address);
			return t.getIsRegistered();
		} catch (Exception e) {
			// continue
		}
		return false;
	}

}
