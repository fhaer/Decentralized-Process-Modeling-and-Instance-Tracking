package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * A participant of a collaboration is depicted as a pool, where a pool is used
 * to divide FlowObjects from one another, it might contain one or more Lanes.
 * This Interface is only used as a type definition and does only provide
 * inherited methods.
 *
 */
public interface Participant {

	/**
	 * Returns the ID of this participant. If no ID is set, an empty string is
	 * returned.
	 *
	 * @return ID as String
	 */
	public String getId();

	/**
	 * Sets the ID of this participant. The ID may be any non-empty String.
	 *
	 * @param id
	 *            ID to set
	 */
	void setId(String id);

	/**
	 * Returns the name of this participant. If no name is set, an empty string
	 * is returned.
	 *
	 * @return name as String
	 */
	public String getName();

	/**
	 * Sets the name of this participant. The name may be any non-empty String.
	 *
	 * @param name
	 *            name to set
	 */
	void setName(String name);

	/**
	 * Returns the reference to the process this participant is part of or empty
	 * string, if no reference is set
	 *
	 * @return reference as String
	 */
	public String getProcessRef();

	/**
	 * Sets the reference to the process this participant is part of
	 *
	 * @param ref
	 *            reference to set
	 */
	void setProcessRef(String ref);
}
