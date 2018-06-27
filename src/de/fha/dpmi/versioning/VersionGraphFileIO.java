package de.fha.dpmi.versioning;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * file input/output for files containing version graph information, e.g.
 * repository name, repository URL
 */
public class VersionGraphFileIO {

	public static final String SETTINGS_DIRECTORY_NAME = ".dpmi";

	public static final String KNOWN_VERSION_GRAPHS_SETTINGS_FILE_NAME = ".KnownVersionGraphs";

	private Path settingsDirectory;

	public Path getOrCreateSettingsDirectoryPath() throws VersionGraphFileIOException {
		if (settingsDirectory == null)
			createSettingsDirectory();
		return settingsDirectory;
	}

	public void createSettingsDirectory() throws VersionGraphFileIOException {
		Path systemSettingsDir = getSystemUserAppSettingsDirectory();
		if (systemSettingsDir == null)
			throw new VersionGraphFileIOException(
					"Unable to find a system directory for application settings. All changes and settings will not be saved");
		Path settingsPath = systemSettingsDir.resolve(SETTINGS_DIRECTORY_NAME);
		if (!Files.exists(settingsPath)) {
			try {
				Files.createDirectory(settingsPath);
			} catch (IOException e) {
				throw new VersionGraphFileIOException("Unable to create settings directory", settingsPath);
			}
		}
		this.settingsDirectory = settingsPath;
	}

	/**
	 * @return system settings directory or null if no suitable directory is
	 *         found
	 */
	private Path getSystemUserAppSettingsDirectory() {
		final String[] settingsDirectories = { System.getenv("APPDATA"),
				System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH"), System.getenv("HOME"),
				System.getProperty("user.home") + File.pathSeparator + "Library" + File.pathSeparator
						+ "Application Support",
				System.getProperty("user.home") };
		for (String sd : settingsDirectories) {
			File settingsDirFile = new File(sd);
			if (settingsDirFile.exists() && settingsDirFile.canRead() && settingsDirFile.canWrite()) {
				return settingsDirFile.toPath();
			}
		}
		return null;
	}

	public void writeVersionGraphMetadataToFile(Path versionGraphDirectory, String name, String uri)
			throws VersionGraphFileIOException {
		Path settingsFile = getOrCreateSettingsDirectoryPath().resolve(KNOWN_VERSION_GRAPHS_SETTINGS_FILE_NAME);
		int index = -1;
		if (Files.isReadable(settingsFile)) {
			try {
				List<String> lines = Files.readAllLines(settingsFile);
				int i = 0;
				for (String vgData : lines) {
					if (vgData.equals(versionGraphDirectory.toAbsolutePath().toString())) {
						index = i;
					}
					i++;
				}
				if (index != -1) {
					lines.set(index + 1, name);
					lines.set(index + 2, uri);
				} else {
					lines.add(versionGraphDirectory.toAbsolutePath().toString());
					lines.add(name);
					lines.add(uri);
				}
				Files.write(settingsFile, lines);
			} catch (IOException e) {
				throw new VersionGraphFileIOException(
						"Unable to write known Version Graphs to file: " + KNOWN_VERSION_GRAPHS_SETTINGS_FILE_NAME,
						settingsFile);
			}
		} else {
			// new file
			List<String> lines = new ArrayList<>();
			lines.add(versionGraphDirectory.toAbsolutePath().toString());
			lines.add(name);
			lines.add(uri);
			try {
				Files.write(settingsFile, lines);
			} catch (IOException e) {
				throw new VersionGraphFileIOException(
						"Unable to write known Version Graphs to file: " + KNOWN_VERSION_GRAPHS_SETTINGS_FILE_NAME,
						settingsFile);
			}
		}
	}

	public List<String> readVersionGraphMetadataFromFile(Path versionGraphDirectory)
			throws VersionGraphFileIOException {
		Path settingsFile = getOrCreateSettingsDirectoryPath().resolve(KNOWN_VERSION_GRAPHS_SETTINGS_FILE_NAME);
		List<String> vg = new ArrayList<>();
		if (Files.isReadable(settingsFile)) {
			try {
				List<String> lines = Files.readAllLines(settingsFile);
				boolean read = false;
				for (String vgData : lines) {
					if (read)
						vg.add(vgData);
					if (vg.size() == 2)
						read = false;
					if (vgData.equals(versionGraphDirectory.toAbsolutePath().toString())) {
						read = true;
					}
				}
			} catch (IOException e) {
				throw new VersionGraphFileIOException(
						"Unable to read known Version Graphs from file: " + KNOWN_VERSION_GRAPHS_SETTINGS_FILE_NAME,
						settingsFile);
			}
		}
		return vg;
	}

	public void writeKnownVersionGraphsToFile(List<VersionGraph> versionGraphList) throws VersionGraphFileIOException {
		Path settingsFile = getOrCreateSettingsDirectoryPath().resolve(KNOWN_VERSION_GRAPHS_SETTINGS_FILE_NAME);
		if (!Files.isWritable(settingsDirectory))
			throw new VersionGraphFileIOException("Unable to write settings file", settingsFile);
		StringBuilder content = new StringBuilder();
		for (VersionGraph vg : versionGraphList) {
			content.append(vg.getDirectory().toPath().toAbsolutePath());
			content.append(System.lineSeparator());
			content.append(vg.getName());
			content.append(System.lineSeparator());
			content.append(vg.getUri());
			content.append(System.lineSeparator());
		}
		try {
			Files.write(settingsFile, content.toString().getBytes());
		} catch (IOException e) {
			throw new VersionGraphFileIOException("Unable to write settings file", settingsFile);
		}
	}

	public List<String> readKnownVersionGraphDirectoriesFromFile() throws VersionGraphFileIOException {
		Path settingsFile = getOrCreateSettingsDirectoryPath().resolve(KNOWN_VERSION_GRAPHS_SETTINGS_FILE_NAME);
		List<String> vg = new ArrayList<>();
		if (Files.isReadable(settingsFile)) {
			try {
				List<String> lines = Files.readAllLines(settingsFile);
				int i = 0;
				for (String l : lines) {
					if (i % 3 == 0)
						vg.add(l);
					i++;
				}
			} catch (IOException e) {
				throw new VersionGraphFileIOException(
						"Unable to read known Version Graphs from file: " + KNOWN_VERSION_GRAPHS_SETTINGS_FILE_NAME,
						settingsFile);
			}
		}
		return vg;
	}

	private static VersionGraphFileIO instance;

	private VersionGraphFileIO() {
	}

	public synchronized static VersionGraphFileIO getInstance() {
		if (instance == null) {
			instance = new VersionGraphFileIO();
		}
		return instance;
	}

}
