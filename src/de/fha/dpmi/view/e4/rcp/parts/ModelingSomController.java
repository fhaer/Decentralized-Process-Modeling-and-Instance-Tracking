package de.fha.dpmi.view.e4.rcp.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXParseException;

import de.fha.dpmi.modeling.Model;
import de.fha.dpmi.modeling.ModelElement;
import de.fha.dpmi.modeling.ModelElementImpl;
import de.fha.dpmi.modeling.ModelRelation;
import de.fha.dpmi.modeling.ModelRelationImpl;
import de.fha.dpmi.modeling.fileio.ModelingFileIO;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.modeling.fileio.XmlReader;
import de.fha.dpmi.modeling.fileio.XmlWriter;
import de.fha.dpmi.modeling.som.SomModel;
import de.fha.dpmi.modeling.som.SomModelImpl;
import de.fha.dpmi.modeling.som.elements.BusinessObject;
import de.fha.dpmi.modeling.som.elements.BusinessObjectDiscourse;
import de.fha.dpmi.modeling.som.elements.BusinessObjectEnvironment;
import de.fha.dpmi.modeling.som.elements.BusinessTransaction;
import de.fha.dpmi.modeling.som.fileio.SomXmlReader;
import de.fha.dpmi.modeling.som.fileio.SomXmlWriterImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * UI controller of a SOM model
 */
public class ModelingSomController extends ModelingController {

	@FXML
	private Button btCreate;

	@FXML
	private Button btLoad;

	@FXML
	private Button btSave;

	@FXML
	private Button btOpenInTool;

	@FXML
	private Label lbStatus;

	/**
	 * Anchor pane for the model editor
	 */
	@FXML
	private AnchorPane apModel;

	/**
	 * Group located in apModel, contains cvModel
	 */
	@FXML
	private Group gpModel;

	/**
	 * Model scroll pane, contains apModel
	 */
	@FXML
	private ScrollPane spModel;

	/**
	 * Canvas where overlays are drawn on
	 */
	@FXML
	private Canvas cvModel;

	private List<Point2D> edgeSupportPoints = new ArrayList<>();

	/**
	 * model last opened from file
	 */
	@SuppressWarnings("unused")
	private SomModel openModel;

	/**
	 * model currently visible
	 */
	private SomModel visibleModel;

	private static ModelingSomController instance;

	private List<ModelElementImpl> selectedElements = new ArrayList<>();

	private double objectWidth = 90;
	private double objectHeight = 50;
	private double scaleFactor = 1;

	public ModelingSomController() {
		instance = this;
	}

	protected void clearModel() {
		setOpenFile(null);
		setModel(null);
		gpModel.getChildren().clear();
	}

	@Override
	protected Model getModel() {
		return visibleModel;
	}

	@Override
	protected void setModel(Model model) {
		openModel = (SomModel) model;
	}

	@Override
	protected void create() {
		scaleFactor = JavaFXUtil.getScaleFactor();
		objectHeight *= scaleFactor;
		objectWidth *= scaleFactor;
		lbStatus.setText("Scale factor: " + scaleFactor);
		interceptScrollEvents(apModel, spModel, cvModel.getWidth(), cvModel.getHeight());
		super.create();
	}

	@FXML
	private void btClearClicked() {
		lbStatus.setText("");
		clearModel();
	}

	@FXML
	private void btLoadClicked() {
		try {
			loadFile("Choose Business Process Model", ModelingFileIO.BUSINESS_PROCESS_DIRECTORY_NAME,
					"Business Process Model", "*." + ModelingFileIO.FILE_EXTENSION_SOM, "*.adl");
		} catch (ModelingFileIOException e) {
			File file = e.getFile();
			if (e.getCause() instanceof SAXParseException && file.getAbsolutePath().endsWith("adl")) {
				JavaFXUtil.infoBox(
						"The adl file type is not supported by the built-in model viewer, however, it may still be part of the versioned files. To view or edit this file with the application associated with its file extension, please click the \"ext. Tool\" button.");
				loadAdlFile(file);
				log("Opened ADL file: \"" + file + "\"");
			} else {
				JavaFXUtil.showFailureWithException(e);
			}
		}
	}

