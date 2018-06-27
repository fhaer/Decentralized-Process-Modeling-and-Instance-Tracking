package de.fha.dpmi.modeling.bpmn;

import java.util.Map;
import java.util.Observable;

import de.fha.dpmi.modeling.ModelElement;
import de.fha.dpmi.modeling.bpmn.elements.ConnectingObjectSequenceFlowImpl;
import de.fha.dpmi.modeling.bpmn.elements.FlowObject;
import de.fha.dpmi.modeling.bpmn.elements.FlowObjectTaskImpl;
import de.fha.dpmi.modeling.bpmn.elements.Process;
import de.fha.dpmi.modeling.bpmn.elements.ProcessImpl;
import de.fha.dpmi.modeling.bpmn.elements.SequenceFlow;
import de.fha.dpmi.modeling.som.SomModel;
import de.fha.dpmi.modeling.som.elements.BusinessObject;
import de.fha.dpmi.modeling.som.elements.BusinessTransaction;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementing base class for model transformation
 *
 */
public class VtsBpmnTransformationImpl extends Observable implements VtsBpmnTransformation {

	/**
	 * SOM model to transform.
	 */
	protected SomModel somModel;
	/**
	 * Resulting BPMN model.
	 */
	protected BpmnModel bpmnModel;

	/**
	 * Assigns a BPMN task node to every SOM business object
	 */
	protected Map<BusinessObject, FlowObjectTaskImpl> transformationMapping;

	@Override
	public BpmnModel getDestinationModel() {
		if (bpmnModel == null)
			transform();
		return bpmnModel;
	}

	@Override
	public SomModel getSourceModel() {
		if (somModel == null)
			throw new IllegalStateException("source model missing");
		return somModel;
	}

	@Override
	public void transform() {
		initialize();
		// VtsBpmnModelTransformationPreparation.prepareModel(somModel);
		try {
			transformModel();
		} catch (Exception e) {
			// TODO: NotTransformableException e
		}
	}

	/**
	 * Creates the destinationModel and performs initialization steps in it
	 *
	 * @return destination model
	 */
	private void initialize() {
		log("Transformation started ...", 0, 2);
		bpmnModel = new BpmnModelImpl();
		bpmnModel.setName(somModel.getName());
		// transformationMapping = new HashMap<FlowObject, Node>();
		if (somModel.getBusinessObjects().size() > 1) {

		}
	}

	/**
	 * Transforms the model in four steps: initialize, pool transformation,
	 * message flow transformation, finalize
	 */
	private void transformModel() {
		Process p = new ProcessImpl();
		for (BusinessObject obj : somModel.getBusinessObjects()) {
			if (obj.getIsVisible()) {
				FlowObject task = new FlowObjectTaskImpl();
				task.setId(obj.getId() + "");
				task.setName(obj.getName());
				p.addFlowObject(task);
			}
		}
		for (BusinessTransaction tx : somModel.getBusinessTransactions()) {
			if (tx.getIsVisible()) {
				ModelElement source = tx.getSource();
				ModelElement target = tx.getTarget();
				FlowObject sourceFlowObj = p.getFlowObject(source.getId());
				FlowObject targetFlowObj = p.getFlowObject(target.getId());
				SequenceFlow sf = new ConnectingObjectSequenceFlowImpl(sourceFlowObj, targetFlowObj);
				sf.setId(tx.getId());
				sf.setName(tx.getName());
				p.addSequenceFlow(sf);
			}
		}
		bpmnModel.addProcess(p);
	}

	private void log(String msg, int x, int y) {
		System.out.println(msg);
	}

}
