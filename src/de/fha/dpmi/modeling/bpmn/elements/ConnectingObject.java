package de.fha.dpmi.modeling.bpmn.elements;

import de.fha.dpmi.modeling.ModelRelation;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Representation of a Connecting Object which is either a MessageFlow or a
 * SequenceFlow
 */
public interface ConnectingObject extends ModelRelation {

	/**
	 * Returns the ID of this connecting object. If no ID is set, an empty
	 * string is returned.
	 *
	 * @return ID as String
	 */
	public String getId();

	/**
	 * Sets the ID of this connecting object. The ID may be any non-empty
	 * String.
	 *
	 * @param id
	 *            ID to set
	 */
	void setId(String id);

}
