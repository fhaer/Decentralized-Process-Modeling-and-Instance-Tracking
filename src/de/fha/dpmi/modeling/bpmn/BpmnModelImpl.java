package de.fha.dpmi.modeling.bpmn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.w3c.dom.Document;

import de.fha.dpmi.modeling.ModelImpl;
import de.fha.dpmi.modeling.bpmn.elements.Collaboration;
import de.fha.dpmi.modeling.bpmn.elements.FlowObject;
import de.fha.dpmi.modeling.bpmn.elements.Lane;
import de.fha.dpmi.modeling.bpmn.elements.Participant;
import de.fha.dpmi.modeling.bpmn.elements.Process;
import de.fha.dpmi.modeling.bpmn.elements.SwimLane;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementation of BpmnModel
 *
 */
public class BpmnModelImpl extends ModelImpl implements BpmnModel {

	/**
	 * @see BpmnModel
	 */
	protected Document xmlDocument;

	/**
	 * list of all lanes in the model
	 */
	protected ArrayList<Lane> lanes;

	/**
	 * list of all message flows in the model
	 */
	protected ArrayList<Process> processes;

	/**
	 * list of all message flows in the model
	 */
	protected ArrayList<Collaboration> collaborations;

	/**
	 * Creates a new BpmnModel with random ID.
	 */
	public BpmnModelImpl() {
		this.id = UUID.randomUUID().toString();
		lanes = new ArrayList<Lane>();
		processes = new ArrayList<Process>();
		collaborations = new ArrayList<Collaboration>();
	}

	@Override
	public void addSwimLane(SwimLane swimLane) {
		if (swimLane == null)
			throw new IllegalArgumentException("swimLane is null");
		if (swimLane.getId().isEmpty())
			throw new IllegalArgumentException("swimLane has no ID " + swimLane);
		else if (swimLane instanceof Lane)
			addSwimLane((Lane) swimLane);
		else
			throw new IllegalArgumentException("unknown swimLane");
	}

	/**
	 * adds a swimlane, see method public void addSwimLane(SwimLane swimLane)
	 *
	 * @param lane
	 *            lane where swimlane is contained
	 */
	public void addSwimLane(Lane lane) {
		if (lanes == null)
			throw new IllegalStateException("lanes data structure not initialized");
		for (Lane l : lanes)
			if (lane.getId().equals(l.getId()))
				throw new IllegalArgumentException("ID not unique");
		lanes.add(lane);
	}

	@Override
	public Lane getLane(String id) {
		for (Lane lane : lanes) {
			if (lane.getId().equals(id)) {
				return lane;
			}
		}
		throw new IllegalArgumentException("Lane with given ID not found: " + id);
	}

	@Override
	public String getId() {
		if (id == null)
			return "";
		return id;
	}

	@Override
	public String getName() {
		if (name == null)
			return "";
		return name;
	}

	@Override
	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Name of mode is null");
		this.name = name;
	}

	@Override
	public void addProcess(Process process) {
		if (process == null)
			throw new IllegalArgumentException("process is null");
		if (process.getId().isEmpty())
			throw new IllegalArgumentException("process has no ID " + process);
		if (process instanceof Process)
			processes.add(process);
		else
			throw new IllegalArgumentException("unknown process");
	}

	@Override
	public void addCollaboration(Collaboration collaboration) {
		if (collaboration == null)
			throw new IllegalArgumentException("participant is null");
		if (collaboration.getId().isEmpty())
			throw new IllegalArgumentException("collaboration has no ID " + collaboration);
		if (collaboration instanceof Collaboration)
			collaborations.add(collaboration);
		else
			throw new IllegalArgumentException("unknown collaboration");
	}

	@Override
	public void removeProcess(String id) {
		for (Process p : processes) {
			if (p.getId().equals(id)) {
				processes.remove(p);
				return;
			}
		}
		throw new IllegalArgumentException("A Process with the given ID does not exist: " + id);
	}

	@Override
	public void removeCollaboration(String id) {
		for (Collaboration c : collaborations) {
			if (c.getId().equals(id)) {
				collaborations.remove(c);
				return;
			}
		}
		throw new IllegalArgumentException("A Collaboration with the given ID does not exist: " + id);
	}

	@Override
	public void setXmlDocument(Document document) {
		this.xmlDocument = document;
	}

	@Override
	public Document getXmlDocument() {
		return xmlDocument;
	}

	@Override
	public List<Process> getProcesses() {
		return processes;
	}

	@Override
	public List<Collaboration> getCollaborations() {
		return collaborations;
	}

	@Override
	public FlowObject getFlowObject(String flowObjectID) {
		for (Process proc : processes) {
			for (FlowObject f : proc.getFlowObjects())
				if (flowObjectID.equals(f.getId()))
					return f;
		}
		return null;
	}

	@Override
	public Participant getParticipant(String participantID) {
		for (Collaboration collab : collaborations) {
			for (Participant p : collab.getParticipants())
				if (participantID.equals(p.getId()))
					return p;
		}
		return null;
	}

}
