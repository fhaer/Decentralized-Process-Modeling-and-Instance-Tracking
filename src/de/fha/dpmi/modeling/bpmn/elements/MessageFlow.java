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
 */
public interface MessageFlow extends ConnectingObject {

	/**
	 * Returns whether the source is a pool or a FlowObject.
	 *
	 * @return true if source is a pool, false otherwise.
	 */
	public boolean sourceIsPool();

	/**
	 * Returns, whether the destination is a pool or a FlowObject.
	 *
	 * @return true if destination is a pool, false otherwise.
	 */
	public boolean destinationIsPool();

	/**
	 * Returns the source, where the object is outgoing
	 *
	 * @return source Pool or null if source is not a pool
	 */
	public Participant getSourceParticipant();

	/**
	 * Returns the destination, where the object is incoming
	 *
	 * @return destination Pool or null if destination is not a pool
	 */
	public Participant getDestinationParticipant();

	/**
	 * Returns the source, where the object is outgoing
	 *
	 * @return source FlowObject or null if source is not a FlowObject.
	 */
	public FlowObject getSourceFlowObject();

	/**
	 * Returns the destination, where the object is incoming
	 *
	 * @return destination FlowObject or null if destination is not a FlowObject
	 */
	public FlowObject getDestinationFlowObject();
}
