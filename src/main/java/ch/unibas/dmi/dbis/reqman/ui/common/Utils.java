package ch.unibas.dmi.dbis.reqman.ui.common;

import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.svg.SVGLoader;
import ch.unibas.dmi.dbis.reqman.ui.svg.SVGNode;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.openiconic.Openiconic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Utils {
  
  public static final String ARROW_DOWN = "\u25bc";
  public static final String HEAVY_PLUS = "\u2795";
  public static final String HEAVY_MINUS = "\u2212";
  public static final String ARROW_UP = "\u25b2";
  
  public static final String DASHICONS_CARET_UP = "dashicons-arrow-up";
  public static final String DASHICONS_CARET_DOWN = "dashicons-arrow-down";
  public static final String DASHICONS_CARET_UP_ALT = "dashicons-up-alt2";
  public static final String DASHICONS_CARET_DONW_ALT = "dashicons-down-alt2";
  public static final String DASHICONS_PLUS_THIN = "dashicons-plus-light";
  public static final String DASHICONS_MINUS_THIN = "dashicons-minus-light";
  
  public static final int ICON_WIDHT = 15; // px, totally magic
  
  public static final FileChooser.ExtensionFilter[] JSON_ANY_FILTER = new FileChooser.ExtensionFilter[]{
      new FileChooser.ExtensionFilter("JSON", "*.json"),
      new FileChooser.ExtensionFilter("Any", "*.*")
  };
  
  private static Node arrowDownIcon = null;
  private static Node arrowUpIcon = null;
  
  private static List<Image> iconList = null;
  
  public static List<Image> getIconList() {
    if (iconList == null) {
      List<String> pathList = new ArrayList<>();
      iconList = new ArrayList<>();
      for (int i = 16; i <= 1024; i *= 2) {
        pathList.add("logo/logo" + i + ".png");
      }
      pathList.sort(Comparator.reverseOrder());
      pathList.forEach(path -> iconList.add(new Image(Utils.class.getClassLoader().getResourceAsStream(path))));
    }
    return iconList;
  }
  
  public static void applyLogoIcon(Stage s) {
    s.getIcons().addAll(getIconList());
  }
  
  /**
   * Creates the widely used {@link GridPane} with its default styling.
   *
   * @return
   */
  public static GridPane generateDefaultGridPane() {
    GridPane grid = new GridPane();
    applyDefaultGridSetup(grid);
    return grid;
  }
  
  public static void applyDefaultGridSetup(GridPane grid) {
    grid.setStyle("-fx-padding: 10px; -fx-spacing: 10px; -fx-hgap: 10px;-fx-vgap: 10px");
  }
  
  public static void applyDefaultFineGridSpacing(GridPane grid) {
    grid.setStyle("-fx-padding: 2px; -fx-spacing: 2px; -fx-hgap: 2px;-fx-vgap: 1px");
  }
  
  public static void showInfoDialog(String title, String header, String content) {
    showDialog(Alert.AlertType.INFORMATION, title, header, content);
  }
  
  public static void showInfoDialog(String header, String content) {
    showInfoDialog("Information", header, content);
  }
  
  public static void showWarningDialog(String title, String header, String content) {
    showDialog(Alert.AlertType.WARNING, title, header, content);
  }
  
  public static void showWarningDialog(String header, String content) {
    showWarningDialog("Warning", header, content);
  }
  
  public static void showErrorDialog(String title, String header, String content) {
    showDialog(Alert.AlertType.ERROR, title, header, content);
  }
  
  public static void showErrorDialog(String header, String content) {
    showErrorDialog("Error", header, content);
  }
  
  public static Button createArrowUpButton() {
    try {
      SVGNode node = SVGLoader.getInstance().load("feather/chevron-up");
      return new Button("", node);
    } catch (IOException e) {
      return new Button(ARROW_UP);
    }
  }
  
  public static Button createArrowUpButton(boolean unicode) {
    return createArrowButton(unicode, ARROW_UP, Openiconic.CARET_TOP);
  }
  
  public static Button createArrowDownButton() {
    try {
      SVGNode node = SVGLoader.getInstance().load("feather/chevron-down");
      return new Button("", node);
    } catch (IOException e) {
      return new Button(ARROW_DOWN);
    }
  }
  
  public static Button createArrowDownButton(boolean unicode) {
    return unicode ? createUnicodCaretDown() : createArrowButton(unicode, ARROW_DOWN, Openiconic.CARET_BOTTOM);
  }
  
  public static Button createPlusButton() {
    return createPlusButton(false);
  }
  
  public static Button createPlusButton(boolean unicode) {
    if (unicode) {
      return new Button(HEAVY_PLUS);
    } else {
      /*FontIcon fi = new FontIcon(Openiconic.PLUS);
      fi.setIconSize(ICON_WIDHT);
      return new Button("",fi);*/
      try {
        SVGNode node = SVGLoader.getInstance().load("feather/plus");
        return new Button("", node);
      } catch (IOException e) {
        return new Button(HEAVY_PLUS);
      }
    }
  }
  
  public static Button createMinusButton() {
    return createMinusButton(false);
  }
  
  public static Button createMinusButton(boolean unicode) {
    if (unicode) {
      return new Button(HEAVY_MINUS);
    } else {
     /* FontIcon fi = new FontIcon(Openiconic.MINUS);
      fi.setIconSize(ICON_WIDHT);
      return new Button("",fi);*/
      try {
        SVGNode node = SVGLoader.getInstance().load("feather/minus");
        return new Button("", node);
      } catch (IOException e) {
        return new Button(HEAVY_MINUS);
      }
    }
  }
  
  public static ToggleButton createArrowUpToggle() {
    return new ToggleButton("", arrowUpIcon);
  }
  
  public static Node createArrowUpNode() {
    if (arrowUpIcon == null) {
      try {
        arrowUpIcon = SVGLoader.getInstance().load("feather/chevron-up");
      } catch (IOException e) {
        arrowUpIcon = new Label(ARROW_UP);
      }
    } else if (arrowUpIcon instanceof SVGNode) {
      return ((SVGNode) arrowUpIcon).copy();
    }
    return arrowUpIcon;
  }
  
  public static ToggleButton createArrowDownToggle() {
    return new ToggleButton("", createArrowDownNode());
  }
  
  public static FileChooser createCatalogueFileChooser(String action) {
    FileChooser fc = new FileChooser();
    fc.setTitle(action + " Catalogue");
    fc.getExtensionFilters().addAll(JSON_ANY_FILTER);
    return fc;
  }
  
  public static FileChooser createGroupFileChooser(String action) {
    FileChooser fc = new FileChooser();
    fc.setTitle(action + " Group");
    fc.getExtensionFilters().addAll(JSON_ANY_FILTER);
    return fc;
  }
  
  /**
   * Creates a hfill element, similar to the latex {@code \hfill}.
   * Suggested by <a href="https://stackoverflow.com/a/40884079">this SO answer</a>
   *
   * @return An hfill / rubber element, growing horizontally
   */
  public static Node createHFill() {
    final Region r = new Region();
    HBox.setHgrow(r, Priority.ALWAYS);
    return r;
  }
  
  public static boolean showConfirmationDialog(String header, String content) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Caution");
    alert.setHeaderText(header);
    alert.setContentText(content);
    Optional<ButtonType> out = alert.showAndWait();
    if (out.isPresent()) {
      return out.get().equals(ButtonType.OK);
    }
    return false;
  }
  
  public static void showFeatureDisabled(String featureName, String reason) {
    showWarningDialog("Feature Disabled", String.format("Feature %s disabled", featureName), String.format("The feature %s, is in the current version (%s) disabled.\n\nThe reason for this is:\n%s", featureName, Version.getInstance().getVersion(), reason));
  }
  
  public static void showFeatureDisabled(String featureName) {
    showFeatureDisabled(featureName, "");
  }
  
  public static DirectoryChooser createDirectoryChooser(String title) {
    DirectoryChooser dc = new DirectoryChooser();
    dc.setTitle(title);
    return dc;
  }
  
  public static FileChooser createFileChooser(String title) {
    FileChooser fc = new FileChooser();
    fc.setTitle(title);
    return fc;
  }
  
  public static void applyDefaultSpacing(Node node) {
    node.setStyle("-fx-padding: 10px; -fx-spacing: 10px;" + node.getStyle());
  }
  
  public static Node createArrowDownNode() {
    if (arrowDownIcon == null) {
      try {
        arrowDownIcon = SVGLoader.getInstance().load("feather/chevron-down");
      } catch (IOException e) {
        arrowDownIcon = new Label(ARROW_DOWN);
      }
    } else if (arrowDownIcon instanceof SVGNode) {
      return ((SVGNode) arrowDownIcon).copy();
    }
    return arrowDownIcon;
  }
  
  @NotNull
  private static Button createArrowButton(boolean unicode, String arrowUp, Openiconic icon) {
    if (unicode) {
      return createUnicodCaretUp();
    } else {
      FontIcon fi = new FontIcon(icon);
      fi.setIconSize(ICON_WIDHT);
      return new Button("", fi);
    }
  }
  
  private static Button createUnicodCaretUp() {
    Button b = new Button(ARROW_UP);
    b.setStyle("-fx-text-fill: dimgray;");
    return b;
  }
  
  private static Button createUnicodCaretDown() {
    Button b = new Button(ARROW_DOWN);
    b.setStyle("-fx-text-fill: dimgray;");
    return b;
  }
  
  private static void showDialog(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }
  
  public static class MilestoneCell extends ListCell<Milestone> {
    
    @Override
    public void updateItem(Milestone item, boolean empty) {
      super.updateItem(item, empty);
      if (!empty) {
        setText(item.getName());
      } else {
        setText("");
      }
    }
  }
  
}
