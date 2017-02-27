package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Progress;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
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
    private Requirement requirement;

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

        lblTitle.setText(requirement.getName());

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
        collapsible.getChildren().add(taDesc);
        //getChildren().add(collapsible);
        content.setStyle("-fx-spacing: 10px;-fx-padding: 10px;-fx-border-color: silver;-fx-border-width: 1px;");
        //content.setStyle("-fx-background-color: lime;"+ content.getStyle() );
        //setStyle("-fx-background-color: crimson;");
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
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
        }else{
            progress.setPoints(0);
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
