package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementation of a Pool
 *
 */
public class PoolImpl extends SwimLaneImpl implements Pool {

	/**
	 * Participant corresponding to this pool
	 */
	Participant participant;

	/**
	 * Creates a Lane. It is required to specify the participant of the pool.
	 *
	 * @param participant
	 */
	public PoolImpl(Participant participant) {
		this.participant = participant;
	}

	@Override
	public Participant getParticipant() {
		if (participant == null)
			throw new IllegalStateException("lane without participant");
		return participant;
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
