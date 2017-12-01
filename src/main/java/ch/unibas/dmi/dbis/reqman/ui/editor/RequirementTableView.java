package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.reqman.ui.event.TargetEntity;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class RequirementTableView extends BorderPane {
  
  private static final Logger LOGGER = LogManager.getLogger(RequirementTableView.class);
  EventHandler<CUDEvent> modifyHandler = null;
  private Label title;
  private HBox header;
  private Button addBtn;
  private Button rmBtn;
  private GridPane footer;
  private TableView<ObservableRequirement> table;
  private ObservableList<ObservableRequirement> tableData = FXCollections.observableArrayList();
  private Catalogue cat;
  
  private Label lblFooterHeader;
  private Label lblTotalPoints;
  private Label lblTotalBonus;
  private Label lblTotalMalus;
  private SimpleDoubleProperty totalPoints;
  private SimpleDoubleProperty totalBonus;
  private SimpleDoubleProperty totalMalus;
  
  RequirementTableView() {
    initComponents();
    layoutComponents();
  }
  
  public void setOnAdd(EventHandler<CUDEvent> handler) {
    addBtn.setOnAction(event -> {
      handler.handle(CUDEvent.generateCreationEvent(event, TargetEntity.REQUIREMENT));
    });
  }
  
  public void setOnModify(EventHandler<CUDEvent> handler) {
    this.modifyHandler = handler;
  }
  
  public void setOnRemove(EventHandler<CUDEvent> handler) {
    rmBtn.setOnAction(event -> {
      handler.handle(CUDEvent.generateDeletionEvent(event, TargetEntity.REQUIREMENT, table.getSelectionModel().getSelectedIndex(), table.getSelectionModel().getSelectedItem()));
    });
  }
  
  public void setRequirements(ObservableList<Requirement> requirements, Catalogue catalogue) {
    cat = catalogue;
    // Ensures that this view is really simply view - and nothing more!
    LOGGER.traceEntry();
    LOGGER.debug("Requirements={}\ncat={}", requirements, catalogue);
    tableData.clear();
    requirements.forEach(r -> tableData.add(ObservableRequirement.fromRequirement(r)));
    LOGGER.trace(":setRequirements - Created " + tableData.size() + " observable requirements");
    requirements.addListener(new ListChangeListener<Requirement>() {
      @Override
      public void onChanged(Change<? extends Requirement> c) {
        LOGGER.trace(":changed");
        while (c.next()) {
          if (c.wasPermutated()) {
            // Permutation
            for (int i = c.getFrom(); i < c.getTo(); ++i) {
              // With c.getPermutation(i) new index is provided
            }
          } else if (c.wasUpdated()) {
            // Update
            for (int i = c.getFrom(); i < c.getTo(); ++i) {
              // are updated
              // Was updated: c.getList().get(i);
              LOGGER.trace("Updated: {}", c);
            }
          } else {
            for (Requirement removeItem : c.getRemoved()) {
              LOGGER.trace("Removed {}", c.getRemoved());
              tableData.remove(ObservableRequirement.fromRequirement(removeItem));
              updatePoints();
            }
            for (Requirement addItem : c.getAddedSubList()) {
              LOGGER.trace("Added  {}", c.getAddedSubList());
              if (addItem == null) {
                continue;
              }
              ObservableRequirement obsReq = ObservableRequirement.fromRequirement(addItem);
              LOGGER.debug("Table contains to add: {}", tableData.contains(obsReq));
              if (!tableData.contains(obsReq)) {
                tableData.add(obsReq);
                updatePoints();
              }
            }
          }
          
        }
        c.reset();
      }
    });
    updatePoints();
  }
  
  public int getRequirementsSize() {
    return tableData.size();
  }
  
  public Requirement getSelectedRequirement() {
    ObservableRequirement req = table.getSelectionModel().getSelectedItem();
    return req.getRequirement();
  }
  
  public void updateRequirement(Requirement requirement) {
    removeFromTable(requirement);
    tableData.add(ObservableRequirement.fromRequirement(requirement));
    table.refresh();
    updatePoints();
  }
  
  private void updatePoints() {
    CatalogueAnalyser analyser = EntityController.getInstance().getCatalogueAnalyser();
    if (analyser != null) {
      totalPoints.set(analyser.getMaximalRegularSum());
      totalBonus.set(analyser.getMaximalBonusSum());
      totalMalus.set(analyser.getMaximalMalusSum());
    }
    
  }
  
  private void removeFromTable(Requirement requirement) {
    tableData.removeIf(or -> or.getRequirement().equals(requirement));
  }
  
  private void layoutComponents() {
    // Header layout
    header.setSpacing(10);
    header.setPadding(new Insets(10));
    header.setAlignment(Pos.CENTER_LEFT);
    
    // Spacer
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    
    // The buttons
    HBox btnWrapper = new HBox();
    btnWrapper.setSpacing(10);
    btnWrapper.getChildren().addAll(addBtn, rmBtn);
    
    header.getChildren().addAll(title, spacer, btnWrapper);
    
    
    footer.add(lblFooterHeader, 0, 0, 3, 1);
    TextField tfPoints = createAndBind(totalPoints);
    TextField tfBonus = createAndBind(totalBonus);
    TextField tfMalus = createAndBind(totalMalus);
    footer.add(lblTotalPoints, 0, 1);
    footer.add(lblTotalBonus, 1, 1);
    footer.add(lblTotalMalus, 2, 1);
    footer.add(tfPoints, 0, 2);
    footer.add(tfBonus, 1, 2);
    footer.add(tfMalus, 2, 2);
    
    
    // Adding header
    setTop(header);
    
    // Table already layed out
    
    // Adding table
    setCenter(table);
    
    // Adding footer
    setBottom(footer);
    
  }
  
  private TextField createAndBind(SimpleDoubleProperty contentProperty) {
    TextField tf = new TextField();
    tf.setEditable(false);
    tf.textProperty().bind(contentProperty.asString());
    tf.setAlignment(Pos.CENTER_RIGHT);
    return tf;
  }
  
  private void initComponents() {
    title = new Label("Requirements");
    header = new HBox();
    
    addBtn = Utils.createPlusButton();
    addBtn.setTooltip(new Tooltip("Opens a dialog to add a new requirement"));
    rmBtn = Utils.createMinusButton();
    rmBtn.setTooltip(new Tooltip("Removes the currently selected requirement"));
    
    table = initTable();
    table.setTooltip(new Tooltip("Double-click to modify requirement"));
    table.setOnMouseClicked(this::handleModification);
    
    footer = Utils.generateDefaultGridPane();
    lblFooterHeader = new Label("Catalogue Statistics");
    lblTotalPoints = new Label("Total regular Points:");
    lblTotalBonus = new Label("Total Bonus:");
    lblTotalMalus = new Label("Total Malus:");
    
    totalPoints = new SimpleDoubleProperty();
    totalBonus = new SimpleDoubleProperty();
    totalMalus = new SimpleDoubleProperty();
  }
  
  private void handleModification(MouseEvent evt) {
    if (evt.getClickCount() == 2) {
      if (modifyHandler != null) {
        ObservableRequirement obsReq = table.getSelectionModel().getSelectedItem();
        if (obsReq != null) {
          modifyHandler.handle(CUDEvent.generateModificationEvent(new ActionEvent(evt.getSource(), evt.getTarget()), TargetEntity.REQUIREMENT, obsReq));
        }
      }
    }
  }
  
  private TableView<ObservableRequirement> initTable() {
    table = new TableView<>();
    table.setEditable(false);
    
    TableColumn<ObservableRequirement, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(c -> c.getValue().nameProperty()); // For unknown reason new PropertyValueFactory is not working.
    nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    
    TableColumn<ObservableRequirement, Number> pointsColumn = new TableColumn<>("Points");
    pointsColumn.setCellValueFactory(c -> c.getValue().pointsProperty());
    pointsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
      
      @Override
      public String toString(Number object) {
        return StringUtils.prettyPrint(object.doubleValue());
      }
      
      @Override
      public Number fromString(String string) {
        return Double.valueOf(string);
      }
    }));
    
    TableColumn<ObservableRequirement, String> minMSColumn = new TableColumn<>("Available from");
    minMSColumn.setCellValueFactory(c -> c.getValue().minMSNameProperty());
    minMSColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    
    TableColumn<ObservableRequirement, String> maxMSColumn = new TableColumn<>("Available until");
    maxMSColumn.setCellValueFactory(c -> c.getValue().maxMSNameProperty());
    maxMSColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    
    TableColumn<ObservableRequirement, String> typeColumn = new TableColumn<>("Type");
    typeColumn.setCellValueFactory(c -> c.getValue().typeProperty());
    typeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
  
    TableColumn<ObservableRequirement, String> categoryColumn = new TableColumn<>("Category");
    typeColumn.setCellValueFactory(c -> c.getValue().categoryProperty());
    typeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    
    
    table.getColumns().addAll(nameColumn, pointsColumn, minMSColumn, maxMSColumn, typeColumn, categoryColumn);
    
    table.setItems(tableData);
    
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
    // DEBUG
    
    
    return table;
  }
  
  /**
   * An observable representation of a {@link Requirement}, used to display
   * a requirement in a JavaFX {@link TableView}
   */
  static class ObservableRequirement {
    private final SimpleStringProperty name;
    private final SimpleDoubleProperty points;
    
    private final SimpleStringProperty type;
    private final SimpleStringProperty category;
    
    private final SimpleStringProperty minMSName;
    private final SimpleStringProperty maxMSName;
    
    
    private Requirement requirement;
    
    ObservableRequirement(Requirement req) {
      requirement = req;
      name = new SimpleStringProperty(req.getName());
      points = new SimpleDoubleProperty(req.getMaxPoints());
      
      type = new SimpleStringProperty(req.getType().toString());
      
      category = new SimpleStringProperty(req.getCategory() != null ? req.getCategory() : "");
      
      minMSName = new SimpleStringProperty(EntityController.getInstance().getCatalogueAnalyser().getMilestoneById(req.getMinimalMilestoneUUID()).getName());
      maxMSName = new SimpleStringProperty(EntityController.getInstance().getCatalogueAnalyser().getMilestoneById(req.getMaximalMilestoneUUID()).getName());
    }
    
    
    static ObservableRequirement fromRequirement(Requirement r) {
      LOGGER.trace(":fromRequirement");
      if (r == null) {
        throw new IllegalArgumentException("Cannot create ObservableRequirement from null-Requirement");
      }
      Milestone miMS = EntityController.getInstance().getCatalogueAnalyser().getMilestoneById(r.getMinimalMilestoneUUID());
      Milestone maMS = EntityController.getInstance().getCatalogueAnalyser().getMilestoneById(r.getMaximalMilestoneUUID());
      if (miMS == null || maMS == null) {
        throw new IllegalArgumentException("Passed Requirement is illegal formed (has null entries): " + r.toString());
      }
      ObservableRequirement rep = new ObservableRequirement(r);
      LOGGER.trace(":fromRequirement - Created " + String.format("the representation: %s", rep));
      return rep;
    }
    
    @Override
    public String toString() {
      // TODO Rewrite
      final StringBuffer sb = new StringBuffer("ObservableRequirement{");
      sb.append("name=").append(name.get());
      sb.append(", points=").append(points.get());
      sb.append('}');
      return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      
      ObservableRequirement that = (ObservableRequirement) o;
      
      return getRequirement().equals(that.getRequirement());
    }
    
    @Override
    public int hashCode() {
      // TODO regen
      int result = getName() != null ? getName().hashCode() : 0;
      result = 31 * result + (int) getPoints();
      return result;
    }
    
    public Requirement getRequirement() {
      return requirement;
    }
    
    double getPoints() {
      return points.get();
    }
    
    void setPoints(double points) {
      this.points.set(points);
    }
    
    String getName() {
      return name.get();
    }
    
    void setName(String name) {
      this.name.set(name);
    }
    
    String getMinMSName() {
      return minMSName.getValue();
    }
    
    void setMinMSName(String minMSName) {
      this.minMSName.set(minMSName);
    }
    
    String getMaxMSName() {
      return maxMSName.get();
    }
    
    void setMaxMSName(String maxMSName) {
      this.maxMSName.set(maxMSName);
    }
    
    DoubleProperty pointsProperty() {
      return points;
    }
    
    StringProperty nameProperty() {
      return name;
    }
    
    StringProperty typeProperty() {
      return type;
    }
    
    StringProperty categoryProperty() {
      return category;
    }
    
    StringProperty minMSNameProperty() {
      return minMSName;
    }
    
    StringProperty maxMSNameProperty() {
      return maxMSName;
    }
  }
}
