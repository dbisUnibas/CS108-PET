package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class RequirementPropertiesScene extends AbstractVisualCreator<Requirement>{

    private TextField tfName = new TextField();
    private TextArea taDesc = new TextArea();
    private ComboBox cbMinMS = new ComboBox(); // TODO Type safety
    private ComboBox cbMaxMS = new ComboBox();
    private Spinner spinnerPoints = new Spinner(0d, Double.MAX_VALUE, 0.0);

    private RadioButton binaryYes = new RadioButton("Yes");
    private RadioButton binaryNo = new RadioButton("No");

    private RadioButton mandatoryYes = new RadioButton("Yes");
    private RadioButton mandatoryNo = new RadioButton("No");

    private RadioButton malusYes = new RadioButton("Yes");
    private RadioButton malusNo = new RadioButton("No");


    public RequirementPropertiesScene(){
        super();
        populateScene();
    }

    private Requirement requirement = null;

    public RequirementPropertiesScene(Requirement requirement){
        this();
        this.requirement = requirement;
    }

    private void loadRequirement(){
        if(requirement != null){

        }
    }


    @Override
    protected void populateScene() {
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

        loadRequirement();

        // TODO Proper MS choice

        ObservableList<String> list = FXCollections.observableArrayList("Create new...", "Milestone1", "Milestone2");
        cbMinMS.setItems(list);
        cbMaxMS.setItems(list); // NOTE: This is intentionally the same list


        spinnerPoints.setEditable(true);

        ModifiableListView<String> inputPredecessors = new ModifiableListView<>("Predecessors", new ModifiableListController<String>() {
            // TODO Proper handling of Predecessors
            private int counter = 0;
            @Override
            protected String createNew() {
                return "New element"+(counter++);
            }
        });

        SaveCancelPane buttonWrapper = new SaveCancelPane();
        // TODO SaveCancelHandler
        buttonWrapper.setOnSave(event -> {
            requirement = new Requirement(
                    tfName.getText(),
                    taDesc.getText(),
                    cbMinMS.getValue().toString(),
                    cbMaxMS.getValue().toString(),
                    (double)spinnerPoints.getValue(),
                    binaryYes.isSelected(),
                    mandatoryYes.isSelected(),
                    malusYes.isSelected()
            );
            // TODO Add Predecessors / Properties to req
            getWindow().hide();
        });

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
        mandatoryNo.setSelected(true);
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

        // Predecessor list
        grid.add(lblPredecessors, 0, rowIndex);
        grid.add(inputPredecessors.getView(), 1, rowIndex++,1,2);
        rowIndex += 2;

        // Buttons, last row
        grid.add(buttonWrapper, 1, ++rowIndex, 2, 1);

    }

    @Override
    public Requirement create() throws IllegalStateException {
        if(!isCreatorReady() ){
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
}
