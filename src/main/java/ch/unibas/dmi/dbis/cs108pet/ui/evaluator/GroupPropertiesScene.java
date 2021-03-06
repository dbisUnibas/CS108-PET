package ch.unibas.dmi.dbis.cs108pet.ui.evaluator;

import ch.unibas.dmi.dbis.cs108pet.control.EntityController;
import ch.unibas.dmi.dbis.cs108pet.data.Group;
import ch.unibas.dmi.dbis.cs108pet.data.Member;
import ch.unibas.dmi.dbis.cs108pet.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.cs108pet.ui.common.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupPropertiesScene extends AbstractVisualCreator<Group> {
  
  private TextField tfName;
  private TextField tfProjectName;
  private TextField tfExportFileName;
  private TableView<ObservableMember> table;
  private Group group = null;
  private ObservableList<ObservableMember> tableData;
  
  GroupPropertiesScene() {
    populateScene();
  }
  
  GroupPropertiesScene(Group group) {
    this();
    this.group = group;
    loadGroup();
  }
  
  @Override
  public String getPromptTitle() {
    return "Group Properties";
  }
  
  @Override
  public void handleSaving(ActionEvent event) {
    String name = tfName.getText();
    String projectName = tfProjectName.getText();
    
    if (StringUtils.isNotEmpty(name)) {
      boolean empty = false;
      if (tableData.size() == 1) {
        if (tableData.get(0).isEmpty()) {
          empty = true;
        }
      }
      
      List<Member> members = tableData.stream().map(ObservableMember::getMember).collect(Collectors.toList());
      
      //TEMPORARY:
      empty = false;
      
      if (empty) {
        Utils.showWarningDialog("Invalid Members", "The group has too few members, at least one is needed!");
        return;
      }
      
      if (group == null) {
        group = EntityController.getInstance().createGroup(name, members.toArray(new Member[0]));
      } else {
        group.setName(name);
        if (!members.isEmpty()) {
          group.setMembers(members);
        }
      }
      
      if (StringUtils.isNotEmpty(tfExportFileName.getText())) {
        group.setExportFileName(tfExportFileName.getText());
      }
      if (StringUtils.isNotBlank(projectName)) {
        group.setProjectName(projectName);
      }
      dismiss();
    } else {
      String msg = "";
      if (!StringUtils.isNotEmpty(name)) {
        msg += "Group name is missing.\n";
      }
      Utils.showWarningDialog("Mandatory field(s) missing", msg);
      return;
    }
  }
  
  @Override
  public Group create() throws IllegalStateException {
    if (!isCreatorReady()) {
      throw new IllegalStateException("Cannot create Group, creator not ready");
    }
    return group;
  }
  
  @Override
  public boolean isCreatorReady() {
    return group != null;
  }
  
  @Override
  protected void populateScene() {
    initComponents();
    
    loadGroup();
  }
  
  private void loadGroup() {
    if (group != null) {
      tfName.setText(group.getName());
      tfProjectName.setText(group.getProjectName());
      tfExportFileName.setText(group.getExportFileName());
    }
    
    loadMembers();
  }
  
  private void initComponents() {
    Label lblName = new Label("Group Name*");
    Label lblProjectName = new Label("Project Name");
    Label lblExportFileName = new Label("Export File Name");
    Label lblMembers = new Label("Members");
    
    table = createTableView();
    
    tfName = new TextField();
    tfProjectName = new TextField();
    tfExportFileName = new TextField();
    
    int rowIndex = 0;
    
    grid.add(lblName, 0, rowIndex);
    grid.add(tfName, 1, rowIndex++);
    
    grid.add(lblProjectName, 0, rowIndex);
    grid.add(tfProjectName, 1, rowIndex++);
    
    grid.add(lblExportFileName, 0, rowIndex);
    grid.add(tfExportFileName, 1, rowIndex++);
    
    grid.add(lblMembers, 0, rowIndex);
    grid.add(table, 1, rowIndex, 1, 2);
    rowIndex += 2;
    
    
    grid.add(buttons, 0, ++rowIndex, 2, 1);
    
    setRoot(grid);
  }
  
  private TableView<ObservableMember> createTableView() {
    TableView<ObservableMember> table = new TableView<>();
    table.setEditable(true);
    
    Callback<TableColumn<ObservableMember, String>, TableCell<ObservableMember, String>> cellFactory = (TableColumn<ObservableMember, String> c) -> new UpdatingCell();
    
    TableColumn<ObservableMember, String> firstCol = new TableColumn<>("Name");
    firstCol.setCellValueFactory(
        new PropertyValueFactory<>("name")
    );
    firstCol.setCellFactory(cellFactory);
    firstCol.setOnEditCommit((TableColumn.CellEditEvent<ObservableMember, String> t) -> {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).setName(t.getNewValue());
    });
    TableColumn<ObservableMember, String> secondCol = new TableColumn<>("First Name");
    secondCol.setCellValueFactory(
        new PropertyValueFactory<>("firstName")
    );
    secondCol.setCellFactory(cellFactory);
    secondCol.setOnEditCommit((TableColumn.CellEditEvent<ObservableMember, String> t) -> {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).setFirstName(t.getNewValue());
    });
    TableColumn<ObservableMember, String> thirdCol = new TableColumn<>("Email");
    thirdCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    thirdCol.setCellFactory(cellFactory);
    thirdCol.setOnEditCommit((TableColumn.CellEditEvent<ObservableMember, String> t) -> {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).setEmail(t.getNewValue());
    });
    
    
    table.getColumns().addAll(firstCol, secondCol, thirdCol);
    
    // ContextMenu
    ContextMenu cm = new ContextMenu();
    MenuItem addMember = new MenuItem("Add Row");
    addMember.setOnAction(this::handleAddMember);
    MenuItem rmMember = new MenuItem("Remove current row");
    rmMember.setOnAction(this::handleRemoveMember);
    cm.getItems().addAll(addMember, rmMember);
    
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
    table.setOnMouseClicked(event -> {
      if (MouseButton.SECONDARY.equals(event.getButton())) {
        cm.show(table, event.getScreenX(), event.getScreenY());
      }
    });
    table.setItems(tableData);
    return table;
  }
  
  private void handleAddMember(ActionEvent event) {
    Member member = EvaluatorPromptFactory.promptMember();
    if (member != null) {
      // Check if the list contains only the empty one. If so replace empty one with new one.
      if (isMemberListOnlyEmpty()) {
        tableData.remove(0);
      }
      tableData.add(ObservableMember.fromMember(member));
    }
  }
  
  private void loadMembers() {
    if (group != null) {
      tableData.clear();
      tableData.addAll(Arrays.stream(group.getMembers()).map(ObservableMember::fromMember).collect(Collectors.toList()));
    } else {
      setMemberListOnlyEmpty();
    }
    table.setItems(tableData);
  }
  
  private boolean isMemberListOnlyEmpty() {
    if (tableData.size() > 1) {
      return false;
    }
    ObservableMember first = tableData.get(0);
    return first.isEmpty();
  }
  
  private void handleRemoveMember(ActionEvent event) {
    int index = table.getSelectionModel().getSelectedIndex();
    ObservableMember item = table.getSelectionModel().getSelectedItem();
    if (item != null) {
      tableData.remove(index);
    }
    if (tableData.isEmpty()) {
      setMemberListOnlyEmpty();
    }
  }
  
  private void setMemberListOnlyEmpty() {
    tableData = FXCollections.observableArrayList(ObservableMember.empty());
  }
  
  public static class ObservableMember {
    
    
    private static final String DELIMETER = ",";
    private final SimpleStringProperty name, firstName, email;
    
    private final Member member;
    
    public ObservableMember(Member member) {
      if (member == null) {
        this.member = null;
        name = new SimpleStringProperty();
        firstName = new SimpleStringProperty();
        email = new SimpleStringProperty();
      } else {
        this.member = member;
        this.name = new SimpleStringProperty(member.getName());
        this.firstName = new SimpleStringProperty(member.getFirstName());
        this.email = new SimpleStringProperty(member.getEmail());
      }
      
    }
    
    public static String convertToString(ObservableMember m) {
      StringBuilder sb = new StringBuilder();
      sb.append(m.getName());
      sb.append(DELIMETER);
      sb.append(m.getFirstName());
      if (StringUtils.isNotEmpty(m.getEmail())) {
        sb.append(DELIMETER);
        sb.append(m.getEmail());
      }
      return sb.toString();
    }
    
    public static ObservableMember fromMember(Member member) {
      return new ObservableMember(member);
    }
    
    public static ObservableMember empty() {
      return new ObservableMember(null);
    }
    
    public String getName() {
      return name.getValue();
    }
    
    public void setName(String name) {
      this.name.setValue(name);
    }
    
    public String getFirstName() {
      return firstName.getValue();
    }
    
    public void setFirstName(String firstName) {
      this.firstName.setValue(firstName);
    }
    
    public String getEmail() {
      return email.getValue();
    }
    
    public void setEmail(String email) {
      this.email.setValue(email);
    }
    
    public Member getMember() {
      return member;
    }
    
    public SimpleStringProperty nameProperty() {
      return name;
    }
    
    public SimpleStringProperty firstNameProperty() {
      return firstName;
    }
    
    public SimpleStringProperty emailProperty() {
      return email;
    }
    
    public boolean isEmpty() {
      return member == null;
    }
  }
  
  private static class UpdatingCell extends TableCell<ObservableMember, String> {
    private TextField textField;
    
    public UpdatingCell() {
    
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
      if (isEmpty()) {
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
    
    public String getString() {
      return getItem() == null ? "" : getItem();
    }
    
    private void createTextField() {
      textField = new TextField(getString());
      textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
      textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) -> {
        if (!newValue) {
          commitEdit(textField.getText());
        }
      });
    }
  }
  
  
}
