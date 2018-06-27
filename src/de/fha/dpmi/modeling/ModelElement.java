package de.fha.dpmi.modeling;

import java.util.List;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Represents a single syntactical element in a model
 */
public interface ModelElement {

	/**
	 * adds an outgoing relation to the element
	 *
	 * @param modelRelation
	 */
	void addModelOutRelation(ModelRelationImpl modelRelation);

	/**
	 * adds an incoming relation to the element
	 *
	 * @param modelRelation
	 */
	void addModelInRelation(ModelRelationImpl modelRelation);

	/**
	 * returns all outgoing relations of this element
	 *
	 * @return
	 */
	List<ModelRelationImpl> getModelOutRelations();

	/**
	 * returns all incoming relations for this element
	 *
	 * @return
	 */
	List<ModelRelationImpl> getModelInRelations();

	String getId();

	void setId(String id);

	void setName(String n);

	String getName();

	/**
	 * returns true if the element is to be displayed
	 *
	 * @return
	 */
	boolean getIsVisible();

	/**
	 * sets the position of the model element
	 * 
	 * @param x
	 * @param y
	 */
	void setPosition(double x, double y);

}