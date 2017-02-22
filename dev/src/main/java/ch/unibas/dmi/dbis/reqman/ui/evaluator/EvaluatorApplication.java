package ch.unibas.dmi.dbis.reqman.ui.evaluator;/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import ch.unibas.dmi.dbis.reqman.ui.evaluator.EvaluatorScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class EvaluatorApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        EvaluatorScene scene = new EvaluatorScene(800, 600);
        primaryStage.setTitle("Assessment");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
