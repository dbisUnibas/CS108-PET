package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
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
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupPropertiesScene extends AbstractVisualCreator<ch.unibas.dmi.dbis.reqman.core.Group> {

    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(GroupPropertiesScene.class);
    private final String catalogueName;
    private final EvaluatorController controller;
    private TextField tfName;
    private TextField tfProjectName;
    private TextField tfExportFileName;
    private TableView<Member> table;
    private ch.unibas.dmi.dbis.reqman.core.Group group = null;
    private ObservableList<Member> tableData;

    public GroupPropertiesScene(EvaluatorController controller) {
        this.controller = controller;
        this.catalogueName = controller.getActiveCatalogue().getName();

        populateScene();
    }

    public GroupPropertiesScene(EvaluatorController controller, Group group) {
        this(controller);
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
            if (!controller.isGroupNameUnique(name)) {
                Utils.showWarningDialog("Invalid group name", "Group names must be unique. There is already another group with name: \n\n" + name);
                return;
            }

            group = new Group(name, projectName, memberToStringList(tableData), catalogueName);
            if (StringUtils.isNotEmpty(tfExportFileName.getText())) {
                group.setExportFileName(tfExportFileName.getText());
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
    public ch.unibas.dmi.dbis.reqman.core.Group create() throws IllegalStateException {
        if (!isCreatorReady()) {
            throw new IllegalStateException("Cannot create Group, creator not ready");
        }
        return group;
    }

    @Override
    public boolean isCreatorReady() {
        return group != null;
    }

    private void loadGroup() {
        if (group != null) {
            tfName.setText(group.getName());
            tfProjectName.setText(group.getProjectName());
            tfExportFileName.setText(group.getExportFileName());
        }

        loadMembers();
    }

    private List<String> memberToStringList(List<Member> members) {
        ArrayList<String> out = new ArrayList<>();
        members.forEach(
                m -> {
                    out.add(m.convertToString());
                }
        );
        return out;
    }

    private List<Member> stringToMemberList(List<String> members) {
        ArrayList<Member> out = new ArrayList<>();
        members.forEach(m -> {
            out.add(Member.convertFromString(m));
        });
        return out;
    }

    private void initComponents() {
        Label lblName = new Label("Group Name*");
        Label lblProjectName = new Label("Project Name");
        Label lblExportFileName = new Label("Export file name");
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

    private TableView<Member> createTableView() {
        TableView<Member> table = new TableView<>();
        table.setEditable(true);

        Callback<TableColumn<Member, String>, TableCell<Member, String>> cellFactory = (TableColumn<Member, String> c) -> new UpdatingCell();

        TableColumn<Member, String> firstCol = new TableColumn<>("First name");
        firstCol.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        firstCol.setCellFactory(cellFactory);
        firstCol.setOnEditCommit((TableColumn.CellEditEvent<Member, String> t) -> {
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setName(t.getNewValue());
        });
        TableColumn<Member, String> secondCol = new TableColumn<>("Surname");
        secondCol.setCellValueFactory(
                new PropertyValueFactory<>("surname")
        );
        secondCol.setCellFactory(cellFactory);
        secondCol.setOnEditCommit((TableColumn.CellEditEvent<Member, String> t) -> {
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setSurname(t.getNewValue());
        });
        TableColumn<Member, String> thirdCol = new TableColumn<>("Email");
        thirdCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        thirdCol.setCellFactory(cellFactory);
        thirdCol.setOnEditCommit((TableColumn.CellEditEvent<Member, String> t) -> {
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
            tableData.add(member);
        }
    }

    private void loadMembers() {
        if (group != null) {
            group.getMembers().forEach(str -> {
                Member member = Member.convertFromString(str);
                if (member != null) {
                    tableData.add(member);
                } else {
                    // TODO Handle illegal member-string!
                }
            });

        } else {
            setMemberListOnlyEmpty();
        }
        table.setItems(tableData);
    }

    private boolean isMemberListOnlyEmpty() {
        if (tableData.size() > 1) {
            return false;
        }
        Member first = tableData.get(0);
        return first.isEmpty();
    }

    private void handleRemoveMember(ActionEvent event) {
        int index = table.getSelectionModel().getSelectedIndex();
        Member item = table.getSelectionModel().getSelectedItem();
        if (item != null) {
            tableData.remove(index);
        }
        if (tableData.isEmpty()) {
            setMemberListOnlyEmpty();
        }
    }

    private void setMemberListOnlyEmpty() {
        tableData = FXCollections.observableArrayList(new Member("", "", ""));
    }

    @Override
    protected void populateScene() {
        initComponents();

        loadGroup();
    }

    public static class Member {
        private static final String DELIMETER = ",";
        private final SimpleStringProperty name, surname, email;

        public Member(String name, String surname, String email) {
            this.name = new SimpleStringProperty(name);
            this.surname = new SimpleStringProperty(surname);
            this.email = new SimpleStringProperty(email);
        }

        public static String convertToString(Member m) {
            StringBuilder sb = new StringBuilder();
            sb.append(m.getName());
            sb.append(DELIMETER);
            sb.append(m.getSurname());
            if (StringUtils.isNotEmpty(m.getEmail())) {
                sb.append(DELIMETER);
                sb.append(m.getEmail());
            }
            return sb.toString();
        }

        public static Member convertFromString(String m) {
            LOGGER.debug("Member:convertFormString - " + m);
            int firstDelim = m.indexOf(DELIMETER);
            LOGGER.debug("Member:convertFormString - First delim: " + firstDelim);
            if (firstDelim < 0) {
                throw new IllegalArgumentException("Member invalid: " + m);
            }
            String name = m.substring(0, firstDelim);
            LOGGER.debug("Member:convertFormString - Extracted name: " + name);
            int secondDelim = m.lastIndexOf(DELIMETER);
            LOGGER.debug("Member:convertFormString - Second delim: " + secondDelim);
            String surname = "";
            String email = "";
            if (secondDelim < 0 || secondDelim == firstDelim) {
                surname = m.substring(firstDelim + 1);
                LOGGER.debug("Member:convertFormString - Surname: " + surname);
            } else {
                surname = m.substring(firstDelim + 1, secondDelim);
                LOGGER.debug("Member:convertFormString - Surname: " + surname);
                email = m.substring(secondDelim + 1);
                LOGGER.debug("Member:convertFormString - Email: " + email);
            }

            return new Member(name, surname, email);
        }

        public String getName() {
            return name.getValue();
        }

        public void setName(String name) {
            this.name.setValue(name);
        }

        public String getSurname() {
            return surname.getValue();
        }

        public void setSurname(String surname) {
            this.surname.setValue(surname);
        }

        public String getEmail() {
            return email.getValue();
        }

        public void setEmail(String email) {
            this.email.setValue(email);
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public SimpleStringProperty surnameProperty() {
            return surname;
        }

        public SimpleStringProperty emailProperty() {
            return email;
        }

        public boolean isEmpty() {
            boolean emptyName = name.getValue().isEmpty();
            boolean emptySurname = surname.getValue().isEmpty();
            boolean emptyEmail = email.getValue().isEmpty();

            return emptyEmail && emptySurname && emptyName;
        }

        public String convertToString() {
            return convertToString(this);
        }
    }

    private static class UpdatingCell extends TableCell<Member, String> {
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
