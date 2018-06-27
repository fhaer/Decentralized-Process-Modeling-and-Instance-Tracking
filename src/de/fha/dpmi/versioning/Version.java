package de.fha.dpmi.versioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

import de.fha.dpmi.hashing.HashValue;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Represents on version on the version graph
 */
public class Version {

	private List<Version> branches = new ArrayList<>();

	private List<Version> merges = new ArrayList<>();

	/**
	 * git commit of this version; set to null before commit
	 */
	private RevCommit revCommit;

	/**
	 * hash associated to the git commit; set to null before commit
	 */
	private HashValue hashValue;

	/**
	 * branch containing this version
	 */
	private Branch branch;

	/**
	 * version identifier
	 */
	private byte[] version;
	private String versionStr;

	private boolean isAgreed = false;
	private boolean isAgreementProcedureFinished = false;
	private boolean isVoteOngoing = false;

	/**
	 * tag associated to the git commit; set to null if no tag is set
	 */
	private String gitTag = null;

	private boolean isCommited = false;

	public Version(Branch b, RevCommit revCommit) {
		this.branch = b;
		this.revCommit = revCommit;
		this.hashValue = new HashValue(revCommit.getName());
	}

	public String toString() {
		return getVersionIdString();
	}

	public String getGitTag() {
		return gitTag;
	}

	public void setGitTag(String gitTag) {
		this.gitTag = gitTag;
	}

	public HashValue getHashValue() {
		return hashValue;
	}

	public byte[] getHashBytes() {
		return hashValue.getByteArray();
	}

	public void setHashValue(HashValue commitHash) {
		this.hashValue = commitHash;
	}

	public boolean isCommited() {
		return isCommited;
	}

	public void setCommited(boolean isCommited) {
		this.isCommited = isCommited;
	}

	public RevCommit getRevCommit() {
		return revCommit;
	}

	public void setRevCommit(RevCommit revCommit) {
		this.revCommit = revCommit;
	}

	public String getVersionIdString() {
		String v = "";
		if (versionStr != null && versionStr.length() > 0)
			return versionStr;
		if (revCommit != null)
			return revCommit.getName().substring(0, 8);
		for (int i = 0; i < version.length; i++) {
			if (i > 0) {
				v += ".";
			}
			v += Byte.toUnsignedInt(version[i]);
		}
		return v;
	}

	public byte[] getVersionBytes(byte[] version) {
		return version;
	}

	public void setVersionBytes(byte[] version) {
		this.version = version;
	}

	public void addBranch(Version v) {
		branches.add(v);
	}

	public List<Version> getBranches() {
		return branches;
	}

	public void setIsAgreed(boolean b) {
		isAgreed = b;
	}

	public void setIsAgreementProcedureCompleted(boolean b) {
		isAgreementProcedureFinished = b;
	}

	public boolean getIsAgreed() {
		return isAgreed;
	}

	public boolean getIsAgreementProcedureFinished() {
		return isAgreementProcedureFinished;
	}

	public void addMerge(Version v) {
		merges.add(v);
	}

	public List<Version> getMerges() {
		return merges;
	}

	@Override
	public int hashCode() {
		String s = getVersionIdString();
		return s.hashCode();
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Version) {
			boolean vMatches = ((Version) obj).getVersionIdString().equals(getVersionIdString());
			boolean bMatches = (((Version) obj).getBranch().equals(branch));
			return (vMatches && bMatches);
		}
		return false;
	}

	public void setIsVoteOngoing(boolean isVoteOngoing) {
		this.isVoteOngoing = isVoteOngoing;
	}

	public boolean getIsVoteOngoing() {
		return isVoteOngoing;
	}
}
