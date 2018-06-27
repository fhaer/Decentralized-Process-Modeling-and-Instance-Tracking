package de.fha.dpmi.view.e4.rcp.parts;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fha.dpmi.hashing.FileHash;
import de.fha.dpmi.hashing.FileHashException;
import de.fha.dpmi.modeling.fileio.ModelingFileIO;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.state.ActualState;
import de.fha.dpmi.state.CheckpointState;
import de.fha.dpmi.state.CheckpointStateReached;
import de.fha.dpmi.state.InstanceState;
import de.fha.dpmi.util.DpmiTooling;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * UI controller base class for JavaFX UIs
 */
public class ExecutionController extends JavaFXController implements DpmiTooling {

	@FXML
	private Button btCheckOutInstances;

	@FXML
	private Button btStartInstanceBranch;

	@FXML
	private Button btCommitStateChange;

	@FXML
	private Button btInspect;

	@FXML
	private TreeView<InstanceState> tvInstance;

	@FXML
	private Label lbVersion;

	private static ExecutionController instance;

	private List<ActualState> actualStatesShared = new ArrayList<>();
	private List<CheckpointState> checkpointStatesShared = new ArrayList<>();
	private List<CheckpointState> checkpointStatesReachedShared = new ArrayList<>();

	private List<ActualState> actualStatesPeer = new ArrayList<>();
	private List<CheckpointState> checkpointStatesPeer = new ArrayList<>();
	private List<CheckpointState> checkpointStatesReachedPeer = new ArrayList<>();

	public final static String NO_INSTANCE_SELECTED = "<No Instance Selected>";

	private boolean isShared = false;

	public ExecutionController() {
		instance = this;
	}

	@Override
	protected void create() {
	}

	private void updateInstanceView() {
		tvInstance.setShowRoot(false);

		TreeItem<InstanceState> rootItem = new TreeItem<InstanceState>();
		rootItem.setValue(new InstanceState("Root"));

		TreeItem<InstanceState> rootItem1 = addCheckpointStates();
		TreeItem<InstanceState> rootItem2 = addActualStates();
		TreeItem<InstanceState> rootItem3 = addCheckpointStatesReached();

		rootItem.getChildren().add(rootItem1);
		rootItem.getChildren().add(rootItem2);
		rootItem.getChildren().add(rootItem3);

		tvInstance.setRoot(rootItem);
	}

	protected TreeItem<InstanceState> addCheckpointStatesReached() {
		TreeItem<InstanceState> rootItem1 = new TreeItem<InstanceState>();
		rootItem1.setValue(new InstanceState("Reached States"));
		List<CheckpointState> cl = checkpointStatesReachedShared;
		if (!isShared)
			cl = checkpointStatesReachedPeer;
		addTvItems(rootItem1, cl);
		return rootItem1;
	}

	protected void addTvItems(TreeItem<InstanceState> rootItem1, List<CheckpointState> cl) {
		for (CheckpointState s : cl) {
			TreeItem<InstanceState> item = new TreeItem<InstanceState>(s);
			rootItem1.getChildren().add(item);
		}
	}

	protected TreeItem<InstanceState> addCheckpointStates() {
		TreeItem<InstanceState> rootItem1 = new TreeItem<InstanceState>();
		rootItem1.setValue(new InstanceState("Checkpoint State"));
		List<CheckpointState> cl = checkpointStatesShared;
		if (!isShared)
			cl = checkpointStatesPeer;
		addTvItems(rootItem1, cl);
		return rootItem1;
	}

	protected TreeItem<InstanceState> addActualStates() {
		List<ActualState> al = actualStatesShared;
		if (!isShared)
			al = actualStatesPeer;
		TreeItem<InstanceState> rootItem2 = new TreeItem<InstanceState>();
		rootItem2.setValue(new InstanceState("Actual State"));
		for (ActualState i : al) {
			TreeItem<InstanceState> item = new TreeItem<InstanceState>(i);
			rootItem2.getChildren().add(item);
		}
		return rootItem2;
	}

	@FXML
	public void tvInstanceSelected() {
		InstanceState i = getSelectedInstance();
		if (i != null)
			lbVersion.setText(i.getId());
		else
			lbVersion.setText(NO_INSTANCE_SELECTED);
	}

	public ActualState getSelectedInstance() {
		if (tvInstance.getSelectionModel() != null && tvInstance.getSelectionModel().getSelectedItem() != null) {
			InstanceState is = tvInstance.getSelectionModel().getSelectedItem().getValue();
			if (is instanceof ActualState)
				return (ActualState) is;
		}
		return null;
	}

	public CheckpointState getSelectedCheckpointState() {
		if (tvInstance.getSelectionModel() != null && tvInstance.getSelectionModel().getSelectedItem() != null) {
			InstanceState is = tvInstance.getSelectionModel().getSelectedItem().getValue();
			if (is instanceof CheckpointState)
				return (CheckpointState) is;
		}
		return null;
	}

	@FXML
	public void btCheckOutInstancesClicked() {

	}

