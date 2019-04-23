package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.backup.BackupManager;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.ui.common.CourseInfoView;
import ch.unibas.dmi.dbis.reqman.ui.common.FilterBar;
import ch.unibas.dmi.dbis.reqman.ui.common.PopupStage;
import ch.unibas.dmi.dbis.reqman.ui.common.TitleProvider;
import ch.unibas.dmi.dbis.reqman.ui.editor.EditorView;
import ch.unibas.dmi.dbis.reqman.ui.overview.GroupStatisticsView;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorView extends VBox implements TitleProvider {
  
  final static Logger LOGGER = LogManager.getLogger(EditorView.class);
  
  private final String title = "Evaluator";
  private final EvaluatorHandler handler;
  private SplitPane horizontalSplit;
  private SplitPane verticalSplit;
  private VBox leftContent;
  private VBox rightContent;
  private TabPane tabPane;
  private FilterBar filterBar;
  private GroupListView groupView;
  private CourseInfoView courseView;
  private HashMap<String, Tab> legacyGroupTabMap = new HashMap<>();
  private HashMap<UUID, Tab> groupTapMap = new HashMap<>();
  
  public EvaluatorView(EvaluatorHandler handler) {
    super();
    this.handler = handler;
    this.handler.setEvaluatorView(this);
    
    initComponents();
    layoutComponents();
    disableAll();
  }
  
  @Override
  public String getTitle() {
    return title;
  }
  
  public void displayCatalogueInfo(Catalogue catalogue) {
    courseView.refresh();
  }
  
  public boolean isGroupTabbed(Group active) {
    return groupTapMap.containsKey(active.getUuid());
  }
  
  public void addGroupTab(AssessmentView view, boolean fresh) {
    Tab tab = new Tab();
    tab.setText(view.getActiveGroup().getName());
    view.bindToParentSize(rightContent);
    tab.setContent(view);
    view.bindActiveIndicator(tab.selectedProperty());
    tabPane.getTabs().addAll(tab);
    tab.setUserData(view.getActiveGroup().getUuid());
    groupTapMap.put(view.getActiveGroup().getUuid(), tab);
    tab.setOnClosed(event -> {
      groupTapMap.remove(tab.getUserData());
    });
    view.addDirtyListener(new DirtyListener() {
      @Override
      public void markDirty() {
        EvaluatorView.this.markDirty(view.getActiveGroup());
      }
      
      @Override
      public void unmarkDirty() {
        EvaluatorView.this.unmarkDirty(view.getActiveGroup());
      }
    });
    if (fresh) {
      markDirty(view.getActiveGroup());
    }
    
  }
  
  public void markDirty(Group group) {
    Tab tab = groupTapMap.get(group.getUuid());
    if (tab.getText().indexOf("*") < 0) {
      tab.setText(tab.getText() + "*");
    }
    if (!tab.getStyleClass().contains("modified")) {
      tab.getStyleClass().add("modified");
    }
  }
  
  public void unmarkDirty(Group modified) {
    Tab tab = groupTapMap.get(modified.getUuid());
    tab.getStyleClass().remove("modified");
    if (tab.getText().indexOf("*") >= 0) {
      String text = tab.getText().substring(0, tab.getText().indexOf("*"));
      tab.setText(text);
    }
  }
  
  public boolean isDirty(Group group) {
    Tab tab = groupTapMap.get(group.getUuid());
    return tab.getStyleClass().contains("modified");
  }
  
  public void setActiveTab(Group group) {
    Tab toActive = groupTapMap.get(group.getUuid());
    tabPane.getSelectionModel().select(toActive);
  }
  
  public void removeTab(Group g) {
    Tab tab = groupTapMap.get(g.getUuid());
    groupTapMap.remove(g.getUuid());
    tabPane.getTabs().remove(tab);
  }
  
  public void refreshCourseInfoView() {
    courseView.refresh();
  }
  
  public UUID getActiveGroupUUID() {
    Tab tab = tabPane.getSelectionModel().getSelectedItem();
    Object obj = tab.getUserData();
    if (obj instanceof UUID) {
      UUID groupID = (UUID) obj;
      return groupID;
    } else {
      LOGGER.error("A tab without group-id was found. This should not happen. Saving will probably not work!");
      return null;
    }
  }
  
  public void updateDisplayOf(Group gr) {
    Tab tab = groupTapMap.get(gr.getUuid());
    tab.setText(gr.getName());
    groupView.updateDisplayOf(gr);
  }
  
  public void showFilterBar() {
    filterBar.clear();
    if (!getChildren().contains(filterBar)) {
      getChildren().add(0, filterBar);
    } else {
      getChildren().remove(filterBar);
    }
  }
  
  public void closeAll() {
    this.groupTapMap.clear();
    this.tabPane.getTabs().clear();
    this.groupView.getItems().clear();
  }
  
  public void closeFilterBar() {
    if (filterBar != null) {
      filterBar.close();
    }
  }
  
  void enableAll() {
    LOGGER.traceEntry();
    groupView.setDisable(false);
    courseView.setDisable(false);
    tabPane.setDisable(false);
  }
  
  private void layoutComponents() {
    horizontalSplit.prefWidthProperty().bind(widthProperty());
    horizontalSplit.prefHeightProperty().bind(heightProperty());
    verticalSplit.setOrientation(Orientation.VERTICAL);
    verticalSplit.prefWidthProperty().bind(widthProperty());
    verticalSplit.prefHeightProperty().bind(heightProperty());
    
    VBox upper = new VBox();
    upper.getChildren().add(courseView);
    
    VBox lower = new VBox();
    lower.getChildren().add(horizontalSplit);
    
    verticalSplit.getItems().addAll(upper, lower);
    verticalSplit.setDividerPositions(0.08);
    leftContent.getChildren().add(groupView);
    
    rightContent.setPadding(new Insets(10));
    rightContent.setSpacing(10);
    rightContent.prefHeightProperty().bind(heightProperty());
    
    tabPane.setPadding(new Insets(10));
    tabPane.getStylesheets().add("style.css");
    rightContent.getChildren().addAll(tabPane); // TODO Iff no catalogue loaded display usage message like intellij
    VBox.setVgrow(tabPane, Priority.ALWAYS);
    
    
    horizontalSplit.setDividerPositions(0.33);
    horizontalSplit.getItems().addAll(leftContent, rightContent);
    
    getChildren().addAll(verticalSplit);
  }
  
  private void initComponents() {
    horizontalSplit = new SplitPane();
    verticalSplit = new SplitPane();
    
    leftContent = new VBox();
    rightContent = new VBox();
    tabPane = new TabPane();
    
    courseView = new CourseInfoView();
    groupView = new GroupListView(handler);
    
    filterBar = new FilterBar();
  }
  
  public void disableAll() {
    groupView.setDisable(true);
    courseView.setDisable(true);
    tabPane.setDisable(true);
  }
  
  private Function<Void, Void> statisticsCloser;
  
  public void showStatistics(HashMap<UUID, AssessmentView> assessmentViewMap) {
    GroupStatisticsView view = new GroupStatisticsView();
    Scene scene = new Scene(view);
    PopupStage stage = new PopupStage("Groups Point Overview", scene, false);
    // Following code hides the stage asap the focus is lost
    /*stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if(!stage.isFocused()){
        stage.hide();
      }
    });*/
    stage.show();
    
    assessmentViewMap.values().forEach(av -> {
      av.setOnPointsChanged(pts -> {
        if (stage.isShowing()) {
          view.update();
        }
      });
    });
    
    /*
    // TODO add listener to each assessmentview
    .setOnPointsChanged(pts -> {
      if(stage.isShowing()){
        view.update();
      }
    });
    */
    statisticsCloser = (__) -> {
      stage.close();
      return null;
    };
  }
  
  public void stop() {
    if (statisticsCloser != null) {
      statisticsCloser.apply(null);
    }
    BackupManager.getInstance().storeBackups();
  }
}
