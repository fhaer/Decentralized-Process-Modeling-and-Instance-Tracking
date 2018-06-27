package de.fha.dpmi.modeling.bpmn.elements;

import java.util.List;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * A single process, e.g. part of a collaboration. A process might be referenced
 * by a participant.
 *
 */
public interface Process {

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
	 * Returns whether this process is executable
	 *
	 * @return isExecutable boolean
	 */
	public boolean getIsExecutable();

	/**
	 * Sets this process executable flag
	 *
	 * @param isExecutable
	 *            boolean to set
	 */
	void setIsExecutable(boolean isExecutable);

	/**
	 * Adds a sequence flow to the model if and object with this ID does not
	 * exist.
	 *
	 * @param sequenceFlow
	 *            sequenceFlow to add
	 */
	void addSequenceFlow(SequenceFlow sequenceFlow);

	/**
	 * Adds a flow object to the model if and object with this ID does not
	 * exist.
	 *
	 * @param flowObject
	 *            flowObject to add
	 */
	void addFlowObject(FlowObject flowObject);;

	/**
	 * Returns all child nodes of type FlowObject of a FlowObject, i.e. the
	 * destination of all SequenceFlows with the given source (flowObject).
	 *
	 * @param flowObject
	 *            source of the sequence flow where children are the destination
	 * @return children of the given FlowObject
	 */
	FlowObject[] getChildren(FlowObject flowObject);

	/**
	 * Returns all parent nodes of type FlowObject of a FlowObject, i.e. the
	 * source of all SequenceFlows with the given destination (flowObject).
	 *
	 * @param flowObject
	 *            destination of the sequence flow where parents are the source
	 * @return children of the given FlowObject
	 */
	FlowObject[] getParents(FlowObject flowObject);

	/**
	 * Returns the sequence flow which has the given source and destination
	 * FlowObject.
	 *
	 * @param source
	 *            source of the sequence flow
	 * @param destination
	 *            destination of the sequence flow
	 * @return sequence flow between source and destination
	 */
	SequenceFlow getSequenceFlow(FlowObject source, FlowObject destination);

	/**
	 * Returns all sequence flows where the given node is source or destination
	 * node
	 *
	 * @param node
	 *            source or destination of the sequence flows
	 * @param isSourceNode
	 *            if true, node is the source node, if false, node is the
	 *            destination node
	 * @return sequence flows with given source or destination node
	 */
	SequenceFlow[] getSequenceFlows(FlowObject node, boolean isSourceNode);

	/**
	 * Returns a FlowObject with the given ID
	 *
	 * @param id
	 *            ID of the FlowObject to return
	 * @return FlowObject with the given ID
	 */
	FlowObject getFlowObject(String id);

	/**
	 * Returns all FlowObjects of this process
	 *
	 */
	List<FlowObject> getFlowObjects();

	/**
	 * Removes a FlowObject from the model. Also searches for SequenceFlows
	 * where the removed FlowObject is source or destionation and removes those
	 * as well.
	 *
	 * @param id
	 *            ID of the SequenceFlow to remove
	 */
	void removeFlowObject(String id);

	/**
	 * Removes a SequenceFlow from the model
	 *
	 * @param id
	 *            ID of the SequenceFlow to remove
	 */
	void removeSequenceFlow(String id);

}
