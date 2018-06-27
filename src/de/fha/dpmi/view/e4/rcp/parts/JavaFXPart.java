package de.fha.dpmi.view.e4.rcp.parts;

import java.net.URL;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * super class of any tab using JavaFX
 */
public class JavaFXPart {

	private final String partClassName;

	protected JavaFXController controller;

	private FXCanvas fxCanvas;

	protected Stage fxStage;

	public JavaFXPart(String partClassName) {
		this.partClassName = partClassName;
	}

	@PostConstruct
	public void createComposite(Composite parent) {
		jfxInit(parent);
	}

	private void createScene(String fxmlPath) {

		try {
			final URL location = getClass().getResource(fxmlPath + partClassName + ".fxml");
			final FXMLLoader fXMLLoader = new FXMLLoader();
			fXMLLoader.setLocation(location);
			fXMLLoader.setBuilderFactory(new JavaFXBuilderFactory());
			final Parent root = fXMLLoader.load(location.openStream());
			final Scene scene = new Scene(root);
			scene.getStylesheets().add("/css/" + partClassName + ".css");
			if (fxCanvas != null) {
				fxCanvas.setScene(scene);
			} else if (fxStage != null) {
				fxStage.setScene(scene);
				fxStage.show();
			}
			controller = fXMLLoader.getController();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void jfxInit(Composite parent) {

		// this will initialize the FX Toolkit
		fxCanvas = new FXCanvas(parent, SWT.NONE);

		Platform.setImplicitExit(false);
		Platform.runLater(() -> createScene("/fxml/"));
	}

	public void javaFXApplicationStart(Stage stage) {
		this.fxStage = stage;

		Platform.setImplicitExit(false);
		Platform.runLater(() -> createScene(""));
	}
}