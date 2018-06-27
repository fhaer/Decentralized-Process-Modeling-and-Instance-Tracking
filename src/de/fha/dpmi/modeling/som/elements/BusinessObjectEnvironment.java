package de.fha.dpmi.modeling.som.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * An environment object is a business object which can not be decomposed
 */
public class BusinessObjectEnvironment extends BusinessObject {

	public BusinessObjectEnvironment(int id, String name) {
		super(id, name);
		super.isVisible = true;
	}

	public BusinessObjectEnvironment(String name) {
		super(name);
		super.isVisible = true;
	}

	public BusinessObjectEnvironment(int id, String name, double x, double y) {
		super(id, name);
		super.x = x;
		super.y = y;
		super.isVisible = true;
	}

	public BusinessObjectEnvironment(String name, double x, double y) {
		super(name);
		super.x = x;
		super.y = y;
		super.isVisible = true;
	}
}
