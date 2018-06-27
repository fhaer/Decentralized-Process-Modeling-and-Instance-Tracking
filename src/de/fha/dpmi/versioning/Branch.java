package de.fha.dpmi.versioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Represents a branch of the version graph
 */
public class Branch implements Comparable<Branch> {

	private String branchName;
	private String branchDisplayName;

	private final boolean isSharedBranch;

	private List<Version> versions = new ArrayList<>();

	/**
	 * creates a branch
	 *
	 * @param name
	 *            name of branch
	 * @param isSharedBranch
	 *            true if shared branch, false if private branch
	 */
	public Branch(String name, boolean isSharedBranch) {
		this.branchName = name;
		this.branchDisplayName = name;
		this.isSharedBranch = isSharedBranch;
	}

	public String getBranchName() {
		return branchName;
	}

	public String getBranchDisplayName() {
		return branchDisplayName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public void addVersion(Version v) {
		versions.add(v);
	}

	public List<Version> getVersions() {
		return versions;
	}

	public int getNVersions() {
		return versions.size();
	}

	public Version getFirstVersion() {
		if (versions.size() > 0)
			return versions.get(0);
		return null;
	}

	public Version getLatestVersion() {
		if (versions.size() > 0)
			return versions.get(versions.size() - 1);
		return null;
	}

	@Override
	public String toString() {
		return branchName;
	}

	@Override
	public int compareTo(Branch o) {
		return branchName.compareTo(o.getBranchName());
	}

	public void clearVersions() {
		versions.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Branch)
			return ((Branch) obj).getBranchName().equals(branchName);
		return false;
	}

	public boolean isSharedBranch() {
		return isSharedBranch;
	}

	public Version getVersion(RevCommit revCommit) {
		for (Version v : versions)
			if (v.getRevCommit().equals(revCommit))
				return v;
		return null;
	}

}
