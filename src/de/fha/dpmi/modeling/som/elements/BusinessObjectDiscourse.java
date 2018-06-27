package de.fha.dpmi.modeling.som.elements;

import java.util.ArrayList;
import java.util.List;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * An object of discourse is a business object which can be decomposed
 *
 */
public class BusinessObjectDiscourse extends BusinessObject {

	private List<BusinessObjectDiscourse> decompositionProducts = new ArrayList<>();

	public BusinessObjectDiscourse(String name) {
		super(name);
		super.isVisible = true;
	}

	public BusinessObjectDiscourse(int id, String name) {
		super(id, name);
		super.isVisible = true;
	}

	public BusinessObjectDiscourse(String name, double x, double y) {
		super(name);
		super.x = x;
		super.y = y;
		super.isVisible = true;
	}

	public BusinessObjectDiscourse(int id, String name, double x, double y) {
		super(id, name);
		super.x = x;
		super.y = y;
		super.isVisible = true;
	}

	public void addDecompositionProduct(BusinessObjectDiscourse o) {
		decompositionProducts.add(o);
	}

	public List<BusinessObjectDiscourse> getDecompositionProducts() {
		return decompositionProducts;
	}

}
