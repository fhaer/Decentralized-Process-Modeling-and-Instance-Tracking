package de.fha.dpmi.modeling.bpmn.fileio;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Element;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.modeling.bpmn.BpmnModel;
import de.fha.dpmi.modeling.bpmn.BpmnModelImpl;
import de.fha.dpmi.modeling.bpmn.elements.Collaboration;
import de.fha.dpmi.modeling.bpmn.elements.CollaborationImpl;
import de.fha.dpmi.modeling.bpmn.elements.ConnectingObjectMessageFlowImpl;
import de.fha.dpmi.modeling.bpmn.elements.ConnectingObjectSequenceFlowImpl;
import de.fha.dpmi.modeling.bpmn.elements.FlowObject;
import de.fha.dpmi.modeling.bpmn.elements.FlowObjectEventImpl;
import de.fha.dpmi.modeling.bpmn.elements.FlowObjectGatewayImpl;
import de.fha.dpmi.modeling.bpmn.elements.FlowObjectSubProcessImpl;
import de.fha.dpmi.modeling.bpmn.elements.FlowObjectTaskImpl;
import de.fha.dpmi.modeling.bpmn.elements.Participant;
import de.fha.dpmi.modeling.bpmn.elements.ParticipantImpl;
import de.fha.dpmi.modeling.bpmn.elements.Process;
import de.fha.dpmi.modeling.bpmn.elements.ProcessImpl;
import de.fha.dpmi.modeling.bpmn.elements.SequenceFlow;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.modeling.fileio.XmlReader;
/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * File Input for BPMN XML files
 */
public class BpmnXmlReader extends XmlReader implements BpmnXmlIO {

	/**
	 * resulting model
	 */
	private BpmnModel model;

	/**
	 * A file is required to create a new reader
	 *
	 * @param file
	 *            File to write
	 * @throws ModelingFileIOException
	 */
	public BpmnXmlReader(File file) throws ModelingFileIOException {
		super(file, XMLNS, XMLSCHEMALOCAL);
	}

	/**
	 * An input stream of the XML document is required to create a new reader
	 *
	 * @param xmlInputStream
	 *            input stream with XML document as string
	 */
	public BpmnXmlReader(InputStream xmlInputStream) {
		super(xmlInputStream, XMLNS, XMLSCHEMALOCAL);
	}

	/**
	 * Parses a model
	 */
	@Override
	protected void parseModel(Element root) throws ModelingFileIOException {
		model = new BpmnModelImpl();
		model.setXmlDocument(root.getOwnerDocument());

		List<Element> processes = getElements(root, ELEM_PROCESS);
		Element collaboration = getSingleElement(root, ELEM_COLLABORATION);
		// Element diagram = getSingleElement(root, XMLNSBPMNDI,
		// ELEM_BPMNDI_BPMNDIAGRAM);

		log("Parsing Processes ...");
		for (Element e : processes)
			parseProcess(e);
		log("Parsing Collaboration ...");
		parseCollaboration(collaboration);
		// parseDiagram(diagram);

		isValid = true;
	}

	@Override
	protected Model getModel() {
		return model;
	}

	private void parseProcess(Element elem) throws ModelingFileIOException {
		String id = parseAttributeStr(elem, ATTR_ID);
		if (id != null && !id.isEmpty()) {
			boolean isExecutable = parseAttributeBool(elem, ATTR_EXECUTABLE);
			Process p = new ProcessImpl();
			p.setId(id);
			p.setIsExecutable(isExecutable);
			parseTasks(p, elem);
			parseEvents(p, elem);
			parseGateways(p, elem);
			parseSubProcesses(p, elem);
			parseSequenceFlows(p, elem);
			model.addProcess(p);
			log("Added to model: " + p);
		}
	}

