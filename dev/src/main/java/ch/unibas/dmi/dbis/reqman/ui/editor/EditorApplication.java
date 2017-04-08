package ch.unibas.dmi.dbis.reqman.ui.editor;


import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.editor.event.EditorEvent;
import ch.unibas.dmi.dbis.reqman.ui.editor.event.TargetEntity;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorApplication extends Application {

    private EditorController controller;

    private RequirementsView reqView;
    private MilestonesView msView;
    private BorderPane wrapperPane = new BorderPane();
    private Label lblName, lblLecture, lblSemester;

    private static volatile boolean exp = false;

    private static final Logger LOG = LogManager.getLogger(EditorApplication.class);

    public static void main(String[] args) {
        Log4J2Fix.applyHotFix();

        if(args.length >= 1){
            if("--exp".equals(args[0]) || "--experimental".equals(args[0]) ){
                exp = true;
            }
        }

        launch(args);
    }

    @Override
    public void stop() {
        // Ask if sure, unsaved changes blabla
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) {
        if(exp){
            LOG.info("Experimental env");
            //startTableProofConcept(primaryStage);
            startNew(primaryStage);
        }else{
            LOG.info("Normal");
            startOld(primaryStage);
        }
    }

    private EditorHandler handler = new EditorHandler();
    private Scene scene;

    private void startNew(Stage primaryStage) {
        LOG.trace(":startNew");

        EditorView editor = new EditorView(handler);

        scene = new Scene(createExperimentalWrapper(editor), 800,600);
        primaryStage.setScene(scene);
        primaryStage.setTitle(editor.getTitle() + " " + String.format("(%s-EXPERIMENTAL)", Version.getInstance().getVersion()));
        primaryStage.show();
    }

    private BorderPane createExperimentalWrapper(EditorView editor) {
        LOG.trace(":createExpWrapper");
        BorderPane pane = new BorderPane();
        pane.setTop(createExperimentalMenu());
        pane.setCenter(editor);
        return pane;
    }

    private MenuBar createExperimentalMenu(){
        LOG.trace(":createExpMenu");
        MenuBar menu = new MenuBar();

        Menu menuFile = new Menu("File");

        MenuItem itemOpen = new MenuItem("Open");
        itemOpen.setOnAction(evt -> {
            FileChooser openChooser = Utils.createCatalogueFileChooser("Open");
            File file = openChooser.showOpenDialog(scene.getWindow());
            if(file != null){
                handler.openCatalogue(file);
            }
        });

        MenuItem itemSave = new MenuItem("Save");
        itemSave.setOnAction(evt -> {
            if(handler.isCatalogueFilePresent() ){
                handler.saveCatalogue();
            }else{
                saveAs();
            }
        });

        MenuItem itemNew = new MenuItem("New");
        itemNew.setOnAction(evt -> {
            if(handler.isCatalogueLoaded() ){
                System.out.println("WILL DISCARD PREVIOUS CHANGES");
            }
            handler.handleCreation(EditorEvent.generateCreationEvent(evt, TargetEntity.CATALOGUE));
        });

        MenuItem itemExport = new MenuItem("Export");
        itemExport.setOnAction(handler::handleExportCatalogue);

        menuFile.getItems().addAll(itemNew,itemOpen, itemSave, itemExport);
        menu.getMenus().add(menuFile);
        return menu;
    }

    private void saveAs(){
            handler.saveAsCatalogue();
    }


    private void startOld(Stage primaryStage){
        primaryStage.setTitle("ReqMan: Editor");
        EditorScene editor = new EditorScene(primaryStage,800, 600);
        primaryStage.setScene(editor);
        primaryStage.setTitle(editor.getTitle());
        primaryStage.show();
    }

    private ObservableList<Requirement> reqs;

    private void startTableProofConcept(Stage primaryStage){
        LOG.trace(":startTableProofConcept");
        primaryStage.setTitle("ReqMan: Editor (EXPERIMENTAL)");
        RequirementTableView view = new RequirementTableView();
        List<Requirement> requirements = generateReqs();
        LOG.trace(":startTableProofConcept - generated reqs");
        reqs =  FXCollections.observableArrayList(requirements );
        view.setOnAdd(this::handleAdd );
        LOG.trace(":startTableProofConcept - past setOnAdd");
        view.setOnRemove(this::handleRm);
        LOG.trace(":startTableProofConcept - past setOnRemove");
        view.setRequirements(reqs);
        LOG.trace(":startTableProofConcept - past setReqs");
        Scene scene = new Scene(view, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<Requirement> generateReqs(){
        LOG.trace(":generateReqs");
        ArrayList<Requirement> list = new ArrayList<>();
        for(int i=0; i<10;i++){
            Random rand = new Random();
            String name = "Req"+i;
            double points = rand.nextInt(5)+(rand.nextInt(10)/10f);
            boolean binary = rand.nextInt(10) <=5;
            boolean mandatory = rand.nextInt(10)<=8;
            boolean malus = rand.nextInt(10)<2;
            Requirement req = new Requirement(name, "Description", 1,5,points, binary, mandatory, malus);
            list.add(req);
            LOG.trace(String.format("Generated: %s", req.toString()));
        }
        return list;
    }

    private void handleAdd(ActionEvent evt){
        LOG.trace(":handleAdd");
        reqs.add(new Requirement("R"+reqs.size()+1,"DESC", 1, 5, Math.random()+3, Math.random() <= 0.5, Math.random() >= 0.6, Math.random() <0.1));
    }

    private void handleRm(ActionEvent evt){
        LOG.trace(":handleRM");
        Random r = new Random();
        reqs.remove(r.nextInt(reqs.size()));
    }

    private void handleQuit(ActionEvent event) {
        stop();
    }

}