	@FXML
	private void btSaveClicked() {
		try {
			if (openFile != null && openFile.getAbsolutePath().endsWith(".adl")) {
				Path gitDirectory = getRepositoryDirectoryIfExists();
				Path initDirectory = null;
				if (gitDirectory != null && Files.isDirectory(gitDirectory)) {
					initDirectory = gitDirectory.resolve(ModelingFileIO.BUSINESS_PROCESS_DIRECTORY_NAME);
				}
				File saveToFile = JavaFXUtil.fileSaveChooser("Save Business Process Model", initDirectory,
						"Business Process Model", "*.adl");
				if (saveToFile != null) {
					Files.copy(openFile.toPath(), saveToFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					loadAdlFile(saveToFile);
					log("Saved ADL file to: \"" + saveToFile + "\"");
				}
			} else if (openFile != null) {
				saveFile("Save Business Process Model", ModelingFileIO.BUSINESS_PROCESS_DIRECTORY_NAME,
						"Business Process Model", "*." + ModelingFileIO.FILE_EXTENSION_SOM);
			}
		} catch (ModelingFileIOException | IOException e) {
			JavaFXUtil.showFailureWithException(e);
		}
	}

	private void loadAdlFile(File file) {
		clearModel();
		Text shape = new Text(15, 35, "Open Model: " + file.getAbsolutePath());
		shape.setStrokeWidth(0);
		shape.setStroke(Color.BLACK);
		shape.setWrappingWidth(1024);
		gpModel.getChildren().add(shape);
		setOpenFile(file);
	}

	protected XmlReader getXmlReader(File file) throws ModelingFileIOException {
		SomXmlReader r = new SomXmlReader(file);
		return r;
	}

	protected XmlWriter getXmlWriter(File file) throws ModelingFileIOException {
		XmlWriter r = new SomXmlWriterImpl(file);
		return r;
	}

	protected void drawModel(Model m) {
		apModel.setStyle("-fx-background-color: #ffffff");
		visibleModel = new SomModelImpl(m.getName());
		for (ModelElementImpl me : m.getModelElements()) {
			double x = me.getX();
			double y = me.getY();
			if (x % 10 != 0)
				x = Math.round(x / 10) * 10;
			if (y % 10 != 0)
				y = Math.round(y / 10) * 10;
			GraphicsContext gc = cvModel.getGraphicsContext2D();
			gc.setFill(Color.GREEN);
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(1);

			if (me instanceof BusinessObjectDiscourse)
				drawObjectDiscourse((BusinessObjectDiscourse) me, x, y);
			if (me instanceof BusinessObjectEnvironment)
				drawObjectEnvironment((BusinessObjectEnvironment) me, x, y);
		}

		for (ModelRelation mr : m.getModelRelations()) {
			GraphicsContext gc = cvModel.getGraphicsContext2D();
			gc.setFill(Color.GREEN);
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(1);
			drawTransaction((BusinessTransaction) mr);
		}
	}

	@FXML
	private void cvModelClicked(MouseEvent me) {
		// int clickCount = me.getClickCount();
		double x = me.getX();
		double y = me.getY();
		if (x % 10 != 0)
			x = Math.round(x / 10) * 10;
		if (y % 10 != 0)
			y = Math.round(y / 10) * 10;
		GraphicsContext gc = cvModel.getGraphicsContext2D();
		gc.setFill(Color.GREEN);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		lbStatus.setText("Coordinate: " + x + ", " + y);
		if (me.isAltDown() && me.isControlDown()) {
			edgeSupportPoints.add(new Point2D(x, y));
		} else {
			int nSelElem = selectedElements.size();
			if (me.isAltDown() && nSelElem > 1 && edgeSupportPoints.size() > 0) {
				int direction = getArrowDirection(edgeSupportPoints);
				drawArrow(gc, edgeSupportPoints, selectedElements.get(nSelElem - 2), selectedElements.get(nSelElem - 1),
						direction);
				this.edgeSupportPoints.clear();
				this.selectedElements.clear();
			} else {
				if (me.isShiftDown()) {
					drawObjectEnvironment(gc, x, y);
				} else if (me.isControlDown()) {
					drawObjectDiscourse(gc, x, y);
				}
			}
		}

	}

	private void drawObjectDiscourse(BusinessObjectDiscourse me, double x, double y) {
		visibleModel.addBusinessObject(me);

		Rectangle shape = new Rectangle(objectWidth, objectHeight);
		shape.setStrokeWidth(1);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.WHITE); // .deriveColor(1, 1, 1, 0.7)
		shape.relocate(x * scaleFactor, y * scaleFactor);

		Group g = new Group();
		g.getChildren().add(shape);

		double textX = (x + 7) * scaleFactor;
		double textY = (y + 15) * scaleFactor;
		Text text = new Text(textX, textY, me.getName());
		text.setStrokeWidth(0);
		text.setWrappingWidth(78 * scaleFactor);
		text.setLineSpacing(0);
		text.setStroke(Color.BLACK);
		// shape.setFill(Color.BLACK);
		// shape.relocate(x, y);
		g.getChildren().add(text);

		me.setShape(shape);
		me.setGroup(g);

		ModelElementOnClickOnDragListener ds = new ModelElementOnClickOnDragListener();
		ds.register(me, g);
		me.setDraggableShape(ds);

		gpModel.getChildren().addAll(g);
	}

