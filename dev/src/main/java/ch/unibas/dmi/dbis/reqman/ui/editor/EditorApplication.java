package ch.unibas.dmi.dbis.reqman.ui.editor;


import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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
        /*
        Workaround for:
        https://issues.apache.org/jira/browse/LOG4J2-1799
         */
        System.getProperties().remove("sun.stdout.encoding");
        System.getProperties().remove("sun.stderr.encoding");

        try {
            /*
            Test to see if executing jar name can be obtained
             */
            System.out.print("Code source: ");
            System.out.println(URLDecoder.decode(EditorApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
