package de.fha.dpmi.versioning;

import java.util.HashMap;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Stores the total order of versions on a version graph
 */
public class VersionsSortedListTotalOrdering {

	/**
	 * all known versions
	 */
	private HashMap<String, UniqueOrderedVersion> orderedVersionSet = new HashMap<>();

	/**
	 * first version in ordering
	 */
	private UniqueOrderedVersion versionFirst = null;

	/**
	 * last version in ordering
	 */
	private UniqueOrderedVersion versionLast = null;

	/**
	 * points to version most recently inserted
	 */
	private UniqueOrderedVersion versionPointer = null;

	/**
	 * total number of versions
	 */
	private int nVersions = 0;

	public void addBranchVersions(Branch b) {

		versionPointer = null;

		for (Version v : b.getVersions()) {
			UniqueOrderedVersion u = insertVersionAfter(v, versionPointer, 0);
			versionPointer = u;
		}
	}

	public void addVersion(Version newV) {
		UniqueOrderedVersion u = insertVersionAfter(newV, versionPointer, 0);
		versionPointer = u;
	}

	public UniqueOrderedVersion addVersionAfter(Version newV, Version insertAfterV) {
		UniqueOrderedVersion insertAfterUV = null;
		if (insertAfterV != null)
			insertAfterUV = orderedVersionSet.get(insertAfterV.getRevCommit().getName());
		versionPointer = insertVersionAfter(newV, insertAfterUV, 0);
		return versionPointer;
	}

	public UniqueOrderedVersion addVersionParallelTo(Version newV, Version insertAfterV) {
		UniqueOrderedVersion insertAfterUV = null;
		if (insertAfterV != null)
			insertAfterUV = orderedVersionSet.get(insertAfterV.getRevCommit().getName());
		versionPointer = insertVersionAfter(newV, insertAfterUV, 1);
		return versionPointer;
	}

	public void addVersionEnd(Version newV) {
		if (versionLast != null) {
			versionPointer = insertVersionAfter(newV, versionLast, 0);
		}
	}

	private UniqueOrderedVersion insertVersionAfter(Version v, UniqueOrderedVersion insertAfter, int orderNrOffset) {
		String versionHash = v.getRevCommit().getName(); // v.getVersionIdString();
															// //
		UniqueOrderedVersion u;
		if (orderedVersionSet.containsKey(versionHash)) {
			u = orderedVersionSet.get(versionHash);
			u.addVersion(v);
		} else {
			u = new UniqueOrderedVersion(versionHash);
			u.addVersion(v);
			insertReferenceAfter(u, insertAfter, orderNrOffset);
			orderedVersionSet.put(versionHash, u);
		}
		return u;
	}

	private void insertReferenceAfter(UniqueOrderedVersion u, UniqueOrderedVersion insertAfter, int orderNrOffset) {
		nVersions++;
		if (versionFirst == null && versionLast == null) {
			versionFirst = u;
			versionLast = u;
			u.setOrderNr(1);
		} else if (versionFirst == versionLast) {
			versionFirst.setNextVersion(u);
			u.setNextVersion(null);
			versionLast = u;
			u.setPrevVersion(versionFirst);
			int orderNr = 2;
			if (orderNrOffset < 2)
				orderNr -= orderNrOffset;
			u.setOrderNr(orderNr);
		} else {
			UniqueOrderedVersion currElem = insertAfter;
			UniqueOrderedVersion nextElem;
			if (currElem == null) {
				// prepare insert before first
				nextElem = versionFirst;
			} else {
				// prepare insert after currElem (at nextElem)
				nextElem = insertAfter.getNextVersion();
			}
			if (currElem != null && nextElem != null) {
				// insert middle
				// int orderNr = nextElem.orderNr - orderNrOffset;
				int orderNr = currElem.orderNr + 1 - orderNrOffset;
				currElem.setNextVersion(u);
				u.setNextVersion(nextElem);
				nextElem.setPrevVersion(u);
				u.setPrevVersion(currElem);
				u.setOrderNr(orderNr);
				incrementOrderNr(u.getNextVersion(), orderNrOffset);
			} else if (currElem != null && nextElem == null) {
				// insert last
				int orderNr = currElem.orderNr + 1 - orderNrOffset;
				currElem.setNextVersion(u);
				u.setNextVersion(null);
				u.setPrevVersion(currElem);
				versionLast = u;
				u.setOrderNr(orderNr);
			} else if (currElem == null && nextElem != null) {
				// insert first
				u.setNextVersion(nextElem);
				nextElem.setPrevVersion(u);
				u.setPrevVersion(null);
				versionFirst = u;
				u.setOrderNr(1);
				incrementOrderNr(u.getNextVersion(), orderNrOffset);
			}
		}
	}

	private void incrementOrderNr(UniqueOrderedVersion version, int orderNrOffset) {
		UniqueOrderedVersion v = version;
		while (v != null) {
			v.orderNr = v.orderNr + 1 - orderNrOffset;
			v = v.getNextVersion();
		}
	}

	public UniqueOrderedVersion getFirst() {
		return versionFirst;
	}

	public UniqueOrderedVersion getLast() {
		return versionLast;
	}

	public int getNVersions() {
		return nVersions;
	}

	public int getOrderNumber(String hashValue) {
		UniqueOrderedVersion u = orderedVersionSet.get(hashValue);
		if (u == null)
			return 0;
		return u.getOrderNr();
	}

}
