package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Progress;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressView extends VBox {

    // TODO Extract code for collapsible pane. Adjust content's size.

    private Progress progress;
    private Requirement requirement;

    private Label lblTitle = new Label();
    private ToggleButton collapseButton = new ToggleButton(Utils.ARROW_DOWN);

    private Spinner<Double> spinnerPoints;
    private CheckBox check;

    private VBox content = new VBox();

    public ProgressView(Progress progress, Requirement requirement){
        super();
        this.progress = progress;
        this.requirement = requirement;

        initComponents();
        initCollapsible();
    }


    private void initCollapsible(){
        collapseButton.setOnAction(this::handleCollapse);
        //content.setVisible(false);
        content.setStyle("-fx-background-color: white;-fx-padding: 10px; -fx-spacing: 10px");
    }

    private void handleCollapse(ActionEvent event){
        if(collapseButton.isSelected() ){
            collapseButton.setText(Utils.ARROW_UP);
            getChildren().add(content);
            //content.setVisible(true);
        }else{
            collapseButton.setText(Utils.ARROW_DOWN);

            getChildren().remove(content);
            //content.setVisible(false);
        }
        event.consume();
    }

    private void initComponents(){

        lblTitle.setText(requirement.getName());

        AnchorPane outer = new AnchorPane();

        HBox title = new HBox();

        title.setStyle("-fx-padding: 10px; -fx-spacing: 10px");

        title.getChildren().addAll(collapseButton, lblTitle);

        outer.getChildren().add(title);

        Node control;
        if(requirement.isBinary() ){
            check = new CheckBox();
            control = check;
        }else{
            spinnerPoints = new Spinner<>(0d, requirement.getMaxPoints(), 0.0);
            control = spinnerPoints;
        }

        outer.getChildren().add(control);

        outer.prefHeightProperty().bind(prefWidthProperty() );


        AnchorPane.setRightAnchor(control, 10d); // not handled by padding?
        AnchorPane.setTopAnchor(control, 10d); // not handled by padding?

        AnchorPane.setLeftAnchor(title, 0d); // handled by padding?

        getChildren().add(outer);

        content.getChildren().add(new Label("Collapsible"));
        //getChildren().add(content);
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
}
