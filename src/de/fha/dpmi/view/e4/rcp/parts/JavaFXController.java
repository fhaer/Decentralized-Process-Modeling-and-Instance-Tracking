package de.fha.dpmi.view.e4.rcp.parts;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Super class for UI controllers using JavaFX
 */
public abstract class JavaFXController implements Initializable {

	/**
	 * Called when JavaFX initializes the controller. To be overwritten with
	 * controller-specific initialization logic.
	 */
	protected void create() {
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		create();
	}

	public abstract Button[] getButtons();
}
