package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
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
public class ConnectingObjectSequenceFlowImpl extends ConnectingObjectImpl implements SequenceFlow {

	/**
	 * source of sequence flow, where the flow object is outgoing
	 */
	public final FlowObject source;

	/**
	 * destination of sequence flow, where the flow object is incoming
	 */
	public final FlowObject destination;

	/**
	 * Condition which must be true to execute the sequence flow.
	 */
	protected String condition;

	protected boolean isDefaultFlow;

	/**
	 * Creates a sequence flow without an ID. The ID can be set using the setId
	 * method.
	 *
	 * @param source
	 *            source flow object, where the sequence flow is outgoing
	 * @param destination
	 *            destination flow object, where the sequence flow is incoming
	 */
	public ConnectingObjectSequenceFlowImpl(FlowObject source, FlowObject destination) {
		this.source = source;
		this.destination = destination;
		this.isDefaultFlow = false;
	}

	/**
	 * Creates a sequence flow using a given ID.
	 *
	 * @param source
	 *            source flow object, where the sequence flow is outgoing
	 * @param destination
	 *            destination flow object, where the sequence flow is incoming
	 * @param id
	 *            unique identifier of the sequence flow, can be any string, but
	 *            not an empty string.
	 */
	public ConnectingObjectSequenceFlowImpl(FlowObject source, FlowObject destination, String id) {
		super(id);
		this.source = source;
		this.destination = destination;
	}

	/**
	 * Returns a condition which must be true to execute the sequence flow. If
	 * no condition is set, null is returned.
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * Sets a condition which must be true to execute the sequence flow.
	 *
	 * @param condition
	 *            as String
	 */
	public void setCondition(String condition) {
		if (condition == null)
			throw new IllegalArgumentException("attempt to set a condition to null");
		this.condition = condition;
	}

	/**
	 * Returns the source, where the flow object is outgoing
	 *
	 * @return source FlowObject
	 */
	public FlowObject getSourceFlowObject() {
		if (source == null)
			throw new IllegalStateException("unknown source");
		return source;
	}

	/**
	 * Returns the destination, where the flow object is incoming
	 *
	 * @return destination FlowObject
	 */
	public FlowObject getDestinationFlowObject() {
		if (destination == null)
			throw new IllegalStateException("unknwon destination");
		return destination;
	}

	/**
	 * Returns, whether this is a default flow, meaning that it is executed if
	 * no other SequenceFlow with the same Source FlowObject has a true
	 * condition.
	 *
	 * @return true if SequenceFlow is a default flow, false otherwise
	 */
	public boolean isDefaultFlow() {
		return isDefaultFlow;
	}

	/**
	 * Makes this SequenceFlow a default flow, meaning that it is executed if no
	 * other SequenceFlow with the same Source FlowObject has a true condition.
	 */
	public void setDefaultFlow() {
		isDefaultFlow = true;
	}

	@Override
	public String toString() {
		return "SequenceFlow " + getId() + ": [" + source + "] -> [" + destination + "]";
	}

}
