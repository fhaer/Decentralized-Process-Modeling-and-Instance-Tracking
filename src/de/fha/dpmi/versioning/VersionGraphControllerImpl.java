package de.fha.dpmi.versioning;

import java.io.File;
import java.util.List;

import de.fha.dpmi.util.DpmiTooling;
import de.fha.dpmi.view.e4.rcp.parts.JavaFXUtil;
import de.fha.dpmi.view.e4.rcp.parts.VersioningControllerCred;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * implements the version graph controller
 */
public class VersionGraphControllerImpl implements VersionGraphController, DpmiTooling {

	private static VersionGraphController instance;

	private ObservableList<VersionGraph> knownVersionGraphs;

	public static synchronized VersionGraphController getInstance() {
		if (instance == null) {
			instance = new VersionGraphControllerImpl();
		}
		return instance;
	}

	private VersionGraphControllerImpl() {
		knownVersionGraphs = FXCollections.observableArrayList();
		loadLocally();
	}

	@Override
	public VersionGraph createVersionGraph(String name, File path, String uri, String username, String password)
			throws VersionGraphException {
		VersionGraph vg = new VersionGraphImpl(name, path, uri, username, password);
		addVersionGraph(vg);
		return vg;
	}

	@Override
	public VersionGraph createVersionGraph(String name, File path, String uri) throws VersionGraphException {
		String user, password;
		if (!uri.equals(DEMO_REPOSITORY_URI)) {
			String title = "Create a Version Graph";
			user = JavaFXUtil.inputBox(title, "Remote Repository", "Git repository User", "");
			password = JavaFXUtil.inputBox(title, "Remote Repository", "Git repository Password", "");
		} else {
			user = VersioningControllerCred.DEMO_REPOSITORY_US;
			password = VersioningControllerCred.DEMO_REPOSITRY_S;
		}
		VersionGraph vg = new VersionGraphImpl(name, path, uri, user, password);
		addVersionGraph(vg);
		return vg;
	}

	@Override
	public void persistLocally() {
		try {
			VersionGraphFileIO.getInstance().writeKnownVersionGraphsToFile(knownVersionGraphs);
		} catch (VersionGraphFileIOException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	@Override
	public void loadLocally() {
		try {
			for (String dir : VersionGraphFileIO.getInstance().readKnownVersionGraphDirectoriesFromFile()) {
				File dirF = new File(dir);
				List<String> metadata = VersionGraphFileIO.getInstance()
						.readVersionGraphMetadataFromFile(dirF.toPath());
				if (metadata.size() > 1)
					addVersionGraph(metadata.get(0), dirF, metadata.get(1));
			}
		} catch (VersionGraphException | VersionGraphFileIOException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	@Override
	public ObservableList<VersionGraph> getKnownVersionGraphs() {
		return knownVersionGraphs;
	}

	@Override
	public VersionGraph addVersionGraph(String name, File path, String uri, String username, String password)
			throws VersionGraphException {
		VersionGraph vg = new VersionGraphImpl(name, path, uri, username, password);
		addVersionGraph(vg);
		return vg;
	}

	@Override
	public VersionGraph addVersionGraph(String name, File path, String uri) throws VersionGraphException {
		String user, password;
		if (!uri.equals(DEMO_REPOSITORY_URI)) {
			String title = "Create a Version Graph";
			user = JavaFXUtil.inputBox(title, "Remote Repository", "Git repository User", "");
			password = JavaFXUtil.inputBox(title, "Remote Repository", "Git repository Password", "");
		} else {
			user = VersioningControllerCred.DEMO_REPOSITORY_US;
			password = VersioningControllerCred.DEMO_REPOSITRY_S;
		}
		VersionGraph vg = new VersionGraphImpl(name, path, uri, user, password);
		addVersionGraph(vg);
		return vg;
	}

	@Override
	public void addVersionGraph(VersionGraph vg) throws VersionGraphException {
		if (knownVersionGraphs.contains(vg))
			throw new VersionGraphException(
					"This Version Graph is added already, please choose it from the drop down menu of Known Version Graphs");
		writeVersionGraphToFile(vg);
		knownVersionGraphs.add(vg);
		FXCollections.sort(knownVersionGraphs);
	}

	protected void writeVersionGraphToFile(VersionGraph vg) {
		try {
			VersionGraphFileIO.getInstance().writeVersionGraphMetadataToFile(vg.getDirectory().toPath(), vg.getName(),
					vg.getUri());
		} catch (VersionGraphFileIOException e) {
		}
	}

	@Override
	public void removeVersionGraph(VersionGraph vg) {
		knownVersionGraphs.remove(vg);
		persistLocally();
	}

}
