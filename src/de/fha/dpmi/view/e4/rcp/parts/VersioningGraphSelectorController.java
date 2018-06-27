package de.fha.dpmi.view.e4.rcp.parts;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.fha.dpmi.blockchain.EthereumGethController;
import de.fha.dpmi.blockchain.view.ShowStatusRegistry;
import de.fha.dpmi.modeling.fileio.ModelingFileIO;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.util.DpmiException;
import de.fha.dpmi.util.DpmiTooling;
import de.fha.dpmi.versioning.Branch;
import de.fha.dpmi.versioning.Version;
import de.fha.dpmi.versioning.VersionGraph;
import de.fha.dpmi.versioning.VersionGraphController;
import de.fha.dpmi.versioning.VersionGraphControllerImpl;
import de.fha.dpmi.versioning.VersionGraphException;
import de.fha.dpmi.versioning.VersionGraphFileIO;
import de.fha.dpmi.versioning.VersionGraphFileIOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * UI controller for showing the selector of version graphs
 */
public class VersioningGraphSelectorController extends JavaFXController implements DpmiTooling {

	private static final String NO_VERSION_GRAPH_IN_USE_LABEL = "<no Version Graph in use>";

	@FXML
	private Label lbVersionGraph;

	@FXML
	private ComboBox<VersionGraph> cbVersionGraphSelector;

	@FXML
	private Button btCreateVersionGraph;

	@FXML
	private Button btAddVersionGraph;

	@FXML
	private Button btRemoveVersionGraph;

	@FXML
	private Button btInspect;

	@FXML
	private Button btCreateBranch;

	@FXML
	private Button btCheckOutBranch;

	@FXML
	private ListView<Branch> tvVersionGraphBranches;

	private VersionGraphController versionGraphController;

	private static VersioningGraphSelectorController instance;

	public VersioningGraphSelectorController() {
		versionGraphController = VersionGraphControllerImpl.getInstance();
		instance = this;
		ShowStatusRegistry.initialize();
	}

	@Override
	protected void create() {
		cbVersionGraphSelector.setItems(versionGraphController.getKnownVersionGraphs());
		EthereumGethController.getInstance().startGeth();
	}

	@FXML
	public void cbVersionGraphSelectorChanged() {
		updateBranchView();
	}

	private void updateBranchView() {
		VersionGraph vg = cbVersionGraphSelector.getValue();
		if (vg != null) {
			tvVersionGraphBranches.setItems(vg.getKnownSharedBranches());
			lbVersionGraph.setText(vg.getName());
		}
	}

	@FXML
	public void tvVersionGraphBranchesSelected() {
	}

