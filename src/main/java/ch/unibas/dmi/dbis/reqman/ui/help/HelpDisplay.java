package ch.unibas.dmi.dbis.reqman.ui.help;

import ch.unibas.dmi.dbis.reqman.common.MarkdownHelper;
import ch.unibas.dmi.dbis.reqman.ui.common.PopupStage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Responsible for the help text.
 *
 * @author loris.sauter
 */
public class HelpDisplay extends VBox {
  
  public static final String GENERAL_HELP = "help/general.md";
  private static final Logger LOGGER = LogManager.getLogger();
  private String html;
  private PopupStage popupStage;
  private WebView webView;
  private WebEngine webEngine;

  public HelpDisplay() {
    try {
      html = MarkdownHelper.loadAndRender(GENERAL_HELP);
    } catch (IOException e) {
      LOGGER.error("Couldn't read help text");
      LOGGER.catching(Level.ERROR, e);
      html = "<b>Error on loading content</b><br>See log file";
    }
    webView = new WebView();
    webEngine = webView.getEngine();
    webView.prefHeightProperty().bind(heightProperty());
    webView.prefWidthProperty().bind(widthProperty());
    Scene scene = new Scene(this, 800, 600);
    this.prefWidthProperty().bind(scene.widthProperty());
    this.prefHeightProperty().bind(scene.heightProperty());
    this.getChildren().add(webView);
    webEngine.loadContent(html);
    popupStage = new PopupStage("Help", scene);
  }
  
  public void show() {
    popupStage.show();
  }
  
}
