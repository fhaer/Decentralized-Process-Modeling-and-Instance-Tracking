package de.fha.dpmi.modeling;

import java.util.ArrayList;
import java.util.List;

import de.fha.dpmi.view.e4.rcp.parts.ModelingSomController.ModelElementOnClickOnDragListener;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementation of a model element
 */
public class ModelRelationImpl implements ModelRelation {

	protected String name;
	protected String id;

	protected ModelElement source;
	protected ModelElement target;

	protected ModelElementOnClickOnDragListener draggableShape;
	protected ModelElementOnClickOnDragListener draggableShapeText;

	protected boolean isVisible;

	protected List<Point2D> edgeSupportPoints = new ArrayList<>();

	protected List<Shape> shapes = new ArrayList<>();

	protected double textX = 10;
	protected double textY = 10;

	public ModelRelationImpl() {
	}

	public ModelRelationImpl(int id, String name) {
		this.name = name;
		this.id = Integer.toString(id);
	}

	public ModelRelationImpl(String name) {
		this.name = name;
		this.id = Integer.toString(name.hashCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelRelation#setSource(de.fha.dpmi.modeling.
	 * ModelElement)
	 */
	@Override
	public void setSource(ModelElement s) {
		source = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelRelation#setTarget(de.fha.dpmi.modeling.
	 * ModelElement)
	 */
	@Override
	public void setTarget(ModelElement t) {
		target = t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelRelation#getSource()
	 */
	@Override
	public ModelElement getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelRelation#getTarget()
	 */
	@Override
	public ModelElement getTarget() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelRelation#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelRelation#setId(int)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelRelation#setName(java.lang.String)
	 */
	@Override
	public void setName(String n) {
		name = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fha.dpmi.modeling.ModelRelation#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	public void addShape(Shape s) {
		shapes.add(s);
	}

	public List<Shape> getShapes() {
		return shapes;
	}

	public void setShapeDragPosition(ModelElementOnClickOnDragListener ds) {
		draggableShape = ds;
	}

	public void setShapeDragPositionText(ModelElementOnClickOnDragListener ds) {
		draggableShapeText = ds;
	}

	public ModelElementOnClickOnDragListener getShapeDragPosition() {
		return draggableShape;
	}

	public ModelElementOnClickOnDragListener getShapeDragPositionText() {
		return draggableShapeText;
	}

	public List<Point2D> getEdgeSupportPoints() {
		return edgeSupportPoints;
	}

	public double getTextX() {
		return textX;
	}

	public double getTextY() {
		return textY;
	}

	public void setTextPosition(double x, double y) {
		this.textX = x;
		this.textY = y;
	}

	public boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
}
