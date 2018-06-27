package de.fha.dpmi.modeling;

import java.util.ArrayList;
import java.util.List;

import de.fha.dpmi.view.e4.rcp.parts.ModelingSomController.ModelElementOnClickOnDragListener;
import javafx.scene.Group;
import javafx.scene.shape.Shape;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implements a model element
 */
public class ModelElementImpl implements ModelElement {

	List<ModelRelationImpl> modelOutRelations = new ArrayList<>();
	List<ModelRelationImpl> modelInRelations = new ArrayList<>();

	protected boolean isVisible;

	private Shape shape;
	private Group group;
	protected ModelElementOnClickOnDragListener draggableShape;

	protected String name;
	protected String id;
	protected String ethereumAccount;

	protected double x;
	protected double y;

	public ModelElementImpl() {
	}

	public ModelElementImpl(int id, String name) {
		this.name = name;
		this.id = Integer.toString(id);
	}

	public ModelElementImpl(String name) {
		this.name = name;
		this.id = Integer.toString(name.hashCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelElement#addModelOutRelation(de.fha.dpmi.
	 * modeling.ModelRelation)
	 */
	@Override
	public void addModelOutRelation(ModelRelationImpl modelRelation) {
		modelOutRelations.add(modelRelation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fha.dpmi.modeling.ModelElement#addModelInRelation(de.fha.dpmi.modeling
	 * .ModelRelation)
	 */
	@Override
	public void addModelInRelation(ModelRelationImpl modelRelation) {
		modelInRelations.add(modelRelation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelElement#getModelOutRelations()
	 */
	@Override
	public List<ModelRelationImpl> getModelOutRelations() {
		return modelOutRelations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelElement#getModelInRelations()
	 */
	@Override
	public List<ModelRelationImpl> getModelInRelations() {
		return modelInRelations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelElement#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelElement#setId(int)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelElement#setName(java.lang.String)
	 */
	@Override
	public void setName(String n) {
		name = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelElement#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public void setDraggableShape(ModelElementOnClickOnDragListener draggableShape) {
		this.draggableShape = draggableShape;
	}

	public ModelElementOnClickOnDragListener getShapeDraggableShape() {
		return draggableShape;
	}

	public void setShape(Shape s) {
		shape = s;
	}

	public Shape getShape() {
		return shape;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelElement#getIsVisible()
	 */
	@Override
	public boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelElement#setPosition(double, double)
	 */
	@Override
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void setEthereumIdentity(String identity) {
		ethereumAccount = identity;
	}

	public String getEthereumIdentity() {
		return ethereumAccount;
	}

}
