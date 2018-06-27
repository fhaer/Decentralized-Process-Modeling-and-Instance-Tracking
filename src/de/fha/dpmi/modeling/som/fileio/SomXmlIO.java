package de.fha.dpmi.modeling.som.fileio;

import de.fha.dpmi.modeling.fileio.XmlIO;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * XML input/output definitions for the SOM model file format
 */
public interface SomXmlIO extends XmlIO {

	static final String XMLNS = "http://seda.wiai.uni-bamberg.de/sommodel";

	final String XMLSCHEMA = "http://www.wfmc.org/standards/docs/bpmnxpdl_31.xsd";

	final String XMLSCHEMALOCAL = "/de/bpmnaftool/resources/xmlvalidation/somxml.xsd";

	static final String ELEM_ROOT = "model";

	static final String ATTR_ID = "id";
	static final String ATTR_NAME = "name";
	static final String ATTR_ETHEREUM_IDENTITY = "ethereumidentity";
	static final String ATTR_ISVISIBLE = "isvisible";

	static final String ELEM_BUSINESSOBJECTS = "businessobjects";
	static final String ELEM_BUSINESSOBJECT_DISCOURSE = "discourse";
	static final String ELEM_BUSINESSOBJECT_ENVIRONMENT = "environment";

	static final String ELEM_BUSINESSTRANSACTIONS = "businesstransactions";
	static final String ELEM_BUSINESSTRANSACTION = "transaction";
	static final String ELEM_BUSINESSTRANSACTION_SUPPORTPOINTS = "supportpoints";
	static final String ELEM_BUSINESSTRANSACTION_POINT = "point";

	static final String ATTR_X = "x";
	static final String ATTR_Y = "y";
	static final String ATTR_TEXT_X = "textx";
	static final String ATTR_TEXT_Y = "texty";
	static final String ATTR_TYPE = "type";
	static final String ATTR_ORDERINGTYPE = "orderingtype";
	static final String ATTR_ISSPECIALIZED = "isspecialized";
	static final String ATTR_SOURCE_ID = "sourceid";
	static final String ATTR_TARGET_ID = "targetid";

}
