package de.fha.dpmi.view.e4.rcp.parts;

import java.awt.Toolkit;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import de.fha.dpmi.util.DpmiException;
import de.fha.dpmi.util.DpmiTooling;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * JavaFX utility dialogs, e.g. info and choice boxes
 */
public class JavaFXUtil implements DpmiTooling {

	/**
	 * Shows a file chooser for opening files
	 *
	 * @param title
	 *            title of window
	 * @param initDirectory
	 *            directory opened initially
	 * @param extension
	 *            file name extension
	 * @param extensionMask
	 *            extension mask for filtering files to open, e.g. *.xml
	 * @return file if user made a choice, null if user canceled
	 */
	public static File fileOpenChooser(String title, Path initDirectory, String extension, String... extensionMask) {
		FileChooser fileChooser = new FileChooser();
		if (initDirectory != null && Files.exists(initDirectory))
			fileChooser.setInitialDirectory(initDirectory.toFile());
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(extension, extensionMask));
		File file = fileChooser.showOpenDialog(new Stage());
		return file;
	}

	/**
	 * Shows a file chooser for saving files
	 *
	 * @param title
	 *            title of window
	 * @param initDirectory
	 *            directory opened initially
	 * @param extension
	 *            file name extension
	 * @param extensionMask
	 *            extension mask for filtering files to save, e.g. *.xml
	 * @return file if user made a choice, null if user canceled
	 */
	public static File fileSaveChooser(String title, Path initDirectory, String extension, String... extensionMask) {
		FileChooser fileChooser = new FileChooser();
		if (initDirectory != null && Files.isDirectory(initDirectory))
			fileChooser.setInitialDirectory(initDirectory.toFile());
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(extension, extensionMask));
		File file = fileChooser.showSaveDialog(new Stage());
		return file;
	}

	/**
	 * Shows a dialog box with given text
	 *
	 * @param title
	 * @param header
	 * @param text
	 * @return true if ok clicked, false otherwise
	 */
	public static boolean infoBox(String title, String header, String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(text);
		Optional<ButtonType> btn = alert.showAndWait();
		if (btn.isPresent() && btn.get() == ButtonType.OK) {
			return true;
		}
		return false;
	}

	/**
	 * Shows a dialog box with given text
	 *
	 * @param text
	 * @return true if ok clicked, false otherwise
	 */
	public static boolean infoBox(String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("DPMI");
		alert.setContentText(text);
		Optional<ButtonType> btn = alert.showAndWait();
		if (btn.isPresent() && btn.get() == ButtonType.OK) {
			return true;
		}
		return false;
	}

	/**
	 * @param title
	 * @param header
	 * @param text
	 * @param hint
	 * @return entered text or empty string if cancel was pressed instead of ok
	 */
	public static String inputBox(String title, String header, String text, String hint) {
		TextInputDialog dialog = new TextInputDialog(hint);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(text);
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
			return result.get();
		return "";
	}

	/**
	 * dialog with 2 input boxes
	 *
	 * @param title
	 * @param text1
	 * @param text2
	 * @param input1
	 * @param input2
	 * @return entered text or empty string if cancel was pressed instead of ok
	 */
	public static String inputBox(String title, String text1, String text2, StringProperty input1,
			StringProperty input2) {
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle(title);

		GridPane lvPane = new GridPane();
		int width = 500;
		lvPane.setPrefSize(width, 200);
		lvPane.setVgap(15);
		lvPane.setHgap(10);

		Label label1 = new Label(text1);
		TextField textField1 = new TextField(input1.get());
		Label label2 = new Label(text2);
		TextField textField2 = new TextField(input2.get());

		lvPane.add(label1, 0, 0);
		lvPane.add(textField1, 1, 0);
		lvPane.add(label2, 0, 1);
		lvPane.add(textField2, 1, 1);

		alert.getDialogPane().setContent(lvPane);
		alert.showAndWait();
		input1.set(textField1.getText());
		input2.set(textField2.getText());
		return "";
	}

	public static File direcotryChooser(String title) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle(title);
		File initDir = new File(System.getenv().get("SystemDrive") + System.getenv().get("HOMEPATH"));
		if (initDir == null || !initDir.exists())
			initDir = new File(".");
		directoryChooser.setInitialDirectory(initDir);
		File selectedDirectory = directoryChooser.showDialog(new Stage());
		// if (selectedDirectory != null) {
		// selectedDirectory.getAbsolutePath();
		// }
		return selectedDirectory;
	}

	public static void showFailureWithException(String title, String headerText, String text, Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Failure " + title);
		alert.setHeaderText(headerText);
		alert.setContentText(text);

		showException(e, alert);
	}

	private static void showException(Exception e, Alert alert) {
		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("Exception type: " + e.getClass().getCanonicalName() + System.lineSeparator());

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

	public static void showFailureWithException(Exception e) {
		e.printStackTrace();
		String message = "";
		if (e.getCause() != null && e.getCause().getLocalizedMessage() != null
				&& e.getCause().getLocalizedMessage().length() > 0) {
			message += e.getLocalizedMessage() + System.lineSeparator() + e.getCause().getLocalizedMessage();
		} else if (e.getMessage() != null && e.getMessage().length() > 0) {
			message += e.getMessage();
		} else {
			message += e.getClass().getSimpleName();
		}
		message += System.lineSeparator();
		showFailureWithException("", "Unable to complete request", message, e);
	}

	public static void showFailureWithException(DpmiException e) {
		e.printStackTrace();
		String message = e.getLocalizedMessage();
		if (e.getCause() != null && e.getCause().getLocalizedMessage().length() > 0) {
			message += System.lineSeparator() + e.getCause().getLocalizedMessage();
		}
		message += System.lineSeparator();
		String title = "" + e.getCommonMessage();
		showFailureWithException(title, "Unable to complete request", message, e);
	}

	@SafeVarargs
	public static <V> void propertyView(String title, String description, Map<String, V>... propertyMap) {
		propertyView(title, description, SortType.ASCENDING, propertyMap);
	}

	@SafeVarargs
	public static <V> void propertyViewRev(String title, String description, Map<String, V>... propertyMap) {
		propertyView(title, description, SortType.DESCENDING, propertyMap);
	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <V> void propertyView(String title, String description, SortType sortType,
			Map<String, V>... propertyMap) {
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle(title);
		alert.setHeaderText(description);

		GridPane kvPane = new GridPane();
		int width = 820;
		kvPane.setPrefSize(width, 470);
		kvPane.setVgap(10);

		int index = 0;
		for (Map<String, V> properties : propertyMap) {
			TableView<Map.Entry<String, V>> tv = new TableView<>();
			TableColumn<Map.Entry<String, V>, String> col1 = new TableColumn<>("Property");
			TableColumn<Map.Entry<String, V>, String> col2 = new TableColumn<>("Value");
			col1.setSortType(sortType);
			col1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));
			col2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().toString()));
			tv.getColumns().addAll(col1, col2);

			ObservableList<Map.Entry<String, V>> propertyList = FXCollections.observableArrayList();
			for (Map.Entry<String, V> e : properties.entrySet()) {
				propertyList.add(e);
			}
			tv.setItems(propertyList);
			tv.setPrefWidth(width);
			tv.getSortOrder().add(col1);
			tv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			tv.getSelectionModel().setCellSelectionEnabled(true);
			tableViewEnableClipboard(tv);
			kvPane.add(tv, 0, index++);
		}
		alert.getDialogPane().setContent(kvPane);
		alert.show();

	}

	public static Alert listView(String title, String description, ObservableList<String> list) {
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle(title);
		alert.setHeaderText(description);

		GridPane lvPane = new GridPane();
		int width = 1020;
		lvPane.setPrefSize(width, 600);
		lvPane.setVgap(10);
		ListView<String> lv = new ListView<>();
		lv.setItems(list);
		lv.setPrefSize(width, 600);
		lv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewEnableClipboard(lv);
		lvPane.add(lv, 0, 0);
		alert.getDialogPane().setContent(lvPane);
		alert.show();
		return alert;
	}

	public static <V> void listViewEnableClipboard(ListView<String> lv) {
		lv.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.C && e.isControlDown()) {
				String clipboard = "";
				for (String entry : lv.getSelectionModel().getSelectedItems()) {
					if (!clipboard.isEmpty())
						clipboard += System.lineSeparator();
					clipboard += entry;
				}
				if (!clipboard.isEmpty()) {
					final ClipboardContent clipboardContent = new ClipboardContent();
					clipboardContent.putString(clipboard);
					Clipboard.getSystemClipboard().setContent(clipboardContent);
				}
			}
		});
	}

	public static <V> void tableViewEnableClipboard(TableView<Map.Entry<String, V>> tv) {
		tv.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.C && e.isControlDown()) {
				String clipboard = "";
				for (Map.Entry<String, V> entry : tv.getSelectionModel().getSelectedItems()) {
					if (!clipboard.isEmpty())
						clipboard += System.lineSeparator();
					clipboard += entry.getValue();
				}
				if (!clipboard.isEmpty()) {
					final ClipboardContent clipboardContent = new ClipboardContent();
					clipboardContent.putString(clipboard);
					Clipboard.getSystemClipboard().setContent(clipboardContent);
				}
			}
		});
	}

	/**
	 * shows a choice box
	 *
	 * @param title
	 *            title of dialog box
	 * @param description
	 *            shown in dialog box
	 * @param choice
	 *            choice displayed next to radio button
	 * @return returns the selected choice as string or empty string if nothing
	 *         is selected
	 */
	public static String choiceBox(String title, String description, String... choice) {
		Alert alert = new Alert(AlertType.CONFIRMATION);

		alert.setTitle(title);
		alert.setHeaderText(description);

		GridPane choicePane = new GridPane();
		choicePane.setMaxWidth(Double.MAX_VALUE);
		choicePane.setVgap(10);
		ToggleGroup radioButtonToggleGroup = new ToggleGroup();
		int index = 0;
		for (String c : choice) {
			RadioButton rb = new RadioButton(c);
			rb.setToggleGroup(radioButtonToggleGroup);
			if (index == 0)
				rb.setSelected(true);
			choicePane.add(rb, 0, index++);
		}
		alert.getDialogPane().setContent(choicePane);

		Optional<ButtonType> bt = alert.showAndWait();
		if (bt.isPresent() && bt.get() == ButtonType.OK) {
			RadioButton rb = (RadioButton) radioButtonToggleGroup.getSelectedToggle();
			return rb.getText();
		}

		return "";
	}

	public static void showFailure(String string) {
		showFailure("DPMI", string);
	}

	public static void showFailure(String title, String description) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setContentText(description);
		alert.showAndWait();
	}

	/**
	 * returns DPI/96 as a scaling factor relative to a DPI of 96
	 *
	 * @return scaling factor
	 */
	public static double getScaleFactor() {
		double dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		return dpi / 96;
	}

	public static void showInfoException(Exception e) {
		infoBox(e.getMessage());
	}

}
