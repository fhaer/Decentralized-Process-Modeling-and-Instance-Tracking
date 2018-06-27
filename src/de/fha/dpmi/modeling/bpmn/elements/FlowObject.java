package de.fha.dpmi.modeling.bpmn.elements;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import de.fha.dpmi.modeling.ModelElement;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Representation of a Flow Object which is a Activity, Event or Gateway.
 *
 */
public interface FlowObject extends ModelElement {

	/**
	 * Returns the ID of this flow object. If no ID is set, an empty string is
	 * returned.
	 *
	 * @return ID as String
	 */
	public String getId();

	/**
	 * Sets the ID of this flow object. The ID may be any non-empty String.
	 *
	 * @param id
	 *            ID to set
	 */
	void setId(String id);

	/**
	 * Returns the name of this flow object. If no name is set, an empty string
	 * is returned.
	 *
	 * @return name as String
	 */
	public String getName();

	/**
	 * Sets the name of this flow object. The name may be any non-empty String.
	 *
	 * @param name
	 *            name to set
	 */
	void setName(String name);

	/**
	 * Adds a connected sequence flow ID, direction: incoming
	 *
	 * @param sequenceFlowIDs
	 */
	void addIncoming(String sequenceFlowIDs);

	/**
	 * Adds a connected sequence flow ID, direction: outgoing
	 *
	 * @param sequenceFlowIDs
	 */
	void addOutgoing(String sequenceFlowIDs);

	/**
	 * returns all connected sequence flow IDs, direction: incoming
	 *
	 */
	List<String> getIncoming();

	/**
	 * returns all connected sequence flow IDs, direction: outgoing
	 */
	List<String> getOutgoing();

	/**
	 * adds a key-value pair with arbitrary string data as extension attribute
	 * to this FlowObject
	 *
	 * @param name
	 *            name/key of attribute
	 * @param value
	 *            value of attribute
	 */
	void addExtensionAttribute(String name, String value);

	/**
	 * returns a list of all extension attributes of this FlowObject which have
	 * the given name
	 *
	 * @param name
	 *            name of extension attributes to retrieve
	 * @return list of extension attributes as strings
	 */
	List<String> getExtensionAttributes(String name);

	/**
	 * returns a list of all extension attributes of this FlowObject
	 *
	 * @return list of extension attributes with key as name of attribute and
	 *         value as value
	 */
	public List<SimpleEntry<String, String>> getExtensionAttributes();

	/**
	 * returns whether this object has any extension attributes set
	 *
	 * @return boolean
	 */
	boolean hasExtensionAttributes();

}
