package ch.unibas.dmi.dbis.cs108pet.ui.overview;

import ch.unibas.dmi.dbis.cs108pet.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.cs108pet.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.cs108pet.common.StringUtils;
import ch.unibas.dmi.dbis.cs108pet.control.EntityController;
import ch.unibas.dmi.dbis.cs108pet.data.Catalogue;
import ch.unibas.dmi.dbis.cs108pet.data.Group;
import ch.unibas.dmi.dbis.cs108pet.data.Milestone;
import ch.unibas.dmi.dbis.cs108pet.ui.common.Utils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.math3.util.Precision;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * UI element to show an overview of the participated groups.
 * This overview may contain statistical information, charts and others.
 * <p>
 *
 * @author silvan.heller
 * @author loris.sauter
 */
public class GroupStatisticsView extends VBox {

  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * TabPane where the different tabs with different plots are added
   */
  private TabPane tabPane;

  /**
   * Overview of all groups with points per milestone & grades
   */
  private TreeTableView<GroupOverviewItem> groupOverviewTable;
  /**
   * Overview of all requirements, how often they were achieved or not achieved
   */
  private TreeTableView<RequirementOverviewItem> requirementAnalysis;
  /**
   * Visualization of progress w.r.t. overall points
   */
  private LineChart<String, Number> pointsChart;
  /**
   * Visualization of progress w.r.t. points per milestone
   */
  private LineChart<String, Number> perMilestoneChart;

  /**
   * The box where the user enters the function which calculates the grade based on points
   */
  private HBox gradeContainer;
  private TextField gradingFunction;

  /**
   * Utility to reduce code-duplication
   */
  private EntityController ctrl = EntityController.getInstance();
  private CatalogueAnalyser analyser = ctrl.getCatalogueAnalyser();

  public GroupStatisticsView() {
    initComps();
    layoutComps();
    this.setPrefWidth(1000);
    this.setPrefHeight(600);
  }

  public void update() {
    LOGGER.trace("Not doing anything in update");
  }

  private void initComps() {
    setupGradingBox();
    setupGroupOverviewTable();
    setupRequirementAnalysis();
    pointsChart = createOverviewChart();
    pointsChart.setPrefSize(800, 600);
    perMilestoneChart = createPerMilestoneOverviewChart();
    perMilestoneChart.setPrefSize(800, 600);
    tabPane = new TabPane();
  }

  private void setupGradingBox() {
    gradingFunction = new TextField();
    gradingFunction.setAlignment(Pos.CENTER_LEFT);
    gradingFunction.setText("5*(p/max)+1");
    Label gradeLabel = new Label("Grade Function: ");
    Button update = new Button("Update Grades");
    update.setOnAction(event -> {
      if (gradingFunction.getText() != null && !gradingFunction.getText().isEmpty()) {
        fillGroupOverviewTable();
      } else {
        LOGGER.debug("Empty text for grading function, grades are not updated");
      }
    });
    gradeContainer = new HBox();
    gradeContainer.getChildren().addAll(gradeLabel, gradingFunction, update);
    gradeContainer.setSpacing(10);
    gradeContainer.setMinHeight(40);
    gradeContainer.setPrefHeight(100);
    gradeContainer.setMinWidth(200);
  }

  private void layoutComps() {
    tabPane.prefWidthProperty().bind(widthProperty());
    tabPane.prefHeightProperty().bind(heightProperty());
    getChildren().add(tabPane);

    // === Tab with tables ===
    Tab tableTab = new Tab("Group Overviews");
    VBox tableContainer = new VBox();
    Utils.applyDefaultSpacing(tableContainer);
    ScrollPane tableScroll = new ScrollPane();
    tableScroll.setContent(tableContainer);
    tableContainer.prefHeightProperty().bind(tableScroll.prefHeightProperty());
    tableContainer.prefWidthProperty().bind(tableScroll.prefWidthProperty());
    VBox first = new VBox();
    Utils.applyDefaultSpacing(first);
    Label firstTitle = new Label("Points per Milestone Overview");
    firstTitle.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");
    first.getChildren().addAll(firstTitle, groupOverviewTable);
    Utils.applyDefaultSpacing(gradeContainer);
    tableContainer.getChildren().addAll(first, gradeContainer);
    tableTab.setContent(tableScroll);
    tableScroll.setFitToWidth(true);
    tabPane.getTabs().add(tableTab);

    // === Tab with requirement analysis
    var reqAnalysisTab = new Tab("Requirements Analysis");
    var container = new VBox();
    Utils.applyDefaultSpacing(container);
    container.getChildren().add(requirementAnalysis);
    reqAnalysisTab.setContent(container);
    tabPane.getTabs().add(reqAnalysisTab);

    // === Tab with line chart
    Tab lineChartTab = new Tab("Overall Progress Chart");
    VBox chartContainer = new VBox();
    Utils.applyDefaultSpacing(chartContainer);
    chartContainer.getChildren().add(pointsChart);
    lineChartTab.setContent(chartContainer);
    tabPane.getTabs().add(lineChartTab);

    // === Tab with grade / percentage per milestone chart
    lineChartTab = new Tab("Per Milestone Progress Chart");
    chartContainer = new VBox();
    Utils.applyDefaultSpacing(chartContainer);
    chartContainer.getChildren().add(perMilestoneChart);
    lineChartTab.setContent(chartContainer);
    tabPane.getTabs().add(lineChartTab);

  }

