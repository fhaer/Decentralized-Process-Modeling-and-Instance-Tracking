package de.fha.dpmi.modeling.fileio;

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
 * File Input/Output for model files
 */
public class ModelingFileIO {

	public static final String SETTINGS_DIRECTORY_NAME = ".dpmi";

	public static final String BUSINESS_PROCESS_DIRECTORY_NAME = "M-BP";

	public static final String SHARED_WORKFLOW_DIRECTORY_NAME = "M-WF-S";

	public static final String PEER_WORKFLOW_DIRECTORY_NAME = "M-WF-P";

	public static final String SHARED_INSTANCE_DIRECTORY_NAME = "M-IS-S";

	public static final String PEER_INSTANCE_DIRECTORY_NAME = "M-IS-P";

	public static final String CHECK_POINT_STATE_DIRECTORY_NAME = "Checkpoint-State";

	public static final String ACTUAL_STATE_DIRECTORY_NAME = "Actual-State";

	public static final String FILE_EXTENSION_SOM = "xml";

	public static final String FILE_EXTENSION_BPMN = "bpmn";

	private Path settingsDirectory;

	public Path getOrCreateSettingsDirectoryPath() throws ModelingFileIOException {
		if (settingsDirectory == null)
			createSettingsDirectory();
		return settingsDirectory;
	}

	public void createSettingsDirectory() throws ModelingFileIOException {
		Path systemSettingsDir = getSystemUserAppSettingsDirectory();
		if (systemSettingsDir == null)
			throw new ModelingFileIOException(
					"Unable to find a system directory for application settings. All changes and settings will not be saved");
		Path settingsPath = systemSettingsDir.resolve(SETTINGS_DIRECTORY_NAME);
		if (!Files.exists(settingsPath)) {
			try {
				Files.createDirectory(settingsPath);
			} catch (IOException e) {
				throw new ModelingFileIOException("Unable to create settings directory", settingsPath);
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

	public void createModelingSystemDirectories(File directory) throws ModelingFileIOException {
		createModelingSystemDirectories(directory.toPath());
	}

	public void createModelingSystemDirectories(Path repositoryDirectory) throws ModelingFileIOException {
		createRepositoryDirectoryIfNotExists(repositoryDirectory, BUSINESS_PROCESS_DIRECTORY_NAME);
		createRepositoryDirectoryIfNotExists(repositoryDirectory, SHARED_WORKFLOW_DIRECTORY_NAME);
		createRepositoryDirectoryIfNotExists(repositoryDirectory, PEER_WORKFLOW_DIRECTORY_NAME);
		createRepositoryDirectoryIfNotExists(repositoryDirectory, PEER_INSTANCE_DIRECTORY_NAME);
		createRepositoryDirectoryIfNotExists(repositoryDirectory, SHARED_INSTANCE_DIRECTORY_NAME);
	}

	private static Path createRepositoryDirectoryIfNotExists(Path repositoryDirectory, String subDir)
			throws ModelingFileIOException {
		if (repositoryDirectory != null) {
			Path basePath = repositoryDirectory;
			Path subDirPath = basePath.resolve(subDir);
			if (!Files.exists(subDirPath)) {
				try {
					return Files.createDirectory(subDirPath);
				} catch (IOException e) {
					throw new ModelingFileIOException("Unable to create directory in the repository",
							subDirPath.toFile());
				}
			} else {
				return subDirPath;
			}
		}
		return null;
	}

	public static Path getSharedWorkflowDirectory(File directory) {
		return directory.toPath().resolve(SHARED_WORKFLOW_DIRECTORY_NAME);
	}

	public static Path getSharedInstanceDirectory(File directory) {
		return directory.toPath().resolve(SHARED_INSTANCE_DIRECTORY_NAME);
	}

	public static Path getPeerWorkflowDirectory(File directory) {
		return directory.toPath().resolve(PEER_WORKFLOW_DIRECTORY_NAME);
	}

	public static Path getPeerInstanceDirectory(File directory) {
		return directory.toPath().resolve(PEER_INSTANCE_DIRECTORY_NAME);
	}

	public static String[] getSharedBranchDirectoryFilePatterns() {
		String[] filePatterns = { ModelingFileIO.BUSINESS_PROCESS_DIRECTORY_NAME + "/",
				ModelingFileIO.SHARED_WORKFLOW_DIRECTORY_NAME + "/",
				ModelingFileIO.SHARED_INSTANCE_DIRECTORY_NAME + "/", };
		return filePatterns;
	}

	public static String[] getPrivateBranchDirectoryFilePatterns() {
		String[] filePatterns = { ModelingFileIO.PEER_WORKFLOW_DIRECTORY_NAME + "/",
				ModelingFileIO.PEER_INSTANCE_DIRECTORY_NAME + "/" };
		return filePatterns;
	}

	private static ModelingFileIO instance;

	private ModelingFileIO() {
	}

	public synchronized static ModelingFileIO getInstance() {
		if (instance == null) {
			instance = new ModelingFileIO();
		}
		return instance;
	}

	public static Path getInstanceCheckpointStateDirectory(Path repositoryDirectory, boolean isShared)
			throws ModelingFileIOException {
		Path dir;
		if (isShared)
			dir = getSharedInstanceDirectory(repositoryDirectory.toFile());
		else
			dir = getPeerInstanceDirectory(repositoryDirectory.toFile());
		Path csDir = createRepositoryDirectoryIfNotExists(dir, CHECK_POINT_STATE_DIRECTORY_NAME);
		return csDir;
	}

	public static Path getInstanceActualStateDirectory(Path repository, boolean isShared)
			throws ModelingFileIOException {
		Path dir;
		if (isShared)
			dir = getSharedInstanceDirectory(repository.toFile());
		else
			dir = getPeerInstanceDirectory(repository.toFile());
		Path csDir = createRepositoryDirectoryIfNotExists(dir, ACTUAL_STATE_DIRECTORY_NAME);
		return csDir;
	}

	public static Path getInstanceActualStateFile(Path repositoryDirectory, String instanceId, boolean isShared)
			throws ModelingFileIOException {
		Path dir;
		if (isShared)
			dir = getSharedInstanceDirectory(repositoryDirectory.toFile());
		else
			dir = getPeerInstanceDirectory(repositoryDirectory.toFile());
		Path isDir = createRepositoryDirectoryIfNotExists(dir, ACTUAL_STATE_DIRECTORY_NAME);
		Path instanceFile = isDir.resolve(instanceId + ".bpmn");
		return instanceFile;
	}

	public static File getSharedInstanceStateModelFile(Path repository, String instance, boolean isShared)
			throws ModelingFileIOException {
		if (instance == null || instance.isEmpty())
			return null;
		Path file = getInstanceActualStateFile(repository, instance, isShared);
		if (Files.exists(file))
			return file.toFile();
		return null;
	}

	public static List<String> getFileNames(Path dir) {
		List<String> fileNames = new ArrayList<>();
		try (java.nio.file.DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
			for (Path path : directoryStream) {
				fileNames.add(path.getFileName().toString());
			}
		} catch (IOException ex) {
		}
		return fileNames;
	}

}
