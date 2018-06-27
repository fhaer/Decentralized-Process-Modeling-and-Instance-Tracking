package de.fha.dpmi.modeling.bpmn;

import de.fha.dpmi.modeling.som.SomModel;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * This interface controls the model transformation. It calls methods to perform
 * the model transformation where new objects in the BPMN model are created and
 * assigned to existing objects in the SOM model.
 */
public interface VtsBpmnTransformation {

	/**
	 * Returns the source of the model transformation
	 *
	 * @return SOM model
	 */
	SomModel getSourceModel();

	/**
	 * Returns the result of the model transformation
	 *
	 * @return BPMN model
	 */
	BpmnModel getDestinationModel();

	/**
	 * Performs model transformation
	 */
	void transform();

}
