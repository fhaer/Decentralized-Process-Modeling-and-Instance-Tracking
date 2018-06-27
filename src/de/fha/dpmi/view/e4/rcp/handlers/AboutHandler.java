package de.fha.dpmi.view.e4.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * About menu entry
 */
public class AboutHandler {
	@Execute
	public void execute(Shell shell) {
		MessageDialog.openInformation(shell, "About",
				"Decentralized Business Process Modeling and Instance Tracking Prototype Application for the Git DVCS and the Ethereum Blockchain"
						+ System.lineSeparator() + System.lineSeparator() + "License: GNU General Public License v3.0"
						+ System.lineSeparator() + "Copyright: (c) 2018 Felix Härer");
	}
}
