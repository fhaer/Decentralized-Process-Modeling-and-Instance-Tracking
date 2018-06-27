package de.fha.dpmi.modeling.bpmn.fileio;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.modeling.bpmn.BpmnModel;
import de.fha.dpmi.modeling.bpmn.elements.FlowObject;
import de.fha.dpmi.modeling.bpmn.elements.Process;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.modeling.fileio.XmlWriter;
import de.fha.dpmi.modeling.fileio.XmlWriterImpl;
import de.fha.dpmi.util.DpmiTooling;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * File Output for BPMN XML files
 */
public class BpmnXmlWriterImpl extends XmlWriterImpl implements BpmnXmlIO, XmlWriter, DpmiTooling {

	/**
	 * resulting model
	 */
	private BpmnModel model;

	/**
	 * A file is required to create a new writer
	 *
	 * @param file
	 *            File to write
	 * @throws ModelingFileIOException
	 */
	public BpmnXmlWriterImpl(File file) throws ModelingFileIOException {
		super(file);
	}

	public void setModel(BpmnModel model) {
		this.model = model;
	}

	/**
	 * Generates an XML document (DOM tree)
	 *
	 * @return XML Document
	 * @throws ParserConfigurationException
	 * @throws ModelingFileIOException
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Document generateXml() throws ParserConfigurationException, ModelingFileIOException {
		log("Creating XML file ...", 0, 1);

		Document document = model.getXmlDocument();
		Element root = document.getDocumentElement();

		log("Generating XML extension attributes ...", 1, 1);
		generateExtensionAttributes(root);

		return document;
	}

	@Override
	protected void setModel(Model model) {
		this.model = (BpmnModel) model;
	}

	private void generateExtensionAttributes(Element root) throws ModelingFileIOException {
		for (Process proc : model.getProcesses()) {
			for (FlowObject obj : proc.getFlowObjects()) {
				if (obj.hasExtensionAttributes()) {
					Element elem = retrieveXmlElementByAttributeId(root, obj.getId());
					if (elem != null) {
						Element extElem = null;
						Element propElem = null;
						for (SimpleEntry<String, String> attribute : obj.getExtensionAttributes()) {
							String name = attribute.getKey();
							String value = attribute.getValue();
							if (!hasExtensionAttribute(obj, elem, name, value)) {
								if (extElem == null)
									extElem = addElement(elem, ELEM_EXTENSIONELEMENTS);
								if (propElem == null)
									propElem = addElement(extElem, XMLNSDPMI, XMLNSDPMI_PREFIX, ELEM_PROPERTIES);
								Element property = addElement(propElem, XMLNSDPMI, XMLNSDPMI_PREFIX, ELEM_PROPERTY);
								property.setAttribute(ATTR_NAME, name);
								property.setAttribute(ATTR_VALUE, value);
							}
						}
					}
				}
			}
		}
	}

	private boolean hasExtensionAttribute(FlowObject t, Element e, String name, String value)
			throws ModelingFileIOException {
		if (elementExists(e, ELEM_EXTENSIONELEMENTS)) {
			Element extElem = getSingleElement(e, ELEM_EXTENSIONELEMENTS);
			Element propElem = getSingleElement(extElem, XMLNSDPMI, ELEM_PROPERTIES);
			for (Element property : getElements(propElem, XMLNSDPMI, ELEM_PROPERTY)) {
				String existingName = parseAttributeStr(property, ATTR_NAME);
				String existingValue = parseAttributeStr(property, ATTR_VALUE);
				if (name.equals(existingName) && value.equals(existingValue))
					return true;
			}
		}
		return false;
	}

	private Element retrieveXmlElementByAttributeId(Element element, String id) {
		String attrId = element.getAttribute(ATTR_ID);
		if (attrId.equals(id))
			return element;
		NodeList nodes = element.getElementsByTagNameNS(XMLNS, "*"); // *
																		// matches
																		// all
																		// tags
		for (int i = 0; i < nodes.getLength(); i++) {
			Element child = (Element) nodes.item(i);
			attrId = child.getAttribute(ATTR_ID);
			if (attrId.equals(id))
				return child;
			retrieveXmlElementByAttributeId(child, id);

		}
		return null;
	}

	/**
	 * Checks if an element with the given name exists as a child of the given
	 * element. If exactly one element is found it is returned, otherwise an
	 * Exception is thrown.
	 *
	 * @param element
	 *            Element which is the parent of the single element to return
	 * @param tagName
	 *            name of the single element to return
	 * @return single element
	 * @throws ModelingFileIOException
	 */
	private Element getSingleElement(Element element, String tagName) throws ModelingFileIOException {
		NodeList nodes = element.getElementsByTagNameNS(XMLNS, tagName);
		int length = nodes.getLength();
		if (length != 1)
			throw new ModelingFileIOException(
					"One element named \"" + tagName + "\" must exist. Number of elements found: " + length, file);
		return (Element) nodes.item(0);
	}

	/**
	 * Checks if an element with the given name exists as a child of the given
	 * element.
	 *
	 * @param element
	 *            Element which is the parent of the element to check for
	 * @param elementName
	 *            name of the Element to check
	 * @return true if element exists, false otherwise
	 */
	private boolean elementExists(Element element, String elementName) {
		NodeList nodes = element.getElementsByTagNameNS(XMLNS, elementName);
		return (nodes.getLength() > 0);
	}
}
