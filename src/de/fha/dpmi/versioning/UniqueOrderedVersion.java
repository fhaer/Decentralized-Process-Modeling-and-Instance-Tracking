package de.fha.dpmi.versioning;

import java.util.ArrayList;
import java.util.List;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Stores versions and computes a total order of given versions
 */
public class UniqueOrderedVersion {

	private String hashValue;

	private UniqueOrderedVersion prevVersion;
	private UniqueOrderedVersion nextVersion;

	private List<Version> versions = new ArrayList<>();

	protected int orderNr = 0;

	public String getVersionHash() {
		return hashValue;
	}

	public UniqueOrderedVersion(Version v) {
		hashValue = v.getRevCommit().getName();
	}

	public UniqueOrderedVersion(String hashValue) {
		this.hashValue = hashValue;
	}

	@Override
	public int hashCode() {
		return hashValue.hashCode();
	}

	public UniqueOrderedVersion getPrevVersion() {
		return prevVersion;
	}

	public void setPrevVersion(UniqueOrderedVersion prevVersion) {
		this.prevVersion = prevVersion;
	}

	public UniqueOrderedVersion getNextVersion() {
		return nextVersion;
	}

	public void setNextVersion(UniqueOrderedVersion nextVersion) {
		this.nextVersion = nextVersion;
	}

	public void addVersion(Version v) {
		versions.add(v);
	}

	public Version getVersion(Branch b) {
		for (Version v : versions) {
			if (v.getBranch().equals(b)) {
				return v;
			}
		}
		return null;
	}

	public int getOrderNr() {
		return orderNr;
	}

	public void setOrderNr(int orderNr) {
		this.orderNr = orderNr;
	}
}
