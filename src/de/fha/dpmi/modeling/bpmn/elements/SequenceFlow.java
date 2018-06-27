package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix H�rer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * A sequence flow is a directed edge between two flow objects inside a pool. It
 * defines the control flow between two nodes. A condition might be set which
 * must be true in order to sequence the control flow.
 *
 */
public interface SequenceFlow extends ConnectingObject {

	/**
	 * Returns the source, where the flow object is outgoing
	 *
	 * @return source FlowObject
	 */
	public FlowObject getSourceFlowObject();

	/**
	 * Returns the destination, where the flow object is incoming
	 *
	 * @return destination FlowObject
	 */
	public FlowObject getDestinationFlowObject();

	/**
	 * Returns a condition which must be true to execute the sequence flow. If
	 * no condition is set, null is returned.
	 */
	public String getCondition();

	/**
	 * Sets a condition which must be true to execute the sequence flow.
	 *
	 * @param condition
	 *            as String
	 */
	public void setCondition(String condition);

	/**
	 * Returns, whether this is a default flow, meaning that it is executed if
	 * no other SequenceFlow with the same Source FlowObject has a true
	 * condition.
	 *
	 * @return true if SequenceFlow is a default flow, false otherwise
	 */
	public boolean isDefaultFlow();

	/**
	 * Makes this SequenceFlow a default flow, meaning that it is executed if no
	 * other SequenceFlow with the same Source FlowObject has a true condition.
	 */
	public void setDefaultFlow();
}
