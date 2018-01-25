package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.java2d.pipe.SpanShapeRenderer;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueStatisticsView extends VBox {
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  private TreeTableView<SimpleDisplayItem> treeTableView;
  private EntityController ctrl;
  private CatalogueAnalyser analyser;
  
  public CatalogueStatisticsView() {
    initComps();
    layoutComps();
  }
  
  private void initComps() {
    setupTreeTable();
  }
  
  private void layoutComps() {
    getChildren().add(treeTableView);
  }
  
  private void setupTreeTable() {
    ctrl = EntityController.getInstance();
    analyser = ctrl.getCatalogueAnalyser();
    Catalogue cat = ctrl.getCatalogue();
    TreeItem<SimpleDisplayItem> root = new TreeItem<>(new SimpleDisplayItem(cat.getName(), analyser.getMaximalRegularSum()));
    root.setExpanded(true);
    cat.getMilestones().forEach(ms -> {
      TreeItem<SimpleDisplayItem> msItem = new TreeItem<>(createFor(ms));
      msItem.setExpanded(true);
      analyser.getRequirementsFor(ms).stream().sorted(analyser.getRequirementComparator()).forEach(r -> {
        TreeItem<SimpleDisplayItem> reqItem = new TreeItem<>(createFor(r));
        msItem.getChildren().add(reqItem);
      });
      root.getChildren().add(msItem);
    });
    
    TreeTableColumn<SimpleDisplayItem, String> nameCol = new TreeTableColumn<>("Name");
    nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<SimpleDisplayItem, String> param) ->
        new ReadOnlyStringWrapper(param.getValue().getValue().getName()));
    
    TreeTableColumn<SimpleDisplayItem, String> pointsCol = new TreeTableColumn<>("Points");
    pointsCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<SimpleDisplayItem, String> param) ->
        new ReadOnlyStringWrapper(StringUtils.prettyPrint(param.getValue().getValue().getPoints())));
    
    treeTableView = new TreeTableView<>(root);
    
    treeTableView.getColumns().add(nameCol);
    treeTableView.getColumns().add(pointsCol);
    nameCol.setPrefWidth(150);
  
    treeTableView.setTableMenuButtonVisible(true);
    
    treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    treeTableView.getSelectionModel().setCellSelectionEnabled(false);
    
    treeTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
      LOGGER.debug("SelectionIndex changed: old={}, new={}", observable,oldValue,newValue);
      // Overall, the index is not so important, but this property can be used to get notified about the selection (and changes)!
    });
    
    
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
