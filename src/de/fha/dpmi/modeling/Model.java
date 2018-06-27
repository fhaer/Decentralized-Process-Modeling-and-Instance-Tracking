package de.fha.dpmi.modeling;

import java.util.List;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Represents a generic model
 */
public interface Model {

	/**
	 * returns all model elements
	 *
	 * @return
	 */
	List<ModelElementImpl> getModelElements();

	/**
	 * returns all model relations
	 *
	 * @return
	 */
	List<ModelRelationImpl> getModelRelations();

	/**
	 * returns the unique id of this model
	 *
	 * @return
	 */
	String getId();

	/**
	 * sets the unique id of this model
	 * 
	 * @param id
	 */
	void setId(String id);

	/**
	 * returns model name
	 * 
	 * @return
	 */
	String getName();

	/**
	 * sets model name
	 *
	 * @param name
	 */
	void setName(String name);

}