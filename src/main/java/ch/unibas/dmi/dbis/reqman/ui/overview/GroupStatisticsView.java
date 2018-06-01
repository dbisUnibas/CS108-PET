package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * UI element to show an overview of the participated groups.
 * This overview may contain statistical information, charts and others.
 *
 * Current features:
 * <ul>
 *   <li>Table with group-milestone point mapping</li>
 *   <li>Table with how many groups fulfilled a requirement, per requirement</li>
 *   <li>Line chart with progress over milestones of groups</li>
 *   <li>Bar chart per group, with visualization of regular / malus / bonus ratio per milestone</li>
 * </ul>
 *
 *
 * @author loris.sauter
 */
public class GroupStatisticsView extends VBox {
  // TODO Code cleanup and restructure code
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  private TabPane tabPane;
  
  private TreeTableView<GroupOverviewItem> treeTableView;
  private EntityController ctrl;
  private CatalogueAnalyser analyser;
  private LineChart<String, Number> chart;
  private TreeTableView<RequirementOverviewItem> requirementAnalysis;
  
  public GroupStatisticsView() {
    
    initComps();
    layoutComps();
    addDetailCharts();
    update();
  }
  
  public void update() {
    // TODO re-implement
  }
  
  private void initComps() {
    setupTreeTable();
    setupRequirementAnalysis();
    chart = createOverviewChart();
    chart.setPrefSize(800, 600);
    tabPane = new TabPane();
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
    first.getChildren().addAll(firstTitle, treeTableView);
    VBox second = new VBox();
    Utils.applyDefaultSpacing(second);
    requirementAnalysis.setMinHeight(requirementAnalysis.getExpandedItemCount() * 5); // TODO Fix magic number
    Label secondTitle = new Label("Requirement Analysis");
    secondTitle.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");
    second.getChildren().addAll(secondTitle, requirementAnalysis);
    tableContainer.getChildren().addAll(first, second);
    tableTab.setContent(tableScroll);
    tableScroll.setFitToWidth(true);
    tabPane.getTabs().add(tableTab);
    
    // === Tab with line chart
    Tab lineChartTab = new Tab("Overall Progress Chart");
    VBox chartContainer = new VBox();
    Utils.applyDefaultSpacing(chartContainer);
    chartContainer.getChildren().add(chart);
    lineChartTab.setContent(chartContainer);
    tabPane.getTabs().add(lineChartTab);
    
  }
  
  private void addDetailCharts() {
    for (Group g : EntityController.getInstance().groupList()) {
      StackedBarChart<String, Number> chart = createDetailChart(g);
      chart.setMinHeight(150);
      chart.setMinWidth(200);
      Tab chartTab = new Tab(chart.getTitle());
      chartTab.setContent(chart);
      tabPane.getTabs().add(chartTab);
    }
  }
  
