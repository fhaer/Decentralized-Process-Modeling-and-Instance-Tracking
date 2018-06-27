package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * A single collaboration with participants and message flows. A process might
 * be referenced by a participant.
 *
 */
public interface Collaboration {

	/**
	 * Returns the ID of this process. If no ID is set, an empty string is
	 * returned.
	 *
	 * @return ID as String
	 */
	public String getId();

	/**
	 * Sets the ID of this process. The ID may be any non-empty String.
	 *
	 * @param id
	 *            ID to set
	 */
	void setId(String id);

	/**
	 * Adds a participant to the model
	 *
	 * @param participant
	 *            participant to add
	 */
	void addParticipant(Participant participant);

	/**
	 * Adds a message flow to the model if and object with this ID does not
	 * exist.
	 *
	 * @param messageFlow
	 *            messageFlow to add
	 */
	void addMessageFlow(MessageFlow messageFlow);

	/**
	 * Returns the participants with the given ID
	 *
	 * @param participant
	 *            ID of the participant to return
	 * @return participant with given ID
	 */
	Participant getParticipant(String participant);

	/**
	 * Returns all participants of this model
	 *
	 * @return array of participants
	 */
	Participant[] getParticipants();

	/**
	 * Returns all message flows of this model
	 *
	 * @return array with all message flows
	 */
	MessageFlow[] getMessageFlows();

	/**
	 * Removes a MessageFlow from the model
	 *
	 * @param id
	 *            ID of the MessageFlow to remove
	 */
	void removeMessageFlow(String id);

}
