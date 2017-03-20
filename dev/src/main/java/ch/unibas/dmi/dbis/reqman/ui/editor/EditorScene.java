package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.ReqmanApplication;
import ch.unibas.dmi.dbis.reqman.ui.common.TitleProvider;
import ch.unibas.dmi.dbis.reqman.ui.common.TitledScene;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang.StringUtils;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorScene extends TitledScene{

    private EditorController controller;

    private RequirementsView reqView;
    private MilestonesView msView;
    private BorderPane root = new BorderPane();
    private Label lblName, lblLecture, lblSemester;

    public String getTitle() {
        return title;
    }

    private String title = "Editor";

    public EditorScene(int width, int height){
        super(new BorderPane(), width, height);
        this.controller = new EditorController(this);
        initUI();
    }

    private MenuBar createMenuBar() {
        MenuBar bar = new MenuBar();


        // Menu File
        Menu menuFile = new Menu("File");

        MenuItem itemNewCat = new MenuItem("New Catalogue");
        itemNewCat.setOnAction(controller::handleNewCatalogue);
        itemNewCat.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        MenuItem itemOpenCat = new MenuItem("Open Catalogue");
        itemOpenCat.setOnAction(controller::handleOpenCatalogue);
        itemOpenCat.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        MenuItem itemSaveCat = new MenuItem("Save Catalogue");
        itemSaveCat.setOnAction(controller::handleSaveCatalogue);
        itemSaveCat.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        MenuItem itemSaveAsCat = new MenuItem("Save As Catalogue");
        itemSaveAsCat.setOnAction(controller::handleSaveAsCatalogue);
        itemSaveAsCat.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        MenuItem itemExportCat = new MenuItem("Export Catalogue");
        itemExportCat.setOnAction(controller::handleExportCatalogue);
        itemExportCat.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
        MenuItem itemNewReq = new MenuItem("New Requirement");
        itemNewReq.setOnAction(controller::handleAddRequirement);
        itemNewReq.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
        MenuItem itemNewMS = new MenuItem("New Milestone");
        itemNewMS.setOnAction(controller::handleAddMilestone);
        itemNewMS.setAccelerator(KeyCombination.keyCombination("Ctrl+M"));
        MenuItem itemQuit = new MenuItem("Quit");
        //itemQuit.setOnAction(this::handleQuit);
        itemQuit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));

        // Order matters
        menuFile.getItems().addAll(itemNewCat, itemNewReq, itemNewMS, new SeparatorMenuItem(), itemOpenCat, itemSaveCat, itemSaveAsCat, itemExportCat, new SeparatorMenuItem(), itemQuit);

        // Menu Edit
        Menu menuEdit = new Menu("Edit");
        MenuItem itemModifyCat = new MenuItem("Modify Catalogue");
        itemModifyCat.setOnAction(controller::handleModifyCatalogue);

        menuEdit.getItems().addAll(itemModifyCat);

        Menu menuView = new Menu("View");
        MenuItem itemChangeViewToEvaluator = new MenuItem("Evaluator");
        itemChangeViewToEvaluator.setOnAction(this::handleChangeView);
        menuView.getItems().addAll(itemChangeViewToEvaluator);

        Menu menuHelp = new Menu("Help");

        bar.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);

        return bar;
    }

    private void initReqView() {
        reqView = new RequirementsView(controller);
    }

    private void initMSView() {
        msView = new MilestonesView(controller);
    }

    private void initUI() {
        // Set the menubar to the top
        root.setTop(createMenuBar());

        // Setting the main node.
        //root.setCenter(main);

        initReqView();
        initMSView();

        // TODO Make width *relative* to total width: Use property
        //reqView.setPrefWidth(scene.getWidth()/3.0);
        //msView.setPrefWidth(scene.getWidth()/3.0);

        // Looks nicer anyhow
        VBox box = new VBox();
        box.setStyle("-fx-spacing: 10px; -fx-padding: 10px");
        box.getChildren().addAll(createCatalogueInfo(), reqView, msView);
        //main.add(box,0,0);
        root.setCenter(box);

        setRoot(root);
    }

    private HBox createCatalogueInfo() {
        HBox box = new HBox();
        box.setStyle("-fx-spacing: 10px; -fx-padding: 10px");
        Label lblCatalogue = new Label("Catalgoue");
        Label lblCatName = new Label("Name:");
        Label lblCatLecture = new Label("Lecture:");
        Label lblCatSemester = new Label("Semester:");

        lblName = new Label();
        lblLecture = new Label();
        lblSemester = new Label();

        box.getChildren().addAll(lblCatalogue, lblCatName, lblName, lblCatLecture, lblLecture, lblCatSemester, lblSemester);

        return box;
    }

    public void updateCatalogueInfo(String name, String lecture, String semester) {
        lblName.setText(StringUtils.isNotEmpty(name) ? name : "");
        lblLecture.setText(StringUtils.isNotEmpty(lecture) ? lecture : "");
        lblSemester.setText(StringUtils.isNotEmpty(semester) ? semester : "");
    }

    public void disableAll() {
        reqView.setDisable(true);
        msView.setDisable(true);
    }

    public void enableAll() {
        reqView.setDisable(false);
        msView.setDisable(false);
    }

    public void setDisableReqView(boolean disable) {
        reqView.setDisable(disable);
    }

    public void setDisableMsView(boolean disable) {
        msView.setDisable(disable);
    }

    public void passRequirementsToView(ObservableList<Requirement> requirements) {
        reqView.setItems(requirements);
    }

    public void passMilestonesToView(ObservableList<Milestone> milestones) {
        msView.setItems(milestones);
    }

    private EventHandler<ReqmanApplication.ChangeEvent> changeHandler = null;

    private void handleChangeView(ActionEvent event){
        if(changeHandler != null){
            ReqmanApplication.ChangeEvent evt = new ReqmanApplication.ChangeEvent(event, ReqmanApplication.EVALUATOR_VIEW);
            changeHandler.handle(evt);
        }
    }

    public void setOnChangeEvent(EventHandler<ReqmanApplication.ChangeEvent> handler){
        changeHandler = handler;
    }
}
