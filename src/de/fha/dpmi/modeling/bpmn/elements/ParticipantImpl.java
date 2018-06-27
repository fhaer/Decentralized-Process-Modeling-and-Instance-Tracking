package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementation of the participant interface. This class is for type
 * definition only and does not contain any methods.
 *
 */
public class ParticipantImpl implements Participant {

	/**
	 * ID
	 */
	protected String id;

	/**
	 * name
	 */
	protected String name;

	/**
	 * reference to process
	 */
	protected String processref;

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
	public String getProcessRef() {
		if (processref == null)
			return "";
		return processref;
	}

	@Override
	public void setProcessRef(String ref) {
		this.processref = ref;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (object instanceof Participant) {
			if (id != null && ((Participant) object).getId().equals(id))
				return true;
		}
		return false;
	}
}
