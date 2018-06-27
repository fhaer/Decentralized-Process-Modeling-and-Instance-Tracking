package de.fha.dpmi.view.e4.rcp.parts;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.modeling.bpmn.BpmnModel;
import de.fha.dpmi.modeling.bpmn.fileio.BpmnXmlReader;
import de.fha.dpmi.modeling.bpmn.fileio.BpmnXmlWriterImpl;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.modeling.fileio.XmlReader;
import de.fha.dpmi.modeling.fileio.XmlWriter;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * abstract super class for all BPMN modeling UI controllers
 */
public abstract class ModelingBpmnController extends ModelingController {

	@FXML
	protected Button btCreate;

	@FXML
	protected Button btLoad;

	@FXML
	protected Button btSave;

	@FXML
	protected Label lbStatus;

	@FXML
	protected ScrollPane spModel;

	@FXML
	protected BorderPane bpModel;

	@FXML
	protected WebView wvModel;

	@FXML
	protected Button btOpenInTool;

	protected WebEngine wvEngine;

	protected String selectedModelElementId = "";

	protected String selectedModelElementName = "";

	protected String selectedModelElementType = "";

	protected BpmnModel openModel;

	@Override
	protected Model getModel() {
		return openModel;
	}

	@Override
	protected void setModel(Model model) {
		openModel = (BpmnModel) model;
	}

	@Override
	protected void create() {
		initialize();
		super.create();
	}

	@FXML
	private void btClearClicked() {
		lbStatus.setText("");
		clearModel();
	}

	@Override
	protected void clearModel() {
		setOpenFile(null);
		setModel(null);
		if (wvEngine == null)
			initialize();
		else
			wvEngine.executeScript("clearModel();");
	}

	protected void initialize() {
		int width = 1100;
		int height = 6000;
		wvModel.setMinHeight(height);
		wvModel.setMinWidth(width);
		wvModel.setPrefHeight(height);
		wvModel.setPrefWidth(width);
		wvEngine = wvModel.getEngine();
		interceptScrollEvents(wvModel, spModel, width, height);
		registerModelElementSelectListener();
		loadHtml();
	}

	protected abstract void loadHtml();

	protected void saveFile(String title, String subdirectory, String filetype, String... mask)
			throws ModelingFileIOException {
		if (openFile != null && openModel != null) {
			updateModelXml();
			super.saveFile(title, subdirectory, filetype, mask);
		}
	}

	protected void updateModelXml() throws ModelingFileIOException {
		String xml = (String) wvEngine.executeScript("saveModel();");
		InputStream xmlIs = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		BpmnXmlReader r = new BpmnXmlReader(xmlIs);
		setModel(r.parse());
	}

	@Override
	protected XmlReader getXmlReader(File file) throws ModelingFileIOException {
		BpmnXmlReader r = new BpmnXmlReader(file);
		return r;
	}

	@Override
	protected XmlWriter getXmlWriter(File file) throws ModelingFileIOException {
		XmlWriter w = new BpmnXmlWriterImpl(file);
		return w;
	}

	@Override
	protected void drawModel(Model model) {
		loadHtml();
	}

	private void registerModelElementSelectListener() {
		wvEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
			if (newState == State.SUCCEEDED) {
				JSObject window = (JSObject) wvEngine.executeScript("window");
				window.setMember("onModelElementSelectListener", this);
				if (openFile != null) {
					drawModel(openFile);
				}
			}
		});
	}

	abstract void loadOverlayExtensionAttributes();

	private void drawModel(File openFile) {
		String content = "";
		try {
			byte[] encoded = Files.readAllBytes(openFile.toPath());
			content = new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSObject window = (JSObject) wvEngine.executeScript("window");
		window.setMember("xml", content);

		String loadModelCall = "drawModel();";
		wvEngine.executeScript(loadModelCall);
	}

	public void setSelectedModelElement(String id, String name, String type) {
		String msg = "Selected:";
		selectedModelElementId = id;
		if (name.equals("undefined")) {
			selectedModelElementName = "";
		} else {
			selectedModelElementName = name;
			msg += " ";
		}
		selectedModelElementType = type.replaceFirst("bpmn:", "");
		lbStatus.setText(
				msg + selectedModelElementName + " " + selectedModelElementType + " (" + selectedModelElementId + ")");
	}

	public void log(String message) {
		// log to status label
		lbStatus.setText(message);
	}

	public File getOpenFile() {
		return openFile;
	}

	@Override
	public Button[] getButtons() {
		Button[] b = { btCreate, btLoad, btSave, btOpenInTool };
		return b;
	}
}
