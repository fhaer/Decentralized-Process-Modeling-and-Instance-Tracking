package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * A lane is a SwimLane which is used to divide pools. A Lane is always inside a
 * pool.
 *
 */
public interface Lane extends SwimLane {

	/**
	 * Returns the pool, where this lane is located.
	 *
	 * @return pool of this lane
	 */
	public SwimLane getSwimLane();
}