	private void parseTasks(Process proc, Element elem) throws ModelingFileIOException {
		for (Element e : getElements(elem, ELEM_TASK)) {
			String id = parseAttributeStr(e, ATTR_ID);
			String name = parseAttributeStr(e, ATTR_NAME);
			FlowObjectTaskImpl t = new FlowObjectTaskImpl();
			t.setId(id);
			t.setName(name);
			parseIncomingOutgoing(t, e);
			parseExtensionAttribute(t, e);
			proc.addFlowObject(t);
			log("Added to model: " + t);
		}
	}

	private void parseEvents(Process proc, Element elem) throws ModelingFileIOException {
		for (Element e : getElements(elem, ELEM_EVENT_START)) {
			parseEvent(proc, e, FlowObjectEventImpl.Type.StartEvent);
		}
		for (Element e : getElements(elem, ELEM_EVENT_END)) {
			parseEvent(proc, e, FlowObjectEventImpl.Type.EndEvent);
		}
		for (Element e : getElements(elem, ELEM_EVENT_INTERMED_CATCH)) {
			parseEvent(proc, e, FlowObjectEventImpl.Type.IntermediateCatchEvent);
		}
		for (Element e : getElements(elem, ELEM_EVENT_INTERMED_THORW)) {
			parseEvent(proc, e, FlowObjectEventImpl.Type.IntermediateThrowEvent);
		}
	}

	private void parseEvent(Process proc, Element e, FlowObjectEventImpl.Type type) throws ModelingFileIOException {
		String id = parseAttributeStr(e, ATTR_ID);
		String name = parseAttributeStr(e, ATTR_NAME);
		FlowObjectEventImpl event = new FlowObjectEventImpl(type);
		event.setId(id);
		event.setName(name);
		parseIncomingOutgoing(event, e);
		parseExtensionAttribute(event, e);
		proc.addFlowObject(event);
		log("Added to model: " + event);
	}

	private void parseGateways(Process proc, Element elem) throws ModelingFileIOException {
		for (Element e : getElements(elem, ELEM_GATEWAY_EXCL)) {
			parseGateway(proc, e, FlowObjectGatewayImpl.Type.Exclusive);
		}
		for (Element e : getElements(elem, ELEM_GATEWAY_INCL)) {
			parseGateway(proc, e, FlowObjectGatewayImpl.Type.Inclusive);
		}
		for (Element e : getElements(elem, ELEM_GATEWAY_COMP)) {
			parseGateway(proc, e, FlowObjectGatewayImpl.Type.Complex);
		}
		for (Element e : getElements(elem, ELEM_GATEWAY_PARA)) {
			parseGateway(proc, e, FlowObjectGatewayImpl.Type.Parallel);
		}
	}

	private void parseGateway(Process proc, Element e, FlowObjectGatewayImpl.Type type) throws ModelingFileIOException {
		String id = parseAttributeStr(e, ATTR_ID);
		String name = parseAttributeStr(e, ATTR_NAME);
		FlowObjectGatewayImpl gateway = new FlowObjectGatewayImpl(type);
		gateway.setId(id);
		gateway.setName(name);
		parseIncomingOutgoing(gateway, e);
		parseExtensionAttribute(gateway, e);
		proc.addFlowObject(gateway);
		log("Added to model: " + gateway);
	}

	private void parseSubProcesses(Process proc, Element elem) throws ModelingFileIOException {
		for (Element e : getElements(elem, ELEM_SUBPROCESS)) {
			String id = parseAttributeStr(e, ATTR_ID);
			String name = parseAttributeStr(e, ATTR_NAME);
			FlowObjectSubProcessImpl t = new FlowObjectSubProcessImpl();
			t.setId(id);
			t.setName(name);
			parseIncomingOutgoing(t, e);
			parseExtensionAttribute(t, e);
			proc.addFlowObject(t);
			log("Added to model: " + t);
		}
	}

