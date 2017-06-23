package ch.unibas.dmi.dbis.reqman.ui.common;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Utils {

    public static final String ARROW_DOWN = "\u25bc";
    public static final String HEAVY_PLUS = "\u2795";
    public static final String HEAVY_MINUS = "\u2212";
    public static final String ARROW_UP = "\u25b2";
    public static final FileChooser.ExtensionFilter[] JSON_ANY_FILTER = new FileChooser.ExtensionFilter[]{
            new FileChooser.ExtensionFilter("JSON", "*.json"),
            new FileChooser.ExtensionFilter("Any", "*.*")
    };

    /**
     * Creates the widely used {@link GridPane} with its default styling.
     *
     * @return
     */
    public static GridPane generateDefaultGridPane() {
        GridPane grid = new GridPane();
        applyDefaultGridSetup(grid);
        return grid;
    }

    public static void applyDefaultGridSetup(GridPane grid) {
        grid.setStyle("-fx-padding: 10px; -fx-spacing: 10px; -fx-hgap: 10px;-fx-vgap: 10px");
    }

    public static void showInfoDialog(String title, String header, String content) {
        showDialog(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void showInfoDialog(String header, String content) {
        showInfoDialog("Information", header, content);
    }

    public static void showWarningDialog(String title, String header, String content) {
        showDialog(Alert.AlertType.WARNING, title, header, content);
    }

    public static void showWarningDialog(String header, String content) {
        showWarningDialog("Warning", header, content);
    }

    public static void showErrorDialog(String title, String header, String content) {
        showDialog(Alert.AlertType.ERROR, title, header, content);
    }

    public static void showErrorDialog(String header, String content) {
        showErrorDialog("Error", header, content);
    }

    public static Button createArrowUpButton() {
        Button b = new Button(ARROW_UP);
        b.setStyle("-fx-text-fill: dimgray;");
        return b;
    }

    public static Button createArrowDownButton() {
        Button b = new Button(ARROW_DOWN);
        b.setStyle("-fx-text-fill: dimgray;");
        return b;
    }

    public static Button createPlusButton() {
        return new Button(HEAVY_PLUS); // Unicode: heavy plus sign
    }

    public static Button createMinusButton() {
        return new Button(HEAVY_MINUS); // Unicode: heavy minus sign
    }

    public static FileChooser createCatalogueFileChooser(String action) {
        FileChooser fc = new FileChooser();
        fc.setTitle(action + " Catalogue");
        fc.getExtensionFilters().addAll(JSON_ANY_FILTER);
        return fc;
    }

    public static FileChooser createGroupFileChooser(String action) {
        FileChooser fc = new FileChooser();
        fc.setTitle(action + " Group");
        fc.getExtensionFilters().addAll(JSON_ANY_FILTER);
        return fc;
    }

    private static void showDialog(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class MilestoneCell extends ListCell<Milestone> {

        @Override
        public void updateItem(Milestone item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setText(item.getName() + " (" + item.getOrdinal() + ")");
            } else {
                setText("");
            }
        }
    }

}
