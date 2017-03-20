package ch.unibas.dmi.dbis.reqman.ui.evaluator;


import javafx.application.Application;
import javafx.stage.Stage;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        EvaluatorScene scene = new EvaluatorScene(800, 600);
        primaryStage.setTitle(scene.getTitle());
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