  private void setupRequirementAnalysis() {
    Catalogue cat = ctrl.getCatalogue();

    RequirementOverviewItemFactory factory = new RequirementOverviewItemFactory(ctrl.getCourse(), ctrl.getCatalogue());
    TreeItem<RequirementOverviewItem> root = new TreeItem<>(factory.createForRequirements(cat.getRequirements()));
    root.setExpanded(true);

    /*
     * For each requirement, sum the number of groups that achieved it. Reqs are only displayed if they are achieved by at least one group or if they are mali.
     * For mali, it is undesirable that mali are displayed which have not been achieved by anybody because we are not at that milestone yet. However, implementing this would be tedious and require hacking around the architecture of pet.
     */
    cat.getRequirements().forEach(req -> {
      TreeItem<RequirementOverviewItem> it = new TreeItem<>(factory.createForRequirement(req, ctrl.groupList()));
      if (it.getValue().getAchievedCount() > 0 || it.getValue().getRequirement().isMalus()) {
        root.getChildren().add(it);
      }
    });

    TreeTableColumn<RequirementOverviewItem, String> nameCol = new TreeTableColumn<>("Name");
    nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getRequirement().getName()));

    TreeTableColumn<RequirementOverviewItem, String> achievedCol = new TreeTableColumn<>("Achieved");
    achievedCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(""+param.getValue().getValue().getAchievedCount()));

    TreeTableColumn<RequirementOverviewItem, String> achievedMalCol = new TreeTableColumn<>("Achieved (Malus)");
    achievedMalCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getRequirement().isMalus() ? ""+param.getValue().getValue().getAchievedCount() : ""));

    TreeTableColumn<RequirementOverviewItem, String> achievedBonCol = new TreeTableColumn<>("Achieved (Bonus)");
    achievedBonCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getRequirement().isBonus() ? ""+param.getValue().getValue().getAchievedCount() : ""));

    TreeTableColumn<RequirementOverviewItem, String> achievedRegCol = new TreeTableColumn<>("Achieved (Regular)");
    achievedRegCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getRequirement().isRegular() ? ""+param.getValue().getValue().getAchievedCount() : ""));


    requirementAnalysis = new TreeTableView<>(root);
    requirementAnalysis.setPrefHeight(500);

    requirementAnalysis.getColumns().add(nameCol);
    requirementAnalysis.getColumns().add(achievedCol);
    requirementAnalysis.getColumns().add(achievedMalCol);
    requirementAnalysis.getColumns().add(achievedBonCol);
    requirementAnalysis.getColumns().add(achievedRegCol);
    requirementAnalysis.setShowRoot(false);

    nameCol.setPrefWidth(150);

    requirementAnalysis.setTableMenuButtonVisible(true);

    requirementAnalysis.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    requirementAnalysis.getSelectionModel().setCellSelectionEnabled(false);

    requirementAnalysis.setMinHeight(requirementAnalysis.getPrefHeight());
    requirementAnalysis.refresh();
  }

  /**
   * Redraws the whole table. Both called when the grading function is updated and on initialization.
   */
  private void fillGroupOverviewTable() {
    groupOverviewTable.getColumns().clear();
    Catalogue cat = ctrl.getCatalogue();
    GroupOverviewItemFactory factory = new GroupOverviewItemFactory(ctrl.getCourse(), ctrl.getCatalogue());
    TreeItem<GroupOverviewItem> root = new TreeItem<>(factory.createForCatalogue(ctrl.groupList()));
    root.setExpanded(true);
    cat.getMilestones().forEach(ms -> {
      TreeItem<GroupOverviewItem> msItem = new TreeItem<>(factory.createFor(ms, ctrl.groupList()));
      root.getChildren().add(msItem);
    });

    TreeTableColumn<GroupOverviewItem, String> nameCol = new TreeTableColumn<>("Name");
    nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<GroupOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getName()));

    TreeTableColumn<GroupOverviewItem, String> maxCol = new TreeTableColumn<>("Maximal Available");
    maxCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<GroupOverviewItem, String> param) -> new ReadOnlyStringWrapper(StringUtils.prettyPrint(param.getValue().getValue().getPoints(cat.getUuid()))));


    groupOverviewTable.setRoot(root);

    groupOverviewTable.getColumns().add(nameCol);
    groupOverviewTable.getColumns().add(maxCol);

    ctrl.groupList().forEach(g -> {
      TreeTableColumn<GroupOverviewItem, String> groupCol = new TreeTableColumn<>(g.getProjectName());

      groupCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<GroupOverviewItem, String> param) -> {
        double points = param.getValue().getValue().getPoints(g.getUuid());
        double max = param.getValue().getValue().getPoints(cat.getUuid());
        try {
          double grade = new ExpressionBuilder(gradingFunction.getText()).variables("p", "max").build().setVariable("p", points).setVariable("max", max).evaluate();
          grade = Precision.round(grade, 2);
          return new ReadOnlyStringWrapper(StringUtils.prettyPrint(points) + " (" + StringUtils.prettyPrint(grade) + "="+StringUtils.prettyPrint(Precision.round(grade*2, 0)/2)+")");
        } catch (Exception e) {
          LOGGER.error(e);
          return new ReadOnlyStringWrapper(StringUtils.prettyPrint(points));
        }
      });
      groupOverviewTable.getColumns().add(groupCol);
    });

    nameCol.setPrefWidth(150);
  }

  private void setupGroupOverviewTable() {
    groupOverviewTable = new TreeTableView<>();
    fillGroupOverviewTable();
    groupOverviewTable.setTableMenuButtonVisible(true);
    groupOverviewTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    groupOverviewTable.getSelectionModel().setCellSelectionEnabled(false);
    groupOverviewTable.setMinHeight(100);
  }

  private LineChart<String, Number> createPerMilestoneOverviewChart() {
    Catalogue cat = ctrl.getCatalogue();

    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Normalized Points per Milestone");
    xAxis.setLabel("Milestones");
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("Points per Milestone Overview");

    var milestones = new HashMap<Milestone, Boolean>();
    cat.getMilestones().forEach(m -> milestones.put(m, false));

    ArrayList<XYChart.Series<String, Number>> series = new ArrayList<>();
    for (Group g : ctrl.groupList()) {
      GroupAnalyser groupAnalyser = ctrl.getGroupAnalyser(g);

      XYChart.Series<String, Number> serie = new XYChart.Series<>();
      serie.setName(g.getName());
      for (Milestone ms : cat.getMilestones()) {
        var points = groupAnalyser.getSumFor(groupAnalyser.getProgressSummaryFor(ms));
        if (points != 0) {
          milestones.put(ms, true);
          serie.getData().add(new XYChart.Data<>(ms.getName(), points / analyser.getMaximalRegularSumFor(ms)));
        }
      }
      series.add(serie);
    }

    XYChart.Series<String, Number> catSeries = new XYChart.Series<>();
    catSeries.setName("Maximal Points");
    for (Milestone ms : cat.getMilestones()) {
      if (milestones.get(ms)) {
        catSeries.getData().add(new XYChart.Data<>(ms.getName(), 1));
      }
    }

    lineChart.getData().add(catSeries);
    lineChart.getData().addAll(series);

    lineChart.setMinHeight(300);

    return lineChart;
  }


  private LineChart<String, Number> createOverviewChart() {
    Catalogue cat = ctrl.getCatalogue();

    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Normalized Cumulative Points Achieved");
    xAxis.setLabel("Milestones");
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("Point Progress Overview");

    ArrayList<XYChart.Series<String, Number>> series = new ArrayList<>();

    var milestones = new HashMap<Milestone, Boolean>();
    cat.getMilestones().forEach(m -> milestones.put(m, false));

    for (Group g : ctrl.groupList()) {
      GroupAnalyser groupAnalyser = ctrl.getGroupAnalyser(g);

      XYChart.Series<String, Number> serie = new XYChart.Series<>();
      serie.setName(g.getName());
      for (Milestone ms : cat.getMilestones()) {
        var points = groupAnalyser.getCumulativeSumFor(groupAnalyser.getProgressSummaryFor(ms));
        if (points != 0) {
          milestones.put(ms, true);
          serie.getData().add(new XYChart.Data<>(ms.getName(), points / analyser.getCumulativeMaximalRegularSumFor(ms)));
        }
      }
      series.add(serie);
    }

    XYChart.Series<String, Number> catSeries = new XYChart.Series<>();
    catSeries.setName("Maximal Points");
    for (Milestone ms : cat.getMilestones()) {
      if (milestones.get(ms)) {
        catSeries.getData().add(new XYChart.Data<>(ms.getName(), 1));
      }
    }


    lineChart.getData().add(catSeries);
    lineChart.getData().addAll(series);

    lineChart.setMinHeight(300);

    return lineChart;
  }
}
