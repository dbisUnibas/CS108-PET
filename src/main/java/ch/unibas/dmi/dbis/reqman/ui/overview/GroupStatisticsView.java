package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
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
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupStatisticsView extends VBox {
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  private TreeTableView<GroupOverviewItem> treeTableView;
  private EntityController ctrl;
  private CatalogueAnalyser analyser;
  private LineChart<String, Number> chart;
  private ScrollPane scrollPane;
  private VBox container;
  
  public GroupStatisticsView() {
    // TODO Change layout entirely: Use tabs instead of VBox, make all responsive
    
    initComps();
    layoutComps();
    addDetailCharts();
  }
  
  public void update() {
    container.getChildren().remove(treeTableView);
    treeTableView = null;
    setupTreeTable();
    container.getChildren().add(0, treeTableView);
    chart = createOverviewChart();
  }
  
  private void initComps() {
    setupTreeTable();
    chart = createOverviewChart();
    chart.setPrefSize(800, 600);
    scrollPane = new ScrollPane();
    scrollPane.setPrefSize(850, 650);
    container = new VBox();
  }
  
  private void layoutComps() {
    scrollPane.setContent(container);
    Utils.applyDefaultSpacing(container);
    container.getChildren().addAll(treeTableView, chart);
    getChildren().add(scrollPane);
//    VBox.setVgrow(treeTableView, Priority.SOMETIMES);
    
    // sizing
    container.prefWidthProperty().bind(scrollPane.widthProperty());
    container.prefHeightProperty().bind(scrollPane.heightProperty());
    
    scrollPane.prefWidthProperty().bind(widthProperty());
    scrollPane.prefHeightProperty().bind(heightProperty());
    
    scrollPane.setMinHeight(200);
    scrollPane.setMinWidth(300);
  }
  
  private void addDetailCharts() {
    for (Group g : EntityController.getInstance().groupList()) {
      container.getChildren().add(createDetailChart(g));
    }
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
    
    double malus,regular,bonus, offset;
    
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
    
    sbc.setMinHeight(100);
    
    return sbc;
  }
}
