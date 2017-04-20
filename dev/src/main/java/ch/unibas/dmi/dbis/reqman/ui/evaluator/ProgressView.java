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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressView extends VBox {

    private final static Logger LOG = LogManager.getLogger(ProgressView.class);

    // TODO Extract code for collapsible pane. Adjust collapsible's size.

    private final Requirement requirement;
    private Progress progress;
    private Label lblTitle = new Label();
    private ToggleButton collapseButton = new ToggleButton(Utils.ARROW_DOWN);
    private TextArea taDesc = new TextArea();

    private HBox controlWrapper;

    private Spinner<Double> spinnerPoints;
    private CheckBox check;

    private ToggleGroup toggleGroup = new ToggleGroup();
    private RadioButton yesBtn;
    private RadioButton noBtn;

    private VBox collapsible = new VBox();
    private AnchorPane content;
    private List<PointsChangeListener> listeners = new ArrayList<>();

    private List<DirtyListener> dirtyListeners = new ArrayList<>();
    @Deprecated // replaced by previousSavedYesNoConfig
    private double previousPoints = -1d;

    /**
     * To track, if the user has reverted its change.
     */
    private volatile boolean[] previousSavedYesNoConfig = new boolean[]{false, false};
    private volatile boolean first = true;

    private Milestone active = null;

    public ProgressView(Requirement requirement) {
        this(null, requirement);
    }


    public ProgressView(Progress progress, Requirement requirement) {
        super();
        this.progress = progress == null ? new Progress() : progress;
        this.requirement = requirement;

        initComponents();
        initCollapsible();
        loadProgress();
    }

    @Deprecated
    private boolean hasPointsChanged(double newPoints){
        return Double.compare(previousPoints, newPoints) == 0;
    }

    private boolean hasYesNoConfigChaned(boolean yesSelected, boolean noSelected){
        if(first){
            first = false;
            return true;
        }
        return previousSavedYesNoConfig[0] != yesSelected && previousSavedYesNoConfig[1] != noSelected;
    }

    private void initYesNoButtons() {
        if (!requirement.isBinary()) {
            return;
        }
        controlWrapper.getChildren().clear();

        controlWrapper.getChildren().addAll(yesBtn, noBtn);
        controlWrapper.setStyle("-fx-spacing: 10px;");

        yesBtn.setOnAction(action -> {
            progress.setPoints(requirement.getMaxPoints(), requirement.getMaxPoints());
            progress.setDate(active != null ? active.getDate() : new Date());
            notifyPointsListener();
            handleToggling(action);
        });

        noBtn.setOnAction(action -> {
            progress.setPoints(Progress.NO_POINTS, requirement.getMaxPoints());
            progress.setDate(active != null ? active.getDate() : new Date());
            notifyPointsListener();
            handleToggling(action);
        });
    }

    private void handleToggling(ActionEvent event) {
        double points = -1d;
        boolean changes = false;
        if (yesBtn.equals(event.getSource())) {
            LOG.trace(":handleYes");
            points = requirement.getMaxPoints();
            changes = true;
        } else if (noBtn.equals(event.getSource())) {
            LOG.trace(":handleNo");
            points = Progress.NO_POINTS;
            changes = true;
        }

        if (hasYesNoConfigChaned(yesBtn.isSelected(), noBtn.isSelected())) {
            LOG.trace(":configChanged");
            progress.setPoints(points, requirement.getMaxPoints());
            progress.setDate(active.getDate());
            progress.setMilestoneOrdinal(active.getOrdinal());
            notifyDirtyListeners(true);
            notifyPointsListener();
        } else {
            notifyDirtyListeners(false);
        }

    }

    public Milestone getActiveMilestone() {
        return active;
    }

    public void setActiveMilestone(Milestone active) {
        this.active = active;
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

    public void addPointsChangeListener(PointsChangeListener listener) {
        listeners.add(listener);
    }

    public void removePointsChangeListener(PointsChangeListener listener) {
        listeners.remove(listener);
    }

    private void loadProgress() {
        if (progress != null) {
            if (progress.hasDefaultPercentage()) {
                return; // Do nothing, if default percentage.
            }
            if (requirement.isBinary()) {
                if (progress.hasProgress()) {
                    yesBtn.setSelected(true);
                } else {
                    noBtn.setSelected(true);
                }
                previousSavedYesNoConfig[0] = yesBtn.isSelected();
                previousSavedYesNoConfig[1] = noBtn.isSelected();
            } else {
                spinnerPoints.getValueFactory().setValue(progress.getPoints());
            }
        }
    }

    private void initCollapsible() {
        collapseButton.setOnAction(this::handleCollapse);
        //collapsible.setVisible(false);
        collapsible.setStyle("-fx-background-color: white;-fx-padding: 10px; -fx-spacing: 10px;-fx-border-width: 1px;-fx-border-color: silver");

    }

    private void handleCollapse(ActionEvent event) {
        if (collapseButton.isSelected()) {
            collapseButton.setText(Utils.ARROW_UP);
            getChildren().add(collapsible);
            //collapsible.setVisible(true);
        } else {
            collapseButton.setText(Utils.ARROW_DOWN);

            getChildren().remove(collapsible);
            //collapsible.setVisible(false);
        }
        event.consume();
    }

    private void initComponents() {

        lblTitle.setText(requirement.getName() + "\t(" + ch.unibas.dmi.dbis.reqman.common.StringUtils.prettyPrint(requirement.getMaxPointsSensitive()) + ")" + (!requirement.isMandatory() ? "\t[BONUS]" : ""));

        taDesc.setEditable(false);

        content = new AnchorPane();

        HBox title = new HBox();

        title.setStyle("-fx-spacing: 15px");

        title.getChildren().addAll(collapseButton, lblTitle);

        content.getChildren().add(title);

        yesBtn = new RadioButton("Yes");
        yesBtn.setToggleGroup(toggleGroup);
        noBtn = new RadioButton("No");
        noBtn.setToggleGroup(toggleGroup);

        controlWrapper = new HBox();
        if (requirement.isBinary()) {
            initYesNoButtons();
        } else {
            spinnerPoints = new Spinner<>(0d, requirement.getMaxPoints(), -1d);
            controlWrapper.getChildren().add(spinnerPoints);
            // Solution by: http://stackoverflow.com/a/39380146
            spinnerPoints.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    spinnerPoints.increment(0);
                }
            });
            spinnerPoints.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (Double.compare(oldValue, newValue) != 0) { // Only if really new value
                    progress.setPoints(newValue, requirement.getMaxPoints());
                    notifyPointsListener();
                }
            });
        }

        content.getChildren().add(controlWrapper);

        content.prefWidthProperty().bind(prefWidthProperty());


        AnchorPane.setRightAnchor(controlWrapper, 10d); // not affected by padding?
        AnchorPane.setTopAnchor(controlWrapper, 10d); // not affected by padding?

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

        grid.add(lblBinary, 0, 0);
        grid.add(cbBinary, 1, 0);
        grid.add(lblMandatory, 3, 0);
        grid.add(cbMandatory, 4, 0);
        grid.add(lblMalus, 6, 0);
        grid.add(cbMalus, 7, 0);

        grid.add(lblDesc, 0, 1);
        grid.add(taDesc, 1, 1, 6, 1);

        collapsible.getChildren().add(grid);
        //getChildren().add(collapsible);
        content.setStyle("-fx-spacing: 10px;-fx-padding: 10px;-fx-border-color: silver;-fx-border-width: 1px;");
        //content.setStyle("-fx-background-color: lime;"+ content.getStyle() );
        //setStyle("-fx-background-color: crimson;");
    }

    private void handleAssessmentAction(ActionEvent event) {
        if (check.isSelected()) {
            progress.setPoints(requirement.getMaxPoints(), requirement.getMaxPoints());
        } else {
            progress.setPoints(0, requirement.getMaxPoints());
        }
        progress.setDate(new Date());
        notifyPointsListener();
    }

    private void notifyPointsListener() {
        listeners.forEach(l -> l.pointsChanged(progress.getPoints()));
    }

    void addDirtyListener(DirtyListener listener) {
        dirtyListeners.add(listener);
    }

    void removeDirtyList(DirtyListener listener) {
        dirtyListeners.remove(listener);
    }

    private void notifyDirtyListeners(boolean dirty) {
        dirtyListeners.forEach(listener -> listener.mark(dirty));
    }

    void markSaved() {
        if (yesBtn != null && noBtn != null) {
            previousSavedYesNoConfig[0] = yesBtn.isSelected();
            previousSavedYesNoConfig[1] = noBtn.isSelected();
        }
    }
}
