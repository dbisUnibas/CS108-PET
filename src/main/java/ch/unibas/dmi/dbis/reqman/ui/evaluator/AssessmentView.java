package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Progress;
import ch.unibas.dmi.dbis.reqman.data.ProgressSummary;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class AssessmentView extends BorderPane implements PointsChangeListener, DirtyListener {
  
  public static final String POINTS_MAX_POINTS_SEPARATOR = "/";
  private final Logger LOGGER = LogManager.getLogger(getClass());
  @Deprecated
  private EvaluatorHandler handler;
  
  
  private HBox headerContainer;
  private Label choiceLbl;
  private Button summaryBtn;
  private ComboBox<ProgressSummary> summaryCb;
  private VBox content;
  private ScrollPane scrollPane;
  private Group group;
  private HBox footerContainer;
  private Label sumLbl;
  private Label maxPointLbl;
  private Label pointsLbl;
  private List<ProgressView> activeProgressViews = new ArrayList<>();
  private ObservableList<Progress> currentlyFilteredProgress;
  
  AssessmentView(Group group) {
    super();
    LOGGER.debug("Initializing for group {}", group);
    
    this.group = group;
    
    initComponents();
    layoutComponents();
    
    //Milestone firstMS = EntityController.getInstance().getCourseManager().getFirstMilestone();
    //displayProgressViews(EntityController.getInstance().getGroupAnalyser(group).getProgressSummaryFor(firstMS));
    summaryCb.getSelectionModel().selectFirst();
    checkDependencies();
  }
  
  @Override
  public void pointsChanged(double newValue) {
    LOGGER.trace("Points changed");
    if(currentlyFilteredProgress != null && !currentlyFilteredProgress.isEmpty()){
      updateSumDisplay(true);
    }else{
      updateSumDisplay();
    }
    checkDependencies();
  }
  
  public void bindToParentSize(Region parent) {
    prefWidthProperty().bind(parent.widthProperty());
    prefHeightProperty().bind(parent.heightProperty());
    scrollPane.prefWidthProperty().bind(widthProperty());
    scrollPane.prefHeightProperty().bind(heightProperty());
    //bindContent();
    scrollPane.setFitToWidth(true);
  }
  
  public void recalculatePoints() {
    updateSumDisplay();
  }
  
  public void displayAll() {
    displayProgressViews(summaryCb.getSelectionModel().getSelectedItem());
    updateSumDisplay();
    checkDependencies();
  }
  
  public Group getActiveGroup() {
    return group;
  }
  
  @Override
  public void markDirty() {
    LOGGER.trace("Dirty");
    
  }
  
  @Override
  public void unmarkDirty() {
    LOGGER.trace("Undirty");
    
  }
  
  void checkDependencies(){
    for(ProgressView pv : activeProgressViews){
      Progress p = pv.getProgress();
      if(EntityController.getInstance().getGroupAnalyser(group).isProgressUnlocked(p)){
        // Everything fine: Progress unlocked
        pv.setLocked(false);
        LOGGER.trace("Unlocking pv of {}", pv.getRequirement().getName());
      }else{
        pv.setLocked(true);
        LOGGER.trace("Locking pv of {}", pv.getRequirement().getName());
      }
      pv.updatePredecessorDisplay();
    }
  }
  
  void displayProgressViews(List<Requirement> toDisplay) {
    detachProgressViews();
    if (currentlyFilteredProgress != null) {
      currentlyFilteredProgress.clear();
    }
    GroupAnalyser analyser = EntityController.getInstance().getGroupAnalyser(group);
    currentlyFilteredProgress = FXCollections.observableList(analyser.getProgressFor(toDisplay, getActiveProgressSummary()));
    
    double sum = EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSumForProgressList(currentlyFilteredProgress);
    setupMaxPointsDisplay(sum);
    
    activeProgressViews.clear();
    for (Progress p : currentlyFilteredProgress) {
      activeProgressViews.add(new ProgressView(group,p, getActiveProgressSummary()));
    }
    
    for (ProgressView pv : activeProgressViews) {
      pv.addPointsChangeListener(this);
    }
    
    attachProgressViews();
    updateSumDisplay(true);
    checkDependencies();
  }
  
  private void updateSumDisplay() {
    double sum = EntityController.getInstance().getGroupAnalyser(group).getSumFor(summaryCb.getSelectionModel().getSelectedItem());
    updateSumDisplay(sum);
  }
  
  private void updateSumDisplay(double sum) {
    if (sum < 0) {
      pointsLbl.setTextFill(Color.RED);
    } else {
      pointsLbl.setTextFill(Color.BLACK);
    }
    pointsLbl.setText(StringUtils.prettyPrint(sum));
  }
  
  private void updateSumDisplay(boolean onlyVisible) {
    if (onlyVisible) {
      double sum = EntityController.getInstance().getGroupAnalyser(group).getSumFor(currentlyFilteredProgress);
      updateSumDisplay(sum);
    } else {
      updateSumDisplay();
    }
  }
  
  private void initComponents() {
    headerContainer = new HBox();
    choiceLbl = new Label("Current Milestone: ");
    summaryCb = new ComboBox<>();
    summaryCb.setCellFactory(param -> new ProgressSummaryCell());
    summaryCb.setButtonCell(new ProgressSummaryCell());
    summaryBtn = new Button("Comments");
    content = new VBox();
    scrollPane = new ScrollPane();
    footerContainer = new HBox();
    sumLbl = new Label("Points:");
    pointsLbl = new Label("0");
    maxPointLbl = new Label(POINTS_MAX_POINTS_SEPARATOR + StringUtils.prettyPrint(EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSum()));
  }
  
  private ProgressSummary getActiveProgressSummary() {
    return summaryCb.getSelectionModel().getSelectedItem();
  }
  
  private void layoutComponents() {
    // Forge top aka title bar:
    headerContainer.setAlignment(Pos.CENTER_LEFT);
    headerContainer.getChildren().addAll(choiceLbl, summaryCb, Utils.createHFill(), summaryBtn);
    Utils.applyDefaultSpacing(headerContainer);
    
    if (EntityController.getInstance().hasCatalogue()) {
      if (group != null) {
        summaryCb.setItems(EntityController.getInstance().getObservableProgressSummaries(group));
      }
//      summaryCb.setItems(FXCollections.observableArrayList(EntityController.getInstance().createProgressSummaries() ));
      summaryCb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        LOGGER.debug("Selected ProgressSummary: {}", newValue);
        CatalogueAnalyser analyiser = EntityController.getInstance().getCatalogueAnalyser();
        displayProgressViews(newValue);
      });
    }
    
    summaryBtn.setOnAction(this::handleComments);
    
    setTop(headerContainer);
    
    
    scrollPane.setContent(content);
    setCenter(scrollPane);
    Utils.applyDefaultSpacing(footerContainer);
    footerContainer.setAlignment(Pos.CENTER_RIGHT);
    footerContainer.getChildren().addAll(sumLbl, pointsLbl, maxPointLbl);
    setBottom(footerContainer);
  }
  
  private void displayProgressViews(ProgressSummary progressSummary) {
    detachProgressViews();
    ObservableList<Progress> progressList = EntityController.getInstance().getObservableProgressOf(group, progressSummary);
    
    double sum = EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSumFor(progressSummary);
    setupMaxPointsDisplay(sum);
    
    activeProgressViews.clear();
    for (Progress p : progressList) {
      activeProgressViews.add(new ProgressView(group,p, progressSummary));
    }
    
    for (ProgressView pv : activeProgressViews) {
      pv.addPointsChangeListener(this);
    }
    
    attachProgressViews();
    checkDependencies();
    updateSumDisplay();
  }
  
  private void setupMaxPointsDisplay(double sum) {
    maxPointLbl.setText(POINTS_MAX_POINTS_SEPARATOR + StringUtils.prettyPrint(sum));
  }
  
  private void handleComments(ActionEvent event) {
    LOGGER.debug("Handling commenting");
    
  }
  
  
  private void attachProgressViews() {
    activeProgressViews.forEach(pv -> {
      addProgressView(pv);
    });
  }
  
  private void detachProgressViews() {
    activeProgressViews.forEach(pv -> removeProgressView(pv));
  }
  
  private void addProgressView(ProgressView pv) {
    content.getChildren().add(pv);
    pv.prefWidthProperty().bind(scrollPane.widthProperty());
  }
  
  private void removeProgressView(ProgressView pv) {
    content.getChildren().remove(pv);
  }
  
  private void bindContent() {
    content.prefWidthProperty().bind(widthProperty());
    content.prefHeightProperty().bind(heightProperty());
  }
  
  
}
