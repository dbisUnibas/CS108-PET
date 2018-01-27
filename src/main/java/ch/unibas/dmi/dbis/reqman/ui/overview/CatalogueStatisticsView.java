package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import javafx.beans.property.*;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueStatisticsView extends VBox {
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  private TreeTableView<CatalogueOverviewItem> treeTableView;
  private EntityController ctrl;
  private CatalogueAnalyser analyser;
  private CatalogueSummaryView summaryView;
  
  public CatalogueStatisticsView() {
    initComps();
    layoutComps();
  }
  
  private void initComps() {
    setupTreeTable();
    summaryView = new CatalogueSummaryView(EntityController.getInstance().getCatalogueAnalyser());
  }
  
  private void layoutComps() {
    getChildren().addAll(treeTableView, summaryView);
    VBox.setVgrow(treeTableView, Priority.SOMETIMES);
  }
  
  private void setupTreeTable() {
    ctrl = EntityController.getInstance();
    analyser = ctrl.getCatalogueAnalyser();
    Catalogue cat = ctrl.getCatalogue();
    CatalogueOverviewItemFactory factory = new CatalogueOverviewItemFactory(analyser);
    TreeItem<CatalogueOverviewItem> root = new TreeItem<>(factory.createFor(cat));
    root.setExpanded(true);
    cat.getMilestones().forEach(ms -> {
      TreeItem<CatalogueOverviewItem> msItem = new TreeItem<>(factory.createFor(ms));
      msItem.setExpanded(true);
      analyser.getRequirementsFor(ms).stream().sorted(analyser.getRequirementComparator()).forEach(r -> {
        TreeItem<CatalogueOverviewItem> reqItem = new TreeItem<>(factory.createFor(r));
        msItem.getChildren().add(reqItem);
      });
      root.getChildren().add(msItem);
    });
    
    TreeTableColumn<CatalogueOverviewItem, String> nameCol = new TreeTableColumn<>("Name");
    nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<CatalogueOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getName()));
    
    TreeTableColumn<CatalogueOverviewItem, String> pointsCol = new TreeTableColumn<>("Actual Points");
    pointsCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<CatalogueOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(StringUtils.prettyPrint(param.getValue().getValue().getActualPoints())));
    
    TreeTableColumn<CatalogueOverviewItem, String> typeCol = new TreeTableColumn<>("Type");
    typeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<CatalogueOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getType()));
    
    TreeTableColumn<CatalogueOverviewItem, String> regularPointsCol = new TreeTableColumn<>("Regular Points");
    regularPointsCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<CatalogueOverviewItem, String> param) ->
    {
      if (param.getValue().getValue().getType().equalsIgnoreCase(Requirement.Type.REGULAR.toString())) {
        return new ReadOnlyStringWrapper(StringUtils.prettyPrint(param.getValue().getValue().getRegularPoints()));
      } else {
        return new ReadOnlyStringWrapper("");
      }
    });
    
    TreeTableColumn<CatalogueOverviewItem, String> bonusPointsCol = new TreeTableColumn<>("Bonus Points");
    bonusPointsCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<CatalogueOverviewItem, String> param) ->
    {
      if (param.getValue().getValue().getType().equalsIgnoreCase(Requirement.Type.BONUS.toString())) {
        return new ReadOnlyStringWrapper(StringUtils.prettyPrint(param.getValue().getValue().getBonusPoints()));
      } else {
        return new ReadOnlyStringWrapper("");
      }
    });
    
    TreeTableColumn<CatalogueOverviewItem, String> malusPointsCol = new TreeTableColumn<>("Malus Points");
    malusPointsCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<CatalogueOverviewItem, String> param) ->
    {
      if (param.getValue().getValue().getType().equalsIgnoreCase(Requirement.Type.MALUS.toString())) {
        return new ReadOnlyStringWrapper(StringUtils.prettyPrint(param.getValue().getValue().getMalusPoints()));
      } else {
        return new ReadOnlyStringWrapper("");
      }
    });
    
    TreeTableColumn<CatalogueOverviewItem, String> categoryCol = new TreeTableColumn<>("Category");
    categoryCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<CatalogueOverviewItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getCategory()));
    
    treeTableView = new TreeTableView<>(root);
    
    treeTableView.getColumns().add(nameCol);
    treeTableView.getColumns().add(pointsCol);
    treeTableView.getColumns().add(typeCol);
    treeTableView.getColumns().add(regularPointsCol);
    treeTableView.getColumns().add(bonusPointsCol);
    treeTableView.getColumns().add(malusPointsCol);
    treeTableView.getColumns().add(categoryCol);
    nameCol.setPrefWidth(150);
    
    treeTableView.setTableMenuButtonVisible(true);
    
    treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    treeTableView.getSelectionModel().setCellSelectionEnabled(false);
    
    treeTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
      LOGGER.debug("SelectionIndex changed: old={}, new={}", observable, oldValue, newValue);
      // Overall, the index is not so important, but this property can be used to get notified about the selection (and changes)!
      if (newValue.intValue() != -1) {
        handleSelection();
      }else{
        summaryView.clearSelected();
      }
    });
    
  }
  
  public void update(){
    getChildren().remove(treeTableView);
    treeTableView = null;
    setupTreeTable();
    getChildren().add(0, treeTableView);
    summaryView.update();
  }
  
  private void handleSelection() {
    summaryView.update(treeTableView.getSelectionModel().getSelectedCells().stream().map(o -> o.getTreeItem().getValue()).collect(Collectors.toList() ));
  }
  
  
  private SimpleDisplayItem createFor(Requirement requirement) {
    return new SimpleDisplayItem(requirement.getName(), (requirement.isMalus() ? -1 : 1) * requirement.getMaxPoints());
  }
  
  private SimpleDisplayItem createFor(Milestone milestone) {
    return new SimpleDisplayItem(milestone.getName(), analyser.getMaximalRegularSumFor(milestone));
  }
  
  public static class SimpleDisplayItem {
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleDoubleProperty points = new SimpleDoubleProperty();
    
    public SimpleDisplayItem(String name) {
      this.name.set(name);
    }
    
    public SimpleDisplayItem(String name, double points) {
      this.name.set(name);
      this.points.set(points);
    }
    
    @Override
    public String toString() {
      final StringBuffer sb = new StringBuffer("SimpleDisplayItem{");
      sb.append("name=").append(name);
      sb.append(", points=").append(points);
      sb.append('}');
      return sb.toString();
    }
    
    public double getPoints() {
      return points.get();
    }
    
    public String getName() {
      return name.get();
    }
    
    StringProperty nameProperty() {
      return name;
    }
    
    DoubleProperty pointsProperty() {
      return points;
    }
    
  }
}