	private void parseIncomingOutgoing(FlowObject flowObj, Element elem) {
		for (Element e : getElements(elem, ELEM_INCOMING)) {
			String sequenceFlowId = e.getTextContent();
			flowObj.addIncoming(sequenceFlowId);
		}
		for (Element e : getElements(elem, ELEM_OUTGOING)) {
			String sequenceFlowId = e.getTextContent();
			flowObj.addOutgoing(sequenceFlowId);
		}
	}

	private void parseExtensionAttribute(FlowObject t, Element e) throws ModelingFileIOException {
		if (elementExists(e, ELEM_EXTENSIONELEMENTS)) {
			Element extElem = getSingleElement(e, ELEM_EXTENSIONELEMENTS);
			Element propElem = getSingleElement(extElem, XMLNSDPMI, ELEM_PROPERTIES);
			for (Element property : getElements(propElem, XMLNSDPMI, ELEM_PROPERTY)) {
				String name = parseAttributeStr(property, ATTR_NAME);
				String value = parseAttributeStr(property, ATTR_VALUE);
				t.addExtensionAttribute(name, value);
				log("Found extension attribute \"" + name + "\" with value: " + value);
			}
		}
	}

	private void parseSequenceFlows(Process proc, Element elem) {
		for (Element e : getElements(elem, ELEM_SEQUENCEFLOW)) {
			String id = parseAttributeStr(e, ATTR_ID);
			String sourceRef = parseAttributeStr(e, ATTR_SOURCEREF);
			String targetRef = parseAttributeStr(e, ATTR_TARGETREF);
			FlowObject source = proc.getFlowObject(sourceRef);
			FlowObject target = proc.getFlowObject(targetRef);
			SequenceFlow s = new ConnectingObjectSequenceFlowImpl(source, target);
			s.setId(id);
			proc.addSequenceFlow(s);
			log("Added to model: " + s);
		}
	}

	private void parseCollaboration(Element elem) throws ModelingFileIOException {
		String id = parseAttributeStr(elem, ATTR_ID);
		if (id != null && !id.isEmpty()) {
			Collaboration c = new CollaborationImpl();
			c.setId(id);
			for (Element pElem : getElements(elem, ELEM_PARTICIPANT))
				parseParticipant(c, pElem);
			for (Element mElem : getElements(elem, ELEM_MESSAGEFLOW))
				parseMessageFlow(c, mElem);
			model.addCollaboration(c);
			log("Added to model: " + c);
		}
	}

	private void parseParticipant(Collaboration c, Element elem) {
		String id = parseAttributeStr(elem, ATTR_ID);
		String name = parseAttributeStr(elem, ATTR_NAME);
		String processref = parseAttributeStr(elem, ATTR_PROCESSREF);
		Participant p = new ParticipantImpl();
		p.setId(id);
		p.setName(name);
		p.setProcessRef(processref);
		c.addParticipant(p);
		log("Added to model: " + p);
	}

	private void parseMessageFlow(Collaboration c, Element elem) throws ModelingFileIOException {
		String id = parseAttributeStr(elem, ATTR_ID);
		String sourceref = parseAttributeStr(elem, ATTR_SOURCEREF);
		String targetref = parseAttributeStr(elem, ATTR_TARGETREF);
		// source, target might be flow objects or participants
		FlowObject sourceFlowObj = model.getFlowObject(sourceref);
		FlowObject targetFlowObj = model.getFlowObject(targetref);
		Participant sourcePart = model.getParticipant(sourceref);
		Participant targetPart = model.getParticipant(targetref);
		ConnectingObjectMessageFlowImpl mf = new ConnectingObjectMessageFlowImpl();
		mf.setId(id);
		mf.setSource(sourceFlowObj);
		mf.setSource(sourcePart);
		mf.setTarget(targetFlowObj);
		mf.setTarget(targetPart);
		c.addMessageFlow(mf);
		log("Added to model: " + mf);
	}

	public void log(String message) {
		// disable logging, output only
		System.out.println(message);
	}

}
