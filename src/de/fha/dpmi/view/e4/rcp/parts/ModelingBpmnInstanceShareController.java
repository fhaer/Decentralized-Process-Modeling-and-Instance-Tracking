package de.fha.dpmi.view.e4.rcp.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.FileLocator;

import de.fha.dpmi.modeling.fileio.ModelingFileIO;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.state.InstanceState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * UI controller of BPMN instance models of shared BPMN workflows
 */
public class ModelingBpmnInstanceShareController extends ModelingBpmnInstanceController {

	private HashMap<String, ArrayList<String>> instancesById = new HashMap<>();

	@FXML
	private Button btMark;

	@FXML
	protected void btLoadClicked() {
		try {
			File f = getInstanceStateModelFile(true);
			if (f == null) {
				JavaFXUtil.infoBox(
						"A shared workflow model for marking instance states is not loaded, please select one in the following dialog");
				loadFile("Choose BPMN Model", ModelingFileIO.SHARED_WORKFLOW_DIRECTORY_NAME, "BPMN 2.0 Model",
						"*." + ModelingFileIO.FILE_EXTENSION_BPMN);
			} else {
				loadFile(f);
				log("Loading Shared Workflow Model for Marking");
			}
			if (f != null) {
				JavaFXUtil.infoBox("Loading State File: " + f);
				loadOverlayExtensionAttributes();
			}
		} catch (ModelingFileIOException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	@FXML
	protected void btSaveClicked() {
		saveInstanceStateModel(true);
	}

	@Override
	protected void loadHtml() {
		try {
			wvEngine.setJavaScriptEnabled(true);

			Path p = Paths.get("resources", "html", "bpmn-instance-state.html");
			String htmlUri = p.toUri().toURL().toExternalForm();
			if (!Files.isReadable(p)) {
				htmlUri = FileLocator
						.resolve(ModelingBpmnController.class.getResource("/resources/html/bpmn-instance-state.html"))
						.toExternalForm();
			}

			// wvEngine.setOnError(evantHandler -> log("JavaScript onError: " +
			// (evantHandler.getMessage())));
			wvEngine.setOnAlert(evantHandler -> log(evantHandler.getData()));
			wvEngine.load(htmlUri);
			// wvEngine.loadContent(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void btMarkClicked() {
		if (selectedModelElementId == null) {
			JavaFXUtil.infoBox("Please select a model element to mark beforehand");
			return;
		}
		InstanceState selInst = ExecutionController.getInstance().getSelectedInstance();
		ArrayList<String> selObjectInstanceIds = instancesById.get(selectedModelElementId);
		if (selInst != null) {
			String selInstId = selInst.getId();
			if (selObjectInstanceIds == null) {
				selObjectInstanceIds = new ArrayList<>();
				selObjectInstanceIds.add(selInstId);
				instancesById.put(selectedModelElementId, selObjectInstanceIds);
				addOverlay(selectedModelElementId, selInstId, 5);
			} else {
				boolean selInstNotInObj = true;
				for (String instInSelectedObj : selObjectInstanceIds) {
					if (instInSelectedObj.equals(selInstId)) {
						removeOverlays(selectedModelElementId);
						selInstNotInObj = false;
					}
				}
				if (selInstNotInObj) {
					selObjectInstanceIds.add(selInstId);
					instancesById.put(selectedModelElementId, selObjectInstanceIds);
					addOverlay(selectedModelElementId, selInstId, 5);
				} else {
					selObjectInstanceIds.remove(selInstId);
					instancesById.put(selectedModelElementId, selObjectInstanceIds);
				}
			}
		} else {
			JavaFXUtil.infoBox(
					"No actual state instance selected. Please start an instance on the right and select it. Then, select an element to mark and press the mark button.");
		}
	}

	public ModelingBpmnInstanceShareController() {
		instance = this;
	}

	private static ModelingBpmnInstanceShareController instance;

	public synchronized static ModelingBpmnInstanceShareController getInstance() {
		return instance;
	}
}
