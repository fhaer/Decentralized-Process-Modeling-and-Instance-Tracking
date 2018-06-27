package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementation of a Lane
 *
 */
public class LaneImpl extends SwimLaneImpl implements Lane {

	/**
	 * SwimLane where this lane is located
	 */
	SwimLane swimLane;

	/**
	 * Creates a Lane. It is required to specify the swimLane where the lane is
	 * located.
	 *
	 * @param swimLane
	 */
	public LaneImpl(SwimLane swimLane) {
		this.swimLane = swimLane;
	}

	@Override
	public SwimLane getSwimLane() {
		if (swimLane == null)
			throw new IllegalStateException("lane without swimLane");
		return swimLane;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (object instanceof SwimLane) {
			if (id != null && ((SwimLane) object).getId().equals(id))
				return true;
		}
		return false;
	}
}
