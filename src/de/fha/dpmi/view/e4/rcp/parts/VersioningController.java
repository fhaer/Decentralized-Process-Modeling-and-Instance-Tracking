package de.fha.dpmi.view.e4.rcp.parts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fha.dpmi.blockchain.EthereumAgreementProcedureListener;
import de.fha.dpmi.blockchain.EthereumContractSignal;
import de.fha.dpmi.blockchain.EthereumException;
import de.fha.dpmi.blockchain.EthereumGethController;
import de.fha.dpmi.blockchain.EthereumTransactionController;
import de.fha.dpmi.blockchain.TransactionResultAgreementProcedure;
import de.fha.dpmi.blockchain.TransactionResultVersionCommit;
import de.fha.dpmi.blockchain.TransactionResultVersionVote;
import de.fha.dpmi.blockchain.view.ShowStatus;
import de.fha.dpmi.blockchain.view.ShowStatusRegistry;
import de.fha.dpmi.hashing.HashValue;
import de.fha.dpmi.hashing.HashValueException;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.util.DpmiTooling;
import de.fha.dpmi.versioning.Branch;
import de.fha.dpmi.versioning.Version;
import de.fha.dpmi.versioning.VersionGraph;
import de.fha.dpmi.versioning.VersionGraphException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * UI controller for showing the version graph
 */
