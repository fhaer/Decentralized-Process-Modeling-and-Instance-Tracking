package de.fha.dpmi.modeling.bpmn.elements;

import java.util.ArrayList;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementation of a collaboration.
 *
 */
public class CollaborationImpl implements Collaboration {

	/**
	 * ID
	 */
	protected String id;

	/**
	 * list of all participants in the model
	 */
	protected ArrayList<Participant> participants;

	/**
	 * list of all message flows in the model
	 */
	protected ArrayList<MessageFlow> messageFlows;

	public CollaborationImpl() {
		participants = new ArrayList<Participant>();
		messageFlows = new ArrayList<MessageFlow>();
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
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (object instanceof Participant) {
			if (id != null && ((Participant) object).getId().equals(id))
				return true;
		}
		return false;
	}

	/**
	 * @param messageFlow
	 *            flow message flow to add
	 */
	public void addMessageFlow(MessageFlow messageFlow) {
		if (messageFlows == null)
			throw new IllegalStateException("message flows data structure not initialized");
		for (MessageFlow m : messageFlows)
			if (messageFlow.getId().equals(m.getId()))
				throw new IllegalArgumentException("ID not unique");
		messageFlows.add(messageFlow);
	}

	@Override
	public void addParticipant(Participant participant) {
		if (participant == null)
			throw new IllegalArgumentException("participant is null");
		if (participant.getId().isEmpty())
			throw new IllegalArgumentException("participant has no ID " + participant);
		if (participant instanceof Participant)
			participants.add(participant);
		else
			throw new IllegalArgumentException("unknown participant");
	}

	@Override
	public Participant[] getParticipants() {
		if (participants == null) {
			throw new IllegalStateException("participant data structure not initialisiert");
		}
		return (Participant[]) participants.toArray(new Participant[0]);
	}

	@Override
	public Participant getParticipant(String id) {
		for (Participant participant : participants) {
			if (participant.getId().equals(id)) {
				return participant;
			}
		}
		throw new IllegalArgumentException("Participant with given ID not found: " + id);
	}

	@Override
	public MessageFlow[] getMessageFlows() {
		if (messageFlows == null)
			throw new IllegalStateException("message flows data structure not initialized");
		return (MessageFlow[]) messageFlows.toArray(new MessageFlow[0]);
	}

	@Override
	public void removeMessageFlow(String id) {
		for (MessageFlow mFlow : messageFlows) {
			if (mFlow.getId().equals(id)) {
				messageFlows.remove(mFlow);
				return;
			}
		}
		throw new IllegalArgumentException("A SequenceFlow with the given ID does not exist: " + id);
	}

}
