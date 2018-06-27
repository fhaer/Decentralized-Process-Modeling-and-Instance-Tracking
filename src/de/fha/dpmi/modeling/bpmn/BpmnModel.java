package de.fha.dpmi.modeling.bpmn;

import java.util.List;

import org.w3c.dom.Document;

import de.fha.dpmi.modeling.Model;
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
 * Representation of a BPMN model. It constructs, stores and retrieves all model
 * elements.
 */
public interface BpmnModel extends Model {

	/**
	 * Adds a process to the model
	 *
	 * @param process
	 *            to add
	 */
	void addProcess(Process process);

	/**
	 * Adds a collaboration to the model
	 *
	 * @param collaboration
	 *            to add
	 */
	void addCollaboration(Collaboration collaboration);

	/**
	 * Adds a lane to the model if a lane with this ID does not exist.
	 *
	 * @param swimLane
	 *            swimLane to add
	 */
	void addSwimLane(SwimLane swimLane);

	/**
	 * Returns the lane with the given ID
	 *
	 * @param id
	 *            ID of the lane to return
	 * @return lane with given ID
	 */
	Lane getLane(String id);

	/**
	 * Removes a Process from the model
	 *
	 * @param id
	 *            ID of the Process to remove
	 */
	void removeProcess(String id);

	/**
	 * Removes a Collaboration from the model
	 *
	 * @param id
	 *            ID of the Collaboration to remove
	 */
	void removeCollaboration(String id);

	/**
	 * Sets the root xml document from the source xml file
	 *
	 * @param document
	 *            xml document
	 */
	void setXmlDocument(Document document);

	/**
	 * Retrieves the xml document with root xml element node of the source xml
	 * file
	 *
	 * @return root xml document
	 */
	Document getXmlDocument();

	/**
	 * Retrieves a flow object by its ID from a process of this model
	 *
	 * @param flowObjectID
	 *            ID of object to retrieve
	 * @return retrieved FlowObject or null if not found
	 */
	FlowObject getFlowObject(String flowObjectID);

	/**
	 * Retrieves a participant by its ID from a process of this model
	 *
	 * @param participantID
	 *            ID of participant to retrieve
	 * @return retrieved Participant or null if not found
	 */
	Participant getParticipant(String participantID);

	/**
	 * Returns a list of all processes of this model.
	 *
	 * @return process list
	 */
	List<Process> getProcesses();

	/**
	 * Returns a list of all collaborations of this model.
	 *
	 * @return collaborations list
	 */
	List<Collaboration> getCollaborations();
}
