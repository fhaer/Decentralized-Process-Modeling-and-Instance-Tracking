package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementing base class for SwimLanes. This class can not be instantiated,
 * use the sub-types instead.
 *
 */
public abstract class SwimLaneImpl implements SwimLane, Comparable<SwimLane> {

	/**
	 * ID of this Swimlane
	 */
	protected String id;
	/**
	 * name of this Swimlane
	 */
	protected String name;

	/**
	 * Protected constructor to prevent initialization from outside the package.
	 */
	protected SwimLaneImpl() {
	}

	@Override
	public String getId() {
		if (id == null)
			return "";
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getName() {
		if (name == null)
			return "";
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName() + " (ID " + getId() + ")";
	}

	@Override
	public int compareTo(SwimLane o) {
		return o.getId().compareTo(getId());
	}
}
