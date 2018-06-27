package de.fha.dpmi.modeling;

import java.util.ArrayList;
import java.util.List;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implements a model
 */
public abstract class ModelImpl implements Model {

	protected String name;
	protected String id;

	List<ModelElementImpl> modelElements = new ArrayList<>();
	List<ModelRelationImpl> modelRelations = new ArrayList<>();

	public ModelImpl(int id, String name) {
		this.name = name;
		this.id = Integer.toString(id);
	}

	public ModelImpl(String name) {
		this.name = name;
		this.id = Integer.toString(name.hashCode());
	}

	public ModelImpl() {
	}

	protected void addElement(ModelElementImpl modelElement) {
		modelElements.add(modelElement);
	}

	protected void addRelation(ModelRelationImpl modelRelation) {
		modelRelations.add(modelRelation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelI#getModelElements()
	 */
	@Override
	public List<ModelElementImpl> getModelElements() {
		return modelElements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelI#getModelRelations()
	 */
	@Override
	public List<ModelRelationImpl> getModelRelations() {
		return modelRelations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelI#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelI#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.Model#setName()
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}
