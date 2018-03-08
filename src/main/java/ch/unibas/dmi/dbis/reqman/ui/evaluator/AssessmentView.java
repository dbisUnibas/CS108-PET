package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.analysis.*;
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
import javafx.scene.control.*;
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
public class AssessmentView extends BorderPane implements PointsChangeListener, DirtyListener, Filterable {
  
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
  private Label maxVisibleLbl;
  private Label visiblePoints;
  private Label maxTotalLbl;
  private Label totalPoints;
  private List<ProgressView> activeProgressViews = new ArrayList<>();
  private ObservableList<Progress> currentlyFilteredProgress;
  
  AssessmentView(Group group) {
    super();
    LOGGER.debug("Initializing for group {}", group);
    
    this.group = group;
    
    initComponents();
    layoutComponents();
    
    AssessmentManager.getInstance().addFilterable(this);
    
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
  
  @Override
  public void applyFilter(Filter filter) {
    LOGGER.debug("apply filter: "+filter);
  }
  
  @Override
  public void applyProgressSummary(ProgressSummary ps) {
    displayProgressViews(ps);
    ProgressSummary active = summaryCb.getSelectionModel().getSelectedItem();
    if(active != null && active.equals(ps)){
      // nothing
    }else{
      int ordinal = EntityController.getInstance().getCatalogueAnalyser().getProgressSummaryOrdinal(ps);
      summaryCb.getSelectionModel().select(ordinal);
      //summaryCb.getSelectionModel().select(ps);// Doesn't render properly
    }
  }
  
  @Override
  public void clearFilter() {
    displayAll();
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
    toDisplay.sort(EntityController.getInstance().getCatalogueAnalyser().getRequirementComparator());
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
    if(summaryCb.getSelectionModel().getSelectedItem() == null){
      return;
    }
    double visible = EntityController.getInstance().getGroupAnalyser(group).getSumFor(summaryCb.getSelectionModel().getSelectedItem());
    double visibleMax = EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSumFor(summaryCb.getSelectionModel().getSelectedItem());
    double total = EntityController.getInstance().getGroupAnalyser(group).getSum();
    double totalMax = EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSum();
    updatePointsSummary(visible,visibleMax,total,totalMax);
  }
  
  private void updatePointsSummary(double visible, double visibleMax, double total, double totalMax){
    displaySensitivelyPoints(visiblePoints,visible,false);
    displaySensitivelyPoints(maxVisibleLbl,visibleMax,true);
    displaySensitivelyPoints(totalPoints,total,false);
    displaySensitivelyPoints(maxTotalLbl, totalMax, true);
  }
  
  private void displaySensitivelyPoints(Label lbl, double points, boolean paranthesis){
    if(points < 0){
      lbl.setTextFill(Color.RED);
    }else{
      lbl.setTextFill(Color.BLACK);
    }
    if(paranthesis){
      lbl.setText("("+StringUtils.prettyPrint(points)+")");
    }else{
      lbl.setText(StringUtils.prettyPrint(points));
    }
  }
  
  private void updateSumDisplay(boolean onlyVisible) {
    if (onlyVisible) {
      double visible = EntityController.getInstance().getGroupAnalyser(group).getSumFor(currentlyFilteredProgress);
      double visibleMax = EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSumForProgressList(currentlyFilteredProgress);
      double total = EntityController.getInstance().getGroupAnalyser(group).getSum();
      double totalMax = EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSum();
      updatePointsSummary(visible,visibleMax,total,totalMax);
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
    sumLbl = new Label("Visible Points (Total Points): ");
    sumLbl.setTooltip(new Tooltip("Summarizes the currently displayed points."));
    totalPoints = new Label("0");
    totalPoints.setTooltip(new Tooltip("The total amount of points this group has made."));
    visiblePoints = new Label("0");
    visiblePoints.setTooltip(new Tooltip("The amount of currently visible this group has made."));
    maxTotalLbl = new Label("0");
    maxTotalLbl.setTooltip(new Tooltip("The maximal amount of regular points to get."));
    maxVisibleLbl = new Label("0");
    maxVisibleLbl.setTooltip(new Tooltip("The maximal amount of regular points currently visible."));
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
        if(oldValue != null && oldValue.equals(newValue)){
          return; // don't fire the event chain if its already the same ps
        }
        LOGGER.debug("Selected ProgressSummary: {}", newValue);
        CatalogueAnalyser analyiser = EntityController.getInstance().getCatalogueAnalyser();
        AssessmentManager.getInstance().setActiveProgressSummary(newValue);
      });
    }
    
    summaryBtn.setOnAction(this::handleComments);
    
    setTop(headerContainer);
    
    
    scrollPane.setContent(content);
    setCenter(scrollPane);
    Utils.applyDefaultSpacing(footerContainer);
    footerContainer.setAlignment(Pos.CENTER_RIGHT);
    footerContainer.getChildren().addAll(sumLbl, Utils.createHFill(),visiblePoints,maxVisibleLbl, new Label(POINTS_MAX_POINTS_SEPARATOR),totalPoints, maxTotalLbl);
    setBottom(footerContainer);
    
    updateSumDisplay();
  }
  
  private void displayProgressViews(ProgressSummary progressSummary) {
    detachProgressViews();
    ObservableList<Progress> progressList = EntityController.getInstance().getObservableProgressOf(group, progressSummary);
    
    double sum = EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSumFor(progressSummary);
    setupMaxPointsDisplay(sum);
    progressList.sort(EntityController.getInstance().getGroupAnalyser(group).getProgressComparator());
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
    maxTotalLbl.setText(POINTS_MAX_POINTS_SEPARATOR + StringUtils.prettyPrint(sum));
  }
  
  private void handleComments(ActionEvent event) {
    LOGGER.debug("Handling commenting");
    EvaluatorPromptFactory.showProgressSummary(group, summaryCb.getSelectionModel().getSelectedItem());
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
