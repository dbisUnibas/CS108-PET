package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
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
class RequirementTableView extends BorderPane {

    private static final Logger LOGGER = LogManager.getLogger(RequirementTableView.class);

    private Label title;
    private HBox header;

    private Button addBtn;
    private Button rmBtn;

    private TableView<RequirementTableRepresentation> table;
    private ObservableList<RequirementTableRepresentation> tableData = FXCollections.observableArrayList();


    RequirementTableView(){
        initComponents();
        layoutComponents();
    }

    public void setOnAdd(EventHandler<ActionEvent> handler){
        addBtn.setOnAction(handler);
    }

    public void setOnRemove(EventHandler<ActionEvent> handler){
        rmBtn.setOnAction(handler);
    }

    public void setRequirements(ObservableList<Requirement> requirements){
        LOGGER.trace(":setRequirements");
        requirements.forEach(r -> tableData.add(RequirementTableRepresentation.fromRequirement(r)));

        requirements.addListener(new ListChangeListener<Requirement>() {
            @Override
            public void onChanged(Change<? extends Requirement> c) {
                LOGGER.trace(":changed");
                while (c.next()) {
                    if (c.wasPermutated()) {
                        // Permutation
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {

                        }
                    } else if (c.wasUpdated()) {
                        // Update

                    } else {
                        for (Requirement removeItem : c.getRemoved()) {
                            tableData.remove(RequirementTableRepresentation.fromRequirement(removeItem));
                        }
                        for (Requirement addItem : c.getAddedSubList()) {
                            tableData.add(RequirementTableRepresentation.fromRequirement(addItem));
                        }
                    }

                }
            }
        });
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

        header.getChildren().addAll(title,spacer, btnWrapper);


        // Adding header
        setTop(header);

        // Table already layed out

        // Adding table
        setCenter(table);

    }

    private void initComponents(){
        title = new Label("Requirements");
        header = new HBox();
        addBtn = Utils.createPlusButton();
        rmBtn = Utils.createMinusButton();

        table = initTable();
    }

    private TableView<RequirementTableRepresentation> initTable() {
        table = new TableView<>();
        table.setEditable(false);

        TableColumn<RequirementTableRepresentation, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(c -> c.getValue().nameProperty() ); // For unknown reason new PropertyValueFactory is not working.
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<RequirementTableRepresentation, Number> pointsColumn = new TableColumn<>("Points");
        pointsColumn.setCellValueFactory(c -> c.getValue().pointsProperty() );
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

        TableColumn<RequirementTableRepresentation, Boolean> binaryColumn = new TableColumn<>("Binary");
        binaryColumn.setCellValueFactory(c -> c.getValue().binaryProperty() );
        binaryColumn.setCellFactory(CheckBoxTableCell.forTableColumn(binaryColumn));

        TableColumn<RequirementTableRepresentation, Boolean> mandatoryColumn = new TableColumn<>("Mandatory");
        mandatoryColumn.setCellValueFactory(c -> c.getValue().mandatoryProperty() );
        mandatoryColumn.setCellFactory(CheckBoxTableCell.forTableColumn(mandatoryColumn));

        TableColumn<RequirementTableRepresentation, Boolean> malusColumn = new TableColumn<>("Malus");
        malusColumn.setCellValueFactory(c -> c.getValue().malusProperty() );
        malusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(malusColumn));

        table.getColumns().addAll(nameColumn, pointsColumn, binaryColumn, mandatoryColumn, malusColumn);

        table.setItems(tableData);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    static class RequirementTableRepresentation{
        private final SimpleStringProperty name;
        private final SimpleDoubleProperty points;
        private final SimpleBooleanProperty binary;
        private final SimpleBooleanProperty mandatory;
        private final SimpleBooleanProperty malus;

        RequirementTableRepresentation(String name, double points, boolean binary, boolean mandatory, boolean malus){
            this.name = new SimpleStringProperty(name);
            this.points = new SimpleDoubleProperty(points);
            this.binary = new SimpleBooleanProperty(binary);
            this.mandatory = new SimpleBooleanProperty(mandatory);
            this.malus = new SimpleBooleanProperty(malus);
        }

        void setPoints(double points){
            this.points.set(points);
        }
        void setName(String name){
            this.name.set(name);
        }
        void setBinary(boolean binary){
            this.binary.set(binary);
        }
        void setMandatory(boolean mandatory){
            this.mandatory.set(mandatory);
        }
        void setMalus(boolean malus){
            this.malus.set(malus);
        }

        double getPoints(){
            return points.get();
        }
        String getName(){
            return name.get();
        }
        boolean isBinary(){
            return binary.get();
        }
        boolean isMandatory(){
            return mandatory.get();
        }
        boolean isMalus(){
            return malus.get();
        }

        DoubleProperty pointsProperty(){
            return points;
        }

        StringProperty nameProperty(){
            return name;
        }

        BooleanProperty binaryProperty(){
            return binary;
        }

        BooleanProperty mandatoryProperty(){
            return mandatory;
        }

        BooleanProperty malusProperty(){
            return malus;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("RequirementTableRepresentation{");
            sb.append("name=").append(name.get());
            sb.append(", points=").append(points.get());
            sb.append(", binary=").append(binary.get());
            sb.append(", mandatory=").append(mandatory.get());
            sb.append(", malus=").append(malus.get());
            sb.append('}');
            return sb.toString();
        }

        static RequirementTableRepresentation fromRequirement(Requirement r){
            LOGGER.trace(":fromRequirement");
            RequirementTableRepresentation rep = new RequirementTableRepresentation(r.getName(), r.getMaxPoints(), r.isBinary(), r.isMandatory(), r.isMalus());
            LOGGER.trace(":fromRequirement - Created "+String.format("the representation: %s", rep));
            return rep;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RequirementTableRepresentation that = (RequirementTableRepresentation) o;

            return getName().equals(that.getName());
        }

        @Override
        public int hashCode() {
            int result = getName() != null ? getName().hashCode() : 0;
            result = 31 * result + (int)getPoints();
            result = 31 * result + (isBinary() ? 1 : 0);
            result = 31 * result + (isMandatory() ? 1 : 0);
            result = 31 * result + (isMalus() ? 1 : 0);
            return result;
        }
    }
}