  private void setupRequirementAnalysis() {
    ctrl = EntityController.getInstance();
    analyser = ctrl.getCatalogueAnalyser();
    Catalogue cat = ctrl.getCatalogue();
    
    RequirementOverviewItemFactory factory = new RequirementOverviewItemFactory(ctrl.getCourse(), ctrl.getCatalogue());
    TreeItem<RequirementOverviewItem> root = new TreeItem<>(factory.createForRequirements(cat.getRequirements()));
    root.setExpanded(true);
    
    cat.getRequirements().forEach(req -> {
      TreeItem<RequirementOverviewItem> it = new TreeItem<>(factory.createForRequirement(req, ctrl.groupList()));
      root.getChildren().add(it);
    });
    
    TreeTableColumn<RequirementOverviewItem, String> nameCol = new TreeTableColumn<>("Name");
    nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getRequirement().getName()));
    
    TreeTableColumn<RequirementOverviewItem, Number> achievedCol = new TreeTableColumn<>("Achieved");
    achievedCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, Number> param) ->
        new ReadOnlyIntegerWrapper(param.getValue().getValue().getAchievedCount()));
    
    TreeTableColumn<RequirementOverviewItem, Number> achievedMalCol = new TreeTableColumn<>("Achieved (Malus)");
    achievedMalCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, Number> param) ->
        new ReadOnlyIntegerWrapper(param.getValue().getValue().getRequirement().isMalus() ? param.getValue().getValue().getAchievedCount() : -1));
    
    TreeTableColumn<RequirementOverviewItem, Number> achievedBonCol = new TreeTableColumn<>("Achieved (Bonus)");
    achievedBonCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, Number> param) ->
        new ReadOnlyIntegerWrapper(param.getValue().getValue().getRequirement().isBonus() ? param.getValue().getValue().getAchievedCount() : -1));
    
    TreeTableColumn<RequirementOverviewItem, Number> achievedRegCol = new TreeTableColumn<>("Achieved (Regular)");
    achievedRegCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<RequirementOverviewItem, Number> param) ->
        new ReadOnlyIntegerWrapper(param.getValue().getValue().getRequirement().isRegular() ? param.getValue().getValue().getAchievedCount() : -1));
    
    
    requirementAnalysis = new TreeTableView<>(root);
    requirementAnalysis.setPrefHeight(100);
    
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
  
  private void setupTreeTable() {
    ctrl = EntityController.getInstance();
    analyser = ctrl.getCatalogueAnalyser();
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
    
    
    treeTableView = new TreeTableView<>(root);
    
    treeTableView.getColumns().add(nameCol);
    treeTableView.getColumns().add(maxCol);
    
    ctrl.groupList().forEach(g -> {
      TreeTableColumn<GroupOverviewItem, String> groupCol = new TreeTableColumn<>(g.getName());
      
      groupCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<GroupOverviewItem, String> param) -> new ReadOnlyStringWrapper(StringUtils.prettyPrint(param.getValue().getValue().getPoints(g.getUuid()))));
      
      treeTableView.getColumns().add(groupCol);
    });
    
    
    nameCol.setPrefWidth(150);
    
    treeTableView.setTableMenuButtonVisible(true);
    
    treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    treeTableView.getSelectionModel().setCellSelectionEnabled(false);
    
    
    treeTableView.setMinHeight(treeTableView.getPrefHeight());
  }
  
  private LineChart<String, Number> createOverviewChart() {
    ctrl = EntityController.getInstance();
    analyser = ctrl.getCatalogueAnalyser();
    Catalogue cat = ctrl.getCatalogue();
    
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Points");
    xAxis.setLabel("Milestones");
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("Milestone Points Overview");
    
    XYChart.Series<String, Number> catSeries = new XYChart.Series<>();
    catSeries.setName("Maximal Points");
    for (Milestone ms : cat.getMilestones()) {
      catSeries.getData().add(new XYChart.Data<>(ms.getName(), analyser.getCumulativeMaximalRegularSumFor(ms)));
    }
    
    ArrayList<XYChart.Series<String, Number>> series = new ArrayList<>();
    
    for (Group g : ctrl.groupList()) {
      GroupAnalyser groupAnalyser = ctrl.getGroupAnalyser(g);
      
      XYChart.Series<String, Number> serie = new XYChart.Series<>();
      serie.setName(g.getName());
      for (Milestone ms : cat.getMilestones()) {
        serie.getData().add(new XYChart.Data<>(ms.getName(), groupAnalyser.getCumulativeSumFor(groupAnalyser.getProgressSummaryFor(ms))));
      }
      series.add(serie);
    }
    
    lineChart.getData().add(catSeries);
    lineChart.getData().addAll(series);
    
    lineChart.setMinHeight(300);
    
    return lineChart;
  }
  
  private StackedBarChart<String, Number> createDetailChart(Group g) {
    ctrl = EntityController.getInstance();
    analyser = ctrl.getCatalogueAnalyser();
    Catalogue cat = ctrl.getCatalogue();
    GroupAnalyser groupAnalyser = ctrl.getGroupAnalyser(g);
    
    final CategoryAxis xAxis = new CategoryAxis();
    xAxis.setLabel("Milestones");
    final NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Points");
    final StackedBarChart<String, Number> sbc = new StackedBarChart<>(xAxis, yAxis);
    xAxis.setCategories(FXCollections.observableArrayList(cat.getMilestones().stream().map(Milestone::getName).collect(Collectors.toList())));
    
    XYChart.Series<String, Number> malusSeries = new XYChart.Series<>();
    malusSeries.setName("Malus Points");
    XYChart.Series<String, Number> regularSeries = new XYChart.Series<>();
    regularSeries.setName("Regular Points");
    XYChart.Series<String, Number> bonusSeries = new XYChart.Series<>();
    bonusSeries.setName("Bonus Points");
    
    double malus, regular, bonus, offset;
    
    for (Milestone ms : cat.getMilestones()) {
      malus = groupAnalyser.getMalusSumFor(groupAnalyser.getProgressSummaryFor(ms));
      offset = malus < 0 ? malus : 0;
      regular = offset + groupAnalyser.getRegularSumFor(groupAnalyser.getProgressSummaryFor(ms));
      offset = regular < 0 ? regular : 0;
      bonus = offset + groupAnalyser.getBonusSumFor(groupAnalyser.getProgressSummaryFor(ms));
      
      malusSeries.getData().add(new XYChart.Data<>(ms.getName(), malus));
      regularSeries.getData().add(new XYChart.Data<>(ms.getName(), regular));
      bonusSeries.getData().add(new XYChart.Data<>(ms.getName(), bonus));
    }
    sbc.setTitle("Details Per Milestone of " + g.getName());
    
    sbc.getData().addAll(malusSeries, regularSeries, bonusSeries);
    
    sbc.setMinHeight(200);
    
    return sbc;
  }
}
