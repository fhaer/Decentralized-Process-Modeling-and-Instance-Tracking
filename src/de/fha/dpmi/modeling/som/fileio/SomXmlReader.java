package de.fha.dpmi.modeling.som.fileio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.modeling.ModelElement;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.modeling.fileio.XmlReader;
import de.fha.dpmi.modeling.som.SomModel;
import de.fha.dpmi.modeling.som.SomModelImpl;
import de.fha.dpmi.modeling.som.elements.BusinessObject;
import de.fha.dpmi.modeling.som.elements.BusinessObjectDiscourse;
import de.fha.dpmi.modeling.som.elements.BusinessObjectEnvironment;
import de.fha.dpmi.modeling.som.elements.BusinessTransaction;
import javafx.geometry.Point2D;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * File input for SOM XML models
 */
public class SomXmlReader extends XmlReader implements SomXmlIO {

	/**
	 * resulting model
	 */
	private SomModel model;

	/**
	 * A file is required to create a new writer
	 *
	 * @param file
	 *            File to write
	 * @throws ModelingFileIOException
	 */
	public SomXmlReader(File file) throws ModelingFileIOException {
		super(file, XMLNS, XMLSCHEMALOCAL);
	}

	/**
	 * Parses a model
	 */
	@Override
	protected void parseModel(Element root) throws ModelingFileIOException {
		String name = root.getAttribute(ATTR_NAME);
		model = new SomModelImpl(name);
		log("Parsing Business Objects ...");
		parseBusinessObjects(root);
		log("Parsing Business Transactions ...");
		parseTransactions(root);
		isValid = true;
	}

	@Override
	protected Model getModel() {
		return model;
	}

	/**
	 * Parses XPDL Pools
	 *
	 * @param root
	 *            Root of DOM document where elements are added to
	 * @throws ModelingFileIOException
	 */
	private void parseBusinessObjects(Element root) throws ModelingFileIOException {
		Element objects = getSingleElement(root, ELEM_BUSINESSOBJECTS);
		for (Element elem : getElements(objects, ELEM_BUSINESSOBJECT_DISCOURSE)) {
			int id = parseAttributeInt(elem, ATTR_ID);
			String name = parseAttributeStr(elem, ATTR_NAME);
			String ethereumIdentity = parseAttributeStr(elem, ATTR_ETHEREUM_IDENTITY);
			boolean isVisible = parseAttributeBool(elem, ATTR_ISVISIBLE);
			double x = parseAttributeDouble(elem, ATTR_X);
			double y = parseAttributeDouble(elem, ATTR_Y);
			BusinessObjectDiscourse object = new BusinessObjectDiscourse(id, name, x, y);
			object.setIsVisible(isVisible);
			object.setEthereumIdentity(ethereumIdentity);
			model.addBusinessObject(object);
			log("Added to model: " + object);
			if (elementExists(elem, ELEM_BUSINESSOBJECTS)) {
				parseBusinessObjects(elem);
			}
		}
		for (Element elem : getElements(objects, ELEM_BUSINESSOBJECT_ENVIRONMENT)) {
			int id = parseAttributeInt(elem, ATTR_ID);
			String name = parseAttributeStr(elem, ATTR_NAME);
			String ethereumIdentity = parseAttributeStr(elem, ATTR_ETHEREUM_IDENTITY);
			boolean isVisible = parseAttributeBool(elem, ATTR_ISVISIBLE);
			double x = parseAttributeDouble(elem, ATTR_X);
			double y = parseAttributeDouble(elem, ATTR_Y);
			BusinessObjectEnvironment object = new BusinessObjectEnvironment(id, name, x, y);
			object.setIsVisible(isVisible);
			object.setEthereumIdentity(ethereumIdentity);
			model.addBusinessObject(object);
			log("Added to model: " + object);
		}
	}

	private void parseTransactions(Element root) throws ModelingFileIOException {
		Element objects = getSingleElement(root, ELEM_BUSINESSTRANSACTIONS);
		for (Element elem : getElements(objects, ELEM_BUSINESSTRANSACTION)) {
			int id = parseAttributeInt(elem, ATTR_ID);
			String name = parseAttributeStr(elem, ATTR_NAME);
			boolean isVisible = parseAttributeBool(elem, ATTR_ISVISIBLE);
			boolean isSpecialized = parseAttributeBool(elem, ATTR_ISSPECIALIZED);
			String type = parseAttributeStr(elem, ATTR_TYPE);
			String orderingType = parseAttributeStr(elem, ATTR_ORDERINGTYPE);
			int sourceId = parseAttributeInt(elem, ATTR_SOURCE_ID);
			int targetId = parseAttributeInt(elem, ATTR_TARGET_ID);
			double textX = parseAttributeDouble(elem, ATTR_TEXT_X);
			double textY = parseAttributeDouble(elem, ATTR_TEXT_Y);
			List<Point2D> edgeSupportPoints = new ArrayList<>();
			if (elementExists(elem, ELEM_BUSINESSTRANSACTION_SUPPORTPOINTS)) {
				Element elemPoints = getSingleElement(elem, ELEM_BUSINESSTRANSACTION_SUPPORTPOINTS);
				for (Element elemPoint : getElements(elemPoints, ELEM_BUSINESSTRANSACTION_POINT)) {
					double x = parseAttributeDouble(elemPoint, ATTR_X);
					double y = parseAttributeDouble(elemPoint, ATTR_Y);
					Point2D p = new Point2D(x, y);
					edgeSupportPoints.add(p);
				}
			}
			BusinessObject source = getBusinessObjectById(sourceId);
			BusinessObject target = getBusinessObjectById(targetId);
			BusinessTransaction transaction = new BusinessTransaction(id, name, type, source, target, edgeSupportPoints,
					textX, textY);
			transaction.setIsVisible(isVisible);
			transaction.setOrderingTypeFromString(orderingType);
			transaction.setIsSpecialized(isSpecialized);
			model.addBusinessTransaction(transaction);
			log("Added to model: " + transaction);
			if (elementExists(elem, ELEM_BUSINESSTRANSACTIONS)) {
				parseTransactions(elem);
			}
		}
	}

	private BusinessObject getBusinessObjectById(int id) {
		String idStr = Integer.toString(id);
		for (ModelElement e : model.getModelElements()) {
			if (e.getId().equals(idStr) && e instanceof BusinessObject) {
				return (BusinessObject) e;
			}
		}
		return null;
	}

}
