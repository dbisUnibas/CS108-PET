package ch.unibas.dmi.dbis.reqman.ui;/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import ch.unibas.dmi.dbis.reqman.common.LoggingUtils;
import ch.unibas.dmi.dbis.reqman.common.Version;
import javafx.application.Application;
<<<<<<< HEAD
=======
import javafx.event.ActionEvent;
import javafx.event.EventType;
>>>>>>> ae4f5057d1de49a374e94ef0fec6678b21e0f3d0
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
    private int currentView = -1;

    private static volatile boolean exp = false;

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
        primaryStage.setTitle(scene.getTitle() );
        primaryStage.show();
    }
    private MainScene scene;

<<<<<<< HEAD
=======
    private String setupTitle(String title) {
        return "ReqMan: " + title + " (" + version.getVersion() + ")";
    }
>>>>>>> ae4f5057d1de49a374e94ef0fec6678b21e0f3d0

    @Override
    public void stop() {
        if(scene != null){
            scene.stop();
        }

    }


}
