package de.fha.dpmi.modeling.bpmn.elements;

import java.util.ArrayList;
import java.util.List;

import de.fha.dpmi.modeling.bpmn.BpmnModel;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementation of the participant interface. This class is for type
 * definition only and does not contain any methods.
 *
 */
public class ProcessImpl implements Process {

	/**
	 * ID
	 */
	protected String id;

	/**
	 * whether the process can be executed on an engine
	 */
	protected boolean isExecutable;

	/**
	 * list of all flow objects in the model
	 */
	protected ArrayList<FlowObject> flowObjects;

	/**
	 * list of all sequence flows in the model
	 */
	protected ArrayList<SequenceFlow> sequenceFlows;

	public ProcessImpl() {
		sequenceFlows = new ArrayList<>();
		flowObjects = new ArrayList<>();
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

	@Override
	public boolean getIsExecutable() {
		return isExecutable;
	}

	@Override
	public void setIsExecutable(boolean isExecutable) {
		this.isExecutable = isExecutable;
	}

	/**
	 * @see BpmnModel
	 * @param sequenceFlow
	 *            sequence flow to add
	 */
	public void addSequenceFlow(SequenceFlow sequenceFlow) {
		if (sequenceFlows == null)
			throw new IllegalStateException("sequence flow data structure not initialized");
		for (SequenceFlow s : sequenceFlows)
			if (sequenceFlow.getId().equals(s.getId()))
				throw new IllegalArgumentException("ID not unique");
		sequenceFlows.add(sequenceFlow);
	}

	@Override
	public void addFlowObject(FlowObject flowObject) {
		if (flowObject == null)
			throw new IllegalArgumentException("flow object is null");
		if (flowObjects == null)
			throw new IllegalStateException("flow object data structure not initialized");
		if (flowObject.getId().isEmpty())
			throw new IllegalArgumentException("flowObject has no ID " + flowObject);
		for (FlowObject f : flowObjects)
			if (flowObject.getId().equals(f.getId()))
				throw new IllegalArgumentException("ID not unique");
		flowObjects.add(flowObject);
	}

	@Override
	public FlowObject[] getChildren(FlowObject flowObject) {
		if (flowObject == null)
			throw new IllegalArgumentException("flow object is null");
		if (flowObjects == null)
			throw new IllegalStateException("flow objects data structure not initialized");

		ArrayList<FlowObject> children = new ArrayList<FlowObject>();

		// return default flow as last child
		SequenceFlow defaultFlow = null;
		for (SequenceFlow sFlow : sequenceFlows) {
			if (sFlow.getSourceFlowObject().equals(flowObject)) {
				if (sFlow.isDefaultFlow()) {
					defaultFlow = sFlow;
				} else {
					children.add(sFlow.getDestinationFlowObject());
				}
			}
		}
		if (defaultFlow != null) {
			children.add(defaultFlow.getDestinationFlowObject());
		}

		return (FlowObject[]) children.toArray(new FlowObject[0]);
	}

	@Override
	public FlowObject[] getParents(FlowObject flowObject) {
		if (flowObject == null)
			throw new IllegalArgumentException("flow object is null");
		if (flowObjects == null)
			throw new IllegalStateException("flow objects data structure not initialized");

		ArrayList<FlowObject> parents = new ArrayList<FlowObject>();

		for (SequenceFlow sFlow : sequenceFlows) {
			if (sFlow.getDestinationFlowObject().equals(flowObject)) {
				parents.add(sFlow.getSourceFlowObject());
			}
		}

		return (FlowObject[]) parents.toArray(new FlowObject[0]);
	}

	@Override
	public SequenceFlow getSequenceFlow(FlowObject source, FlowObject destination) {
		if (source == null)
			throw new IllegalArgumentException("Source of SequenceFlow is null");
		if (destination == null)
			throw new IllegalArgumentException("Target of SequenceFlow is null");
		if (sequenceFlows == null)
			throw new IllegalStateException("sequence flows data structure not initialized");

		for (SequenceFlow sFlow : sequenceFlows) {
			if (sFlow.getSourceFlowObject().equals(source) && sFlow.getDestinationFlowObject().equals(destination)) {
				return sFlow;
			}
		}
		return null;
	}

	@Override
	public SequenceFlow[] getSequenceFlows(FlowObject node, boolean isSourceNode) {
		if (node == null)
			throw new IllegalArgumentException("node is null");
		if (sequenceFlows == null)
			throw new IllegalStateException("sequence flows data structure not initialized");

		ArrayList<SequenceFlow> sFlowsNode = new ArrayList<SequenceFlow>();

		for (SequenceFlow sFlow : sequenceFlows) {
			FlowObject compareTo = sFlow.getDestinationFlowObject();
			if (isSourceNode) {
				compareTo = sFlow.getSourceFlowObject();
			}
			if (node.equals(compareTo)) {
				sFlowsNode.add(sFlow);
			}
		}
		return (SequenceFlow[]) sFlowsNode.toArray(new SequenceFlow[0]);
	}

	@Override
	public FlowObject getFlowObject(String id) {
		for (FlowObject flowObject : flowObjects) {
			if (flowObject.getId().equals(id)) {
				return flowObject;
			}
		}
		throw new IllegalArgumentException("A FlowObject with given ID does not exist: " + id);
	}

	@Override
	public void removeFlowObject(String id) {
		for (FlowObject flowObject : flowObjects) {
			if (flowObject.getId().equals(id)) {
				// remove sequenceflows where flowObject is source or
				// destination
				SequenceFlow[] sourceFlows = getSequenceFlows(flowObject, true);
				SequenceFlow[] destFlows = getSequenceFlows(flowObject, false);
				for (SequenceFlow sFlow : sourceFlows) {
					removeSequenceFlow(sFlow.getId());
				}
				for (SequenceFlow sFlow : destFlows) {
					removeSequenceFlow(sFlow.getId());
				}
				flowObjects.remove(flowObject);
				return;
			}
		}
		throw new IllegalArgumentException("A FlowObject with given ID does not exist: " + id);
	}

	@Override
	public void removeSequenceFlow(String id) {
		for (SequenceFlow sFlow : sequenceFlows) {
			if (sFlow.getId().equals(id)) {
				sequenceFlows.remove(sFlow);
				return;
			}
		}
		throw new IllegalArgumentException("A MessageFlow with the given ID does not exist: " + id);
	}

	@Override
	public List<FlowObject> getFlowObjects() {
		return flowObjects;
	}

}