	@FXML
	public void btCreateVersionGraphClicked() {
		try {
			createVersionGraph();
		} catch (DpmiException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	private void createVersionGraph() throws ModelingFileIOException, VersioningGraphSelectorException {
		String title = "Create Version Graph";
		String name = askForVersionGraphName(title);
		JavaFXUtil.infoBox(title, "Local Repository",
				"A local repository with branches for private and shared models will be created. "
						+ "Please select an empty directory for the local repository to store public and private branches of the Version Graph.");
		File path = askForRepositoryPath(title);
		JavaFXUtil.infoBox(title, "Remote Repository",
				"A remote repository with a branch for shared models needs to be created. "
						+ "Please create an empty git repository and enter its URL in the following dialog or use the given URL for demonstration purposes.");
		String uri = JavaFXUtil.inputBox(title, "Remote Repository", "Git repository URL",
				VersionGraphController.DEMO_REPOSITORY_URI);
		if (uri.isEmpty() || !uri.startsWith("http")) {
			throw new VersioningGraphSelectorException("Valid URL not confirmed. Abort.");
		}
		createVersionGraph(name, path, uri);
		ModelingFileIO.getInstance().createModelingSystemDirectories(path);
	}

	private String askForVersionGraphName(String title) throws VersioningGraphSelectorException {
		String name = JavaFXUtil.inputBox(title, "Enter name of Version Graph", "", "");
		if (name.isEmpty()) {
			throw new VersioningGraphSelectorException("Name Empty. Abort.");
		}
		return name;
	}

	private File askForRepositoryPath(String title) throws ModelingFileIOException {
		File path = JavaFXUtil.direcotryChooser("Select local direcotry for Version Graph");
		if (path == null || !path.isDirectory()) {
			JavaFXUtil.showFailure(title, "Unable to read selected directory. Abort.");
			throw new ModelingFileIOException("Unable to read selected directory. Abort.", path);
		}
		return path;
	}

	private void createVersionGraph(String name, File path, String uri) {
		try {
			log("Create Version Graph ...");
			VersionGraph vg = versionGraphController.createVersionGraph(name, path, uri);
			cbVersionGraphSelector.setValue(vg);
			log("Version Graph created");
		} catch (VersionGraphException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	@FXML
	public void btAddVersionGraphClicked() {
		try {
			addVersionGraph();
		} catch (DpmiException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	private void addVersionGraph() throws ModelingFileIOException, VersioningGraphSelectorException {
		String title = "Add an existing Version Graph";
		String choiceLocal = "Local Repository";
		String choiceRemote = "Remote Repository";
		File path = new File("DPMI");
		String uri = VersionGraphController.DEMO_REPOSITORY_URI;
		String choice = JavaFXUtil.choiceBox(title, "Select an existing Version Graph from a ...", choiceLocal,
				choiceRemote);
		if (choice == choiceLocal) {
			path = askForRepositoryPath(title);
			addVersionGraph(path);
			ModelingFileIO.getInstance().createModelingSystemDirectories(path);
		} else if (choice == choiceRemote) {
			uri = JavaFXUtil.inputBox(title, "Enter remote git repository URL", "", "https://");
			if (uri.isEmpty() || !uri.startsWith("http")) {
				throw new VersioningGraphSelectorException("Valid URL not confirmed. Abort.");
			}
			JavaFXUtil.infoBox(title, "Local Repository",
					"A local repository with branches for private and shared models will be created. "
							+ "Please select an empty directory for the local repository to store public and private branches of the Version Graph.");
			path = askForRepositoryPath(title);
			String name = askForVersionGraphName(title);
			createVersionGraph(name, path, uri);
			ModelingFileIO.getInstance().createModelingSystemDirectories(path);
		}
	}

	private void addVersionGraph(File path) {
		try {
			List<String> fileContent = VersionGraphFileIO.getInstance().readVersionGraphMetadataFromFile(path.toPath());
			if (fileContent.size() < 2)
				throw new VersionGraphException("Version Graph does not exist in settings file");
			String name = fileContent.get(0);
			String uri = fileContent.get(1);
			log("Add Version Graph " + name + " ...");
			VersionGraph vg = versionGraphController.addVersionGraph(name, path, uri);
			cbVersionGraphSelector.setValue(vg);
			log("Version Graph added");
		} catch (VersionGraphException | VersionGraphFileIOException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	@FXML
	public void btRemoveVersionGraphClicked() {
		VersionGraph vg = getSelectedVersionGraph();
		if (vg != null) {
			versionGraphController.removeVersionGraph(vg);
			tvVersionGraphBranches.getItems().clear();
			lbVersionGraph.setText(NO_VERSION_GRAPH_IN_USE_LABEL);
		}
	}

	@FXML
	public void btInspectClicked() {
		VersionGraph vg = getSelectedVersionGraph();
		Map<String, String> vgMap = new LinkedHashMap<>();
		if (vg != null) {
			vgMap.put("Name", vg.getName());
			vgMap.put("Local Git Repository Path", vg.getDirectory().toString());
			vgMap.put("Remote Git Repository URI", vg.getUri());
			vgMap.put("Number of Shared Branches", vg.getKnownSharedBranches().size() + "");
		}
		Branch b = getSelectedBranch();
		Map<String, String> bMap = new LinkedHashMap<>();
		if (b != null) {
			bMap.put("Name", b.getBranchName());
			bMap.put("Display Name", b.getBranchDisplayName());
			bMap.put("Number of Versions", b.getNVersions() + "");
		}
		JavaFXUtil.propertyView("Inspect", "Properties of selected Version Graph and selected Branch", vgMap, bMap);
	}

	@FXML
	public void btCreateBranchClicked() {
		VersionGraph vg = getSelectedVersionGraph();
		if (vg != null) {
			createBranch();
		} else {
			JavaFXUtil.showFailure("No Version Graph in use. Please add or create a Version Graph first");
		}
	}

	private void createBranch() {
		VersionGraph vg = getSelectedVersionGraph();
		if (vg != null) {
			try {
				Version baseVersion = VersioningController.getInstance().getSelectedVersion();
				if (baseVersion != null) {
					Version v = baseVersion;
					String c1 = "Create branch from selected version: " + v;
					String c2 = "Create an unrelated empty branch (\"orphan branch\")";
					String r = JavaFXUtil.choiceBox("Create new shared branch",
							"Create a branch based on the selected version?", c1, c2);
					if (r.equals(c1))
						baseVersion = v;
					else if (r.equals(c2))
						baseVersion = null;
					else
						return;
				}
				String name = JavaFXUtil.inputBox("Create Branch", "Name", "Enter Branch name", "");
				Branch b = new Branch(name, true);
				if (name == null || name.isEmpty())
					return;
				if (baseVersion == null)
					vg.addSharedBranch(b);
				else
					vg.addSharedBranch(b, baseVersion);
			} catch (VersionGraphException e) {
				JavaFXUtil.showFailureWithException(e);
			}
		}
	}

	@FXML
	public void btCheckOutBranchClicked() {
		VersionGraph vg = getSelectedVersionGraph();
		Branch b = getSelectedBranch();
		if (vg != null && b != null) {
			try {
				Version v = getSelectedVersionGraph().checkOutSharedBranch(b);
				log("Check-out completed: Version " + v);
				VersioningController.getInstance().loadVersionGraphBranch(vg, b, v);
			} catch (VersionGraphException e) {
				JavaFXUtil.showFailureWithException(e);
			}
		} else {
			JavaFXUtil.showFailure("No Version Graph Branch selected. Please add or create a Version Graph first");
		}
	}

	@FXML
	public void btRemoveBranchClicked() {
		Branch b = getSelectedBranch();
		try {
			VersionGraph vg = getSelectedVersionGraph();
			if (vg != null)
				vg.removeSharedBranch(b);
			VersioningController.getInstance().clearVersionGraph();
		} catch (VersionGraphException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	public synchronized static VersioningGraphSelectorController getInstance() {
		return instance;
	}

	public VersionGraph getSelectedVersionGraph() {
		return cbVersionGraphSelector.getSelectionModel().getSelectedItem();
	}

	public Branch getSelectedBranch() {
		return tvVersionGraphBranches.getSelectionModel().getSelectedItem();
	}

	@Override
	public Button[] getButtons() {
		Button[] b = { btCreateVersionGraph, btAddVersionGraph, btRemoveVersionGraph, btCreateBranch, btCheckOutBranch,
				btInspect };
		return b;
	}
}
