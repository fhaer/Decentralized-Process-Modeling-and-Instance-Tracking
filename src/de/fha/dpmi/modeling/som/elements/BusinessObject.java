package de.fha.dpmi.modeling.som.elements;

import de.fha.dpmi.modeling.ModelElementImpl;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * A Business Object is a model element of a SOM model
 */
public abstract class BusinessObject extends ModelElementImpl {

	public BusinessObject(int id, String name) {
		super(id, name);
	}

	public BusinessObject(String name) {
		super(name);
	}

	public String toString() {
		return name;
	}
}
