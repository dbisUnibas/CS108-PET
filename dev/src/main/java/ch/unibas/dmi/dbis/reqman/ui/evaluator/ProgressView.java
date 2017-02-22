package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Progress;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressView extends AnchorPane {

    private Progress progress;
    private Requirement requirement;

    private Label lblTitle = new Label();
    private ToggleButton collapsButton = new ToggleButton(Utils.ARROW_DOWN);

    private Spinner<Double> spinnerPoints;
    private CheckBox check;

    public ProgressView(Progress progress, Requirement requirement){
        super();
        this.progress = progress;
        this.requirement = requirement;

        initComponents();
    }

    private void initComponents(){
        lblTitle.setText(requirement.getName());


        HBox title = new HBox();

        title.setStyle("-fx-padding: 10px; -fx-spacing: 10px");

        title.getChildren().addAll(collapsButton, lblTitle);

        getChildren().add(title);

        Node control;
        if(requirement.isBinary() ){
            check = new CheckBox();
            control = check;
        }else{
            spinnerPoints = new Spinner<>(0d, requirement.getMaxPoints(), 0.0);
            control = spinnerPoints;
        }

        getChildren().add(control);
        AnchorPane.setRightAnchor(control, 10d);
        AnchorPane.setTopAnchor(control, 10d);

        AnchorPane.setLeftAnchor(title, 10d);

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
