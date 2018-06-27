package de.fha.dpmi.view.e4.rcp.parts;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.modeling.fileio.XmlReader;
import de.fha.dpmi.modeling.fileio.XmlWriter;
import de.fha.dpmi.util.DpmiTooling;
import de.fha.dpmi.versioning.VersionGraphException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * UI controller for all modeling controllers
 */
public abstract class ModelingController extends JavaFXController implements DpmiTooling {

	/**
	 * file currently open or null, if no file is open
	 */
	protected File openFile;

	protected abstract Model getModel();

	protected abstract void setModel(Model model);

	@Override
	protected void create() {
		super.create();
	}

	@FXML
	private void btOpenInToolClicked() {
		if (openFile != null) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(openFile.getAbsoluteFile());
				// Runtime.getRuntime().exec(new String[] { "rundll32",
				// "url.dll,FileProtocolHandler", openFile.getAbsolutePath() });
			} catch (IOException e) {
				JavaFXUtil.showFailureWithException(e);
			}
		}
	}

	/**
	 * Intercepts scroll events and scrolls by a custom amount
	 *
	 * @param node
	 *            node where to intercept events
	 * @param scrollPane
	 *            scroll pane
	 */
	void interceptScrollEvents(Node node, ScrollPane scrollPane, double width, double height) {
		// spModel.setVbarPolicy(ScrollBarPolicy.NEVER);
		// spModel.setHbarPolicy(ScrollBarPolicy.NEVER);
		node.addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
			// control+scroll events are passed to JavaScript for zooming
			if (!event.isControlDown() && !event.isShiftDown()) {

				double dx = event.getDeltaX();
				double dy = event.getDeltaY();
				double relX = dx / node.getBoundsInLocal().getWidth() * 1.1;
				double relY = dy / node.getBoundsInLocal().getHeight() * 1.1;

				scrollPane.setHvalue(scrollPane.getHvalue() - relX);
				scrollPane.setVvalue(scrollPane.getVvalue() - relY);
				event.consume();
			}
			// if (event.isInertia())
		});
	}

	/**
	 * shows a file open dialog and loads the selected model form file
	 *
	 * @param title
	 *            title of file open dialog
	 * @param subdirectory
	 *            name subdirectory of repository shown by open dialog
	 * @param filetype
	 *            name of file type to open
	 * @param mask
	 *            mask to filter files available to open, e.g. *.xml
	 * @throws ModelingFileIOException
	 *             throw if an error happens during file input/output or parsing
	 */
	protected void loadFile(String title, String subdirectory, String filetype, String... mask)
			throws ModelingFileIOException {
		Path repositoryDir = getRepositoryDirectoryIfExists();
		Path initDirectory = null;
		if (repositoryDir != null && Files.isDirectory(repositoryDir))
			initDirectory = repositoryDir.resolve(subdirectory);
		File loadFile = JavaFXUtil.fileOpenChooser(title, initDirectory, filetype, mask);
		if (loadFile != null) {
			loadFile(loadFile);
			log("Opened XML file: \"" + loadFile + "\"");
			if (initDirectory == null) {
				JavaFXUtil.infoBox(
						"Versioning is disabled as there is no version graph loaded. To enable versioning, add a version graph and re-load this file.");
			} else {
				if (!fileIsPartOfRepository(loadFile, subdirectory)) {
					boolean okClicked = JavaFXUtil.infoBox(
							"For versioning, the file needs to be saved inside the appropriate repository directory. The file will be saved in \""
									+ initDirectory.toFile().getAbsolutePath() + "\" now.");
					if (okClicked)
						saveFile(title, subdirectory, filetype, mask);
				}
			}
		}
	}

	private boolean fileIsPartOfRepository(File file, String subdirectory) throws ModelingFileIOException {
		Path repositoryDir = getRepositoryDirectoryIfExists();
		if (repositoryDir == null || !Files.isDirectory(repositoryDir))
			return false;
		File repositorySubDir = repositoryDir.resolve(subdirectory).toFile();
		try {
			return file.getCanonicalPath().startsWith(repositorySubDir.getCanonicalPath());
		} catch (IOException e) {
			throw new ModelingFileIOException("Unable to determine cannonical path", file);
		}
	}

	/**
	 * loads a model from the given file
	 *
	 * @param loadFile
	 *            file to load model from
	 * @throws ModelingFileIOException
	 *             throw if an error happens during file input/output or parsing
	 */
	protected void loadFile(File loadFile) throws ModelingFileIOException {
		XmlReader r = getXmlReader(loadFile);
		Model model = r.parse();
		clearModel();
		setModel(model);
		setOpenFile(loadFile);
		drawModel(model);
	}

	/**
	 * shows a file save dialog and saves the selected model to file
	 *
	 * @param title
	 *            title of file save dialog
	 * @param subdirectory
	 *            name subdirectory of repository shown by save dialog
	 * @param filetype
	 *            name of file type to save
	 * @param mask
	 *            mask to filter files available to save, e.g. *.xml
	 * @throws ModelingFileIOException
	 *             throw if an error happens during model serialization or file
	 *             input/output
	 */
	protected void saveFile(String title, String subdirectory, String filetype, String... mask)
			throws ModelingFileIOException {
		if (openFile == null)
			return;
		Path gitDirectory = getRepositoryDirectoryIfExists();
		Path initDirectory = null;
		if (gitDirectory != null && Files.isDirectory(gitDirectory))
			initDirectory = gitDirectory.resolve(subdirectory);
		File saveFile = openFile;
		if (!fileIsPartOfRepository(openFile, subdirectory))
			saveFile = JavaFXUtil.fileSaveChooser(title, initDirectory, filetype, mask);
		if (saveFile != null) {
			writeFile(saveFile);
		}
	}

	/**
	 * Writes model XML to the given file
	 *
	 * @param saveFile
	 * @throws ModelingFileIOException
	 */
	protected void writeFile(File saveFile) throws ModelingFileIOException {
		XmlWriter w = getXmlWriter(saveFile);
		Model model = getModel();
		w.write(model);
		setOpenFile(saveFile);
		log("Saved file to: \"" + saveFile + "\"");
	}

	/**
	 * sets and registers the file last opened
	 *
	 * @param file
	 */
	protected void setOpenFile(File file) {
		openFile = file;
	}

	protected void updateOpenFile() throws ModelingFileIOException {
		File file = openFile;
		if (file != null && fileIsPartOfRepository(file, "")) {
			if (file.exists() && file.canRead()) {
				loadFile(file);
				log("Re-loaded an updated file: \"" + file + "\"");
			} else {
				JavaFXUtil.infoBox("The opened file does not exist in this version and is closed now: " + file);
				clearModel();
			}
		}
	}

	protected abstract void drawModel(Model model);

	protected abstract void clearModel();

	protected abstract XmlReader getXmlReader(File openFile) throws ModelingFileIOException;

	protected abstract XmlWriter getXmlWriter(File openFile) throws ModelingFileIOException;

	protected Path getRepositoryDirectoryIfExists() {
		if (VersioningGraphSelectorController.getInstance().getSelectedVersionGraph() == null)
			return null;
		File repositoryDir = VersioningGraphSelectorController.getInstance().getSelectedVersionGraph().getDirectory();
		if (repositoryDir == null)
			return null;
		return repositoryDir.toPath();
	}

	protected Path getRepositoryDirectory() throws VersionGraphException {
		if (VersioningGraphSelectorController.getInstance().getSelectedVersionGraph() == null)
			throw new VersionGraphException(
					"Repository not initialized, please load a Verion Graph on the left hand side");
		File repositoryDir = VersioningGraphSelectorController.getInstance().getSelectedVersionGraph().getDirectory();
		if (repositoryDir == null)
			throw new VersionGraphException("Repository directory not found");
		return repositoryDir.toPath();
	}
}
