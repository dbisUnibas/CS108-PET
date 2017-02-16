package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.reqman.ui.common.SaveCancelPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class RequirementPropertiesScene extends AbstractVisualCreator<Requirement> {

    private TextField tfName = new TextField();
    private TextArea taDesc = new TextArea();
    private ComboBox<Milestone> cbMinMS = new ComboBox<>();
    private ComboBox<Milestone> cbMaxMS = new ComboBox<>();
    private Spinner spinnerPoints = new Spinner(0d, Double.MAX_VALUE, 0.0);

    private RadioButton binaryYes = new RadioButton("Yes");
    private RadioButton binaryNo = new RadioButton("No");

    private RadioButton mandatoryYes = new RadioButton("Yes");
    private RadioButton mandatoryNo = new RadioButton("No");

    private RadioButton malusYes = new RadioButton("Yes");
    private RadioButton malusNo = new RadioButton("No");

    private EditorController controller;

    public RequirementPropertiesScene(EditorController controller) {
        super();
        this.controller = controller;
        populateScene();
    }

    private Requirement requirement = null;

    public RequirementPropertiesScene(EditorController controller, Requirement requirement) {
        this(controller);
        this.requirement = requirement;
        loadRequirement();
    }

    private void loadRequirement() {
        if (requirement != null) {
            tfName.setText(requirement.getName() );
            taDesc.setText(requirement.getDescription());
            cbMinMS.getSelectionModel().select(getMilestoneFromString(requirement.getMinMilestone()));
            cbMaxMS.getSelectionModel().select(getMilestoneFromString(requirement.getMaxMilestone() ) );
            spinnerPoints.getValueFactory().setValue(requirement.getMaxPoints());

            binaryYes.setSelected(requirement.isBinary());
            binaryNo.setSelected(!requirement.isBinary());

            mandatoryYes.setSelected(requirement.isMandatory());
            mandatoryNo.setSelected(!requirement.isMandatory());

            malusYes.setSelected(requirement.isMalus());
            malusNo.setSelected(!requirement.isMalus());

            loadPredecessors();
        }
    }

    private Milestone getMilestoneFromString(String milestone){
        String ordinalStr = milestone.substring(milestone.indexOf(":")+1);
        int ordinal = Integer.parseInt(ordinalStr);
        return controller.findMilestoneForOrdinal(ordinal);
    }

    private void loadPredecessors(){
        requirement.getPredecessorNames().forEach(name -> {
            Requirement r = controller.findRequirementByName(name);
            if(r != null){
                predecessors.add(r);
            }
        });
    }


    @Override
    protected void populateScene() {
        ScrollPane scrollPane = new ScrollPane();
        Label lblName = new Label("Name*");
        Label lblDesc = new Label("Description");
        Label lblMinMS = new Label("Minimal Milestone*");
        Label lblMaxMS = new Label("Maximal Milestone");
        Label lblMaxPoints = new Label("Maximal Points*");
        Label lblBinary = new Label("Binary");
        Label lblMandatory = new Label("Mandatory");
        Label lblMalus = new Label("Malus");
        Label lblPredecessors = new Label("Predecessors");
        Label lblProps = new Label("Properties");

        loadRequirement();

        // TODO Proper MS choice

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
        cbMinMS.setCellFactory((ListView<Milestone> l) -> new MilestonesView.MilestoneCell());
        cbMinMS.setOnAction(event -> {
            // Make so that the maxMS is set to the same value as this one. (initially as soon as this one is set)
            Milestone selected = cbMinMS.getSelectionModel().getSelectedItem();
            Milestone target = cbMaxMS.getSelectionModel().getSelectedItem();
            if(selected != null && target == null){
                cbMaxMS.getSelectionModel().select(selected);
            }
        });
        cbMinMS.setButtonCell(new MilestonesView.MilestoneCell());
        cbMaxMS.setCellFactory((ListView<Milestone> lv) -> new MilestonesView.MilestoneCell());
        cbMaxMS.setButtonCell(new MilestonesView.MilestoneCell());

        cbMinMS.setItems(milestoneList);
        cbMaxMS.setItems(milestoneList); // NOTE: This is intentionally the same list


        spinnerPoints.setEditable(true);
        // TODO add spinner-handler
        // Solution by: http://stackoverflow.com/a/39380146
        spinnerPoints.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                spinnerPoints.increment(0);
            }
        });

        BorderPane inputPredecessors = createPredecessorChoice();

        SaveCancelPane buttonWrapper = new SaveCancelPane();

        buttonWrapper.setOnSave(this::handleSaving);

        buttonWrapper.setOnCancel(event -> getWindow().hide());

        HBox binaryGroup = new HBox();
        binaryGroup.setStyle("-fx-spacing: 10px");
        ToggleGroup binaryButtons = new ToggleGroup();
        binaryYes.setToggleGroup(binaryButtons);
        binaryNo.setToggleGroup(binaryButtons);
        binaryNo.setSelected(true);
        binaryGroup.getChildren().addAll(lblBinary, binaryYes, binaryNo);


        HBox mandatoryGroup = new HBox();
        mandatoryGroup.setStyle("-fx-spacing: 10px");
        ToggleGroup mandatoryButtons = new ToggleGroup();
        mandatoryYes.setToggleGroup(mandatoryButtons);
        mandatoryNo.setToggleGroup(mandatoryButtons);
        mandatoryYes.setSelected(true);
        mandatoryGroup.getChildren().addAll(lblMandatory, mandatoryYes, mandatoryNo);


        HBox malusGroup = new HBox();
        malusGroup.setStyle("-fx-spacing: 10px");
        ToggleGroup malusButtons = new ToggleGroup();
        malusYes.setToggleGroup(malusButtons);
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
        grid.add(minMSBox, 1, rowIndex++);

        grid.add(lblMaxMS, 0, rowIndex);
        grid.add(maxMSBox, 1, rowIndex++);

        grid.add(lblMaxPoints, 0, rowIndex);
        grid.add(spinnerPoints, 1, rowIndex++);
        // separator
        grid.add(new Separator(), 0, rowIndex++, 2, 1);
        // RadioButton groups
        grid.add(groupWrapper, 0, rowIndex++, 2, 1);
        GridPane.setHgrow(groupWrapper, Priority.ALWAYS);
        // Separator
        grid.add(new Separator(), 0, rowIndex++, 2, 1);

        // Predecessor list
        grid.add(lblPredecessors, 0, rowIndex);
        grid.add(inputPredecessors, 1, rowIndex++, 1, 1);
        rowIndex += 2;

        // Buttons, last row
        grid.add(buttonWrapper, 1, ++rowIndex, 2, 1);

        scrollPane.setContent(grid);
        setRoot(scrollPane);
    }

    public void handleSaving(ActionEvent event) {
        String name = tfName.getText();
        Milestone min = cbMinMS.getValue();
        double maxPoints = (double) spinnerPoints.getValue();

        if((name == null || name.isEmpty()) || min == null || Double.compare(0.0,maxPoints)==0){
            throw new IllegalArgumentException("[Requirement] Name, Minimal Milestone and Max Points are mandatory fields");
        }

        Milestone max = cbMaxMS.getValue() == null ? min : cbMaxMS.getValue();

        requirement = new Requirement(
                name,
                taDesc.getText(),
                min.getName()+":"+min.getOrdinal(),
                max.getName()+":"+max.getOrdinal(),
                maxPoints,
                binaryYes.isSelected(),
                mandatoryYes.isSelected(),
                malusYes.isSelected()
        );

        if(!predecessors.isEmpty() ){
            ArrayList<String> names = new ArrayList<>();
            predecessors.forEach(pred -> names.add(pred.getName()));
            requirement.setPredecessorNames(names);
        }

        getWindow().hide();
    }

    private ObservableList<Requirement> predecessors = FXCollections.observableArrayList();

    private BorderPane createPredecessorChoice(){
        BorderPane pane = new BorderPane();
        HBox upper = new HBox();
        Button addPred = new Button("+");
        Button rmPred = new Button("-");

        ListView<Requirement> predList = new ListView<>();
        predList.setCellFactory((ListView<Requirement> l) -> new RequirementsView.RequirementCell());
        predList.setItems(predecessors);
        ComboBox<Requirement> reqBox = new ComboBox<>();
        reqBox.setButtonCell(new RequirementsView.RequirementCell() );
        reqBox.setCellFactory((ListView<Requirement> l) -> new RequirementsView.RequirementCell());
        reqBox.setItems(controller.getObservableRequirements() );

        upper.getChildren().addAll(reqBox, addPred, rmPred);

        upper.setStyle("-fx-spacing: 10px; -fx-padding: 10px;");
        pane.setStyle("-fx-spacing: 10px; -fx-padding: 10px;");
        pane.setTop(upper);
        pane.setCenter(predList);

        rmPred.setOnAction(event -> {
            int index = predList.getSelectionModel().getSelectedIndex();
            Requirement selected = predList.getSelectionModel().getSelectedItem();
            if(selected == null){
                return; // Do not remove when nothing is selected
            }else{
                predecessors.remove(index);
            }
        });

        addPred.setOnAction(event -> {
            Requirement selected = reqBox.getSelectionModel().getSelectedItem();
            if(selected == null){
                return;
            }
            predecessors.add(selected);
        });

        pane.setStyle("-fx-border-width: 1; -fx-border-color: silver;");

        return pane;
    }

    private void handleNewMaxMS(ActionEvent event) {
        controller.handleAddMilestone(event);
        cbMaxMS.getSelectionModel().select(milestoneList.size() - 1);
    }

    private void handleNewMinMS(ActionEvent event) {
        controller.handleAddMilestone(event);
        cbMinMS.getSelectionModel().select(milestoneList.size() - 1);
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

    private ObservableList<Milestone> milestoneList;

    private void loadMilestoneNames() {
        milestoneList = controller.getObservableMilestones();
    }

}
