package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.reqman.ui.event.TargetEntity;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
    private TableView<ObservableRequirement> table;
    private ObservableList<ObservableRequirement> tableData = FXCollections.observableArrayList();
    private Catalogue cat;

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
        tableData.clear();
        requirements.forEach(r -> tableData.add(ObservableRequirement.fromRequirement(r, catalogue)));
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
                            tableData.remove(ObservableRequirement.fromRequirement(removeItem, catalogue));
                        }
                        for (Requirement addItem : c.getAddedSubList()) {
                            LOGGER.trace("Added  {}", c.getAddedSubList());
                            ObservableRequirement obsReq = ObservableRequirement.fromRequirement(addItem, catalogue);
                            LOGGER.debug("Table contains to add: {}", tableData.contains(obsReq));
                            if (!tableData.contains(obsReq)) {
                                tableData.add(obsReq);
                            }
                        }
                    }

                }
                c.reset();
            }
        });
    }

    public int getRequirementsSize() {
        return tableData.size();
    }

    public String getSelectedRequirement() {
        ObservableRequirement req = table.getSelectionModel().getSelectedItem();
        return req != null ? req.getName() : null;
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


        // Adding header
        setTop(header);

        // Table already layed out

        // Adding table
        setCenter(table);

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

        TableColumn<ObservableRequirement, String> minMSColumn = new TableColumn<>("Min MS");
        minMSColumn.setCellValueFactory(c -> c.getValue().minMSNameProperty() );
        minMSColumn.setCellFactory(TextFieldTableCell.forTableColumn() );

        TableColumn<ObservableRequirement, String> maxMSColumn = new TableColumn<>("Max MS");
        maxMSColumn.setCellValueFactory(c -> c.getValue().maxMSNameProperty() );
        maxMSColumn.setCellFactory(TextFieldTableCell.forTableColumn() );


        TableColumn<ObservableRequirement, Boolean> binaryColumn = new TableColumn<>("Binary");
        binaryColumn.setCellValueFactory(c -> c.getValue().binaryProperty());

        /*
        //Not Working
        binaryColumn.setCellFactory(new Callback<TableColumn<ObservableRequirement, Boolean>, TableCell<ObservableRequirement, Boolean>>() {
            @Override
            public TableCell<ObservableRequirement, Boolean> call(TableColumn<ObservableRequirement, Boolean> param) {
                Callback<TableColumn<ObservableRequirement, Boolean>, TableCell<ObservableRequirement, Boolean>> factory = CheckBoxTableCell.forTableColumn(param);
                TableCell<ObservableRequirement, Boolean> cell = factory.call(param);
                cell.getStyleClass().add("silent");
                return cell;
            }
        });
        */

        binaryColumn.setCellFactory(CheckBoxTableCell.forTableColumn(binaryColumn));

        TableColumn<ObservableRequirement, Boolean> mandatoryColumn = new TableColumn<>("Mandatory");
        mandatoryColumn.setCellValueFactory(c -> c.getValue().mandatoryProperty());
        mandatoryColumn.setCellFactory(CheckBoxTableCell.forTableColumn(mandatoryColumn));

        TableColumn<ObservableRequirement, Boolean> malusColumn = new TableColumn<>("Malus");
        malusColumn.setCellValueFactory(c -> c.getValue().malusProperty());
        malusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(malusColumn));

        table.getColumns().addAll(nameColumn, pointsColumn, minMSColumn, maxMSColumn, binaryColumn, mandatoryColumn, malusColumn);

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
        private final SimpleBooleanProperty binary;
        private final SimpleBooleanProperty mandatory;
        private final SimpleBooleanProperty malus;

        private final SimpleStringProperty minMSName;
        private final SimpleStringProperty maxMSName;

        ObservableRequirement(String name, double points, String minMSName, String maxMSName, boolean binary, boolean mandatory, boolean malus) {
            this.name = new SimpleStringProperty(name);
            this.points = new SimpleDoubleProperty(points);
            this.binary = new SimpleBooleanProperty(binary);
            this.mandatory = new SimpleBooleanProperty(mandatory);
            this.malus = new SimpleBooleanProperty(malus);
            this.minMSName = new SimpleStringProperty(minMSName);
            this.maxMSName = new SimpleStringProperty(maxMSName);
        }

        static ObservableRequirement fromRequirement(Requirement r, Catalogue catalogue) {
            LOGGER.trace(":fromRequirement");
            if (r == null) {
                throw new IllegalArgumentException("Cannot create ObservableRequirement from null-Requirement");
            }
            Milestone miMS = catalogue.getMilestoneByOrdinal(r.getMinMilestoneOrdinal());
            Milestone maMS = catalogue.getMilestoneByOrdinal(r.getMaxMilestoneOrdinal());
            if (miMS == null || maMS == null) {
                throw new IllegalArgumentException("Passed Requirement is illegal formed (has null entries): " + r.toString());
            }
            ObservableRequirement rep = new ObservableRequirement(r.getName(), r.getMaxPoints(), miMS.getName(), maMS.getName(), r.isBinary(), r.isMandatory(), r.isMalus());
            LOGGER.trace(":fromRequirement - Created " + String.format("the representation: %s", rep));
            return rep;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ObservableRequirement{");
            sb.append("name=").append(name.get());
            sb.append(", points=").append(points.get());
            sb.append(", binary=").append(binary.get());
            sb.append(", mandatory=").append(mandatory.get());
            sb.append(", malus=").append(malus.get());
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ObservableRequirement that = (ObservableRequirement) o;

            return getName().equals(that.getName());
        }

        @Override
        public int hashCode() {
            int result = getName() != null ? getName().hashCode() : 0;
            result = 31 * result + (int) getPoints();
            result = 31 * result + (isBinary() ? 1 : 0);
            result = 31 * result + (isMandatory() ? 1 : 0);
            result = 31 * result + (isMalus() ? 1 : 0);
            return result;
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

        boolean isBinary() {
            return binary.get();
        }

        void setBinary(boolean binary) {
            this.binary.set(binary);
        }

        boolean isMandatory() {
            return mandatory.get();
        }

        void setMandatory(boolean mandatory) {
            this.mandatory.set(mandatory);
        }

        boolean isMalus() {
            return malus.get();
        }

        void setMalus(boolean malus) {
            this.malus.set(malus);
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

        BooleanProperty binaryProperty() {
            return binary;
        }

        BooleanProperty mandatoryProperty() {
            return mandatory;
        }

        BooleanProperty malusProperty() {
            return malus;
        }

        StringProperty minMSNameProperty() {
            return minMSName;
        }

        StringProperty maxMSNameProperty() {
            return maxMSName;
        }
    }
}