	@FXML
	public void btStartInstanceBranchClicked() {
		if (isShared) {
			actualStatesShared.add(new ActualState("I" + (actualStatesShared.size() + 1)));
			updateInstanceView();
		} else {
			actualStatesPeer.add(new ActualState("I" + (actualStatesPeer.size() + 1)));
			updateInstanceView();
		}
	}

	@FXML
	public void btCommitStateChangeClicked() {
		VersioningController vc = VersioningController.getInstance();
		if (vc != null)
			vc.btCommitClicked();
	}

	@FXML
	public void btInspectClicked() {
	}

	public static synchronized ExecutionController getInstance() {
		return instance;
	}

	public void updateInstanceStateList(boolean isShared) {
		lbVersion.setText(NO_INSTANCE_SELECTED);
		this.isShared = isShared;
		readActualStateList(isShared);
		readCheckpointStateList(isShared);
		updateInstanceView();
		updateCheckpointStatesReached();
	}

	private void readActualStateList(boolean isShared2) {
		Path repository = getRepositoryDirectoryIfExists();
		if (repository != null) {
			Path dir;
			try {
				dir = ModelingFileIO.getInstanceActualStateDirectory(repository, isShared);
				List<ActualState> l = actualStatesShared;
				if (!isShared)
					l = actualStatesPeer;
				l.clear();
				for (String fname : ModelingFileIO.getFileNames(dir)) {
					String id = fname;
					if (fname.endsWith(".bpmn"))
						id = fname.substring(0, fname.length() - 5);
					if (!l.contains(id))
						l.add(new ActualState(id));
				}
			} catch (ModelingFileIOException e) {
			}
		}
	}

	protected void readCheckpointStateList(boolean isShared) {
		Path repository = getRepositoryDirectoryIfExists();
		if (repository != null) {
			Path dir;
			try {
				dir = ModelingFileIO.getInstanceCheckpointStateDirectory(repository, isShared);
				List<CheckpointState> l = checkpointStatesShared;
				if (!isShared)
					l = checkpointStatesPeer;
				l.clear();
				for (String fname : ModelingFileIO.getFileNames(dir)) {
					String id = fname;
					if (fname.endsWith(".bpmn"))
						id = fname.substring(0, fname.length() - 5);
					if (!l.contains(id))
						l.add(new CheckpointState(id));
				}
			} catch (ModelingFileIOException e) {
			}
		}
	}

	protected Path getRepositoryDirectoryIfExists() {
		if (VersioningGraphSelectorController.getInstance().getSelectedVersionGraph() == null)
			return null;
		File repositoryDir = VersioningGraphSelectorController.getInstance().getSelectedVersionGraph().getDirectory();
		if (repositoryDir == null)
			return null;
		return repositoryDir.toPath();
	}

	@Override
	public Button[] getButtons() {
		Button[] b = { btStartInstanceBranch, btCommitStateChange };
		return b;
	}

	public void updateCheckpointStatesReached() {
		try {
			Path repoDir = getRepositoryDirectoryIfExists();
			Path csDirShared = ModelingFileIO.getInstanceCheckpointStateDirectory(repoDir, true);
			Path csDirPeer = ModelingFileIO.getInstanceCheckpointStateDirectory(repoDir, false);
			updateCheckpointstateReached(checkpointStatesShared, actualStatesShared, repoDir, csDirShared, true);
			updateCheckpointstateReached(checkpointStatesPeer, actualStatesPeer, repoDir, csDirPeer, false);
		} catch (ModelingFileIOException | FileHashException e) {
			e.printStackTrace();
		}
	}

	protected void updateCheckpointstateReached(List<CheckpointState> checkpointStates, List<ActualState> actualStates,
			Path repoDir, Path csDirPeer, boolean isShared) throws ModelingFileIOException, FileHashException {
		for (CheckpointState c : checkpointStates) {
			Path csFile = csDirPeer.resolve(c.getId() + ".bpmn");
			if (Files.exists(csFile)) {
				byte[] csHash = FileHash.calculateSha256(csFile);
				for (ActualState a : actualStates) {
					Path asFile = ModelingFileIO.getInstanceActualStateFile(repoDir, a.getId(), isShared);
					if (Files.exists(asFile)) {
						byte[] asHash = FileHash.calculateSha256(asFile);
						if (Arrays.equals(csHash, asHash)) {
							onCheckpointStateReached(c, a, isShared);
						}
					}
				}
			}
		}
	}

	private void onCheckpointStateReached(CheckpointState c, ActualState a, boolean isShared) {
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd-HHmmss");
		String dateShort = date.format(formatter);
		CheckpointStateReached cs = new CheckpointStateReached(dateShort, c, a);
		if (isShared && !checkpointStatesReachedShared.contains(cs))
			checkpointStatesReachedShared.add(cs);
		else if (isShared && !checkpointStatesReachedPeer.contains(cs))
			checkpointStatesReachedPeer.add(cs);

		String message = "Checkpoint State Reached: " + c.getId();
		log(message);
	}
}
