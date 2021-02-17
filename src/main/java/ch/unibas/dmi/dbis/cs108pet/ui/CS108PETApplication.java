package ch.unibas.dmi.dbis.cs108pet.ui;/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import ch.unibas.dmi.dbis.cs108pet.common.LoggingUtils;
import ch.unibas.dmi.dbis.cs108pet.common.Version;
import ch.unibas.dmi.dbis.cs108pet.ui.common.Utils;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DO NOT START FROM THIS CLASS WITHIN INTELLIJ. INSTEAD USE {@link ch.unibas.dmi.dbis.cs108pet.main.Main}
 */
public class CS108PETApplication extends Application {
  
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
  
  public CS108PETApplication() {
  }
  
  /**
   * DO NOT START FROM THIS CLASS WITHIN INTELLIJ. INSTEAD USE {@link ch.unibas.dmi.dbis.cs108pet.main.Main}
   */
  public static void main(String[] args) {
    
    version = Version.getInstance();
    LOGGER = LogManager.getLogger(CS108PETApplication.class);
    LOGGER.info(LoggingUtils.ROOT_APP_MARKER, "Starting pet @ v" + version.getFullVersion());
    launch(args);
  }
  
  @Override
  public void start(Stage primaryStage) {
    Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
    scene = new MainScene();
    primaryStage.setScene(scene);
    primaryStage.setTitle(scene.getTitle());
    Utils.applyLogoIcon(primaryStage);
    primaryStage.show();
    primaryStage.setOnCloseRequest(event -> stop());
    scene.loadBackups();
  }
  
  @Override
  public void stop() {
    if (scene != null) {
      scene.stop();
    }
  }
  
  private void handleUncaughtException(Thread t, Throwable e) {
    var jv = System.getProperty("java.version");
    var jfxv = System.getProperty("javafx.version") == null ? "null" : System.getProperty("javafx.version");
    LOGGER.fatal("Fatal error occurred, due to uncaught exception. Java: "+jv+ ", JavaFX: "+jfxv+", pet: "+Version.getInstance().getFullVersion());
    LOGGER.error("Uncaught exception on thread {}", t);
    LOGGER.catching(Level.ERROR, e);
    Utils.showErrorDialog("Error - " + e.getClass().getSimpleName(),
        "An exception occurred",
        "An uncaught exception occurred. The exception is of type " + e.getClass().getSimpleName() + ".\n" +
            "The exception's message is as follows:\n\t" + e.getMessage() + "\n" +
            "pet probably would still work, but re-start is recommended.\n");
    
  }
  
  
}