	private void drawObjectDiscourse(GraphicsContext gc, double x, double y) {
		// gc.strokeRect(x, y, x+60, y+30);

		String name = JavaFXUtil.inputBox("Modeling", "Name", "Name of Model Element", "");
		BusinessObjectDiscourse modelElement = new BusinessObjectDiscourse(name);
		visibleModel.addBusinessObject(modelElement);

		Rectangle shape = new Rectangle(objectWidth, objectHeight);
		shape.setStrokeWidth(1);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.WHITE); // .deriveColor(1, 1, 1, 0.7)
		shape.relocate(x * scaleFactor, y * scaleFactor);

		Group g = new Group();
		g.getChildren().add(shape);

		double textX = (x + 8) * scaleFactor;
		double textY = (y + 15) * scaleFactor;
		Text text = new Text(textX, textY, name);
		text.setStrokeWidth(0);
		text.setWrappingWidth(72 * scaleFactor);
		text.setLineSpacing(0);
		text.setStroke(Color.BLACK);
		// shape.setFill(Color.BLACK);
		// shape.relocate(x, y);
		g.getChildren().add(text);

		modelElement.setShape(shape);
		modelElement.setGroup(g);

		ModelElementOnClickOnDragListener ds = new ModelElementOnClickOnDragListener();
		ds.register(modelElement, g);
		modelElement.setDraggableShape(ds);

