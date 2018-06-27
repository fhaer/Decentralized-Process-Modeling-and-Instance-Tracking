package de.fha.dpmi.modeling.som;

import java.util.ArrayList;
import java.util.List;

import de.fha.dpmi.modeling.ModelImpl;
import de.fha.dpmi.modeling.som.elements.BusinessObject;
import de.fha.dpmi.modeling.som.elements.BusinessTransaction;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * implementation of a SOM model
 */
public class SomModelImpl extends ModelImpl implements SomModel {

	private List<BusinessObject> businessObjects = new ArrayList<>();

	private List<BusinessTransaction> businessTransactions = new ArrayList<>();

	public SomModelImpl(String name) {
		super(name);
	}

	@Override
	public List<BusinessObject> getBusinessObjects() {
		return businessObjects;
	}

	@Override
	public List<BusinessTransaction> getBusinessTransactions() {
		return businessTransactions;
	}

	@Override
	public void addBusinessObject(BusinessObject object) {
		businessObjects.add(object);
		addElement(object);
	}

	@Override
	public void addBusinessTransaction(BusinessTransaction transaction) {
		businessTransactions.add(transaction);
		addRelation(transaction);
	}

	@Override
	public void removeBusinessObject(BusinessObject object) {
		businessObjects.remove(object);
	}

	@Override
	public void removeBusinessTransaction(BusinessTransaction transaction) {
		businessTransactions.remove(transaction);
	}

}
