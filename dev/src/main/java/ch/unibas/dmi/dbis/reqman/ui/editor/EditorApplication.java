package ch.unibas.dmi.dbis.reqman.ui.editor;


import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListController;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    private MenuBar createMenuBar(){
        MenuBar bar = new MenuBar();


        // Menu File
        Menu menuFile = new Menu("File");

        MenuItem itemNewCat = new MenuItem("New Catalogue");
        itemNewCat.setOnAction(this::printDebugEvent);
        MenuItem itemOpenCat = new MenuItem("Open Catalogue");
        itemOpenCat.setOnAction(this::printDebugEvent);
        MenuItem itemSaveCat = new MenuItem("Save Catalogue");
        itemSaveCat.setOnAction(this::printDebugEvent);
        MenuItem itemExportCat = new MenuItem("Export Catalogue");
        itemExportCat.setOnAction(this::printDebugEvent);
        MenuItem itemNewReq = new MenuItem("New Requirement");
        itemNewReq.setOnAction(this::printDebugEvent);
        MenuItem itemNewMS = new MenuItem("New Milestone");
        itemNewMS.setOnAction(this::printDebugEvent);
        MenuItem itemQuit = new MenuItem("Quit");
        itemQuit.setOnAction(this::printDebugEvent);

        // Order matters
        menuFile.getItems().addAll(itemNewCat, itemNewReq, itemNewMS, new SeparatorMenuItem(), itemOpenCat, itemSaveCat, itemExportCat, new SeparatorMenuItem(), itemQuit);

        // Menu Edit
        Menu menuEdit = new Menu("Edit");

        Menu menuView = new Menu("View");

        Menu menuHelp = new Menu("Help");

        bar.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);

        return bar;
    }

    private BorderPane wrapperPane = new BorderPane();
    private GridPane main = new GridPane();

    private void initReqView(){
        reqView = new ModifiableListView<Requirement>("Requirements", new ModifiableListController<Requirement>() {
            @Override
            protected Requirement createNew() {
                return null;
            }
        });


    }

    private void initMSView(){
        msView = new ModifiableListView<Milestone>("Milestones", new ModifiableListController<Milestone>() {
            @Override
            protected Milestone createNew() {
                return null;
            }
        });
    }

    private void initUI(Scene scene){
        // Set the menubar to the top
        wrapperPane.setTop(createMenuBar());

        // Setting the main node.
        wrapperPane.setCenter(main);

        initReqView();
        initMSView();

        // TODO Make width *relative* to total width: Use property
        reqView.setPrefWidth(scene.getWidth()/3.0);
        msView.setPrefWidth(scene.getWidth()/3.0);

        main.add(reqView, 0,0);
        //GridPane.setHgrow(topSide, Priority.SOMETIMES);
        GridPane.setVgrow(reqView, Priority.SOMETIMES);
        GridPane.setFillWidth(reqView, true);
        main.add(msView, 0,1);
        //GridPane.setHgrow(bottomSide, Priority.SOMETIMES);
        GridPane.setVgrow(msView, Priority.SOMETIMES);
        GridPane.setFillWidth(msView, true);

        main.add(new Label("Center"), 1,1,1,1);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Editor");
        Scene scene = new Scene(wrapperPane, 800, 600);

        initUI(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
