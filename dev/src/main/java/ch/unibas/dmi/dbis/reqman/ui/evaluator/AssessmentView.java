package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class AssessmentView extends BorderPane {

    private HBox titleBar;
    private AnchorPane titleAnchor;
    private Label lblChoice;
    private ComboBox<Milestone> cbMilestones;
    private Button btnRefresh;
    private HBox statusWrapper;
    private AnchorPane statusBar;
    private Label lblSum;
    private TextField tfSum;
    private VBox content;
    private ScrollPane scrollPane;


    private EvaluatorController controller;

    @Deprecated
    public AssessmentView(){
        super();

        initComponents();
        layoutComponents();
    }

    private Group group;

    public AssessmentView(EvaluatorController controller, Group active){
        super();

        this.controller = controller;
        this.group = active;

        initComponents();
        layoutComponents();

    }

    private void initComponents(){
        titleBar = new HBox();
        titleAnchor = new AnchorPane();
        lblChoice = new Label("Current Milestone: ");
        cbMilestones = new ComboBox<>();
        btnRefresh = new Button("Update");
        statusWrapper = new HBox();
        statusBar = new AnchorPane();
        lblSum = new Label("Sum:");
        tfSum = new TextField();
        tfSum.setEditable(false);
        content = new VBox();
        scrollPane = new ScrollPane();
    }

    private void layoutComponents(){
        // Forge top aka title bar:
        titleBar.getChildren().addAll(lblChoice, cbMilestones, btnRefresh );

        if(controller != null){
            cbMilestones.setItems(FXCollections.observableList(controller.getMilestones()));
        }


        VBox titleWrapper = new VBox();
        Separator sep = new Separator();
        titleWrapper.getChildren().addAll(titleBar, sep);
        setTop(titleWrapper);

        // Forge center aka ProgressView list
        scrollPane.setContent(content);
        setCenter(scrollPane);

        // Forge bottom aka status bar:
        statusWrapper.getChildren().addAll(lblSum, tfSum);
        Separator sep2 = new Separator();
        statusBar.getChildren().add(statusWrapper);
        statusBar.getChildren().add(sep2);
        AnchorPane.setTopAnchor(sep2, 0d);
        AnchorPane.setRightAnchor(statusWrapper, 10d);
        setBottom(statusBar);
    }

    // DEBUG
    public void addProgressView(ProgressView pv){
        content.getChildren().add(pv);
        pv.prefWidthProperty().bind(scrollPane.widthProperty() );
    }
    // DEBUG
    public void removeProgressView(ProgressView pv){
        content.getChildren().remove(pv);
    }

    public void bindToParentSize(Region parent){
        prefWidthProperty().bind(parent.widthProperty());
        prefHeightProperty().bind(parent.heightProperty());
    }

    private void bindContent(){
        content.prefWidthProperty().bind(widthProperty());
        content.prefHeightProperty().bind(heightProperty());
    }
}
