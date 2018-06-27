package de.fha.dpmi.state;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Represents an instance state which is used as a checkpoint, i.e. when the
 * state is reached during execution, a record is made and involved parties can
 * be notified
 */
public class CheckpointState extends InstanceState {

	public CheckpointState(String id) {
		super(id);
	}

}
