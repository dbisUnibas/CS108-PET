package ch.unibas.dmi.dbis.reqman.ui.editor;


import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListController;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorApplication extends Application {

    private EditorController controller;

    private ModifiableListView<Requirement> reqView;
    private ModifiableListView<Milestone> msView;

    public static void main(String[] args) {
        launch(args);
    }

    private void printDebugEvent(ActionEvent event){
        System.out.println("Type: "+event.getEventType().getName()+", Source: "+event.getSource().toString() );
    }

    private void handleQuit(ActionEvent event){
        stop();
    }

    @Override
    public void stop(){
        // Ask if sure, unsaved changes blabla
        System.exit(0);
    }

    private MenuBar createMenuBar(){
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
        MenuItem itemExportCat = new MenuItem("Export Catalogue");
        itemExportCat.setOnAction(controller::handleExportCatalogue);
        MenuItem itemNewReq = new MenuItem("New Requirement");
        itemNewReq.setOnAction(controller::handleAddRequirement);
        MenuItem itemNewMS = new MenuItem("New Milestone");
        itemNewMS.setOnAction(controller::handleAddMilestone);
        MenuItem itemQuit = new MenuItem("Quit");
        itemQuit.setOnAction(this::handleQuit);

        // Order matters
        menuFile.getItems().addAll(itemNewCat, itemNewReq, itemNewMS, new SeparatorMenuItem(), itemOpenCat, itemSaveCat, itemExportCat, new SeparatorMenuItem(), itemQuit);

        // Menu Edit
        Menu menuEdit = new Menu("Edit");
        MenuItem itemModifyCat = new MenuItem("Modify Catalogue");
        itemModifyCat.setOnAction(controller::handleModifyCatalogue);

        menuEdit.getItems().addAll(itemModifyCat);

        Menu menuView = new Menu("View");

        Menu menuHelp = new Menu("Help");

        bar.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);

        return bar;
    }

    private BorderPane wrapperPane = new BorderPane();
    private GridPane main = new GridPane();

    private void initReqView(){
        reqView = new RequirementsView(controller);
    }

    private void initMSView(){
        msView = new MilestonesView(controller);
    }

    private void initUI(Scene scene){
        // Set the menubar to the top
        wrapperPane.setTop(createMenuBar());

        // Setting the main node.
        wrapperPane.setCenter(main);

        initReqView();
        initMSView();

        // TODO Make width *relative* to total width: Use property
        //reqView.setPrefWidth(scene.getWidth()/3.0);
        //msView.setPrefWidth(scene.getWidth()/3.0);

        // Looks nicer anyhow
        VBox box = new VBox();
        box.setStyle("-fx-spacing: 10px; -fx-padding: 10px");
        box.getChildren().addAll(reqView, msView);
        main.add(box,0,0);
        /*
        // Got removed since other space not used yet
        main.add(reqView, 0,0, 2, 1);
        GridPane.setVgrow(reqView, Priority.SOMETIMES);
        GridPane.setFillWidth(reqView, true);
        main.add(msView, 0,1, 2, 1);
        GridPane.setVgrow(msView, Priority.SOMETIMES);
        GridPane.setFillWidth(msView, true);
        */
    }

    @Override
    public void start(Stage primaryStage) {
        controller = new EditorController(this, primaryStage);
        primaryStage.setTitle("ReqMan: Editor");
        Scene scene = new Scene(wrapperPane, 800, 600);

        initUI(scene);
        // Initially disabled
        disableAll();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void disableAll(){
        reqView.setDisable(true);
        msView.setDisable(true);
    }

    public void enableAll(){
        reqView.setDisable(false);
        msView.setDisable(false);
    }

    public void setDisableReqView(boolean disable){
        reqView.setDisable(disable);
    }

    public void setDisableMsView(boolean disable){
        msView.setDisable(disable);
    }

    public void passRequirementsToView(ObservableList<Requirement> requirements){
        reqView.setItems(requirements);
    }

    public void passMilestonesToView(ObservableList<Milestone> milestones){
        msView.setItems(milestones);
    }
}
