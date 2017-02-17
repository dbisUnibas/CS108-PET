package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Utils {

    /**
     * Creates the widely used {@link GridPane} with its default styling.
     * @return
     */
    public static GridPane generateDefaultGridPane() {
        GridPane grid = new GridPane();
        applyDefaultGridSetup(grid);
        return grid;
    }

    public static void applyDefaultGridSetup(GridPane grid){
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-padding: 10px; -fx-spacing: 10px; -fx-hgap: 10px;-fx-vgap: 10px");
    }

    private static void showDialog(Alert.AlertType type, String title, String header, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public static void showInfoDialog(String title, String header, String content){
        showDialog(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void showInfoDialog(String header, String content){
        showInfoDialog("Information", header, content);
    }

    public static void showWarningDialog(String title, String header, String content){
        showDialog(Alert.AlertType.WARNING, title, header, content);
    }

    public static void showWarningDialog(String header, String content){
        showWarningDialog("Warning", header, content);
    }

    public static Button createArrowUpButton(){
        Button b = new Button("\u25b2");
        b.setStyle("-fx-text-fill: dimgray;");
        return b;
    }

    public static Button createArrowDownButton(){
        Button b = new Button("\u25bc");
        b.setStyle("-fx-text-fill: dimgray;");
        return b;
    }

}
