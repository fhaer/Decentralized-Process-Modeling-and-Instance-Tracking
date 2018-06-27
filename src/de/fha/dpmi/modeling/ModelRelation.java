package de.fha.dpmi.modeling;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Represents a syntactical relation between two elements in a model
 */
public interface ModelRelation {

	/**
	 * Sets the source object of this relation
	 *
	 * @param s
	 */
	void setSource(ModelElement s);

	/**
	 * Sets the target object of this relation
	 *
	 * @param t
	 */
	void setTarget(ModelElement t);

	/**
	 * Returns the source object of this relation
	 *
	 * @return
	 */
	ModelElement getSource();

	/**
	 * Returns the target object of this relation
	 *
	 * @return
	 */
	ModelElement getTarget();

	/**
	 * Returns the ID of this relation
	 *
	 * @return
	 */
	String getId();

	/**
	 * Sets the ID of this relation
	 *
	 * @param id
	 */
	void setId(String id);

	/**
	 * Sets the name of this relation
	 *
	 * @param n
	 */
	void setName(String n);

	/**
	 * Returns the name of this relation
	 *
	 * @return
	 */
	String getName();

}