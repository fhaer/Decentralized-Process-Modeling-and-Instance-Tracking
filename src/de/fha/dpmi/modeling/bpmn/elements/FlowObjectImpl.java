package de.fha.dpmi.modeling.bpmn.elements;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import de.fha.dpmi.modeling.ModelElementImpl;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementing base class for flow objects
 *
 */
public abstract class FlowObjectImpl extends ModelElementImpl implements FlowObject, Comparable<FlowObject> {

	/**
	 * Unique identifier for this object, may be any String, but not an empty
	 * string.
	 */
	protected String id;

	/**
	 * Displayed name for this object, may be any String, but not an empty
	 * string.
	 */
	protected String name;

	/**
	 * All incoming sequence flow IDs, "ending" in this object.
	 */
	protected List<String> incoming;

	/**
	 * list of all extension attributes of this FlowObject
	 */
	protected List<SimpleEntry<String, String>> extensionAttributes;

	/**
	 * All outgoing sequence flow IDs, "beginning" in this object.
	 */
	protected List<String> outgoing;

	public FlowObjectImpl() {
		incoming = new ArrayList<>();
		outgoing = new ArrayList<>();
		extensionAttributes = new ArrayList<>();
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
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (object instanceof FlowObject) {
			if (id != null && ((FlowObject) object).getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(FlowObject o) {
		return o.getId().compareTo(getId());
	}

	@Override
	public void addIncoming(String sequenceFlowIDs) {
		incoming.add(sequenceFlowIDs);
	}

	@Override
	public void addOutgoing(String sequenceFlowIDs) {
		outgoing.add(sequenceFlowIDs);
	}

	@Override
	public List<String> getIncoming() {
		return incoming;
	}

	@Override
	public List<String> getOutgoing() {
		return outgoing;
	}

	@Override
	public void addExtensionAttribute(String name, String value) {
		extensionAttributes.add(new SimpleEntry<String, String>(name, value));
	}

	@Override
	public List<String> getExtensionAttributes(String name) {
		List<String> values = new ArrayList<>();
		for (SimpleEntry<String, String> entry : extensionAttributes) {
			if (entry.getKey().equals(name))
				values.add(entry.getValue());
		}
		return values;
	}

	@Override
	public List<SimpleEntry<String, String>> getExtensionAttributes() {
		return extensionAttributes;
	}

	@Override
	public boolean hasExtensionAttributes() {
		if (extensionAttributes.size() > 0)
			return true;
		return false;
	}
}
