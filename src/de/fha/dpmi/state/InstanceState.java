package de.fha.dpmi.state;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Represents the state of an instance of an executed process or workflow
 */
public class InstanceState {

	private String id = "";

	public InstanceState(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CheckpointState)
			return ((CheckpointState) obj).getId().equals(getId());
		if (obj instanceof InstanceState)
			return ((InstanceState) obj).getId().equals(getId());
		return false;
	}
}
