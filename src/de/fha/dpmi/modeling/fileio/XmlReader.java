package de.fha.dpmi.modeling.fileio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.util.DpmiTooling;
/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * File Output for all XML files
 */
public abstract class XmlReader implements XmlIO, DpmiTooling {

	/**
	 * File to be read
	 */
	protected File file;

	/**
	 * Input stream of XML representation to parse
	 */
	protected InputStream xmlInputStream;

	/**
	 * is the XML well formed
	 */
	protected boolean isWellFormed = false;
	/**
	 * is the XML valid against schema
	 */
	protected boolean isValid = false;

	/**
	 * xml namespace
	 */
	public final String xmlns;

	/**
	 * resource path to local copy of schema file
	 */
	public final String xmlschemalocal;

	/**
	 * A file is required to create a new writer
	 *
	 * @param file
	 *            File to write
	 * @throws ModelingFileIOException
	 */
	public XmlReader(File file, String xmlns, String xmlschemalocal) throws ModelingFileIOException {
		setFile(file);
		this.xmlns = xmlns;
		this.xmlschemalocal = xmlschemalocal;
	}

	public XmlReader(InputStream xmlInputStream, String xmlns, String xmlschemalocal) {
		this.xmlInputStream = xmlInputStream;
		this.xmlns = xmlns;
		this.xmlschemalocal = xmlschemalocal;
	}

	public String getFile() {
		return file.toString();
	}

	public void setFile(File file) throws ModelingFileIOException {
		this.file = file;
		if (!file.exists() || !file.canRead()) {
			throw new ModelingFileIOException("Unable to read file: " + file, file);
		}
	}

	public Model parse() throws ModelingFileIOException {
		if (file == null && xmlInputStream == null) {
			throw new ModelingFileIOException("no input set", file);
		}
		try {
			log("Parsing XML ...");
			Element documentRoot = parseXml();
			log("Building model from XML source ...");
			parseModel(documentRoot);
			validateXml();
			// log("Completed loading of XML");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			isWellFormed = false;
			isValid = false;
			throw new ModelingFileIOException(e, file);
		}
		return getModel();
	}

	public Element parseXml() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// use name spaces, so the attribute xmlns in root element can be used
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = null;
		if (file != null)
			document = builder.parse(file);
		else if (xmlInputStream != null)
			document = builder.parse(xmlInputStream);
		Element root = document.getDocumentElement();

		// if there was no exception, the XML is assumed to be well formed
		isWellFormed = true;
		return root;
	}

	public boolean isWellFormed() {
		isWellFormed = false;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.parse(file);
			// if there was no exception, the XML is assumed to be well formed
			isWellFormed = true;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			isWellFormed = false;
			isValid = false;
		}
		return isWellFormed;
	}

	/**
	 * Parses a model
	 */
	protected abstract void parseModel(Element root) throws ModelingFileIOException;

	/**
	 * Returns the parsed model
	 *
	 * @return model
	 */
	protected abstract Model getModel();

	/**
	 * Validate XPDL against schema
	 *
	 * @throws SAXException
	 * @throws IOException
	 */
	protected void validateXml() throws SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLSCHEMALANGUAGE);
		java.net.URL schemaLocation = getClass().getResource(xmlschemalocal);
		if (schemaFactory == null || schemaLocation == null)
			return;
		// Schema schema = schemaFactory.newSchema(schemaLocation);
		// Validator validator = schema.newValidator();
		// Source source = new StreamSource(file);
		// validate against schema, throws exception if fails
		// validator.validate(source);
		isValid = true;
	}

	protected int parseAttributeInt(Element elem, String attr) {
		return Integer.parseInt(elem.getAttribute(attr));
	}

	protected String parseAttributeStr(Element elem, String attr) {
		return elem.getAttribute(attr);
	}

	protected double parseAttributeDouble(Element elem, String attr) {
		return Double.parseDouble(elem.getAttribute(attr));
	}

	protected boolean parseAttributeBool(Element elem, String attr) {
		return Boolean.parseBoolean(elem.getAttribute(attr));
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
	protected Element getSingleElement(Element element, String tagName) throws ModelingFileIOException {
		NodeList nodes = element.getElementsByTagNameNS(xmlns, tagName);
		int length = nodes.getLength();
		if (length != 1)
			throw new ModelingFileIOException(
					"One element named \"" + tagName + "\" must exist. Number of elements found: " + length, file);
		return (Element) nodes.item(0);
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
	 * Returns all elements with the given name which are child elements of the
	 * given element.
	 *
	 * @param element
	 *            Element which is the parent of the elements to return
	 * @param tagName
	 *            name of the elements to return
	 * @return ArrayList of Elements
	 */
	protected ArrayList<Element> getElements(Element element, String tagName) {
		ArrayList<Element> elements = new ArrayList<Element>();
		NodeList nodes = element.getElementsByTagNameNS(xmlns, tagName);
		for (int i = 0; i < nodes.getLength(); i++) {
			elements.add((Element) nodes.item(i));
		}
		return elements;
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
	protected boolean elementExists(Element element, String elementName) {
		NodeList nodes = element.getElementsByTagNameNS(xmlns, elementName);
		return (nodes.getLength() > 0);
	}

	public boolean getIsWellFormed() {
		return isWellFormed;
	}

	public boolean getIsValid() {
		return isValid;
	}
}
