package ch.unibas.dmi.dbis.reqman.ui.evaluator;


import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorApplication extends Application {

    private EvaluatorScene scene;

    public static void main(String[] args) {
        Log4J2Fix.applyHotFix();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        scene = new EvaluatorScene(800, 600);
        primaryStage.setTitle(scene.getTitle());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void stop(){
        scene.stop();
    }
}
