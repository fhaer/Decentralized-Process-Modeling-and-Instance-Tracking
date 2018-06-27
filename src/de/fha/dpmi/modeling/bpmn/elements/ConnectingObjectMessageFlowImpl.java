package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * A MessageFlow is a directed edge between Pools or FlowObjects in different
 * pools.
 *
 */
public class ConnectingObjectMessageFlowImpl extends ConnectingObjectImpl implements MessageFlow {

	/**
	 * source of message flow, where the flow object is outgoing
	 */
	protected Participant sourceParticipant;

	/**
	 * destination of message flow, where the flow object is incoming
	 */
	protected Participant destinationParticipant;

	/**
	 * source of message flow, where the flow object is outgoing
	 */
	protected FlowObject sourceFlowObject;

	/**
	 * destination of message flow, where the flow object is incoming
	 */
	protected FlowObject destinationFlowObject;

	public ConnectingObjectMessageFlowImpl() {
	}

	/**
	 * Creates a MessageFlow between Pools or FlowObjects using source and
	 * destination.
	 *
	 * @param source
	 *            source pool where message flow is outgoing
	 * @param destination
	 *            destination FlowObject where the message flow is incoming
	 */
	public ConnectingObjectMessageFlowImpl(Participant source, Participant destination) {
		sourceParticipant = source;
		destinationParticipant = destination;
		sourceFlowObject = null;
		destinationFlowObject = null;
	}

	/**
	 * Creates a MessageFlow between Pools or FlowObjects using source and
	 * destination.
	 *
	 * @param source
	 *            source pool where message flow is outgoing
	 * @param destination
	 *            destination FlowObject where the message flow is incoming
	 */
	public ConnectingObjectMessageFlowImpl(Participant source, FlowObject destination) {
		sourceParticipant = source;
		destinationParticipant = null;
		sourceFlowObject = null;
		destinationFlowObject = destination;
	}

	/**
	 * Creates a MessageFlow between Pools or FlowObjects using source and
	 * destination.
	 *
	 * @param source
	 *            source FlowObject where message flow is outgoing
	 * @param destination
	 *            destination pool where the message flow is incoming
	 */
	public ConnectingObjectMessageFlowImpl(FlowObject source, Participant destination) {
		sourceParticipant = null;
		destinationParticipant = destination;
		sourceFlowObject = source;
		destinationFlowObject = null;
	}

	/**
	 * Creates a MessageFlow between Pools or FlowObjects using source and
	 * destination.
	 *
	 * @param source
	 *            source FlowObject where message flow is outgoing
	 * @param destination
	 *            destination FlowObject where the message flow is incoming
	 */
	public ConnectingObjectMessageFlowImpl(FlowObject source, FlowObject destination) {
		sourceParticipant = null;
		destinationParticipant = null;
		sourceFlowObject = source;
		destinationFlowObject = destination;
	}

	/**
	 * Returns the source, where the object is outgoing
	 *
	 * @return source Pool or null if source is not a pool
	 */
	public Participant getSourceParticipant() {
		return sourceParticipant;
	}

	/**
	 * Returns the destination, where the object is incoming
	 *
	 * @return destination Pool or null if destination is not a pool
	 */
	public Participant getDestinationParticipant() {
		return destinationParticipant;
	}

	/**
	 * Returns the source, where the object is outgoing
	 *
	 * @return source FlowObject or null if source is not a FlowObject.
	 */
	public FlowObject getSourceFlowObject() {
		return sourceFlowObject;
	}

	/**
	 * Returns the destination, where the object is incoming
	 *
	 * @return destination FlowObject or null if destination is not a FlowObject
	 */
	public FlowObject getDestinationFlowObject() {
		return destinationFlowObject;
	}

	/**
	 * Returns whether the source is a pool or a FlowObject.
	 *
	 * @return true if source is a pool, false otherwise.
	 */
	public boolean sourceIsPool() {
		return (sourceParticipant != null);
	}

	/**
	 * Returns, whether the destination is a pool or a FlowObject.
	 *
	 * @return true if destination is a pool, false otherwise.
	 */
	public boolean destinationIsPool() {
		return (destinationParticipant != null);
	}

	public void setSource(FlowObject o) {
		sourceFlowObject = o;
	}

	public void setSource(Participant p) {
		sourceParticipant = p;
	}

	public void setTarget(FlowObject o) {
		destinationFlowObject = o;
	}

	public void setTarget(Participant p) {
		destinationParticipant = p;
	}

	@Override
	public String toString() {
		String source = "";
		String destination = "";
		if (sourceIsPool())
			source = "Pool " + getSourceParticipant();
		else
			source = "FlowObject " + getSourceFlowObject();
		if (destinationIsPool())
			destination = "Pool " + getDestinationParticipant();
		else
			destination = "FlowObject " + getDestinationFlowObject();
		return "MessageFlow " + getId() + ": [" + source + "] -> [" + destination + "]";
	}
}
