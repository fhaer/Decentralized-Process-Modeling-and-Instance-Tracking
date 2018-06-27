package de.fha.dpmi.modeling.bpmn.fileio;

import de.fha.dpmi.modeling.fileio.XmlIO;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * File Input/Output for BPMN XML files
 */
public interface BpmnXmlIO extends XmlIO {

	static final String XMLNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";

	static final String XMLNSBPMNDI = "http://www.omg.org/spec/BPMN/20100524/DI";

	static final String XMLNSDPMI = "http://dpmi.fha.de";
	static final String XMLNSDPMI_PREFIX = "dpmi";

	final String XMLSCHEMA = "http://www.wfmc.org/standards/docs/bpmnxpdl_31.xsd";

	final String XMLSCHEMALOCAL = "/de/bpmnaftool/resources/xmlvalidation/somxml.xsd";

	static final String ELEM_ROOT = "definitions";

	// process
	static final String ELEM_PROCESS = "process";
	static final String ELEM_SUBPROCESS = "subProcess";
	static final String ELEM_SEQUENCEFLOW = "sequenceFlow";
	static final String ELEM_TASK = "task";
	static final String ELEM_GATEWAY_EXCL = "exclusiveGateway";
	static final String ELEM_GATEWAY_INCL = "inclusiveGateway";
	static final String ELEM_GATEWAY_PARA = "parallelGateway";
	static final String ELEM_GATEWAY_COMP = "complexGateway";
	static final String ELEM_EVENT_START = "startEvent";
	static final String ELEM_EVENT_END = "endEvent";
	static final String ELEM_EVENT_INTERMED_THORW = "intermediateThrowEvent";
	static final String ELEM_EVENT_INTERMED_CATCH = "intermediateCatchEvent";
	static final String ELEM_INCOMING = "incoming";
	static final String ELEM_OUTGOING = "outgoing";
	static final String ELEM_MESSAGE_EVENT_DEF = "messageEventDefinition";

	// collaboration
	static final String ELEM_COLLABORATION = "collaboration";
	static final String ELEM_PARTICIPANT = "participant";
	static final String ELEM_MESSAGEFLOW = "messageFlow";

	static final String ATTR_ID = "id";
	static final String ATTR_NAME = "name";
	static final String ATTR_VALUE = "value";
	static final String ATTR_X = "x";
	static final String ATTR_Y = "y";
	static final String ATTR_WIDTH = "width";
	static final String ATTR_HEIGHT = "height";

	// participant
	static final String ATTR_PROCESSREF = "processRef";

	// message flows, sequence flows
	static final String ATTR_SOURCEREF = "sourceRef";
	static final String ATTR_TARGETREF = "targetRef";

	static final String ATTR_EXECUTABLE = "isExecutable";

	// bpmndi bpmndiagram
	static final String ELEM_BPMNDI_BPMNDIAGRAM = "BPMNDiagram";
	static final String ELEM_BPMNDI_BPMNPLANE = "BPMNPlane";
	static final String ELEM_BPMNDI_BPMNSHAPE = "BPMNShape";
	static final String ELEM_BPMNDI_BPMNLABEL = "BPMNLabel";
	static final String ELEM_BPMNDI_BPMNLABELSTYLE = "BPMNLabelStyle";
	static final String ELEM_BPMNDI_BPMNEDGE = "BPMNEdge";
	static final String ELEM_OMGDC_BOUNDS = "Bounds";
	static final String ELEM_OMGDC_WAYPOINT = "waypoint";
	static final String ATTR_BPMNELEMENT = "bpmnElement";

	// extension attribute
	static final String ELEM_EXTENSIONELEMENTS = "extensionElements";
	static final String ELEM_PROPERTIES = "properties";
	static final String ELEM_PROPERTY = "property";
}