public class VersioningController extends JavaFXController
		implements DpmiTooling, EthereumAgreementProcedureListener, ShowStatus {

	@FXML
	private Button btUpdate;

	@FXML
	private Button btCheckOut;

	@FXML
	private Button btCommit;

	@FXML
	private Button btBranchCommit;

	@FXML
	private Button btMergeCommit;

	@FXML
	private Button btInspect;

	@FXML
	private ScrollPane spVersionGraph;

	@FXML
	private AnchorPane apVersionGraph;

	@FXML
	private Group gpVersionGraph;

	@FXML
	private Canvas cvVersionGraph;

	@FXML
	private Label lbStatus;

	private ObservableList<String> logList = FXCollections.observableArrayList();

	private VersionGraph selectedVersionGraph;
	private Branch selectedSharedBranch;

	private final int hSpace = 100;
	private final int vSpace = 80;
	private final int initVPosition = 60;
	private final int initHPosition = 280;
	private int vPosition;
	private int hPosition;
	private int maxHPosition;

	private Map<HashValue, Version> versionByHash = new HashMap<>();
	private Map<Version, Point2D> versionPositions = new HashMap<>();

	private ToggleGroup toggleGroup = new ToggleGroup();

	private static VersioningController instance;

	public VersioningController() {
		instance = this;
	}

	@Override
	protected void create() {
		ShowStatusRegistry.showStatus = this;
	}

	/**
	 * load the given version graph, branch and version
	 *
	 * @param versionGraph
	 *            version graph
	 * @param sharedBranch
	 *            a shared branch to load
	 * @param versionToSelect
	 *            a version to be selected after loading
	 */
	public void loadVersionGraphBranch(VersionGraph versionGraph, Branch sharedBranch, Version versionToSelect) {
		selectedVersionGraph = versionGraph;
		selectedSharedBranch = sharedBranch;
		if (selectedVersionGraph == null || selectedSharedBranch == null) {
			JavaFXUtil.showFailure("No Version Graph or no Branch selected");
		} else {
			updateVersionGraph(versionToSelect);
		}
	}

	@FXML
	public void lbStatusClicked() {
		if (logList != null)
			JavaFXUtil.listView("DPMI", "Log", logList);
	}

	@FXML
	public void btInspectClicked() {
		Version v = getSelectedVersion();
		if (v != null) {
			Map<String, String> propertyMap = new HashMap<>();
			propertyMap.put("Selected Version: ", v.getVersionIdString());
			propertyMap.put("Hash Value: ", v.getRevCommit().getName());
			propertyMap.put("Branch Name: ", v.getBranch() + "");
			propertyMap.put("Branch is Shared: ", v.getBranch().isSharedBranch() + "");

			if (v.getBranch().isSharedBranch()) {
				propertyMap.put("Agreement Procedure completed: ", v.getIsAgreementProcedureFinished() + "");
				if (v.getIsAgreementProcedureFinished()) {
					propertyMap.put("Agreement Procedure result: ", v.getIsAgreed() + "");
				}
			}

			JavaFXUtil.propertyViewRev("Version", "Version Properties", propertyMap);
		} else {
			JavaFXUtil.infoBox("Nothing selected");
		}
	}

	@FXML
	public void btUpdateClicked() {
		if (selectedVersionGraph == null) {
			JavaFXUtil.showFailure("Please add or create a Version Graph on the left hand side first.");
			return;
		}
		if (selectedSharedBranch == null) {
			JavaFXUtil.showFailure("Please select a branch and click the check-out button on the left-hand side.");
			return;
		}
		updateVersionGraph();
	}

	@FXML
	public void btCheckOutClicked() {
		try {
			Version v = getSelectedVersion();
			if (v != null) {
				Version checkedOutVersion = selectedVersionGraph.checkOutVersion(v);
				updateVersionGraph(checkedOutVersion);
				updateOpenFiles();
			} else {
				JavaFXUtil.showFailure("No version selected");
			}
		} catch (VersionGraphException | ModelingFileIOException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	@FXML
	public void btCommitClicked() {
		Version selectedVersion = getSelectedVersion();
		try {
			if (selectedVersion != null) {
				Branch branch = selectedVersion.getBranch();
				Version firstVersion = branch.getFirstVersion();
				Version lastVersion = branch.getLatestVersion();
				if (!lastVersion.equals(firstVersion))
					checkAgreementProcedureCompleted(lastVersion);

				EthereumTransactionController ethereumTxController = checkEthereumGethReady(
						"a commit is not possible at this time");
				Version committedVersion = dvcsCommit(branch);
				try {
					TransactionResultVersionCommit transactionResult = blockchainCommit(branch, ethereumTxController,
							committedVersion);
					final GethDialogTransaction dialog = new GethDialogTransaction("Sending Modeling System Hash Value",
							transactionResult, ethereumTxController.getConfiguration());
					dialog.show("Modeling System hash value: " + committedVersion.getHashValue().getDisplayString());
					transactionResult.getFutureReceipt().thenAccept(receipt -> {
						if (receipt != null && receipt.isStatusOK()) {
							dialog.logSuccess(receipt);
							dvcsPushVersionAndUpdateGraph(branch, committedVersion);
						} else {
							dialog.logFailure(receipt);
						}
					});
				} catch (EthereumException e) {
					JavaFXUtil.showFailureWithException("DPMI", "Error while sending an Ethereum transaction",
							"Could not broadcast the Ethereum transaction. "
									+ "Please check the connection status to the Ethereum blockchain in the Ethereum menu and make sure the wallet contains sufficient "
									+ "funds to make a transaction. ",
							e);
					selectedVersionGraph.resetToCommit(lastVersion);
					log("Commit and push aborted");
				}
			} else {
				JavaFXUtil.showFailure("No version selected");
			}
		} catch (VersionGraphException | HashValueException | EthereumException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	private void checkAgreementProcedureCompleted(Version lastVersion) throws VersionGraphException {
		if (lastVersion == null)
			throw new VersionGraphException("No version selected");
		if (lastVersion.getBranch() == null)
			throw new VersionGraphException("Unable to determine branch of last version");
		if (lastVersion.getBranch().isSharedBranch() && !lastVersion.getIsAgreementProcedureFinished())
			throw new VersionGraphException(
					"The agreement procedure for the latest version on the shared branch is not completed");
	}

	private EthereumTransactionController checkEthereumGethReady(String thereforeFailureMessage)
			throws EthereumException {
		EthereumTransactionController ethereumTxController = getEthereumTransactionController();
		if (!ethereumTxController.isStarted()) {
			throw new EthereumException("Unable to connect to the Ethereum Geth client, therefore, "
					+ thereforeFailureMessage
					+ ". Please make sure Geth (https://geth.ethereum.org) is installed on this system and started through the Ethereum menu.");
		}
		if (!ethereumTxController.isReady()) {
			throw new EthereumException("The synchronization of the Ethereum blockchain is ongoing, therefore, "
					+ thereforeFailureMessage
					+ ". Connection status and synchronization progress can be monitored in status toolbar and the Ethereum menu.");
		}

		return ethereumTxController;
	}

	private Version dvcsCommit(Branch b) throws VersionGraphException {
		Version committedVersion = selectedVersionGraph.commit(b);
		return committedVersion;
	}

	private TransactionResultVersionCommit blockchainCommit(Branch b,
			EthereumTransactionController ethereumTxController, Version committedVersion) throws EthereumException {
		log("Sending Ethereum transaction ...");
		EthereumContractSignal ethSignalContract = ethereumTxController.getEthereumSignalContract();
		TransactionResultVersionCommit transaction = ethSignalContract
				.sendVersionCommitTransactionAsyc(committedVersion.getHashBytes());
		return transaction;
	}

	public void dvcsPushVersionAndUpdateGraph(Branch b, Version committedVersion) {
		Platform.runLater(() -> {
			try {
				selectedVersionGraph.push(b);
				setAgreementProcedureResult(committedVersion);
				updateVersionGraph(committedVersion);
			} catch (EthereumException | VersionGraphException e) {
				JavaFXUtil.showFailureWithException(e);
			}
		});
	}

	@FXML
	public void btBranchCommitClicked() {
		Version selectedVersion = getSelectedVersion();
		if (!checkVersionAskIfAgreementProcedureIncomplete(selectedVersion))
			return;
		String peerName = getPeerName();
		String name = JavaFXUtil.inputBox("DPMI", "New Private Branch", "Name", peerName);
		if (name != null && !name.isEmpty()) {
			Branch branch = new Branch(name, false);
			try {
				selectedVersion = selectedVersionGraph.branchCommit(selectedVersion, branch);
				reloadVersionGraph(selectedVersion);
			} catch (VersionGraphException e) {
				JavaFXUtil.showFailureWithException(e);
			}
		}
	}

	private String getPeerName() {
		return getEthereumTransactionController().getPeerName();
	}

	private boolean checkVersionAskIfAgreementProcedureIncomplete(Version selectedVersion) {
		try {
			checkAgreementProcedureCompleted(selectedVersion);
		} catch (VersionGraphException e) {
			if (selectedVersion == null || selectedVersion.getBranch() == null) {
				JavaFXUtil.showFailureWithException(e);
				return false;
			}
			String cAbort = "Abort";
			String choice = JavaFXUtil.choiceBox("Create Branch", e.getMessage(), "Continue", cAbort);
			if (choice.equals(cAbort) || choice.equals(""))
				return false;
		}
		return true;
	}

	@FXML
	public void btMergeCommitClicked() {
		Version selectedVersion = getSelectedVersion();
		if (!checkVersionAskIfAgreementProcedureIncomplete(selectedVersion))
			return;
		try {
			if (selectedVersion != null) {
				selectedVersion = selectedVersionGraph.mergeCommit(selectedVersion, selectedSharedBranch);
				reloadVersionGraph(selectedVersion);
			} else {
				JavaFXUtil.showFailure("No version selected");
			}
		} catch (VersionGraphException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	private void reloadVersionGraph(Version selectedVersion) {
		try {
			selectedVersionGraph.checkOutSharedBranch(selectedSharedBranch);
			Version checkedOutVersion = selectedVersionGraph.checkOutVersion(selectedVersion);
			updateVersionGraph(checkedOutVersion);
			updateOpenFiles();
		} catch (VersionGraphException | ModelingFileIOException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	private void updateOpenFiles() throws ModelingFileIOException {
		ModelingController[] ctrl = { ModelingSomController.getInstance(), ModelingBpmnShareController.getInstance(),
				ModelingBpmnPeerController.getInstance(), ModelingBpmnInstanceShareController.getInstance(),
				ModelingBpmnInstancePeerController.getInstance() };
		for (ModelingController c : ctrl)
			if (c != null)
				c.updateOpenFile();
	}

	public void showStatus(String msg) {
		Platform.runLater(() -> {
			if (lbStatus != null)
				lbStatus.setText(msg);
			logList.add(msg);
		});
	}

	protected void clearVersionGraph() {
		gpVersionGraph.getChildren().clear();
		toggleGroup = new ToggleGroup();
		versionPositions = new HashMap<>();
		vPosition = initVPosition;
		hPosition = initHPosition;
		maxHPosition = hPosition;
	}

	private void updateVersionGraph() {
		if (selectedVersionGraph != null && selectedSharedBranch != null) {
			try {
				Version selectedVersion = getSelectedVersion();
				Version v = selectedVersionGraph.updateSharedBranch(selectedSharedBranch);
				if (selectedVersion != null) {
					updateVersionGraph(selectedVersion);
				} else {
					updateVersionGraph(v);
					updateOpenFiles();
				}
			} catch (VersionGraphException | ModelingFileIOException e) {
				JavaFXUtil.showFailureWithException(e);
			}
		}
	}

	private void updateVersionGraph(Version versionToSelect) {
		clearVersionGraph();
		try {
			checkEthereumGethReady("the state of agreement procedures cannot be determined at this time");
			getAgreementProcedureResults(selectedSharedBranch.getVersions());
			updateCheckpointStateReached();
			drawLine(hPosition - 10, initVPosition, hPosition, initVPosition);
			drawBranch(selectedSharedBranch, initHPosition, initVPosition, 0);
			selectVersion(versionToSelect);
		} catch (EthereumException e) {
			JavaFXUtil.showInfoException(e);
		}
	}

	private void updateCheckpointStateReached() {
		ExecutionController ec = ExecutionController.getInstance();
		if (ec != null) {
			ec.updateCheckpointStatesReached();
		}
	}

	/**
	 * called when the agreement procedure for a hash value is completed
	 *
	 * @param hashValue
	 *            the agreement for this hash value is completed
	 * @param result
	 *            true in case of agreement, false otherwise
	 */
	public void onAgreementProcedureCompleted(byte[] hashValue, boolean result) {
		Version v = versionByHash.get(new HashValue(hashValue));
		if (v != null)
			try {
				setAgreementProcedureResult(v);
				updateVersionGraph();
			} catch (EthereumException e) {
				JavaFXUtil.showFailureWithException(e);
			}
	}

	private void getAgreementProcedureResults(List<Version> versions) throws EthereumException {
		for (Version v : versions) {
			setAgreementProcedureResult(v);
		}
	}

	private void setAgreementProcedureResult(Version v) throws EthereumException {
		TransactionResultAgreementProcedure agreementProcedureResult = getEthereumTransactionController()
				.getEthereumSignalContract().getAgreementProcedure(v.getHashValue().getByteArray());
		if (agreementProcedureResult.getCompleted()) {
			v.setIsAgreementProcedureCompleted(true);
			v.setIsAgreed(agreementProcedureResult.getResult());
		} else {
			v.setIsAgreementProcedureCompleted(false);
		}
	}

	private EthereumTransactionController getEthereumTransactionController() {
		EthereumGethController ethereumGeth = EthereumGethController.getInstance();
		EthereumTransactionController ethereumTransactionController = ethereumGeth.getTransactionController();
		if (ethereumTransactionController == null) {
			JavaFXUtil.showFailure("Unable to start the Ethereum geth client");
		}
		return ethereumTransactionController;
	}

	private void selectVersion(Version version) {
		// checkOutVersion = version;
		for (Toggle t : toggleGroup.getToggles()) {
			RadioButton rb = (RadioButton) t;
			Version rbVersion = (Version) t.getUserData();
			if (version.equals(rbVersion)) {
				rb.setSelected(true);
				double pos = versionPositions.get(rbVersion).getX() / maxHPosition;
				if (pos < 0 || pos > spVersionGraph.getHmax())
					pos = 0;
				// spVersionGraph.setHvalue(pos);
			}
		}
	}

	/**
	 * returns the version selected by the user using a radio button
	 *
	 * @return selected version or null if nothing is selected
	 */
	protected Version getSelectedVersion() {
		RadioButton rb = (RadioButton) toggleGroup.getSelectedToggle();
		if (rb != null)
			return (Version) rb.getUserData();
		return null;
	}

	private void drawBranch(Branch b, int hPosition, int vPositionLast, int nBranches) {
		List<Version> versions = b.getVersions();
		drawBranch(b, versions, hPosition, vPositionLast, nBranches);
	}

	private void drawBranch(Branch b, List<Version> versions, int hPosition, int vPositionLast, int nBranches) {
		int nVersions = versions.size();
		double hPositionBranchEnd = hPosition + hSpace * (nVersions - 1);
		Point2D branchLineStart = new Point2D(hPosition, vPosition);
		Point2D branchLineEnd = new Point2D(hPositionBranchEnd, vPosition);
		drawText(hPosition - 240, vPosition + 3, b.getBranchName(), true);
		double hPositionVersion = hPosition;
		for (int i = 0; i < nVersions; i++) {
			Version v = versions.get(i);
			hPositionVersion = calculateVersionPosition(v, nBranches);
			String versionId = v.getVersionIdString();
			drawVersion(v, hPositionVersion, vPosition);
			drawText(hPositionVersion + 6, vPosition - 5, versionId, false);
			if (hPositionVersion > hPositionBranchEnd) {
				hPositionBranchEnd = hPositionVersion;
			}
			if (hPositionVersion > maxHPosition)
				maxHPosition = (int) hPositionVersion;
		}
		for (int i = (nVersions - 1); i >= 0; i--) {
			Version v = versions.get(i);
			List<Version> branches = v.getBranches();
			double hPositionBranch = calculateVersionPosition(v, nBranches);
			for (Version vBranch : branches) {
				drawLine(hPositionBranch, vPositionLast, hPositionBranch, vPosition + vSpace);
				vPosition += vSpace;
				drawBranch(vBranch.getBranch(), (int) hPositionBranch, vPositionLast, nBranches + 1);
			}
		}
		hPositionBranchEnd = maxHPosition + 80;
		branchLineEnd = new Point2D(hPositionBranchEnd + 80, branchLineEnd.getY());
		drawLine(branchLineStart.getX(), branchLineStart.getY(), branchLineEnd.getX(), branchLineEnd.getY());
		drawBranchArrow(branchLineEnd.getX(), branchLineEnd.getY());
	}

	private int calculateVersionPosition(Version v, int nBranches) {
		int n = selectedVersionGraph.getVersionOrderNumber(v);
		int branchOffset = 0; // nBranches * (int) (hSpace * 1.0);
		int pos = hPosition + hSpace * (n - 1) - branchOffset;
		return pos;
	}

	private void drawVersion(Version v, double hPosition, double vPosition) {
		drawVersionMark(hPosition, vPosition - 10, hPosition, vPosition + 10);
		drawRadioButton(v, hPosition + 5, vPosition - 48);
		if (v.getBranch().isSharedBranch()) {
			Version first = v.getBranch().getFirstVersion();
			if (v.getIsAgreementProcedureFinished() || v.equals(first)) {
				if (v.getIsAgreed() || v.equals(first)) {
					drawCheckMark(hPosition + 39, vPosition - 42); // 34 29
				} else {
					drawCrossMark(hPosition + 39, vPosition - 42);
				}
			} else {
				drawVoteButton(hPosition + 39, vPosition - 42, v);
			}
		}
		for (Version mergedFromV : v.getMerges()) {
			Point2D mergedFrom = versionPositions.get(mergedFromV);
			if (mergedFrom != null)
				drawLine(hPosition, mergedFrom.getY(), hPosition, vPosition);
		}
		versionByHash.put(v.getHashValue(), v);
		versionPositions.put(v, new Point2D(hPosition, vPosition));
		registerVersionObserver(v);
	}

	private void registerVersionObserver(Version v) {
		try {
			getEthereumTransactionController().getEthereumSignalContract().registerAgreementProcedureObserver(this,
					v.getHashValue().getByteArray());
		} catch (EthereumException e) {
		}
	}

	private void drawRadioButton(Version version, double hPosition, double vPosition) {
		RadioButton r = new RadioButton();
		r.setUserData(version);
		r.setToggleGroup(toggleGroup);
		r.relocate(hPosition, vPosition);
		gpVersionGraph.getChildren().add(r);
	}

	private void drawLine(double hPosition, double vPosition, double hPosition2, double vPosition2) {
		Line shape = new Line(hPosition, vPosition, hPosition2, vPosition2);
		shape.setStrokeWidth(2);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.BLACK);
		shape.setFill(Color.rgb(255, 255, 255, 1));
		// shape.relocate(x, y);
		gpVersionGraph.getChildren().add(shape);
	}

	private void drawBranchArrow(double hPosition, double vPosition) {
		drawLine(hPosition, vPosition, hPosition - 5, vPosition - 5);
		drawLine(hPosition, vPosition, hPosition - 5, vPosition + 5);
	}

	private void drawVersionMark(double hPosition, double vPosition, double hPosition2, double vPosition2) {
		Line shape = new Line(hPosition, vPosition, hPosition2, vPosition2);
		shape.setStrokeWidth(2);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.BLACK);
		// shape.relocate(x, y);
		gpVersionGraph.getChildren().add(shape);
	}

	private void drawVoteButton(double hPosition, double vPosition, Version version) {
		Circle shape = new Circle(7);
		shape.setStrokeWidth(1);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.WHITE);
		shape.setCenterX(hPosition);
		shape.setCenterY(vPosition);
		if (version.getIsVoteOngoing()) {
			shape.setDisable(true);
		}
		shape.setOnMouseClicked(e -> {
			String c1 = "Vote Agree / Commit";
			String c2 = "Vote Disagree / Abort";
			String voteChoice = JavaFXUtil.choiceBox("Agreement Procedure",
					"Vote on Modeling System Proposed in Version " + version.toString(), c1, c2);
			if (voteChoice.isEmpty())
				return;
			boolean vote = voteChoice.equals(c1);
			EthereumTransactionController etc = getEthereumTransactionController();
			try {
				TransactionResultVersionVote tResult = etc.getEthereumSignalContract()
						.sendVersionVoteTransaction(version.getHashBytes(), vote);
				final GethDialogTransaction dialog = new GethDialogTransaction("Sending Modeling System Version Vote",
						tResult, etc.getConfiguration());
				dialog.show("Modeling System hash value: " + version.getHashValue().getDisplayString(), voteChoice);
				tResult.getFutureReceipt().thenAccept(receipt -> {
					if (receipt != null && receipt.isStatusOK()) {
						dialog.logSuccess(receipt);
						version.setIsVoteOngoing(true);
						shape.setDisable(true);
					} else {
						dialog.logFailure(receipt);
					}
				});
			} catch (EthereumException e1) {
				JavaFXUtil.showFailureWithException("DPMI", "Abort Vote", "Error while submitting vote", e1);
			}
		});
		gpVersionGraph.getChildren().add(shape);
	}

	private void drawCheckMark(double hPosition, double vPosition) {
		Circle shape = new Circle(7);
		shape.setStrokeWidth(1);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.WHITE);
		shape.setCenterX(hPosition);
		shape.setCenterY(vPosition);
		gpVersionGraph.getChildren().add(shape);

		Line l1 = new Line(hPosition, vPosition + 3, hPosition - 4, vPosition - 1);
		l1.setStrokeWidth(1);
		l1.setStroke(Color.GREEN);
		l1.setFill(Color.BLACK);
		gpVersionGraph.getChildren().add(l1);

		Line l2 = new Line(hPosition, vPosition + 3, hPosition + 5, vPosition - 5);
		l2.setStrokeWidth(1);
		l2.setStroke(Color.GREEN);
		l2.setFill(Color.BLACK);
		gpVersionGraph.getChildren().add(l2);
	}

	private void drawCrossMark(double hPosition, double vPosition) {
		Circle shape = new Circle(7);
		shape.setStrokeWidth(1);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.WHITE);
		shape.setCenterX(hPosition);
		shape.setCenterY(vPosition);
		gpVersionGraph.getChildren().add(shape);

		Line l1 = new Line(hPosition - 5, vPosition + 5, hPosition + 5, vPosition - 5);
		l1.setStrokeWidth(1);
		l1.setStroke(Color.RED);
		l1.setFill(Color.BLACK);
		gpVersionGraph.getChildren().add(l1);

		Line l2 = new Line(hPosition - 5, vPosition - 5, hPosition + 5, vPosition + 5);
		l2.setStrokeWidth(1);
		l2.setStroke(Color.RED);
		l2.setFill(Color.BLACK);
		gpVersionGraph.getChildren().add(l2);
	}

	private void drawText(double hPosition, double vPosition, String text, boolean wrapText) {
		Text shape = new Text(hPosition, vPosition, text);
		shape.setStrokeWidth(0);
		shape.setStroke(Color.BLACK);
		if (wrapText)
			shape.setWrappingWidth(190);
		// shape.setFill(Color.BLACK);
		// shape.relocate(x, y);
		gpVersionGraph.getChildren().add(shape);
	}

	public static synchronized VersioningController getInstance() {
		return instance;
	}

	@Override
	public Button[] getButtons() {
		Button[] b = { btUpdate, btCheckOut, btCommit, btBranchCommit, btMergeCommit, btInspect };
		return b;
	}

}
