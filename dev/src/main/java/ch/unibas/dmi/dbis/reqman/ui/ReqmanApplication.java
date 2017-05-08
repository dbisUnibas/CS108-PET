package ch.unibas.dmi.dbis.reqman.ui;/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import ch.unibas.dmi.dbis.reqman.common.LoggingUtils;
import ch.unibas.dmi.dbis.reqman.common.Version;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReqmanApplication extends Application {

    /**
     * Temporary
     */
    public static final int EDITOR_VIEW = 1000;
    /**
     * Temporary
     */
    public static final int EVALUATOR_VIEW = 2000;
    private static Logger LOGGER;
    private static Version version;
    private static volatile boolean exp = false;
    private int currentView = -1;
    private MainScene scene;

    public ReqmanApplication() {
    }

    public static void main(String[] args) {
        Log4J2Fix.applyHotFix();
        version = Version.getInstance();
        LOGGER = LogManager.getLogger(ReqmanApplication.class);
        LOGGER.info(LoggingUtils.REQMAN_MARKER, "Starting reqman @ v" + version.getFullVersion());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        scene = new MainScene();
        primaryStage.setScene(scene);
        primaryStage.setTitle(scene.getTitle());
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (scene != null) {
            scene.stop();
        }

    }


}
