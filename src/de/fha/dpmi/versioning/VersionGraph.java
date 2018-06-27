package de.fha.dpmi.versioning;

import java.io.File;
import java.util.List;

import javafx.collections.ObservableList;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * a version graph for model versioning; it consists of branches which contain
 * versions
 */
public interface VersionGraph extends Comparable<VersionGraph> {

	/**
	 * Adds the given Branch to this VersionGraph.
	 *
	 * @param branch
	 *            branch to add
	 */
	void addSharedBranch(Branch branch) throws VersionGraphException;

	/**
	 * Adds the given Branch to this VersionGraph by branching off from the
	 * given baseVersion.
	 *
	 * @param branch
	 *            branch to add
	 * @param baseVersion
	 *            version to base new branch on
	 */
	void addSharedBranch(Branch branch, Version baseVersion) throws VersionGraphException;

	/**
	 * Removes the given Branch from this VersionGraph.
	 *
	 * @param b
	 * @throws VersionGraphException
	 */
	void removeSharedBranch(Branch b) throws VersionGraphException;

	/**
	 * Returns a list of all known Shared Branches, retrieved from remote
	 * repository.
	 *
	 * @return
	 */
	ObservableList<Branch> getKnownSharedBranches();

	/**
	 * Returns a list of all known Private Branches
	 *
	 * @return
	 */
	ObservableList<Branch> getKnownPrivateBranches();

	/**
	 * Checks out the latest Version of a Branch of this VersionGraph.
	 *
	 * @param b
	 *            Branch to check out
	 * @return latest Version
	 * @throws VersionGraphException
	 */
	Version checkOutSharedBranch(Branch b) throws VersionGraphException;

	/**
	 * Updates the given branch of this VersionGraph by fetching changes from
	 * the remote repository.
	 *
	 * @param branch
	 *            Branch to update
	 * @return latest version
	 * @throws VersionGraphException
	 */
	Version updateSharedBranch(Branch branch) throws VersionGraphException;

	/**
	 * Checks out the given Version of this VersionGraph.
	 *
	 * @param v
	 *            Version to check out
	 * @return Specified version in state after check out
	 * @throws VersionGraphException
	 */
	Version checkOutVersion(Version v) throws VersionGraphException;

	/**
	 * Commits the given file to the given Branch of this VersionGraph.
	 *
	 * @param f
	 *            File to commit
	 * @param b
	 *            Branch to commit to
	 * @return Version given to the file
	 * @throws VersionGraphException
	 */
	Version commit(File f, Branch b) throws VersionGraphException;

	/**
	 * Commits file matching the given list of file patterns by adding them to
	 * the stash and committing them
	 *
	 * @param branch
	 *            branch to use for commit
	 * @return Version given to the committed files
	 */
	Version commit(Branch branch) throws VersionGraphException;

	/**
	 * Commits the given file to a new branch.
	 *
	 * @param version
	 *            the created branch is starting from this version
	 * @param branch
	 *            branch newly created branch
	 * @return Version given to the file
	 * @throws VersionGraphException
	 */
	Version branchCommit(Version version, Branch branch) throws VersionGraphException;

	/**
	 * Merges the given version with a shared branch as a merge base.
	 * Outstanding changes are committed beforehand.
	 *
	 * @param version
	 *            version to merge
	 * @param sharedBranchMergeBase
	 *            merge with this branch
	 * @return latest version
	 * @throws VersionGraphException
	 */
	Version mergeCommit(Version version, Branch sharedBranchMergeBase) throws VersionGraphException;

	/**
	 * Returns the URI of the remote repository.
	 *
	 * @return URI
	 */
	String getUri();

	/**
	 * Returns the local directory of the repository of this Version Graph.
	 *
	 * @return local directory
	 */
	File getDirectory();

	/**
	 * Returns the name of this Version Graph.
	 *
	 * @return name
	 */
	String getName();

	/**
	 * Returns the number of the given version in a total order of all versions
	 * known to this Version Graph.
	 *
	 * @param v
	 *            Version
	 * @return order number
	 */
	int getVersionOrderNumber(Version v);

	/**
	 * Returns all files which are part of the given version
	 *
	 * @param v
	 *            version
	 * @return list of files as strings
	 * @throws VersionGraphException
	 */
	List<String> getVersionedFiles(Version v) throws VersionGraphException;

	/**
	 * Executes a push after a commit
	 *
	 * @param b
	 *            branch
	 * @throws VersionGraphException
	 */
	void push(Branch b) throws VersionGraphException;

	/**
	 * Performs a reset to the given commit in mixed mode, i.e. index and ref
	 * are reset, but no files in the working directory are changed
	 *
	 * @param v
	 *            reset to this version
	 * @throws VersionGraphException
	 */
	void resetToCommit(Version v) throws VersionGraphException;
}