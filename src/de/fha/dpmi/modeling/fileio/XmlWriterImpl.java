package de.fha.dpmi.modeling.fileio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.util.DpmiTooling;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * File input for all XML files
 */
public abstract class XmlWriterImpl implements XmlWriter, DpmiTooling {

	/**
	 * File to be parsed
	 */
	protected File file;

	/**
	 * A file is required to create a new writer
	 *
	 * @param file
	 *            File to write
	 * @throws ModelingFileIOException
	 */
	public XmlWriterImpl(File file) throws ModelingFileIOException {
		setFile(file);
	}

	@Override
	public String getFile() {
		return file.toString();
	}

	@Override
	public void setFile(File file) throws ModelingFileIOException {
		this.file = file;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			throw new ModelingFileIOException(e, file);
		}
		if (file.exists() && !file.canWrite()) {
			throw new ModelingFileIOException("file can not be written " + file, file);
		}
	}

	@Override
	public void write(Model model) throws ModelingFileIOException {
		if (model == null) {
			throw new ModelingFileIOException("Model is null", file);
		}
		setModel(model);
		Document document;
		try {
			document = generateXml();
			writeFile(document);
		} catch (ParserConfigurationException e) {
			throw new ModelingFileIOException("ParserConfigurationException: " + e.getMessage(), file);
		} catch (TransformerException e) {
			throw new ModelingFileIOException("TransformerException: " + e.getMessage(), file);
		}
	}

	/**
	 * Sets the model to be written to file
	 *
	 * @param model
	 */
	protected abstract void setModel(Model model);

	/**
	 * Generates an XML document (DOM tree)
	 *
	 * @return XML Document
	 * @throws ParserConfigurationException
	 * @throws ModelingFileIOException
	 */
	protected abstract Document generateXml() throws ParserConfigurationException, ModelingFileIOException;

	/**
	 * Writes the given document to file
	 *
	 * @param document
	 *            document which is written to file
	 * @throws TransformerException
	 */
	@SuppressWarnings("deprecation")
	protected void writeFile(Document document) throws TransformerException {
		log("Formatting XML", 1, 1);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

		log("Saving xml file \"" + file.getPath() + "\"", 1, 1);
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(file);
		transformer.transform(domSource, streamResult);
	}

	/**
	 * Creates a new Element and adds it to the given element. The new Element
	 * will have the given name and contain the given text.
	 *
	 * @param element
	 *            Element where a new Element is added as child
	 * @param name
	 *            name of the Element to add
	 * @param text
	 *            text of the new Element
	 * @return the new Element
	 */
	protected Element addElement(Element element, String name, String text) {
		Document document = element.getOwnerDocument();
		Element newElement = document.createElement(name);
		Text newElementText = document.createTextNode(text);
		newElement.appendChild(newElementText);
		element.appendChild(newElement);
		return newElement;
	}

	/**
	 * Creates a new Element and adds it to the given element. The new Element
	 * will have the given name and contain the given text.
	 *
	 * @param element
	 *            Element where a new Element is added as child
	 * @param name
	 *            name of the Element to add
	 * @param text
	 *            text of the element (boolean, text is true or false)
	 * @return the new Element
	 */
	protected Element addElement(Element element, String name, boolean text) {
		return addElement(element, name, Boolean.toString(text));
	}

	/**
	 * Creates a new Element with the given namespace and adds it to the given
	 * element. The new Element will have the given name and will not contain
	 * anything.
	 *
	 * @param element
	 *            Element where a new Element is added as child
	 * @param nsUri
	 *            Uri of the namespace
	 * @param nsPrefix
	 *            Prefix of the namespace
	 * @param name
	 *            name of the Element to add
	 * @return the new Element
	 */
	protected Element addElement(Element element, String nsUri, String nsPrefix, String name) {
		Document document = element.getOwnerDocument();
		Element newElement = document.createElementNS(nsUri, nsPrefix + ":" + name);
		element.appendChild(newElement);
		return newElement;
	}

	/**
	 * Creates a new Element and adds it to the given element. The new Element
	 * will have the given name and will not contain anything.
	 *
	 * @param element
	 *            Element where a new Element is added as child
	 * @param name
	 *            name of the Element to add
	 * @return the new Element
	 */
	protected Element addElement(Element element, String name) {
		Document document = element.getOwnerDocument();
		Element newElement = document.createElement(name);
		element.appendChild(newElement);
		return newElement;
	}

	/**
	 * Checks if an element with the given name exists as a child of the given
	 * element. If exactly one element is found it is returned, otherwise an
	 * Exception is thrown.
	 *
	 * @param element
	 *            Element which is the parent of the single element to return
	 * @param namespace
	 *            Namespace of the element to get
	 * @param tagName
	 *            name of the single element to return
	 * @return single element
	 * @throws ModelingFileIOException
	 */
	protected Element getSingleElement(Element element, String namespace, String tagName)
			throws ModelingFileIOException {
		NodeList nodes = element.getElementsByTagNameNS(namespace, tagName);
		int length = nodes.getLength();
		if (length != 1)
			throw new ModelingFileIOException(
					"One element named \"" + tagName + "\" must exist. Number of elements found: " + length, file);
		return (Element) nodes.item(0);
	}

	/**
	 * Returns all elements with the given name of the given namespace which are
	 * child elements of the given element.
	 *
	 * @param element
	 *            Element which is the parent of the elements to return
	 * @param namespace
	 *            namespace of element to return
	 * @param tagName
	 *            name of the elements to return
	 * @return ArrayList of Elements
	 */
	protected ArrayList<Element> getElements(Element element, String namespace, String tagName) {
		ArrayList<Element> elements = new ArrayList<Element>();
		NodeList nodes = element.getElementsByTagNameNS(namespace, tagName);
		for (int i = 0; i < nodes.getLength(); i++) {
			elements.add((Element) nodes.item(i));
		}
		return elements;
	}

	protected String parseAttributeStr(Element elem, String attr) {
		return elem.getAttribute(attr);
	}
}
