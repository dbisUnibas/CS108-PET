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

    // TODO Extract code for collapsible pane. Adjust collapsible's size.

    private Progress progress;
    private Requirement requirement;

    private Label lblTitle = new Label();
    private ToggleButton collapseButton = new ToggleButton(Utils.ARROW_DOWN);
    private TextArea taDesc = new TextArea();

    private Spinner<Double> spinnerPoints;
    private CheckBox check;

    private VBox collapsible = new VBox();

    public ProgressView(Progress progress, Requirement requirement){
        super();
        this.progress = progress;
        this.requirement = requirement;

        initComponents();
        initCollapsible();
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
        //title.setStyle("-fx-background-color: darkorange;"+title.getStyle() );

        title.getChildren().addAll(collapseButton, lblTitle);

        content.getChildren().add(title);

        Node control;
        if(requirement.isBinary() ){
            check = new CheckBox();
            control = check;
        }else{
            spinnerPoints = new Spinner<>(0d, requirement.getMaxPoints(), 0.0);
            control = spinnerPoints;
        }

        content.getChildren().add(control);

        content.prefHeightProperty().bind(prefWidthProperty() );


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
}