		gpModel.getChildren().addAll(g);
	}

	private void drawObjectEnvironment(BusinessObjectEnvironment me, double x, double y) {
		visibleModel.addBusinessObject(me);

		Ellipse shape = new Ellipse(objectWidth / 2, objectHeight / 2);
		shape.setStrokeWidth(1);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.WHITE); // .deriveColor(1, 1, 1, 0.7)
		shape.relocate(x * scaleFactor, y * scaleFactor);

		Group g = new Group();
		g.getChildren().add(shape);

		double textX = (x + 15) * scaleFactor;
		double textY = (y + 27) * scaleFactor;
		Text text = new Text(textX, textY, me.getName());
		text.setStrokeWidth(0);
		text.setWrappingWidth(66 * scaleFactor);
		text.setStroke(Color.BLACK);
		text.setLineSpacing(0);
		// shape.setFill(Color.BLACK);
		// shape.relocate(x, y);
		g.getChildren().add(text);

		me.setShape(shape);
		me.setGroup(g);

		ModelElementOnClickOnDragListener ds = new ModelElementOnClickOnDragListener();
		ds.register(me, g);
		me.setDraggableShape(ds);

		gpModel.getChildren().addAll(g);
	}

	private void drawObjectEnvironment(GraphicsContext gc, double x, double y) {

		String name = JavaFXUtil.inputBox("Modeling", "Name", "Name of Model Element", "");
		BusinessObjectEnvironment modelElement = new BusinessObjectEnvironment(name);
		modelElement.setName(name);
		visibleModel.addBusinessObject(modelElement);

		Ellipse shape = new Ellipse(objectWidth / 2, objectHeight / 2);
		shape.setStrokeWidth(1);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.WHITE); // .deriveColor(1, 1, 1, 0.7)
		shape.relocate(x * scaleFactor, y * scaleFactor);

		Group g = new Group();
		g.getChildren().add(shape);

		double textX = (x + 15) * scaleFactor;
		double textY = (y + 19) * scaleFactor;
		Text text = new Text(textX, textY, name);
		text.setStrokeWidth(0);
		text.setWrappingWidth(66 * scaleFactor);
		text.setStroke(Color.BLACK);
		text.setLineSpacing(0);
		// shape.setFill(Color.BLACK);
		// shape.relocate(x, y);
		g.getChildren().add(text);

		modelElement.setGroup(g);
		modelElement.setShape(shape);

		ModelElementOnClickOnDragListener ds = new ModelElementOnClickOnDragListener();
		ds.register(modelElement, g);
		modelElement.setDraggableShape(ds);

		gpModel.getChildren().addAll(g);
	}

	private void removeObject(ModelElementImpl modelElement) {
		if (modelElement instanceof BusinessObject) {
			Group s = modelElement.getGroup();
			gpModel.getChildren().remove(s);
			visibleModel.removeBusinessObject((BusinessObject) modelElement);
		}
	}

	private void updateObject(ModelElementImpl modelElement) {

		if (modelElement instanceof BusinessObjectDiscourse) {
			removeObject(modelElement);
			drawObjectDiscourse((BusinessObjectDiscourse) modelElement, modelElement.getX(), modelElement.getY());
		} else if (modelElement instanceof BusinessObjectEnvironment) {
			removeObject(modelElement);
			drawObjectEnvironment((BusinessObjectEnvironment) modelElement, modelElement.getX(), modelElement.getY());
		}
	}

	private int getArrowDirection(List<Point2D> edgeSupportPoints) {
		int direction = -1;
		double x1 = Double.NaN;
		double y1 = Double.NaN;
		double x2 = Double.NaN;
		double y2 = Double.NaN;
		for (Point2D p : edgeSupportPoints) {
			x2 = p.getX();
			y2 = p.getY();
			if (Double.compare(x1, Double.NaN) != 0 && Double.compare(y1, Double.NaN) != 0) {
				if (Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
					if (x2 > x1) {
						direction = 1;
					} else {
						direction = 2;
					}
					y2 = y1;
				} else {
					if (y2 > y1) {
						direction = 3;
					} else {
						direction = 4;
					}
					x2 = x1;
				}
			}
			x1 = x2;
			y1 = y2;
		}
		return direction;
	}

	private void drawTransaction(BusinessTransaction mr) {
		List<Point2D> edgeSupportPoints = mr.getEdgeSupportPoints();
		int direction = getArrowDirection(edgeSupportPoints);

		visibleModel.addBusinessTransaction(mr);
		Group g = new Group();

		double x1 = Double.NaN;
		double y1 = Double.NaN;
		double x2 = Double.NaN;
		double y2 = Double.NaN;
		for (Point2D p : edgeSupportPoints) {
			x2 = p.getX();
			y2 = p.getY();
			if (Double.compare(x1, Double.NaN) != 0 && Double.compare(y1, Double.NaN) != 0) {
				if (Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
					if (x2 > x1) {
						direction = 1;
					} else {
						direction = 2;
					}
					y2 = y1;
				} else {
					if (y2 > y1) {
						direction = 3;
					} else {
						direction = 4;
					}
					x2 = x1;
				}
				Line shape = new Line(x1 * scaleFactor, y1 * scaleFactor, x2 * scaleFactor, y2 * scaleFactor);
				shape.setStrokeWidth(1);
				shape.setStroke(Color.BLACK);
				shape.setFill(Color.WHITE); // .deriveColor(1, 1, 1, 0.7)
				g.getChildren().addAll(shape);
				mr.addShape(shape);
			}
			x1 = x2;
			y1 = y2;
		}
		// draw arrow head
		Polygon arrowHead = getArrowHead(x2 * scaleFactor, y2 * scaleFactor, direction);
		g.getChildren().addAll(arrowHead);

		// make draggable
		ModelElementOnClickOnDragListener ds = new ModelElementOnClickOnDragListener();
		ds.register(mr, g);
		mr.setShapeDragPosition(ds);

		gpModel.getChildren().addAll(g);

		// draw text, make draggable
		Text text = new Text(mr.getTextX() * scaleFactor, mr.getTextY() * scaleFactor, mr.getName());
		text.setStrokeWidth(0);
		text.setStroke(Color.BLACK);
		text.setWrappingWidth(60 * scaleFactor);
		text.setLineSpacing(0);
		ModelElementOnClickOnDragListener dst = new ModelElementOnClickOnDragListener();
		dst.register(mr, text);
		// dst.makeDraggable(mr, text);
		mr.setShapeDragPositionText(dst);
		gpModel.getChildren().addAll(text);
	}

	private void drawArrow(GraphicsContext gc, List<Point2D> edgeSupportPoints, ModelElement source,
			ModelElement target, int direction) {

		String name = JavaFXUtil.inputBox("Modeling", "Name", "Name of Model Relation", "");
		String type = JavaFXUtil.choiceBox("Transaction Type", "Choose the type of the new transaction",
				BusinessTransaction.Type.Initiaion.toString(), BusinessTransaction.Type.Contracting.toString(),
				BusinessTransaction.Type.Enforcing.toString(), BusinessTransaction.Type.Control.toString(),
				BusinessTransaction.Type.Feedback.toString());
		BusinessTransaction modelRelation = new BusinessTransaction(name, type);
		modelRelation.setSource(source);
		modelRelation.setTarget(target);
		visibleModel.addBusinessTransaction(modelRelation);
		source.addModelOutRelation(modelRelation);
		target.addModelInRelation(modelRelation);

		Group g = new Group();

		double x1 = Double.NaN;
		double y1 = Double.NaN;
		double x2 = Double.NaN;
		double y2 = Double.NaN;
		for (Point2D p : edgeSupportPoints) {
			x2 = p.getX();
			y2 = p.getY();
			if (Double.compare(x1, Double.NaN) != 0 && Double.compare(y1, Double.NaN) != 0) {
				if (Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
					if (x2 > x1) {
						direction = 1;
					} else {
						direction = 2;
					}
					y2 = y1;
				} else {
					if (y2 > y1) {
						direction = 3;
					} else {
						direction = 4;
					}
					x2 = x1;
				}
				Line shape = new Line(x1 * scaleFactor, y1 * scaleFactor, x2 * scaleFactor, y2 * scaleFactor);
				shape.setStrokeWidth(1);
				shape.setStroke(Color.BLACK);
				shape.setFill(Color.WHITE); // .deriveColor(1, 1, 1, 0.7)
				g.getChildren().addAll(shape);
				modelRelation.addShape(shape);
			}
			x1 = x2;
			y1 = y2;
		}
		// draw arrow head
		Polygon arrowHead = getArrowHead(x2, y2, direction);
		g.getChildren().addAll(arrowHead);

		// make draggable
		ModelElementOnClickOnDragListener ds = new ModelElementOnClickOnDragListener();
		ds.register(modelRelation, g);
		modelRelation.setShapeDragPosition(ds);

		gpModel.getChildren().addAll(g);

		// draw text, make draggable
		Text text = new Text(edgeSupportPoints.get(0).getX() + 5, edgeSupportPoints.get(0).getY() - 10, name);
		text.setStrokeWidth(0);
		text.setStroke(Color.BLACK);
		text.setWrappingWidth(66);
		text.setLineSpacing(0);
		ModelElementOnClickOnDragListener dst = new ModelElementOnClickOnDragListener();
		dst.register(modelRelation, text);
		// dst.makeDraggable(modelRelation, text);
		modelRelation.setShapeDragPositionText(dst);
		gpModel.getChildren().addAll(text);
	}

	private Polygon getArrowHead(double x, double y, int direction) {
		Polygon shape;
		if (direction == 1) {
			// right
			shape = new Polygon(0, 5, 5, 0, 0, -5);
			shape.relocate(x - 5, y - 5);
		} else if (direction == 2) {
			// left
			shape = new Polygon(0, 5, -5, 0, 0, -5);
			shape.relocate(x, y - 5);
		} else if (direction == 3) {
			// down
			shape = new Polygon(5, 0, 0, 5, -5, 0);
			shape.relocate(x - 5, y - 5);
		} else {
			// up
			shape = new Polygon(5, 0, 0, -5, -5, 0);
			shape.relocate(x - 5, y);
		}
		shape.setStrokeWidth(1);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.BLACK); // .deriveColor(1, 1, 1, 0.7)
		return shape;
	}

	@FXML
	private void cvModelDragged() {

	}

	@FXML
	private void cvModelContextMenu() {

	}

	public synchronized static ModelingSomController getInstance() {
		return instance;
	}

	public class ModelElementOnClickOnDragListener {

		ModelElementImpl modelElement;
		ModelRelationImpl modelRelation;

		double orgSceneX, orgSceneY;
		double orgTranslateX, orgTranslateY;

		public void register(ModelElementImpl modelElement, Node node) {
			node.setOnMousePressed(onMousePressed);
			this.modelElement = modelElement;
		}

		public void register(ModelRelationImpl modelRelation, Node node) {
			node.setOnMousePressed(onMousePressed);
			this.modelRelation = modelRelation;
		}

		public void makeDraggable(ModelElement modelElement, Node node) {
			node.setOnMouseDragged(circleOnMouseDraggedEventHandler);
		}

		public void makeDraggable(ModelRelation modelRelation, Node node) {
			node.setOnMouseDragged(circleOnMouseDraggedEventHandler);
		}

		public double getX() {
			if (modelElement != null)
				return modelElement.getShape().getLayoutX() + modelElement.getShape().getTranslateX();
			return 0;
		}

		public double getY() {
			if (modelElement != null)
				return modelElement.getShape().getLayoutY() + modelElement.getShape().getTranslateY();
			return 0;
		}

		EventHandler<MouseEvent> onMousePressed = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent t) {

				orgSceneX = t.getSceneX();
				orgSceneY = t.getSceneY();

				if (t.getSource() instanceof Ellipse) {

					Ellipse p = ((Ellipse) (t.getSource()));

					orgTranslateX = p.getCenterX();
					orgTranslateY = p.getCenterY();

				} else {

					Node p = ((Node) (t.getSource()));

					orgTranslateX = p.getTranslateX();
					orgTranslateY = p.getTranslateY();

				}
				selectedElements.add(modelElement);
				if (modelElement != null) {
					lbStatus.setText(
							"Element selected: " + modelElement.toString() + ", x=" + getX() + ", y=" + getY());
					if (t.getClickCount() == 1) {
						StringProperty name = new SimpleStringProperty(modelElement.getName());
						StringProperty ethereumAddress = new SimpleStringProperty(modelElement.getEthereumIdentity());
						JavaFXUtil.inputBox("Element", "Name", "Ethereum Account Address", name, ethereumAddress);
						if (name != null && !name.get().isEmpty()) {
							modelElement.setName(name.get());
						}
						if (ethereumAddress != null && !ethereumAddress.get().isEmpty()) {
							modelElement.setEthereumIdentity(ethereumAddress.get());
						}
						updateObject(modelElement);
						lbStatus.setText(
								"Element updated: " + modelElement.toString() + ", x=" + getX() + ", y=" + getY());
					}
				}
			}
		};

		EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent t) {

				double offsetX = t.getSceneX() - orgSceneX;
				double offsetY = t.getSceneY() - orgSceneY;

				if (offsetX % 10 != 0)
					offsetX = Math.round(offsetX / 10) * 10;
				if (offsetY % 10 != 0)
					offsetY = Math.round(offsetY / 10) * 10;

				double newTranslateX = orgTranslateX + offsetX;
				double newTranslateY = orgTranslateY + offsetY;

				if (t.getSource() instanceof Ellipse) {

					Ellipse p = ((Ellipse) (t.getSource()));

					p.setCenterX(newTranslateX);
					p.setCenterY(newTranslateY);

				} else {

					Node p = ((Node) (t.getSource()));

					p.setTranslateX(newTranslateX);
					p.setTranslateY(newTranslateY);

				}

				setNewPosition(newTranslateX, newTranslateY);
			}

			private void setNewPosition(double x, double y) {
				lbStatus.setText("Position changed to: x=" + x + ", y=" + y);
				if (modelElement != null)
					modelElement.setPosition(x, y);
				if (modelRelation != null)
					modelRelation.setTextPosition(x, y);
			}
		};

	}

	@Override
	public Button[] getButtons() {
		Button[] b = { btCreate, btLoad, btSave, btOpenInTool };
		return b;
	}
}
