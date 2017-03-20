package ch.unibas.dmi.dbis.reqman.ui.editor;


import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorApplication extends Application {

    private EditorController controller;

    private RequirementsView reqView;
    private MilestonesView msView;
    private BorderPane wrapperPane = new BorderPane();
    private Label lblName, lblLecture, lblSemester;

    public static void main(String[] args) {
        Log4J2Fix.applyHotFix();

        launch(args);
    }

    private void handleQuit(ActionEvent event) {
        stop();
    }

    @Override
    public void stop() {
        // Ask if sure, unsaved changes blabla
        System.exit(0);
    }



    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ReqMan: Editor");
        EditorScene editor = new EditorScene(800, 600);
        primaryStage.setScene(editor);
        primaryStage.setTitle(editor.getTitle() );
        primaryStage.show();
    }

}
