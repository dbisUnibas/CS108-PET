package ch.unibas.dmi.dbis.reqman.ui.common;

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
  protected GridPane grid = Utils.generateDefaultGridPane();
  
  /**
   * Creates a new {@link AbstractPopulatedGridScene}.
   * An implementing subclass must call {@link AbstractPopulatedGridScene#populateScene()} later on to populate the
   * scene.
   */
  public AbstractPopulatedGridScene() {
    super(new Region());
    setRoot(grid);
  }
  
  /**
   * Populates the scene.
   */
  protected abstract void populateScene();
}
