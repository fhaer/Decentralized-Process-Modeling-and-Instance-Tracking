package de.fha.dpmi.modeling.fileio;

import java.io.File;

import de.fha.dpmi.modeling.Model;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * File input for all XML files
 */
public interface XmlWriter extends XmlIO {

	/**
	 * Sets a new file and checks its existence
	 *
	 * @param file
	 *            where to write the XML
	 * @throws ModelingFileIOException
	 */
	void setFile(File file) throws ModelingFileIOException;

	/**
	 * Returns the file name
	 *
	 * @return name of file
	 */
	String getFile();

	/**
	 * Writes the given model to the file which is set using setFile(...)
	 *
	 * @param model
	 *            model to write to file
	 * @throws ModelingFileIOException
	 */
	void write(Model model) throws ModelingFileIOException;
}
