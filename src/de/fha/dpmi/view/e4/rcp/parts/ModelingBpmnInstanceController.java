package de.fha.dpmi.view.e4.rcp.parts;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import de.fha.dpmi.modeling.bpmn.BpmnModel;
import de.fha.dpmi.modeling.bpmn.elements.FlowObject;
import de.fha.dpmi.modeling.bpmn.elements.Process;
import de.fha.dpmi.modeling.fileio.ModelingFileIO;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.state.InstanceState;
import de.fha.dpmi.state.InstanceStateException;
import de.fha.dpmi.versioning.VersionGraphException;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Abstract super class for all UI controller of BPMN instance models
 */
public abstract class ModelingBpmnInstanceController extends ModelingBpmnController {

	ArrayList<SimpleEntry<String, String>> overlays = new ArrayList<>();

	protected void saveInstanceStateModel(boolean isShared) {
		try {
			if (openFile == null) {
				return;
			}
			updateModelXml();
			addOverlayExtensionAttributes();
			Path repositoryDirectory = getRepositoryDirectory();
			String c1 = "Checkpoint State";
			String c2 = "Actual State";
			String choice = JavaFXUtil.choiceBox("DPMI", "Save Instance State as", c1, c2);
			if (choice.length() > 0 && choice.equals(c1)) {
				Path dir = ModelingFileIO.getInstanceCheckpointStateDirectory(repositoryDirectory, isShared);
				String csName = JavaFXUtil.inputBox("DPMI", "Enter Checkpoint State Name", "", "CS1");
				Path file = dir.resolve(csName + ".bpmn");
				if (Files.exists(file)) {
					String ovc1 = "Overwrite";
					String ovc2 = "Cancel";
					String ovChoice = JavaFXUtil.choiceBox("DPMI", "Checkpoint state already exists", ovc1, ovc2);
					if (ovChoice.equals(ovc2))
						return;
				}
				writeFile(file.toFile());
			} else if (choice.length() > 0 && choice.equals(c2)) {
				String instanceId = getSelectedInstanceId();
				Path file = ModelingFileIO.getInstanceActualStateFile(repositoryDirectory, instanceId, isShared);
				writeFile(file.toFile());
			} else {
				JavaFXUtil.showFailure("Abort.");
				return;
			}
			updateCheckpointStateList(isShared);
		} catch (ModelingFileIOException | VersionGraphException | InstanceStateException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	synchronized void addOverlayExtensionAttributes() {
		for (SimpleEntry<String, String> e : overlays) {
			String object = e.getKey();
			String instance = e.getValue();
			BpmnModel m = (BpmnModel) getModel();
			for (Process p : m.getProcesses()) {
				for (FlowObject f : p.getFlowObjects()) {
					if (f.getId().equals(object)) {
						f.addExtensionAttribute(object, instance);
					}
				}
			}
		}
	}

	synchronized void loadOverlayExtensionAttributes() {
		List<SimpleEntry<String, String>> toAdd = new ArrayList<>();
		BpmnModel m = (BpmnModel) getModel();
		for (Process p : m.getProcesses()) {
			for (FlowObject f : p.getFlowObjects()) {
				if (f.hasExtensionAttributes()) {
					for (SimpleEntry<String, String> e : f.getExtensionAttributes()) {
						toAdd.add(e);
					}
				}
			}
		}
		overlays.clear();
		for (SimpleEntry<String, String> e : toAdd) {
			addOverlay(e.getKey(), e.getValue(), 5);
		}
	}

	private void updateCheckpointStateList(boolean isShared) throws ModelingFileIOException, VersionGraphException {
		ExecutionController ec = ExecutionController.getInstance();
		ec.updateInstanceStateList(isShared);
	}

	synchronized void addOverlay(String object, String instance, int offset) {
		if (wvEngine != null && object.length() > 0) {
			String jsCall = "addOverlay(\"" + object + "\", \"" + instance + "\", " + offset + ")";
			wvEngine.executeScript(jsCall);
			overlays.add(new SimpleEntry<>(object, instance));
		}
	}

	synchronized void removeOverlays(String object) {
		if (wvEngine != null && object.length() > 0) {
			String jsCall = "removeOverlay(\"" + object + "\")";
			wvEngine.executeScript(jsCall);
			List<SimpleEntry<String, String>> toRemove = new ArrayList<>();
			for (SimpleEntry<String, String> e : overlays) {
				if (e.getKey().equals(object))
					toRemove.add(e);
			}
			for (SimpleEntry<String, String> e : toRemove) {
				overlays.remove(e);
			}
		}
	}

	protected File getInstanceStateModelFile(boolean isShared) {
		String instance = getSelectedInstanceIdIfExists();
		if (!instance.isEmpty()) {
			Path repository;
			try {
				repository = getRepositoryDirectory();
				File f = ModelingFileIO.getSharedInstanceStateModelFile(repository, instance, isShared);
				return f;
			} catch (VersionGraphException | ModelingFileIOException e) {
				return null;
			}
		}
		String cs = getSelectedCheckpointStateIdIfExists();
		if (!cs.isEmpty()) {
			Path repository;
			try {
				repository = getRepositoryDirectory();
				Path d = ModelingFileIO.getInstanceCheckpointStateDirectory(repository, isShared);
				File f = d.resolve(cs + ".bpmn").toFile();
				if (f.exists())
					return f;
			} catch (VersionGraphException | ModelingFileIOException e) {
				return null;
			}
		}
		return null;
	}

	private String getSelectedInstanceId() throws InstanceStateException {
		InstanceState instance;
		instance = ExecutionController.getInstance().getSelectedInstance();
		if (instance == null) {
			throw new InstanceStateException("Instance ID unkown, please select an instance on the right hand side.");
		}
		return instance.getId();
	}

	private String getSelectedInstanceIdIfExists() {
		InstanceState instance;
		instance = ExecutionController.getInstance().getSelectedInstance();
		if (instance == null) {
			return "";
		}
		return instance.getId();
	}

	private String getSelectedCheckpointStateIdIfExists() {
		InstanceState instance;
		instance = ExecutionController.getInstance().getSelectedCheckpointState();
		if (instance == null) {
			return "";
		}
		return instance.getId();
	}
}
