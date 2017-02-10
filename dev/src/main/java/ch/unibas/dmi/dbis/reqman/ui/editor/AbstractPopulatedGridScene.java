package ch.unibas.dmi.dbis.reqman.ui.editor;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

/**
 * Abstract class for populated scenes which are based on a {@link GridPane}
 *
 * @author loris.sauter
 */
public abstract class AbstractPopulatedGridScene extends Scene {

    /**
     * The {@link GridPane} which manages the layout of this scene.
     */
    protected GridPane grid = UIUtilities.generateDefaultGridPane();

    /**
     * Creates a new {@link AbstractPopulatedGridScene} and populates it.
     * The term <i>populates</i> refers to a call of {@link AbstractPopulatedGridScene#populateScene()}
     * @param root
     */
    public AbstractPopulatedGridScene(Parent root) {
        super(new Region());
        setRoot(grid);

        populateScene();
    }

    /**
     * Populates the scene.
     */
    protected abstract void populateScene();
}
