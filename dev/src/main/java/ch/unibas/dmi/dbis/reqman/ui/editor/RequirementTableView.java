package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
class RequirementTableView extends BorderPane {

    private Label title;
    private HBox header;

    private Button addBtn;
    private Button rmBtn;

    private TableView<RequirementTableRepresentation> table;


    RequirementTableView(){
        initComponents();
        layoutComponents();
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
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<RequirementTableRepresentation, Double> pointsColumn = new TableColumn<>("Points");
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points") );

        TableColumn<RequirementTableRepresentation, Boolean> binaryColumn = new TableColumn<>("Binary");
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("binary") );

        TableColumn<RequirementTableRepresentation, Boolean> mandatoryColumn = new TableColumn<>("Mandatory");
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("mandatory") );

        TableColumn<RequirementTableRepresentation, Boolean> malusColumn = new TableColumn<>("Malus");
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("malus") );

        table.getColumns().addAll(nameColumn, pointsColumn, binaryColumn, mandatoryColumn, malusColumn);

        // TODO add table data

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

    }
}
