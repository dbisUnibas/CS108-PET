package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.*;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressView extends VBox {
  
  private final static Logger LOGGER = LogManager.getLogger(ProgressView.class);
  
  /* === CollapsibleView === */
  /**
   * Container for the top of the view.
   * Is divided into control - header - side
   * Where control is the only container that is set by default with the button to collapse.
   */
  private BorderPane top;
  /**
   * The node containing the control button to collapse the collapsible element
   */
  private Node control;
  /**
   * The header of the always visible part
   */
  private Node header;
  /**
   * An optional sidebar
   */
  private Node side;
  /**
   * The actual collapsible element which may not be visible.
   */
  private Node collapsible;
  
  
  /**
   * The actual button to collapse (or un-collapse)
   */
  private ToggleButton collapseButton;
  
  
  /* === ProgressView === */
  private GridPane headerContainer;
  private Label heading;
  @Nullable
  private Label type; // May be null
  private Label excerpt;
  
  private GridPane assessmentContainer;
  private HBox assessmentWrapper;
  private Label categoryLbl;
  private Label category;
  private Label maxPoints;
  
  
  private Spinner<Double> spinnerPoints;
  
  private CheckBox check;
  private Label points;
  private ToggleGroup toggleGroup = new ToggleGroup();
  private RadioButton yesBtn;
  private RadioButton noBtn;
  
  private GridPane collapsibleContainer;
  private Label descLbl;
  private TextArea taDesc;
  private Label minMSLbl;
  private Label minMS;
  private Label maxMSLbl;
  private Label maxMS;
  private Label commentLbl;
  private TextArea taComment;
  
  private Label lastModifiedLbl;
  private Label lastModifiedDisplay;
  private Label predecessorLbl;
  private ScrollPane predecessorScroll;
  
  
  /* === Model / Controller === */
  private Progress progress;
  private Requirement requirement;
  private ProgressSummary progressSummary;
  private Group group;
  
  private List<PointsChangeListener> listeners = new ArrayList<>();
  private List<DirtyListener> dirtyListeners = new ArrayList<>();
  
  
  /**
   * Creates a new ProgressView and ints all
   *
   * @param progress
   */
  public ProgressView(Group group, Progress progress, ProgressSummary progressSummary) {
    this();
    this.progress = progress;
    this.progressSummary = progressSummary;
    this.requirement = EntityController.getInstance().getCatalogueAnalyser().getRequirementById(progress.getRequirementUUID());
    this.group = group;
    initComponents();
    layoutComponents();
    setupControlHandlers();
    loadProgress();
  }
  
  /**
   * Creates a new ProgressView and inits and layouts all Collapsilbe stuff
   */
  private ProgressView() {
    super();
    initCollapsibleView();
    layoutCollapsibleView();
  }
  
  public Requirement getRequirement() {
    return requirement;
  }
  
  public Progress getProgress() {
    return progress;
  }
  
  
  public void addPointsChangeListener(PointsChangeListener listener) {
    listeners.add(listener);
  }
  
  public void removePointsChangeListener(PointsChangeListener listener) {
    listeners.remove(listener);
  }
  
  public void setSide(Node side) {
    this.side = side;
    if (this.side != null) {
      top.setRight(this.side);
    }
  }
  
  public void setHeader(Node header) {
    this.header = header;
    if (this.header != null) {
      top.setCenter(header);
    }
  }
  
  public void setCollapsible(Node node) {
    this.collapsible = node;
  }
  
  public void setLocked(boolean locked) {
    assessmentContainer.setDisable(locked);
    taComment.setDisable(locked);
  }
  
  public void updatePredecessorDisplay() {
    HBox box = new HBox();
    Utils.applyDefaultSpacing(box);
    EntityController.getInstance().getCatalogueAnalyser().getPredecessors(requirement).forEach(r -> {
      Progress predProg = EntityController.getInstance().getGroupAnalyser(group).getProgressFor(r);
      if (predProg.hasProgress()) {
        box.getChildren().add(new Label(r.getName()));
      } else {
        Label lbl = new Label(r.getName());
        lbl.setTextFill(Color.RED);
        box.getChildren().add(lbl);
      }
    });
    predecessorScroll.setContent(box);
  }
  
  void addDirtyListener(DirtyListener listener) {
    dirtyListeners.add(listener);
  }
  
  void removeDirtyList(DirtyListener listener) {
    dirtyListeners.remove(listener);
  }
  
  void markSaved() {
  
  }
  
  private void initAssessmentComponents() {
    switch (requirement.getType()) {
      case REGULAR:
        if (requirement.isBinary()) {
          // Binary:
          yesBtn = new RadioButton("Yes");
          yesBtn.setToggleGroup(toggleGroup);
          noBtn = new RadioButton("No");
          noBtn.setToggleGroup(toggleGroup);
          points = new Label("0");
        } else {
          spinnerPoints = new Spinner<>(0d, requirement.getMaxPoints(), -1d);
          spinnerPoints.getEditor().setPrefColumnCount(StringUtils.prettyPrint(requirement.getMaxPoints()).length() + 4);
          spinnerPoints.setEditable(true);
        }
        break;
      case BONUS:
      case MALUS:
        if (requirement.isBinary()) {
          type = new Label(requirement.getType().toString());
          check = new CheckBox();
          points = new Label("0");
        } else {
          spinnerPoints = new Spinner<>(0d, requirement.getMaxPoints(), -1d);
          spinnerPoints.getEditor().setPrefColumnCount(StringUtils.prettyPrint(requirement.getMaxPoints()).length() + 4); // 4 is totally a magic number
          spinnerPoints.setEditable(true);
        }
        
        break;
    }
  }
  
  private void initComponents() {
    headerContainer = Utils.generateDefaultGridPane();
    heading = new Label(requirement.getName());
    heading.setStyle("-fx-font-size: 1.5em;-fx-font-weight: bold;");
    excerpt = new Label(requirement.getExcerpt());
    assessmentContainer = Utils.generateDefaultGridPane();
    assessmentWrapper = new HBox();
    
    String sign = requirement.isMalus() ? "-" : "";
    maxPoints = new Label("/ " + sign + StringUtils.prettyPrint(requirement.getMaxPoints()));
    
    categoryLbl = new Label("Category:");
    category = new Label(requirement.getCategory());
    
    collapsibleContainer = Utils.generateDefaultGridPane();
    minMSLbl = new Label("Available from");
    Milestone miMS = EntityController.getInstance().getCourseManager().getMinimalMilestone(requirement);
    Milestone maMS = EntityController.getInstance().getCourseManager().getMaximalMilestone(requirement);
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
    minMS = new Label(sdf.format(EntityController.getInstance().getCourseManager().getMilestoneDate(miMS)));
    maxMSLbl = new Label("up to");
    maxMS = new Label(sdf.format(EntityController.getInstance().getCourseManager().getMilestoneDate(maMS)));
    descLbl = new Label("Description");
    taDesc = new TextArea(requirement.getDescription());
    taDesc.setEditable(false);
    taDesc.setPrefRowCount(4); // is totally a magic number
    commentLbl = new Label("Comment");
    taComment = new TextArea();
    taComment.setPrefRowCount(4);
    
    lastModifiedLbl = new Label("Assessment on");
    lastModifiedDisplay = new Label();
    predecessorLbl = new Label("Predecessors");
    predecessorScroll = new ScrollPane();
    predecessorScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    predecessorScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    initAssessmentComponents();
  }
  
  private void layoutComponents() {
    headerContainer.addRow(0, heading);
    headerContainer.addRow(1, excerpt);
    layoutAssessmentComponents();
    setHeader(headerContainer);
    setSide(assessmentContainer);
    
    collapsibleContainer.addRow(0, minMSLbl, minMS, maxMSLbl, maxMS);
    collapsibleContainer.add(descLbl, 0, 1);
    collapsibleContainer.add(taDesc, 1, 1, 3, 1);
    collapsibleContainer.add(commentLbl, 0, 2);
    collapsibleContainer.add(taComment, 1, 2, 3, 1);
    collapsibleContainer.add(lastModifiedLbl, 0, 3);
    collapsibleContainer.add(lastModifiedDisplay, 1, 3, 3, 1);
    collapsibleContainer.add(predecessorLbl, 0, 4);
    collapsibleContainer.add(predecessorScroll, 1, 4, 3, 1);
    setCollapsible(collapsibleContainer);
    setBorder(new Border(new BorderStroke(Color.SILVER, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
  }
  
  private void layoutAssessmentComponents() {
    Utils.applyDefaultSpacing(assessmentWrapper);
    assessmentContainer.setAlignment(Pos.CENTER_RIGHT);
    switch (requirement.getType()) {
      case REGULAR:
        if (requirement.isBinary()) {
          assessmentWrapper.getChildren().addAll(yesBtn, noBtn, points, maxPoints);
        } else {
          assessmentWrapper.getChildren().addAll(spinnerPoints, maxPoints);
        }
        break;
      case BONUS:
      case MALUS:
        if (requirement.isBinary()) {
          assessmentWrapper.getChildren().addAll(check, points, maxPoints);
        } else {
          assessmentWrapper.getChildren().addAll(spinnerPoints, maxPoints);
        }
        
        break;
    }
    HBox categoryWrapper = new HBox();
    Utils.applyDefaultSpacing(categoryWrapper);
    categoryWrapper.setAlignment(Pos.CENTER_RIGHT);
    categoryWrapper.getChildren().addAll(categoryLbl, category);
    assessmentContainer.add(assessmentWrapper, 0, 0, 2, 1);
    assessmentContainer.add(categoryWrapper, 0, 1, 2, 1);
//    assessmentContainer.addRow(1, categoryLbl, category);
    assessmentContainer.prefWidthProperty().bind(widthProperty().multiply(0.4));
    assessmentContainer.setMinWidth(Math.max(assessmentWrapper.getWidth(), categoryWrapper.getWidth()) + 5); // 5 Totally a magic number
  }
  
  private void initCollapsibleView() {
    // Containers
    top = new BorderPane();
    collapseButton = new ToggleButton("", Utils.createArrowDownNode());
    control = new HBox();
    Utils.applyDefaultSpacing(control);
    collapseButton.setOnAction(this::handleCollapse);
    //collapsible.setStyle("-fx-background-color: white;-fx-padding: 10px; -fx-spacing: 10px;-fx-border-width: 1px;-fx-border-color: silver"); // collapsible is null
  }
  
  private void layoutCollapsibleView() {
    // Cast easy, since always HBox (could actually be typed as HBox)
    ((HBox) control).getChildren().add(collapseButton);
    top.setLeft(control);
    if (header != null) {
      top.setCenter(header);
    }
    if (side != null) {
      top.setRight(side);
    }
    // Set the top part.
    getChildren().add(top);
    setFillWidth(true);
    top.prefWidthProperty().bind(widthProperty());
  }
  
  private void setupControlHandlers() {
    setupCommentHandling();
    setupAssessmentHandling();
  }
  
  private void setupAssessmentHandling() {
    switch (requirement.getType()) {
      case REGULAR:
        if (requirement.isBinary()) {
          yesBtn.setOnAction(this::handleYesNo);
          noBtn.setOnAction(this::handleYesNo);
        } else {
          setupSpinner();
        }
        break;
      case BONUS:
      case MALUS:
        if (requirement.isBinary()) {
          check.setOnAction(this::handleCheck);
        } else {
          setupSpinner();
        }
        break;
    }
    
  }
  
  private void setupSpinner() {
    // Solution by: http://stackoverflow.com/a/39380146
    spinnerPoints.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        spinnerPoints.increment(0);
      }
    });
    spinnerPoints.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (Double.compare(oldValue, newValue) != 0) { // Only if really new value
        progress.setFraction(newValue / requirement.getMaxPoints());
        processAssessment();
      }
    });
  }
  
  private void updatePointsDisplay() {
    // Only update point display, if in non-spinner environment
    if (points != null) {
      points.setText(StringUtils.prettyPrint(EntityController.getInstance().getCatalogueAnalyser().getActualPoints(progress)));
    }
    
  }
  
  private void handleYesNo(ActionEvent event) {
    if (yesBtn.equals(event.getSource())) {
      LOGGER.debug("Handling Yes!");
      progress.setFraction(1);
    } else if (noBtn.equals(event.getSource())) {
      LOGGER.debug("Handling No!");
      progress.setFraction(0);
    } else {
      LOGGER.debug("Ignoring unknown event source: {}", event);
      return;
    }
    processAssessment();
  }
  
  private void handleCheck(ActionEvent event) {
    if (check.isSelected()) {
      LOGGER.debug("Handling check");
      progress.setFraction(1);
    } else {
      LOGGER.debug("Handling uncheck");
      progress.setFraction(0);
    }
    processAssessment();
  }
  
  private void setupCommentHandling() {
    taComment.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null || newValue.isEmpty()) {
        LOGGER.debug("Null or empty comment");
      } else {
        progress.setComment(newValue);
      }
    });
  }
  
  private void handleCollapse(ActionEvent event) {
    if (collapseButton.isSelected()) {
      collapseButton.setGraphic(Utils.createArrowUpNode());
      if (collapsible != null) {
        getChildren().add(collapsible);
      }
    } else {
      collapseButton.setGraphic(Utils.createArrowDownNode());
      if (collapsible != null) {
        getChildren().remove(collapsible);
      }
    }
    event.consume();
  }
  
  private void processAssessment() {
    progress.setAssessmentDate(new Date());
    progress.setProgressSummaryUUID(progressSummary.getUuid());
    LOGGER.debug("Processing assessment: {}", progress);
    updatePointsDisplay();
    displayAssessmentDate();
    notifyPointsListener();
  }
  
  private void loadProgress() {
    if (progress != null && !progress.isFresh()) {
      switch (requirement.getType()) {
        case REGULAR:
          if (requirement.isBinary()) {
            boolean prog = progress.hasProgress();
            yesBtn.setSelected(prog);
            noBtn.setSelected(!prog);
          } else {
            spinnerPoints.getValueFactory().setValue(progress.getFraction() * requirement.getMaxPoints());
          }
          break;
        case BONUS:
        case MALUS:
          if (requirement.isBinary()) {
            check.setSelected(progress.hasProgress());
          } else {
            spinnerPoints.getValueFactory().setValue(progress.getFraction() * requirement.getMaxPoints());
          }
          
          break;
      }
      updatePointsDisplay();
      taComment.setText(progress.getComment());
      displayAssessmentDate();
      updatePredecessorDisplay();
    }
  }
  
  private void displayAssessmentDate() {
    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
    if (progress.getAssessmentDate() != null) {
      lastModifiedDisplay.setText(df.format(progress.getAssessmentDate()));
    }
  }
  
  private void notifyPointsListener() {
    listeners.forEach(l -> l.pointsChanged(progress.getPoints()));
  }
  
  private void notifyDirtyListeners(boolean dirty) {
    dirtyListeners.forEach(listener -> listener.mark(dirty));
  }
}
