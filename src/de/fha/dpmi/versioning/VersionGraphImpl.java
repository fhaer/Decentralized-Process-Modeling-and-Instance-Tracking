package de.fha.dpmi.versioning;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.fha.dpmi.modeling.fileio.ModelingFileIO;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import javafx.collections.ObservableList;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementation of a version graph providing general methods for creation,
 * removal and storage
 */
public class VersionGraphImpl implements VersionGraph {

	final String NAME;

	private ModelingFileIO fileIO;

	private VersionGraphDVCSGit vgGit;

	public VersionGraphImpl(String name, File path, String uri, String username, String password)
			throws VersionGraphException {
		NAME = name;
		vgGit = new VersionGraphDVCSGit(name, path, uri, username, password);
		fileIO = ModelingFileIO.getInstance();
	}

	@Override
	public void addSharedBranch(Branch branch) throws VersionGraphException {
		vgGit.addSharedOrphanBranch(branch);
	}

	@Override
	public void addSharedBranch(Branch branch, Version baseVersion) throws VersionGraphException {
		vgGit.addSharedBranch(branch, baseVersion);
	}

	@Override
	public void removeSharedBranch(Branch b) throws VersionGraphException {
		vgGit.removeSharedBranch(b);
	}

	@Override
	public ObservableList<Branch> getKnownSharedBranches() {
		return vgGit.getKnownSharedBranches();
	}

	@Override
	public ObservableList<Branch> getKnownPrivateBranches() {
		return vgGit.getKnownPrivateBranches();
	}

	@Override
	public Version checkOutSharedBranch(Branch b) throws VersionGraphException {
		Version v = vgGit.checkOutSharedBranch(b);
		try {
			fileIO.createModelingSystemDirectories(vgGit.DIRECTORY);
		} catch (ModelingFileIOException e) {
			throw new VersionGraphException(e);
		}
		updateInstanceStates(v);
		return v;
	}

	@Override
	public Version updateSharedBranch(Branch branch) throws VersionGraphException {
		Version v = vgGit.updateSharedBranch(branch);
		updateInstanceStates(v);
		return v;
	}

	private void updateInstanceStates(Version v) {
		// TODO Auto-generated method stub

	}

	@Override
	public Version checkOutVersion(Version v) throws VersionGraphException {
		return vgGit.checkOutVersion(v);
	}

	@Override
	public Version commit(File f, Branch b) throws VersionGraphException {
		return vgGit.commit(f, b);
	}

	@Override
	public Version commit(Branch branch) throws VersionGraphException {
		String[] filePattern = getFilePatterns(branch);
		return vgGit.commit(branch, filePattern);
	}

	@Override
	public Version branchCommit(Version version, Branch branch) throws VersionGraphException {
		try {
			fileIO.createModelingSystemDirectories(vgGit.DIRECTORY);

			List<Path> peerWorkflowFiles = new ArrayList<>();
			Files.find(ModelingFileIO.getSharedWorkflowDirectory(vgGit.DIRECTORY), 8,
					(file, attr) -> file.toString().endsWith(ModelingFileIO.FILE_EXTENSION_SOM))
					.forEach(peerWorkflowFiles::add);
			Files.find(ModelingFileIO.getSharedWorkflowDirectory(vgGit.DIRECTORY), 8,
					(file, attr) -> file.toString().endsWith(ModelingFileIO.FILE_EXTENSION_BPMN))
					.forEach(peerWorkflowFiles::add);

			List<Path> peerInstanceFiles = new ArrayList<>();
			Files.find(ModelingFileIO.getSharedInstanceDirectory(vgGit.DIRECTORY), 8,
					(file, attr) -> file.toString().endsWith(ModelingFileIO.FILE_EXTENSION_SOM))
					.forEach(peerInstanceFiles::add);
			Files.find(ModelingFileIO.getSharedInstanceDirectory(vgGit.DIRECTORY), 8,
					(file, attr) -> file.toString().endsWith(ModelingFileIO.FILE_EXTENSION_BPMN))
					.forEach(peerInstanceFiles::add);

			Path peerWorkflowDir = ModelingFileIO.getPeerWorkflowDirectory(vgGit.DIRECTORY);
			for (Path f : peerWorkflowFiles) {
				try {
					Files.copy(f, peerWorkflowDir.resolve(f.getFileName()));
				} catch (FileAlreadyExistsException e) {
					// ignore, do not copy
				}
			}

			Path peerInstanceDir = ModelingFileIO.getPeerInstanceDirectory(vgGit.DIRECTORY);
			for (Path f : peerInstanceFiles) {
				try {
					Files.copy(f, peerInstanceDir.resolve(f.getFileName()));
				} catch (FileAlreadyExistsException e) {
					// ignore, do not copy
				}
			}

			String[] filePattern = getFilePatterns(branch);
			Version v = vgGit.branchCommit(version, branch, filePattern);

			return v;
		} catch (IOException | ModelingFileIOException e) {
			throw new VersionGraphException(e);
		}
	}

	@Override
	public Version mergeCommit(Version version, Branch sharedBranchMergeBase) throws VersionGraphException {
		String[] filePattern = getFilePatterns(version.getBranch());
		return vgGit.mergeCommit(version, sharedBranchMergeBase, filePattern);
	}

	@Override
	public String getUri() {
		return vgGit.getUri();
	}

	@Override
	public File getDirectory() {
		return vgGit.getDirectory();
	}

	@Override
	public int getVersionOrderNumber(Version v) {
		return vgGit.getVersionOrderNumber(v);
	}

	private String[] getFilePatterns(Branch branch) {
		if (branch.isSharedBranch()) {
			return ModelingFileIO.getSharedBranchDirectoryFilePatterns();
		} else {
			return ModelingFileIO.getPrivateBranchDirectoryFilePatterns();
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public int compareTo(VersionGraph o) {
		return NAME.compareTo(o.getName());
	}

	@Override
	public List<String> getVersionedFiles(Version v) throws VersionGraphException {
		return vgGit.getVersionedFiles(v);
	}

	@Override
	public void push(Branch b) throws VersionGraphException {
		vgGit.push(b);
	}

	@Override
	public void resetToCommit(Version v) throws VersionGraphException {
		vgGit.resetToCommit(v);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VersionGraph) {
			boolean dirEqual = (((VersionGraph) obj).getDirectory().equals(getDirectory()));
			return dirEqual;
		}
		return false;
	}
}
