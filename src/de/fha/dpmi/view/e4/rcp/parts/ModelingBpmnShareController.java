package de.fha.dpmi.view.e4.rcp.parts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.FileLocator;

import de.fha.dpmi.modeling.fileio.ModelingFileIO;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import javafx.fxml.FXML;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * UI controller of BPMN model of a shared workflow
 */
public class ModelingBpmnShareController extends ModelingBpmnController {

	@FXML
	protected void btLoadClicked() {
		try {
			loadFile("Choose BPMN Model", ModelingFileIO.SHARED_WORKFLOW_DIRECTORY_NAME, "BPMN 2.0 Model",
					"*." + ModelingFileIO.FILE_EXTENSION_BPMN);
		} catch (ModelingFileIOException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	@FXML
	protected void btSaveClicked() {
		try {
			saveFile("Save BPMN Model", ModelingFileIO.SHARED_WORKFLOW_DIRECTORY_NAME, "BPMN 2.0 Model",
					"*." + ModelingFileIO.FILE_EXTENSION_BPMN);
		} catch (ModelingFileIOException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	@Override
	protected void loadHtml() {
		try {
			wvEngine.setJavaScriptEnabled(true);

			Path p = Paths.get("resources", "html", "bpmn-workflow.html");
			String htmlUri = p.toUri().toURL().toExternalForm();
			if (!Files.isReadable(p)) {
				htmlUri = FileLocator
						.resolve(ModelingBpmnController.class.getResource("/resources/html/bpmn-workflow.html"))
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

	public ModelingBpmnShareController() {
		instance = this;
	}

	private static ModelingBpmnShareController instance;

	public synchronized static ModelingBpmnShareController getInstance() {
		return instance;
	}

	@Override
	void loadOverlayExtensionAttributes() {
	}

}
