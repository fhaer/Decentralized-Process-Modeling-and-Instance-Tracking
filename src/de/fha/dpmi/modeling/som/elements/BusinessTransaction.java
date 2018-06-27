package de.fha.dpmi.modeling.som.elements;

import java.util.ArrayList;
import java.util.List;

import de.fha.dpmi.modeling.ModelElement;
import de.fha.dpmi.modeling.ModelRelationImpl;
import javafx.geometry.Point2D;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * A business transaction is a model relation between two business objects
 */
public class BusinessTransaction extends ModelRelationImpl {

	public static enum Type {
		Initiaion, Contracting, Enforcing, Control, Feedback
	}

	public static enum OrderingType {
		Unspecified, Sequential, Parallel
	}

	private List<BusinessTransaction> decompositionProducts = new ArrayList<>();

	private Type type;
	private OrderingType orderingType;
	private boolean isSpecialized = false;

	public BusinessTransaction(int id, String name, String type) {
		super(id, name);
		this.isVisible = true;
		this.orderingType = OrderingType.Unspecified;
		this.isSpecialized = false;
		setTypeFromString(type);
	}

	public BusinessTransaction(String name, String type) {
		super(name);
		this.isVisible = true;
		this.orderingType = OrderingType.Unspecified;
		this.isSpecialized = false;
		setTypeFromString(type);
	}

	public BusinessTransaction(int id, String name, String type, ModelElement source, ModelElement target,
			List<Point2D> edgeSupportPoints, double textX, double textY) {
		super(id, name);
		super.edgeSupportPoints = edgeSupportPoints;
		super.source = source;
		super.target = target;
		source.addModelOutRelation(this);
		target.addModelInRelation(this);
		this.textX = textX;
		this.textY = textY;
		this.isVisible = true;
		setTypeFromString(type);
		this.orderingType = OrderingType.Unspecified;
		this.isSpecialized = false;
	}

	public BusinessTransaction(String name, Type type, ModelElement source, ModelElement target,
			List<Point2D> edgeSupportPoints, double textX, double textY) {
		super(name);
		super.edgeSupportPoints = edgeSupportPoints;
		super.source = source;
		super.target = target;
		source.addModelOutRelation(this);
		target.addModelInRelation(this);
		this.textX = textX;
		this.textY = textY;
		this.isVisible = true;
		this.type = type;
		this.orderingType = OrderingType.Unspecified;
		this.isSpecialized = false;
	}

	public void addDecompositionProduct(BusinessTransaction t) {
		decompositionProducts.add(t);
	}

	public List<BusinessTransaction> getDecompositionProducts() {
		return decompositionProducts;
	}

	public void setName(String n) {
		name = n;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getShortTypeString());
		b.append(getOrderingTypeString());
		b.append(getSpecializedString());
		b.append(": ");
		b.append(name);
		return b.toString();
	}

	private String getShortTypeString() {
		if (type == Type.Initiaion)
			return "I";
		if (type == Type.Contracting)
			return "C";
		if (type == Type.Enforcing)
			return "E";
		if (type == Type.Control)
			return "Ct";
		if (type == Type.Feedback)
			return "Fb";
		return "U";
	}

	public void setTypeFromString(String typeStr) {
		if (typeStr.equals(Type.Initiaion.toString()))
			type = Type.Initiaion;
		if (typeStr.equals(Type.Contracting.toString()))
			type = Type.Contracting;
		if (typeStr.equals(Type.Enforcing.toString()))
			type = Type.Enforcing;
		if (typeStr.equals(Type.Control.toString()))
			type = Type.Control;
		if (typeStr.equals(Type.Feedback.toString()))
			type = Type.Feedback;
	}

	public void setOrderingTypeFromString(String orderingTypeStr) {
		if (orderingTypeStr.equals(OrderingType.Sequential.toString()))
			orderingType = OrderingType.Sequential;
		else if (orderingTypeStr.equals(OrderingType.Parallel.toString()))
			orderingType = OrderingType.Parallel;
		else
			orderingType = OrderingType.Unspecified;
	}

	private String getOrderingTypeString() {
		if (orderingType == OrderingType.Sequential)
			return " seq";
		if (orderingType == OrderingType.Sequential)
			return " par";
		return "";
	}

	private String getSpecializedString() {
		if (isSpecialized)
			return " spec";
		return "";
	}

	public void setTypeInitiation() {
		type = Type.Initiaion;
	}

	public void setTypeContracting() {
		type = Type.Contracting;
	}

	public void setTypeEnforcing() {
		type = Type.Enforcing;
	}

	public void setTypeControl() {
		type = Type.Control;
	}

	public void setTypeFeedback() {
		type = Type.Feedback;
	}

	public void setOrderingTypeSequential() {
		orderingType = OrderingType.Sequential;
	}

	public void setOrderingTypeParllel() {
		orderingType = OrderingType.Parallel;
	}

	public void setIsSpecialized(boolean isSpecialized) {
		this.isSpecialized = isSpecialized;
	}

	public Type getType() {
		return type;
	}

	public OrderingType getOrderingType() {
		return orderingType;
	}

	public boolean getIsSpecialized() {
		return isSpecialized;
	}
}
