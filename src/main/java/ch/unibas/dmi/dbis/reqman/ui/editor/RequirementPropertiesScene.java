package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.reqman.ui.common.MandatoryFieldsMissingException;
import ch.unibas.dmi.dbis.reqman.ui.common.SaveCancelPane;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.reqman.ui.event.TargetEntity;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class RequirementPropertiesScene extends AbstractVisualCreator<Requirement> {
  
  private final static Logger LOGGER = LogManager.getLogger();
  
  private final static MetaKeyValuePair PLACEHOLDER = new MetaKeyValuePair("key", "value");
  private TextField tfName = new TextField();
  private TextField tfShort = new TextField();
  private TextArea taDesc = new TextArea();
  private TextField tfCategory = new TextField();
  private ComboBox<Milestone> cbMinMS = new ComboBox<>();
  private ComboBox<Milestone> cbMaxMS = new ComboBox<>();
  private Spinner spinnerPoints = new Spinner(0d, Double.MAX_VALUE, 0.0);
  private RadioButton rbRegular = new RadioButton("Regular");
  private CheckBox cbBinary = new CheckBox("Binary");
  private CheckBox cbDisabled = new CheckBox("Disabled");
  private RadioButton rbBonus = new RadioButton("Bonus");
  private RadioButton rbMalus = new RadioButton("Malus");
  private Requirement requirement = null;
  private ObservableList<MetaKeyValuePair> tableData;
  private TableView<MetaKeyValuePair> table = createPropertiesTable();
  private ObservableList<Requirement> predecessors = FXCollections.observableArrayList();
  private ObservableList<Milestone> milestoneList;
  
  private EditorHandler handler;
  
  public RequirementPropertiesScene(EditorHandler handler) {
    super();
    this.handler = handler;
    populateScene();
  }
  
  public RequirementPropertiesScene(EditorHandler handler, Requirement requirement) {
    this(handler);
    this.requirement = requirement;
    loadRequirement();
  }
  
  
  public void handleSaving(ActionEvent event) {
    LOGGER.debug("Saving requirement");
    String name = tfName.getText();
    String excerpt = tfShort.getText();
    String desc = taDesc.getText();
    Milestone min = cbMinMS.getValue();
    double maxPoints = (double) spinnerPoints.getValue();
    String cat = tfCategory.getText();
    
    if ((name == null || name.isEmpty()) || min == null) {
      throw MandatoryFieldsMissingException.createWithFormattedMessage("Mandatory fields for entity Requirement:\n\t1) Name\n\t2) Minimal Milestone");
    }
    
    Milestone max = cbMaxMS.getValue() == null ? min : cbMaxMS.getValue();
    
    if (!cbBinary.isSelected() && maxPoints == 0) {
      Utils.showErrorDialog("Invalid Configuration", "A non-binary requirement cannot have 0 points, as having \"0 of 0 points\" is a tautology.\n" +
          "May change your requirement to be binary or consider re-thinking the valuation.");
      return;
    }
    if (requirement == null) {
      if (rbMalus.isSelected()) {
        // Malus
        requirement = EntityController.getInstance().createMalusRequirement(name, excerpt, maxPoints, min, max, cbBinary.isSelected());
      } else if (rbBonus.isSelected()) {
        // Bonus
        requirement = EntityController.getInstance().createBonusRequirement(name, excerpt, maxPoints, min, max, cbBinary.isSelected());
      } else if (cbBinary.isSelected()) {
        // binary
        requirement = EntityController.getInstance().createBinaryRequirement(name, excerpt, maxPoints, min, max);
      } else {
        // regular
        requirement = EntityController.getInstance().createRequirement(name, excerpt, maxPoints, min, max);
      }
      
    } else {
      requirement.setName(name);
      requirement.setExcerpt(excerpt);
      requirement.setMaxPoints(maxPoints);
      requirement.setMinimalMilestoneUUID(min.getUuid());
      requirement.setMaximalMilestoneUUID(max.getUuid());
      
      if (rbRegular.isSelected()) {
        requirement.setType(Requirement.Type.REGULAR);
      } else if (rbBonus.isSelected()) {
        requirement.setType(Requirement.Type.BONUS);
      } else if (rbMalus.isSelected()) {
        requirement.setType(Requirement.Type.MALUS);
      }
      requirement.setBinary(cbBinary.isSelected());
    }
    // Late setting
    requirement.setDisabled(cbDisabled.isSelected());
    
    requirement.setDescription(desc);
    requirement.setCategory(cat);
    
    
    savePredecessors();
    
    saveProperties();
    
    getWindow().hide();
  }
  
  private void savePredecessors() {
    Set<UUID> reqPred = Arrays.stream(requirement.getPredecessors()).collect(Collectors.toSet());
    Set<UUID> pred = predecessors.stream().map(Requirement::getUuid).collect(Collectors.toSet());
    
    if (pred.equals(reqPred)) {
      LOGGER.debug("Predecessors unchanged");
    } else {
      requirement.setPredecessors(pred.toArray(new UUID[0]));
    }
  }
  
  @Override
  public Requirement create() throws IllegalStateException {
    if (!isCreatorReady()) {
      throw new IllegalStateException("Creation failed: Creator not ready");
    }
    return requirement;
  }
  
  @Override
  public boolean isCreatorReady() {
    return requirement != null;
  }
  
  @Override
  public String getPromptTitle() {
    return "Requirement Properties";
  }
  
  @Override
  protected void populateScene() {
    ScrollPane scrollPane = new ScrollPane();
    Label lblName = new Label("Name*");
    Label lblDesc = new Label("Description");
    Label lblShort = new Label("Excerpt*");
    Label lblMinMS = new Label("Minimal Milestone*");
    Label lblMaxMS = new Label("Maximal Milestone");
    Label lblMaxPoints = new Label("Maximal Points");
    Label lblType = new Label("Type");
    Label lblCategory = new Label("Category");
    Label lblPredecessors = new Label("Predecessors");
    Label lblProps = new Label("Meta Data");
    
    loadRequirement();
    loadProperties();
    loadMilestoneNames();
    
    HBox minMSBox = new HBox();
    minMSBox.setStyle("-fx-spacing: 10px;");
    Button newMinMS = new Button("New ...");
    newMinMS.setOnAction(this::handleNewMinMS);
    minMSBox.getChildren().addAll(cbMinMS, newMinMS);
    
    HBox maxMSBox = new HBox();
    maxMSBox.setStyle("-fx-spacing: 10px;");
    Button newMaxMS = new Button("New ...");
    newMaxMS.setOnAction(this::handleNewMaxMS);
    maxMSBox.getChildren().addAll(cbMaxMS, newMaxMS);
    cbMinMS.setCellFactory((ListView<Milestone> l) -> new MilestoneCell());
    cbMinMS.setOnAction(event -> {
      // Make so that the maxMS is set to the same value as this one. (initially as soon as this one is set)
      Milestone selected = cbMinMS.getSelectionModel().getSelectedItem();
      Milestone target = cbMaxMS.getSelectionModel().getSelectedItem();
      if (selected != null && target == null) {
        cbMaxMS.getSelectionModel().select(selected);
      }
    });
    cbMinMS.setButtonCell(new MilestoneCell());
    cbMaxMS.setCellFactory((ListView<Milestone> lv) -> new MilestoneCell());
    cbMaxMS.setButtonCell(new MilestoneCell());
    
    cbMinMS.setItems(milestoneList);
    cbMaxMS.setItems(milestoneList); // NOTE: This is intentionally the same list
    
    
    spinnerPoints.setEditable(true);
    // Solution by: http://stackoverflow.com/a/39380146
    spinnerPoints.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        spinnerPoints.increment(0);
      }
    });
    
    BorderPane inputPredecessors = createPredecessorChoice();
    inputPredecessors.setPrefSize(300, 300);
    
    SaveCancelPane buttonWrapper = new SaveCancelPane();
    
    buttonWrapper.setOnSave(this::handleSaving);
    
    buttonWrapper.setOnCancel(event -> getWindow().hide());
    
    HBox typeGroup = new HBox();
    typeGroup.setStyle("-fx-spacing: 10px");
    ToggleGroup typeButtons = new ToggleGroup();
    rbRegular.setToggleGroup(typeButtons);
    rbRegular.setTooltip(new Tooltip("A regular requirement."));
    rbBonus.setToggleGroup(typeButtons);
    rbBonus.setTooltip(new Tooltip("A bonus requirement's points do not count to the sum."));
    rbBonus.selectedProperty().addListener((observable, oldValue, newValue) -> cbBinary.setSelected(newValue));
    rbMalus.setToggleGroup(typeButtons);
    rbMalus.setTooltip(new Tooltip("A malus requirement's point are handled negative. Thus this requirement should not be fulfilled."));
    rbMalus.selectedProperty().addListener((observable, oldValue, newValue) -> cbBinary.setSelected(newValue));
    rbRegular.setSelected(true);
    
    cbBinary.setTooltip(new Tooltip("If this requirement cannot be partially fulfilled (selected) or might be partially fulfilled (unselected)."));
    cbDisabled.setTooltip(new Tooltip("If this requirement should be disabled and, thus, not count to the score"));
    Label binaryInfo = new Label("(Bonus and Malus defaults to binary being selected.)");
    Font old = binaryInfo.getFont();
    binaryInfo.setFont(Font.font(old.getFamily(), FontPosture.ITALIC, old.getSize()));
    
    typeGroup.getChildren().addAll(rbRegular, rbBonus, rbMalus, cbBinary, binaryInfo,cbDisabled);
    GridPane.setHgrow(typeGroup, Priority.ALWAYS);
    
    
    int leftColsRow = 0;
    int rightColsRow = 0;
    // First pair of columns
    
    grid.add(lblName, 0, leftColsRow);
    grid.add(tfName, 1, leftColsRow++);
    
    grid.add(lblShort, 0, leftColsRow);
    grid.add(tfShort, 1, leftColsRow++);
    
    grid.add(lblDesc, 0, leftColsRow);
    grid.add(taDesc, 1, leftColsRow, 1, 2);
    taDesc.setPrefSize(300, 200); // in relation to other pref size settings
    leftColsRow += 2; // Skip two rows
    
    grid.add(lblMaxPoints, 0, leftColsRow);
    grid.add(spinnerPoints, 1, leftColsRow++);
    
    grid.add(lblMinMS, 0, leftColsRow);
    grid.add(minMSBox, 1, leftColsRow++);
    
    grid.add(lblMaxMS, 0, leftColsRow);
    grid.add(maxMSBox, 1, leftColsRow++);
    
    grid.add(lblCategory, 0, leftColsRow);
    grid.add(tfCategory, 1, leftColsRow++);
    leftColsRow++;
    
    GridPane.setValignment(lblMaxMS, VPos.TOP);
    lblMaxMS.setPadding(new Insets(5, 0, 0, 0));// makes it appear like the others
    
    // second pair of columns: one column gap
    // Predecessor list
    grid.add(lblPredecessors, 3, rightColsRow);
    grid.add(inputPredecessors, 4, rightColsRow, 1, 4);
    rightColsRow += 4;
    
    grid.add(lblProps, 3, rightColsRow);
    grid.add(table, 4, rightColsRow, 1, 5);
    rightColsRow += 5;
    
    
    // Sets the pref size of the table - this is rather an experimental value, but it smallers the size of the grid.
    table.setPrefSize(300, 300);
    
    // separator
    grid.add(new Separator(), 0, leftColsRow++, 5, 1);
    // RadioButton group
    GridPane.setHgrow(typeGroup, Priority.ALWAYS);
    GridPane.setFillWidth(typeGroup, true);
    grid.add(lblType, 0, leftColsRow);
    grid.add(typeGroup, 1, leftColsRow++, 5, 1);
    
    // Separator
    grid.add(new Separator(), 0, leftColsRow++, 5, 1);
    
    
    // Buttons, last row
    grid.add(buttonWrapper, 1, leftColsRow, 5, 1);
    
    
    scrollPane.setContent(grid);
    setRoot(scrollPane);
    
    grid.setPrefHeight(700); // Hacky solution, due to strangely incresed height.
    grid.setAlignment(Pos.CENTER);
  }
  
  private void loadRequirement() {
    if (requirement != null) {
      tfName.setText(requirement.getName());
      tfShort.setText(requirement.getExcerpt());
      tfCategory.setText(requirement.getCategory());
      taDesc.setText(requirement.getDescription());
      Milestone min = EntityController.getInstance().getCatalogueAnalyser().getMilestoneById(requirement.getMinimalMilestoneUUID());
      cbMinMS.getSelectionModel().select(min);
      Milestone max = EntityController.getInstance().getCatalogueAnalyser().getMilestoneById(requirement.getMaximalMilestoneUUID());
      cbMaxMS.getSelectionModel().select(max);
      spinnerPoints.getValueFactory().setValue(requirement.getMaxPoints());
      
      switch (requirement.getType()) {
        case REGULAR:
          rbRegular.setSelected(true);
          break;
        case BONUS:
          rbBonus.setSelected(true);
          break;
        case MALUS:
          rbMalus.setSelected(true);
          break;
      }
      cbBinary.setSelected(requirement.isBinary());
      cbDisabled.setSelected(requirement.isDisabled());
      loadPredecessors();
      loadProperties();
    }
  }
  
  private void setMetaListOnlyEmpty() {
    tableData = FXCollections.observableArrayList(new MetaKeyValuePair("", ""));
  }
  
  private void loadProperties() {
    if (requirement != null) {
      if (requirement.getPropertiesMap().isEmpty()) {
        setMetaListOnlyEmpty();
      } else {
        tableData = convertFromMap(requirement.getPropertiesMap());
      }
      
    } else {
      setMetaListOnlyEmpty();
    }
    table.setItems(tableData);
  }
  
  private void saveProperties() {
    LOGGER.debug("Save Requirement properties");
    LOGGER.debug("Req.props.size={}, ui.prop.size={}", requirement.getPropertiesMap().size(), tableData.size());
    LOGGER.debug("Requirement porps: {}", requirement.getPropertiesMap());
    LOGGER.debug("UI props: {}", tableData);
    if (!isMetaListOnlyEmpty()) {
      requirement.setPropertiesMap(convertFromMetaKeyValuePairList(tableData));
    }
  }
  
  private ObservableList<MetaKeyValuePair> convertFromMap(Map<String, String> props) {
    ObservableList<MetaKeyValuePair> list = FXCollections.observableArrayList();
    
    if (!props.isEmpty()) {
      props.forEach((key, value) -> {
        list.add(new MetaKeyValuePair(key, value));
      });
    }
    
    return list;
  }
  
  private Map<String, String> convertFromMetaKeyValuePairList(List<MetaKeyValuePair> list) {
    Map<String, String> map = new HashMap<>();
    
    if (!list.isEmpty()) {
      list.forEach(item -> map.put(item.getKey(), item.getValue()));
    }
    
    return map;
  }
  
  
  private void loadPredecessors() {
    predecessors.addAll(EntityController.getInstance().getCatalogueAnalyser().getPredecessors(requirement));
  }
  
  private BorderPane createPredecessorChoice() {
    BorderPane pane = new BorderPane();
    HBox upper = new HBox();
    Button addPred = Utils.createPlusButton();
    Button rmPred = Utils.createMinusButton();
    
    ListView<Requirement> predList = new ListView<>();
    predList.setCellFactory((ListView<Requirement> l) -> new RequirementCell());
    predList.setItems(predecessors);
    ComboBox<Requirement> reqBox = new ComboBox<>();
    reqBox.setButtonCell(new RequirementCell());
    reqBox.setCellFactory((ListView<Requirement> l) -> new RequirementCell());
    reqBox.setItems(EntityController.getInstance().getObservableRequirements());
    
    upper.getChildren().addAll(reqBox, addPred, rmPred);
    
    upper.setStyle("-fx-spacing: 10px; -fx-padding: 10px;");
    pane.setStyle("-fx-spacing: 10px; -fx-padding: 10px;");
    pane.setTop(upper);
    pane.setCenter(predList);
    
    rmPred.setOnAction(event -> {
      int index = predList.getSelectionModel().getSelectedIndex();
      Requirement selected = predList.getSelectionModel().getSelectedItem();
      if (selected == null) {
        return; // Do not remove when nothing is selected
      } else {
        predecessors.remove(index);
      }
    });
    
    addPred.setOnAction(event -> {
      Requirement selected = reqBox.getSelectionModel().getSelectedItem();
      if (selected == null) {
        return;
      }
      if (!predecessors.contains(selected)) {
        predecessors.add(selected);
      }
    });
    
    pane.setStyle("-fx-border-width: 1; -fx-border-color: silver;");
    
    return pane;
  }
  
  private void handleNewMaxMS(ActionEvent event) {
    handler.handleCreation(CUDEvent.generateCreationEvent(event, TargetEntity.MILESTONE));
    cbMaxMS.getSelectionModel().select(milestoneList.size() - 1);
  }
  
  private void handleNewMinMS(ActionEvent event) {
    handler.handleCreation(CUDEvent.generateCreationEvent(event, TargetEntity.MILESTONE));
    cbMinMS.getSelectionModel().select(milestoneList.size() - 1);
  }
  
  private void loadMilestoneNames() {
    milestoneList = EntityController.getInstance().getObservableMilestones();
  }
  
  private TableView<MetaKeyValuePair> createPropertiesTable() {
    TableView<MetaKeyValuePair> table = new TableView<>();
    table.setEditable(true);
    
    Callback<TableColumn<MetaKeyValuePair, String>,
        TableCell<MetaKeyValuePair, String>> cellFactory
        = (TableColumn<MetaKeyValuePair, String> p) -> new EditingCell();
    
    TableColumn<MetaKeyValuePair, String> firstCol = new TableColumn<>("Key");
    firstCol.setCellValueFactory(
        new PropertyValueFactory<>("key")
    );
    firstCol.setCellFactory(cellFactory);
    firstCol.setOnEditCommit((TableColumn.CellEditEvent<MetaKeyValuePair, String> t) -> {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).setKey(t.getNewValue());
    });
    TableColumn<MetaKeyValuePair, String> secondCol = new TableColumn<>("Value");
    secondCol.setCellValueFactory(
        new PropertyValueFactory<>("value")
    );
    secondCol.setCellFactory(cellFactory);
    secondCol.setOnEditCommit((TableColumn.CellEditEvent<MetaKeyValuePair, String> t) -> {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue(t.getNewValue());
    });
    
    /* === Make each change update the value*/
    
    
    table.getColumns().addAll(firstCol, secondCol);
    
    // ContextMenu
    ContextMenu cm = new ContextMenu();
    MenuItem addMeta = new MenuItem("Add Row");
    addMeta.setOnAction(this::handleAddMetaRow);
    MenuItem rmMeta = new MenuItem("Remove current row");
    rmMeta.setOnAction(this::handleRemoveMetaRow);
    cm.getItems().addAll(addMeta, rmMeta);
    
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.setOnMouseClicked(event -> {
      if (MouseButton.SECONDARY.equals(event.getButton())) {
        cm.show(table, event.getScreenX(), event.getScreenY());
      }
    });
    table.setItems(tableData);
    return table;
  }
  
  private void handleAddMetaRow(ActionEvent event) {
    MetaKeyValuePair pair = EditorPromptFactory.promptMetaKeyValuePair();
    if (pair != null) {
      // Check if the list contains only the empty one. If so replace empty one with new one.
      if (isMetaListOnlyEmpty()) {
        tableData.remove(0);
      }
      tableData.add(pair);
    }
  }
  
  private boolean isMetaListOnlyEmpty() {
    if (tableData.size() > 1) {
      return false;
    }
    MetaKeyValuePair first = tableData.get(0);
    return first.isEmpty();
  }
  
  private void handleRemoveMetaRow(ActionEvent event) {
    int index = table.getSelectionModel().getSelectedIndex();
    MetaKeyValuePair item = table.getSelectionModel().getSelectedItem();
    if (item != null) {
      tableData.remove(index);
    }
    if (tableData.isEmpty()) {
      setMetaListOnlyEmpty();
    }
  }
  
  public static class MetaKeyValuePair {
    private final SimpleStringProperty key;
    private final SimpleStringProperty value;
    
    public MetaKeyValuePair(String key, String value) {
      this.key = new SimpleStringProperty(key);
      this.value = new SimpleStringProperty(value);
    }
    
    /**
     * Returns if this {@link MetaKeyValuePair} consits of an empty key AND empty value.
     *
     * @return TRUE if key and value are empty strings, FALSE otherwise
     */
    public boolean isEmpty() {
      return key.getValue().isEmpty() && value.getValue().isEmpty();
    }
    
    public String getKey() {
      return key.get();
    }
    
    public void setKey(String key) {
      this.key.set(key);
    }
    
    public SimpleStringProperty keyProperty() {
      return key;
    }
    
    public String getValue() {
      return value.get();
    }
    
    public void setValue(String value) {
      this.value.set(value);
    }
    
    public SimpleStringProperty valueProperty() {
      return value;
    }
  }
  
  /**
   * Solution by
   * https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/table-view.htm
   */
  public static class EditingCell extends TableCell<MetaKeyValuePair, String> {
    
    private TextField textField;
    
    public EditingCell() {
    }
    
    @Override
    public void startEdit() {
      if (!isEmpty()) {
        super.startEdit();
        createTextField();
        setText(null);
        setGraphic(textField);
        textField.selectAll();
      }
    }
    
    @Override
    public void cancelEdit() {
      super.cancelEdit();
      
      setText(getItem());
      setGraphic(null);
    }
    
    @Override
    public void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);
      
      if (empty) {
        setText(null);
        setGraphic(null);
      } else {
        if (isEditing()) {
          if (textField != null) {
            textField.setText(getString());
          }
          setText(null);
          setGraphic(textField);
        } else {
          setText(getString());
          setGraphic(null);
        }
      }
    }
    
    private void createTextField() {
      textField = new TextField(getString());
      textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
      textField.focusedProperty().addListener(
          (ObservableValue<? extends Boolean> arg0,
           Boolean arg1, Boolean arg2) -> {
            if (!arg2) {
              commitEdit(textField.getText());
            }
          });
    }
    
    private String getString() {
      return getItem() == null ? "" : getItem().toString();
    }
  }
  
}
