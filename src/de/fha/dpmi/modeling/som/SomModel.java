package de.fha.dpmi.modeling.som;

import java.util.List;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.modeling.som.elements.BusinessObject;
import de.fha.dpmi.modeling.som.elements.BusinessTransaction;
/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Representation of a SOM model. It constructs, stores and retrieves all model
 * elements.
 *
 */
public interface SomModel extends Model {

	/**
	 * adds a business object to the model
	 *
	 * @param object
	 */
	void addBusinessObject(BusinessObject object);

	/**
	 * removes a business object from the model
	 *
	 * @param object
	 */
	void removeBusinessObject(BusinessObject object);

	/**
	 * adds a business transactions between two objects to the model
	 *
	 * @param transaction
	 */
	void addBusinessTransaction(BusinessTransaction transaction);

	/**
	 * removes a business transaction between two objects from the model
	 * 
	 * @param transaction
	 */
	void removeBusinessTransaction(BusinessTransaction transaction);

	/**
	 * returns all business objects part of the model
	 * 
	 * @return
	 */
	List<BusinessObject> getBusinessObjects();

	/**
	 * returns all business transactions part of the model
	 * 
	 * @return
	 */
	List<BusinessTransaction> getBusinessTransactions();

}
