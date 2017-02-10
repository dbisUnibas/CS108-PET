package ch.unibas.dmi.dbis.reqman.ui;

import ch.unibas.dmi.dbis.reqman.ui.editor.CataloguePropertiesScene;
import ch.unibas.dmi.dbis.reqman.ui.common.SaveCancelPane;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * A sandbox
 */
public class GraphicalUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Requirement Manager");

        // Requirement Properties:
        //primaryStage.setScene(createRequirementPropertiesScene() );
        // Milestone Properties:
        //primaryStage.setScene(createMilestonePropertiesScene() );
        // Catalogue Primary Properties
        primaryStage.setScene(new CataloguePropertiesScene() );

        primaryStage.show();
    }

    private Scene createRequirementPropertiesScene() {
        GridPane grid = generateDefaultGridPane();
        Scene scene = new Scene(grid);

        Label lblName = new Label("Name");
        Label lblDesc = new Label("Description");
        Label lblMinMS = new Label("Minimal Milestone");
        Label lblMaxMS = new Label("Maximal Milestone");
        Label lblMaxPoints = new Label("Maximal Points");
        Label lblBinary = new Label("Binary");
        Label lblMandatory = new Label("Mandatory");
        Label lblMalus = new Label("Malus");
        Label lblPredecessors = new Label("Predecessors");
        Label lblProps = new Label("Properties");

        TextField tfName = new TextField();
        TextArea taDesc = new TextArea();

        ComboBox<String> cbMinMS = new ComboBox<>(); // May be ComboBox
        ObservableList<String> list = FXCollections.observableArrayList("Create new...", "Milestone1", "Milestone2");
        cbMinMS.setItems(list);
        ComboBox<String> cbMaxMS = new ComboBox<>();
        cbMaxMS.setItems(list); // NOTE: This is intentionally the same list

        Spinner<Double> spinnerPoints = new Spinner(0d, Double.MAX_VALUE, 0.0);
        spinnerPoints.setEditable(true);


        AnchorPane buttonWrapper = generateOkCancelButtonWrapper();

        HBox binaryGroup = new HBox();
        binaryGroup.setStyle("-fx-spacing: 10px");
        ToggleGroup binaryButtons = new ToggleGroup();
        RadioButton binaryYes = new RadioButton("Yes");
        binaryYes.setToggleGroup(binaryButtons);
        RadioButton binaryNo = new RadioButton("No");
        binaryNo.setToggleGroup(binaryButtons);
        binaryNo.setSelected(true);
        binaryGroup.getChildren().addAll(lblBinary, binaryYes, binaryNo);


        HBox mandatoryGroup = new HBox();
        mandatoryGroup.setStyle("-fx-spacing: 10px");
        ToggleGroup mandatoryButtons = new ToggleGroup();
        RadioButton mandatoryYes = new RadioButton("Yes");
        mandatoryYes.setToggleGroup(mandatoryButtons);
        RadioButton mandatoryNo = new RadioButton("No");
        mandatoryNo.setToggleGroup(mandatoryButtons);
        mandatoryNo.setSelected(true);
        mandatoryGroup.getChildren().addAll(lblMandatory, mandatoryYes, mandatoryNo);


        HBox malusGroup = new HBox();
        malusGroup.setStyle("-fx-spacing: 10px");
        ToggleGroup malusButtons = new ToggleGroup();
        RadioButton malusYes = new RadioButton("Yes");
        malusYes.setToggleGroup(malusButtons);
        RadioButton malusNo = new RadioButton("No");
        malusNo.setToggleGroup(malusButtons);
        malusNo.setSelected(true);
        malusGroup.getChildren().addAll(lblMalus, malusYes, malusNo);

        GridPane groupWrapper = new GridPane();
        groupWrapper.setPadding(new Insets(0, 10, 0, 10));
        groupWrapper.add(binaryGroup, 0, 0);
        GridPane.setHgrow(binaryGroup, Priority.ALWAYS);
        groupWrapper.add(mandatoryGroup, 1, 0);
        GridPane.setHgrow(mandatoryGroup, Priority.ALWAYS);
        groupWrapper.add(malusGroup, 2, 0);
        GridPane.setHgrow(malusGroup, Priority.ALWAYS);

        int rowIndex = 0;

        grid.add(lblName, 0, rowIndex);
        grid.add(tfName, 1, rowIndex++);

        grid.add(lblDesc, 0, rowIndex);
        grid.add(taDesc, 1, rowIndex, 1, 2);
        rowIndex += 2;
        // Skip two rows
        grid.add(lblMinMS, 0, rowIndex);
        grid.add(cbMinMS, 1, rowIndex++);

        grid.add(lblMaxMS, 0, rowIndex);
        grid.add(cbMaxMS, 1, rowIndex++);

        grid.add(lblMaxPoints, 0, rowIndex);
        grid.add(spinnerPoints, 1, rowIndex++);
        // separator
        grid.add(new Separator(), 0, rowIndex++, 2, 1);
        // RadioButton groups
        grid.add(groupWrapper, 0, rowIndex++, 2, 1);
        GridPane.setHgrow(groupWrapper, Priority.ALWAYS);
        // Separator
        grid.add(new Separator(), 0, rowIndex++, 2, 1);

        grid.add(lblPredecessors, 0, rowIndex);
        grid.add(new Label("Predecessor list or so"), 1, rowIndex++);


        // Buttons, last row
        grid.add(buttonWrapper, 1, ++rowIndex, 2, 1);

        return scene;
    }

    private AnchorPane generateOkCancelButtonWrapper() {
        // TODO Extract class with respective save and cancel handlers
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        HBox buttons = new HBox();
        buttons.setStyle("-fx-padding: 5px; -fx-spacing: 5px");
        buttons.getChildren().addAll(saveButton, cancelButton);
        AnchorPane buttonWrapper = new AnchorPane();
        buttonWrapper.getChildren().add(buttons);
        AnchorPane.setRightAnchor(buttons, 0d);
        return buttonWrapper;
    }

    private GridPane generateDefaultGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-padding: 10px; -fx-spacing: 10px; -fx-hgap: 10px;-fx-vgap: 10px");
        return grid;
    }

    public Scene createMilestonePropertiesScene() {
        GridPane grid = generateDefaultGridPane();
        Scene scene = new Scene(grid);

        Label lblName = new Label("Name");
        Label lblDate = new Label("Date");

        TextField tfName = new TextField();

        TextField inputDate = new TextField(); // TODO: Replace with DatePicker

        AnchorPane buttonWrapper = generateOkCancelButtonWrapper();

        int rowIndex = 0;

        grid.add(lblName, 0, rowIndex);
        grid.add(tfName, 1, rowIndex++);

        grid.add(lblDate, 0, rowIndex);
        grid.add(inputDate, 1, rowIndex++);

        grid.add(buttonWrapper, 0, ++rowIndex, 2, 1);

        return scene;
    }


    @Deprecated
    public Scene createCataloguePropertiesScene() {
        GridPane grid = generateDefaultGridPane();
        Scene scene = new Scene(grid);

        Label lblLecture = new Label("Lecture");
        Label lblName = new Label("Name");
        Label lblDescription = new Label("Description");
        // Milestones and Labels added via different scene
        Label lblSemester = new Label("Semester");

        TextField tfLecture = new TextField();
        TextField tfName = new TextField();
        TextArea taDesc = new TextArea();
        TextField tfSemester = new TextField();

        SaveCancelPane buttonWrapper = new SaveCancelPane();
        buttonWrapper.setOnSave(event -> {
            System.out.println("Saving.... " + tfName.getText());
        });

        buttonWrapper.setOnCancel(event -> {
            System.out.println("Canceling...");
            System.exit(0);
        });


        int rowIndex = 0;

        grid.add(lblLecture, 0, rowIndex);
        grid.add(tfLecture, 1, rowIndex++);
        grid.add(lblSemester, 0, rowIndex);
        grid.add(tfSemester, 1, rowIndex++);
        grid.add(lblName, 0, rowIndex);
        grid.add(tfName, 1, rowIndex++);
        grid.add(lblDescription, 0, rowIndex);
        grid.add(taDesc, 1, rowIndex, 1, 3);
        rowIndex += 3;
        grid.add(buttonWrapper, 0, ++rowIndex, 2, 1);


        return scene;
    }
}
