package ch.unibas.dmi.dbis.reqman.ui;

import ch.unibas.dmi.dbis.reqman.ui.editor.EditorView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
class MainScene extends Scene {

    private BorderPane root;

    private MenuManager menuManager = MenuManager.getInstance();

    private VBox topContainer = new VBox();

    private EditorView editor;


    MainScene(){
        super(new Region(), 800,600);
        root = new BorderPane();
        this.setRoot(root);
        root.setTop(topContainer);
        topContainer.getChildren().add(menuManager.getMenuBar() );
    }


}
