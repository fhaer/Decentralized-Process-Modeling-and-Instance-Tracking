package de.fha.dpmi.view.e4.rcp.parts;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * tab containing a SOM model
 */
public class ModelingSomPart extends ModelingPart {

	private Circle circle_Red, circle_Green, circle_Blue;
	private double orgSceneX, orgSceneY;
	private double orgTranslateX, orgTranslateY;

	public ModelingSomPart() {
		super(ModelingSomPart.class.getSimpleName());
	}

	public void initModelEditor() {
		// Create Circles
		circle_Red = new Circle(50.0f, Color.RED);
		circle_Red.setCursor(Cursor.HAND);
		circle_Red.setOnMousePressed(circleOnMousePressedEventHandler);
		circle_Red.setOnMouseDragged(circleOnMouseDraggedEventHandler);

		circle_Green = new Circle(50.0f, Color.GREEN);
		circle_Green.setCursor(Cursor.MOVE);
		circle_Green.setCenterX(150);
		circle_Green.setCenterY(150);
		circle_Green.setOnMousePressed(circleOnMousePressedEventHandler);
		circle_Green.setOnMouseDragged(circleOnMouseDraggedEventHandler);

		circle_Blue = new Circle(50.0f, Color.BLUE);
		circle_Blue.setCursor(Cursor.CROSSHAIR);
		circle_Blue.setTranslateX(300);
		circle_Blue.setTranslateY(100);
		circle_Blue.setOnMousePressed(circleOnMousePressedEventHandler);
		circle_Blue.setOnMouseDragged(circleOnMouseDraggedEventHandler);

		Group root = new Group();
		root.getChildren().addAll(circle_Red, circle_Green, circle_Blue);

		// fxStage.setResizable(false);
		fxStage.setScene(new Scene(root, 400, 350));

		fxStage.setTitle("Model Editor");
		fxStage.show();
	}

	EventHandler<MouseEvent> circleOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			orgSceneX = t.getSceneX();
			orgSceneY = t.getSceneY();
			orgTranslateX = ((Circle) (t.getSource())).getTranslateX();
			orgTranslateY = ((Circle) (t.getSource())).getTranslateY();
		}
	};

	EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			double offsetX = t.getSceneX() - orgSceneX;
			double offsetY = t.getSceneY() - orgSceneY;
			double newTranslateX = orgTranslateX + offsetX;
			double newTranslateY = orgTranslateY + offsetY;

			((Circle) (t.getSource())).setTranslateX(newTranslateX);
			((Circle) (t.getSource())).setTranslateY(newTranslateY);
		}
	};
}
