package de.fha.dpmi.versioning;

import java.io.File;

import javafx.collections.ObservableList;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Controls the creation, removal, loading and storing of version graphs
 */
public interface VersionGraphController {

	public static final String DEMO_REPOSITORY_URI = "https://gitlab.com/dpmi/demo0x3f.git";

	VersionGraph createVersionGraph(String name, File path, String uri) throws VersionGraphException;

	VersionGraph createVersionGraph(String name, File path, String uri, String username, String password)
			throws VersionGraphException;

	VersionGraph addVersionGraph(String name, File path, String uri) throws VersionGraphException;

	VersionGraph addVersionGraph(String name, File path, String uri, String username, String password)
			throws VersionGraphException;

	void persistLocally();

	void loadLocally();

	ObservableList<VersionGraph> getKnownVersionGraphs();

	void removeVersionGraph(VersionGraph vg);

	void addVersionGraph(VersionGraph vg) throws VersionGraphException;

}
