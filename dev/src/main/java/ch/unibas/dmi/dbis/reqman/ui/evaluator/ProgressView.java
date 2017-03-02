package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Progress;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressView extends VBox {

    // TODO Extract code for collapsible pane. Adjust collapsible's size.

    private Progress progress;
    private final Requirement requirement;

    private Label lblTitle = new Label();
    private ToggleButton collapseButton = new ToggleButton(Utils.ARROW_DOWN);
    private TextArea taDesc = new TextArea();

    private Spinner<Double> spinnerPoints;
    private CheckBox check;

    private VBox collapsible = new VBox();

    public ProgressView(Requirement requirement){
        this(null, requirement);
    }

    public ProgressView(Progress progress, Requirement requirement){
        super();
        this.progress = progress == null ? new Progress() : progress;
        this.requirement = requirement;

        initComponents();
        initCollapsible();
        loadProgress();
    }

    private void loadProgress() {
        if(progress != null){
            if(progress.getPoints() > 0 ){
                if(requirement.isBinary() ){
                    check.setSelected(true);
                }else{
                    spinnerPoints.getValueFactory().setValue(progress.getPoints());
                }
            }
        }
    }


    private void initCollapsible(){
        collapseButton.setOnAction(this::handleCollapse);
        //collapsible.setVisible(false);
        collapsible.setStyle("-fx-background-color: white;-fx-padding: 10px; -fx-spacing: 10px;-fx-border-width: 1px;-fx-border-color: silver");

    }

    private void handleCollapse(ActionEvent event){
        if(collapseButton.isSelected() ){
            collapseButton.setText(Utils.ARROW_UP);
            getChildren().add(collapsible);
            //collapsible.setVisible(true);
        }else{
            collapseButton.setText(Utils.ARROW_DOWN);

            getChildren().remove(collapsible);
            //collapsible.setVisible(false);
        }
        event.consume();
    }

    private AnchorPane content;

    private void initComponents(){

        lblTitle.setText(requirement.getName() + "\t("+ ch.unibas.dmi.dbis.reqman.common.StringUtils.prettyPrint(requirement.getMaxPointsSensitive())+")"+(!requirement.isMandatory() ? "\t[BONUS]":""));

        taDesc.setEditable(false);

        content = new AnchorPane();

        HBox title = new HBox();

        title.setStyle("-fx-spacing: 15px");

        title.getChildren().addAll(collapseButton, lblTitle);

        content.getChildren().add(title);

        Control control;
        if(requirement.isBinary() ){
            check = new CheckBox();
            control = check;
            check.setOnAction(this::handleAssessmentAction);
        }else{
            spinnerPoints = new Spinner<>(0d, requirement.getMaxPoints(), 0.0);
            control = spinnerPoints;
            // Solution by: http://stackoverflow.com/a/39380146
            spinnerPoints.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    spinnerPoints.increment(0);
                }
            });
            spinnerPoints.valueProperty().addListener((observable, oldValue, newValue) -> {
                if(Double.compare(oldValue, newValue) != 0){ // Only if really new value
                    progress.setPoints(newValue);
                    progress.setPercentage(progress.getPoints() / requirement.getMaxPoints());
                    notifyPointsListener();
                }
            });
        }

        content.getChildren().add(control);

        content.prefWidthProperty().bind(prefWidthProperty() );


        AnchorPane.setRightAnchor(control, 10d); // not affected by padding?
        AnchorPane.setTopAnchor(control, 10d); // not affected by padding?

        AnchorPane.setLeftAnchor(title, 0d); // affected by padding?
        AnchorPane.setTopAnchor(title, 10d);// not affected by padding=

        getChildren().add(content);
        taDesc.setText(requirement.getDescription());

        // Builds the collapsible
        GridPane grid = Utils.generateDefaultGridPane();
        Label lblBinary = new Label("Binary:");
        Label lblMandatory = new Label("Mandatory:");
        Label lblMalus = new Label("Malus");
        Label lblDesc = new Label("Description");

        CheckBox cbBinary = new CheckBox();
        cbBinary.setSelected(requirement.isBinary());
        cbBinary.setDisable(true);
        CheckBox cbMandatory = new CheckBox();
        cbMandatory.setSelected(requirement.isMandatory());
        cbMandatory.setDisable(true);
        CheckBox cbMalus = new CheckBox();
        cbMalus.setSelected(requirement.isMalus());
        cbMalus.setDisable(true);

        grid.add(lblBinary, 0,0);
        grid.add(cbBinary, 1, 0);
        grid.add(lblMandatory, 3,0);
        grid.add(cbMandatory, 4,0);
        grid.add(lblMalus, 6,0);
        grid.add(cbMalus, 7, 0);

        grid.add(lblDesc, 0, 1);
        grid.add(taDesc, 1, 1, 6, 1);

        collapsible.getChildren().add(grid);
        //getChildren().add(collapsible);
        content.setStyle("-fx-spacing: 10px;-fx-padding: 10px;-fx-border-color: silver;-fx-border-width: 1px;");
        //content.setStyle("-fx-background-color: lime;"+ content.getStyle() );
        //setStyle("-fx-background-color: crimson;");
    }

    public Requirement getRequirement() {
        return requirement;
    }


    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    private void handleAssessmentAction(ActionEvent event){
        if(check.isSelected() ){
            progress.setPoints(requirement.getMaxPoints());
            progress.setPercentage(progress.getPoints() / requirement.getMaxPoints());
        }else{
            progress.setPoints(0);
            progress.setPercentage(0d);
        }
        notifyPointsListener();
    }

    private List<PointsChangeListener> listeners = new ArrayList<>();

    public void addPointsChangeListener(PointsChangeListener listener){
        listeners.add(listener);
    }

    public void removePointsChangeListener(PointsChangeListener listener){
        listeners.remove(listener);
    }

    private void notifyPointsListener(){
        listeners.forEach(l -> l.pointsChanged(progress.getPoints()));
    }
}
