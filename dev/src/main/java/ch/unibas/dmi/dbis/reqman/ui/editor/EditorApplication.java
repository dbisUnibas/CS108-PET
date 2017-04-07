package ch.unibas.dmi.dbis.reqman.ui.editor;


import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
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

    private static volatile boolean exp = false;

    public static void main(String[] args) {
        Log4J2Fix.applyHotFix();

        if(args.length >= 1){
            if("--exp".equals(args[0]) || "--experimental".equals(args[0]) ){
                exp = true;
            }
        }

        launch(args);
    }

    @Override
    public void stop() {
        // Ask if sure, unsaved changes blabla
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) {
        if(exp){
            startExp(primaryStage);
        }else{
            startOld(primaryStage);
        }
    }

    private void startOld(Stage primaryStage){
        primaryStage.setTitle("ReqMan: Editor");
        EditorScene editor = new EditorScene(primaryStage,800, 600);
        primaryStage.setScene(editor);
        primaryStage.setTitle(editor.getTitle());
        primaryStage.show();
    }

    private void startExp(Stage primaryStage){
        primaryStage.setTitle("ReqMan: Editor (EXPERIMENTAL)");
        RequirementTableView view = new RequirementTableView();
        Scene scene = new Scene(view, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleQuit(ActionEvent event) {
        stop();
    }

}
