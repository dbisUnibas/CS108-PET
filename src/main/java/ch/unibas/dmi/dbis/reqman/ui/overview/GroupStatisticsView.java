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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

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
    initComps();
    layoutComps();
  }
  
  public void update() {
    container.getChildren().remove(treeTableView);
    treeTableView = null;
    setupTreeTable();
    container.getChildren().add(0, treeTableView);
  }
  
  private void initComps() {
    setupTreeTable();
    chart = createOverviewChart();
    chart.setPrefSize(800,600);
    scrollPane = new ScrollPane();
    scrollPane.setPrefSize(850,650);
    container = new VBox();
  }
  
  private void layoutComps() {
    scrollPane.setContent(container);
    Utils.applyDefaultSpacing(container);
    container.getChildren().addAll(treeTableView, chart);
    getChildren().add(scrollPane);
//    VBox.setVgrow(treeTableView, Priority.SOMETIMES);
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
    maxCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<GroupOverviewItem, String>param) -> new ReadOnlyStringWrapper(StringUtils.prettyPrint(param.getValue().getValue().getPoints(cat.getUuid()))));
    
    
    treeTableView = new TreeTableView<>(root);
    
    treeTableView.getColumns().add(nameCol);
    treeTableView.getColumns().add(maxCol);
    
    ctrl.groupList().forEach(g ->{
      TreeTableColumn<GroupOverviewItem, String> groupCol = new TreeTableColumn<>(g.getName());
      
      groupCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<GroupOverviewItem, String>param) -> new ReadOnlyStringWrapper(StringUtils.prettyPrint(param.getValue().getValue().getPoints(g.getUuid()))));
      
      treeTableView.getColumns().add(groupCol);
    });
    
    
    
    nameCol.setPrefWidth(150);
    
    treeTableView.setTableMenuButtonVisible(true);
    
    treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    treeTableView.getSelectionModel().setCellSelectionEnabled(false);
    
  }
  
  private LineChart<String, Number> createOverviewChart(){
    ctrl = EntityController.getInstance();
    analyser = ctrl.getCatalogueAnalyser();
    Catalogue cat = ctrl.getCatalogue();
    
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Points");
    xAxis.setLabel("Milestones");
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis,yAxis);
    lineChart.setTitle("Milestone Points Overview");
  
    XYChart.Series<String, Number> catSeries = new XYChart.Series<>();
    catSeries.setName("Maximal Points");
    for(Milestone ms : cat.getMilestones()){
      catSeries.getData().add(new XYChart.Data<>(ms.getName(), analyser.getCumultativeMaximalRegularSumFor(ms)));
    }
  
    ArrayList<XYChart.Series<String,Number>> series = new ArrayList<>();
    
    for(Group g : ctrl.groupList()){
      GroupAnalyser groupAnalyser = ctrl.getGroupAnalyser(g);
      
      XYChart.Series<String,Number> serie = new XYChart.Series<>();
      serie.setName(g.getName());
      for(Milestone ms : cat.getMilestones()){
        serie.getData().add(new XYChart.Data<>(ms.getName(), groupAnalyser.getCumultativeSumFor(groupAnalyser.getProgressSummaryFor(ms))));
      }
      series.add(serie);
    }
    
    lineChart.getData().add(catSeries);
    lineChart.getData().addAll(series);
    
    return lineChart;
  }
}
