package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.management.EntityManager;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorHandler implements EventHandler<CUDEvent>{

    private final static Logger LOGGER = LogManager.getLogger(EvaluatorHandler.class);

    private final EntityManager manager = EntityManager.getInstance();

    private EvaluatorView evaluator;

    EvaluatorHandler(){
        LOGGER.trace("<init>");
    }

    void setEvaluatorView(EvaluatorView view){
        this.evaluator = view;
    }

    List<Milestone> getMilestones(){
        return new ArrayList<>(manager.getObservableMilestones());
    }

    public String getLecture() {
        return manager.getLecture();
    }

    public String getName() {
        return manager.getCatalogueName();
    }

    public String getDescription() {
        return manager.getDescription();
    }

    public String getSemester() {
        return manager.getSemester();
    }

    public SimpleDoubleProperty sumProperty() {
        return manager.sumProperty();
    }

    public boolean isCatalogueLoaded() {
        return manager.isCatalogueLoaded();
    }

    public Catalogue getCatalogue() {
        return manager.getCatalogue();
    }

    @Override
    public void handle(CUDEvent event) {
        if(event != null){
            if(CUDEvent.CREATION.equals(event.getEventType() )) {
                // CREATION
                handleCreation(event);
            }else if(CUDEvent.DELETION.equals(event.getEventType() ) ){
                // DELETION
                handleDeletion(event);
            }else if(CUDEvent.MODIFICATION.equals(event.getEventType() ) ){
                handleModification(event);
            }else{
                throw new IllegalArgumentException("Cannot handle event type: "+event.getEventType().toString() );
            }
            event.consume();
        }
        // silently ignoring null events

    }



    void handleModification(CUDEvent event) {
        switch(event.getTargetEntity() ){
            case GROUP:
                LOGGER.trace(":handleModificaiton");
                if(event.getDelivery() != null && event.getDelivery() instanceof Group){
                    Group gr = (Group)event.getDelivery();
                    // DONT FORGET TO UPDATE ALL NAME REFERNECES, IF NAME CHANGED!
                    Group mod = EvaluatorPromptFactory.promptGroup(gr, this);
                    manager.replaceGroup(gr, mod);
                }
                break;
            default:
                // Ignoring
        }
    }

    void handleDeletion(CUDEvent event) {
        switch(event.getTargetEntity() ){
            case GROUP:
                // DELETE GROUP
                LOGGER.trace(":handleDeletion");
                if(event.getDelivery() != null && event.getDelivery() instanceof  Group){
                    Group del = (Group)event.getDelivery();
                    manager.removeGroup(del);
                }
                break;
            default:
                // Ignoring
        }
    }

    void handleCreation(CUDEvent event) {
        switch(event.getTargetEntity() ){
            case GROUP:
                LOGGER.trace(":handleCreation");
                // ADD GROUP
                Group gr = EvaluatorPromptFactory.promptGroup(this);
                manager.addGroup(gr);
                break;
            default:
                // Ignoring
        }
    }


    public boolean isGroupNameUnique(String name) {
        return manager.isGroupNameUnique(name);
    }

    public void handleOpenGroups(ActionEvent actionEvent) {
        if(!manager.isCatalogueLoaded() ){
            return;
        }
        FileChooser fc = Utils.createGroupFileChooser("Open");
        if(manager.hasLastOpenLocation()){
            fc.setInitialDirectory(manager.getLastOpenLocation() );
        }
        List<File> files = fc.showOpenMultipleDialog(evaluator.getScene().getWindow() );
        if(files.size() == 1){
            manager.openGroup(files.get(0), this::groupOpened);
        }else if(files.size() >= 2){
            throw new UnsupportedOperationException("not impelemnted yet");
        }
        // USER ABORT
    }

    private void groupOpened() {
        Group g = manager.getLastOpenedGroup();
        LOGGER.trace("Creating UI for group "+g.getName());
    }

    public void handleSaveGroup(ActionEvent actionEvent) {

    }

    public void handleOpenCatalogue(ActionEvent event){
        FileChooser fc = Utils.createCatalogueFileChooser("Open");
        if(manager.hasLastOpenLocation() ){
            fc.setInitialDirectory(manager.getLastOpenLocation());
        }
        File f = fc.showOpenDialog(evaluator.getScene().getWindow());
        if(f != null){
            manager.openCatalogue(f, this::catalogueLoaded);
        }
    }

    private void catalogueLoaded(){
        LOGGER.info("Opened catalogue "+manager.getCatalogueFile().getPath() );
        LOGGER.trace("Enabling all");
        evaluator.enableAll();
        evaluator.displayCatalogueInfo(manager.getCatalogue() );
    }

    public ObservableList<Group> groupList() {
        return manager.groupList();
    }
}
