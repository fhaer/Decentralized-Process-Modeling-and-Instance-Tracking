package de.fha.dpmi.versioning;

import java.io.File;

import de.fha.dpmi.util.DpmiTooling;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * implementation base class for DVCS
 */
public abstract class VersionGraphDVCSBase implements VersionGraphDVCS, DpmiTooling {

	protected final String NAME;

	protected final String URI;

	protected final File DIRECTORY;

	private ObservableList<Branch> knownSharedBranches = FXCollections.observableArrayList();
	private ObservableList<Branch> knownPrivateBranches = FXCollections.observableArrayList();

	public VersionGraphDVCSBase(String name, String uri, File directory) {
		this.NAME = name;
		this.URI = uri;
		this.DIRECTORY = directory;
	}

	@Override
	public String toString() {
		return DIRECTORY.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.fha.dpme.versioning.VersionGraphI#getUri()
	 */
	@Override
	public String getUri() {
		return URI;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.fha.dpme.versioning.VersionGraphI#getDirectory()
	 */
	@Override
	public File getDirectory() {
		return DIRECTORY;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.fha.dpme.versioning.VersionGraphI#getKnownBranches()
	 */
	@Override
	public ObservableList<Branch> getKnownSharedBranches() {
		return knownSharedBranches;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.fha.dpmi.versioning.VersionGraph#getKnownPrivateBranches()
	 */
	@Override
	public ObservableList<Branch> getKnownPrivateBranches() {
		return knownPrivateBranches;
	}

	/**
	 * Adds the given Branch to the known Shared Branch list.
	 *
	 * @param b
	 *            branch to add
	 */
	protected void addKnownSharedBranch(Branch b) {
		knownSharedBranches.add(b);
		FXCollections.sort(knownSharedBranches);
	}

	/**
	 * Removes the given Branch from the known Shared Branch list.
	 *
	 * @param b
	 *            branch to remove
	 */
	protected void removeKnownSharedBranch(Branch b) {
		knownSharedBranches.remove(b);
	}

	/**
	 * Adds the given Branch to the known Private Branch list.
	 *
	 * @param b
	 *            branch to add
	 */
	protected void addKnownPrivateBranch(Branch b) {
		knownPrivateBranches.add(b);
		FXCollections.sort(knownPrivateBranches);
	}

	/**
	 * Removes the given Branch from the known Private Branch list.
	 *
	 * @param b
	 *            branch to remove
	 */
	protected void removeKnownPrivateBranch(Branch b) {
		knownPrivateBranches.remove(b);
	}

	/**
	 * Returns true if the given branch is a shared branch known to this Version
	 * Graph.
	 *
	 * @param b
	 *            Branch
	 * @return boolean
	 */
	protected boolean isKnownSharedBranch(Branch b) {
		return knownSharedBranches.contains(b);
	}

	/**
	 * Returns true if a branch with the given name is a shared branch known to
	 * this Version Graph.
	 *
	 * @param branchName
	 *            Branch name
	 * @return boolean
	 */
	protected boolean isKnownSharedBranch(String branchName) {
		for (Branch n : knownSharedBranches)
			if (branchName.equals(n.getBranchName()))
				return true;
		return false;
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
