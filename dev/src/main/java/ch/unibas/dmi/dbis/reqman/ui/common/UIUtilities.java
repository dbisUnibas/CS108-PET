package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class UIUtilities {

    /**
     * Creates the widely used {@link GridPane} with its default styling.
     * @return
     */
    public static GridPane generateDefaultGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-padding: 10px; -fx-spacing: 10px; -fx-hgap: 10px;-fx-vgap: 10px");
        return grid;
    }

}
