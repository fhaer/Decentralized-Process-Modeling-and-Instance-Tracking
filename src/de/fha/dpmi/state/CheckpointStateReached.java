package de.fha.dpmi.state;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Represents a checkpoint state which has been reached already, i.e. the state
 * has been reached during execution, and this object is created to record it
 */
public class CheckpointStateReached extends CheckpointState {

	private String date;

	public CheckpointStateReached(String date, CheckpointState c, ActualState a) {
		super(c.getId() + "-" + a.getId());
		this.setDate(date);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
