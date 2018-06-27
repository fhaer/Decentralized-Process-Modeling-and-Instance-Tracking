package de.fha.dpmi.modeling.som.fileio;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.modeling.fileio.XmlWriter;
import de.fha.dpmi.modeling.fileio.XmlWriterImpl;
import de.fha.dpmi.modeling.som.SomModel;
import de.fha.dpmi.modeling.som.elements.BusinessObject;
import de.fha.dpmi.modeling.som.elements.BusinessObjectDiscourse;
import de.fha.dpmi.modeling.som.elements.BusinessTransaction;
import de.fha.dpmi.util.DpmiTooling;
import javafx.geometry.Point2D;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * File output for SOM XML files
 */
public class SomXmlWriterImpl extends XmlWriterImpl implements SomXmlIO, XmlWriter, DpmiTooling {

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
	public SomXmlWriterImpl(File file) throws ModelingFileIOException {
		super(file);
	}

	/**
	 * Generates an XML document (DOM tree)
	 *
	 * @return XML Document
	 * @throws ParserConfigurationException
	 */
	@Override
	protected Document generateXml() throws ParserConfigurationException {
		log("Creating XML file ...", 0, 1);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();

		Element root = generateRoot(document);

		log("Generating XML ...", 1, 1);
		generateHeader(root);
		generateBusinessObjects(root, model.getBusinessObjects());
		generateBusinessTransactions(root, model.getBusinessTransactions());

		return document;
	}

	@Override
	protected void setModel(Model model) {
		this.model = (SomModel) model;
	}

	/**
	 * Generates the root Element with all attributes
	 *
	 * @param document
	 *            Docoument (DOM) where root is added to
	 * @return root Element
	 */
	private Element generateRoot(Document document) {
		Element root = document.createElement(ELEM_ROOT);
		root.setAttribute(ATTR_ID, model.getId().toString());
		root.setAttribute(ATTR_NAME, model.getName().toString());
		root.setAttribute("xmlns", XMLNS);
		root.setAttribute("xmlns:xsi", XMLNSXSI);
		// root.setAttribute("xsi:schemaLocation",XMLSCHEMA + " " +
		// XMLSCHEMALOCAL);
		document.appendChild(root);
		return root;
	}

	/**
	 * Generates the header including name, description, processType,
	 * supervisorAgent and version
	 *
	 * @param root
	 *            Element where generated Header is added to
	 */
	private void generateHeader(Element root) {
		// log("Generate Header", 2, 1);
		// addElement(root, "name", model.getName());
		// addElement(root, "id", model.getId().toString());
	}

	/**
	 * Generates the nodes Element including all node Elements
	 *
	 * @param root
	 *            Element where generated Nodes are added to
	 */
	private void generateBusinessObjects(Element root, List<? extends BusinessObject> objects) {
		log("Generate Model Elements", 2, 1);
		Element xmlObjects = addElement(root, ELEM_BUSINESSOBJECTS);
		for (BusinessObject object : model.getBusinessObjects()) {
			Element xmlElem;
			if (object instanceof BusinessObjectDiscourse) {
				xmlElem = addElement(xmlObjects, ELEM_BUSINESSOBJECT_DISCOURSE);
			} else {
				xmlElem = addElement(xmlObjects, ELEM_BUSINESSOBJECT_ENVIRONMENT);
			}
			xmlElem.setAttribute(ATTR_ID, object.getId() + "");
			xmlElem.setAttribute(ATTR_NAME, object.getName());
			xmlElem.setAttribute(ATTR_ETHEREUM_IDENTITY, object.getEthereumIdentity());
			xmlElem.setAttribute(ATTR_X, Double.toString(object.getX()));
			xmlElem.setAttribute(ATTR_Y, Double.toString(object.getY()));
			xmlElem.setAttribute(ATTR_ISVISIBLE, Boolean.toString(object.getIsVisible()));
			if (object instanceof BusinessObjectDiscourse) {
				List<BusinessObjectDiscourse> decompProducts = ((BusinessObjectDiscourse) object)
						.getDecompositionProducts();
				if (decompProducts.size() > 0)
					generateBusinessObjects(xmlElem, decompProducts);
			}
		}
	}

	private void generateBusinessTransactions(Element root, List<BusinessTransaction> transactions) {
		log("Generate Transactions", 2, 1);
		Element xmlObjects = addElement(root, ELEM_BUSINESSTRANSACTIONS);
		for (BusinessTransaction transaction : model.getBusinessTransactions()) {
			Element xmlElem = addElement(xmlObjects, ELEM_BUSINESSTRANSACTION);
			xmlElem.setAttribute(ATTR_ID, transaction.getId() + "");
			xmlElem.setAttribute(ATTR_NAME, transaction.getName());
			xmlElem.setAttribute(ATTR_TYPE, transaction.getType().toString());
			xmlElem.setAttribute(ATTR_SOURCE_ID, transaction.getSource().getId() + "");
			xmlElem.setAttribute(ATTR_TARGET_ID, transaction.getTarget().getId() + "");
			xmlElem.setAttribute(ATTR_ORDERINGTYPE, transaction.getOrderingType().toString());
			xmlElem.setAttribute(ATTR_ISSPECIALIZED, Boolean.toString(transaction.getIsSpecialized()));
			xmlElem.setAttribute(ATTR_TEXT_X, Double.toString(transaction.getTextX()));
			xmlElem.setAttribute(ATTR_TEXT_Y, Double.toString(transaction.getTextY()));
			Element xmlSupportPoints = addElement(xmlElem, ELEM_BUSINESSTRANSACTION_SUPPORTPOINTS);
			for (Point2D point : transaction.getEdgeSupportPoints()) {
				Element xmlPoint = addElement(xmlSupportPoints, ELEM_BUSINESSTRANSACTION_POINT);
				xmlPoint.setAttribute(ATTR_X, Double.toString(point.getX()));
				xmlPoint.setAttribute(ATTR_Y, Double.toString(point.getY()));
			}
			xmlElem.setAttribute(ATTR_ISVISIBLE, Boolean.toString(transaction.getIsVisible()));
			List<BusinessTransaction> decompProducts = transaction.getDecompositionProducts();
			if (decompProducts.size() > 0)
				generateBusinessTransactions(xmlObjects, decompProducts);
		}
	}

}
