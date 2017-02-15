package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListHandler;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.nio.ch.IOUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorController  {

    private Catalogue catalogue;

    private Stage controlledStage;

    private EditorApplication editor;

    private ObservableList<Requirement> observableReqs = FXCollections.observableArrayList();
    private ObservableList<Milestone> observableMs = FXCollections.observableArrayList();

    public EditorController(EditorApplication editor, Stage controlledStage){
        this.editor = editor;
        this.controlledStage = controlledStage;
    }

    public void setCatalogue(Catalogue catalogue){
        this.catalogue = catalogue;
        observableReqs = FXCollections.observableArrayList(catalogue.getRequirements() );
        observableMs = FXCollections.observableArrayList(catalogue.getMilestones() );
    }

    public Catalogue getCatalogue(){
        catalogue.addAllRequirements(observableReqs.toArray(new Requirement[0]));
        catalogue.addAllMilestones(observableMs.toArray(new Milestone[0]));
        return catalogue;
    }

    public ObservableList<Requirement> getObservableRequirements(){
        return observableReqs;
    }

    public ObservableList<Milestone> getObservableMilestones(){
        return observableMs;
    }

    public void handleAddRequirement(ActionEvent event){
        Requirement r = EditorPromptFactory.promptNewRequirement();
        if(r != null){ // user may cancelled the prompt
            observableReqs.add(r );
        }

    }

    public void handleRemoveRequirement(ModifiableListView.RemoveEvent<Requirement> event){
        if(event.getSelected() != null){ // If nothing selected - don't remove it!
            observableReqs.remove(event.getSelectedIndex() );
        }

    }

    public void handleAddMilestone(ActionEvent event){
        Milestone m = EditorPromptFactory.promptNewMilestone();
        if(m != null){
            m.setOrdinal(getNextMsOrdinal() );
            observableMs.add(m);
        }
    }

    private int getNextMsOrdinal(){
        ArrayList<Milestone> temp = new ArrayList<>(observableMs);
        temp.sort((ms1, ms2) -> {
            // TODO nullcheck
            if(ms1.getOrdinal() < ms2.getOrdinal() ){
                return -1;
            }else if(ms1.getOrdinal() == ms2.getOrdinal() ){
                return 0;
            }else{
                return 1;
            }
        });
        if(temp.isEmpty() ){
            return 1;
        }
        return temp.get(temp.size()-1).getOrdinal()+1;
    }

    public void handleRemoveMilestone(ModifiableListView.RemoveEvent<Milestone> event){
        if(event.getSelected() != null){ // If nothing selected - don't remove it!
            observableMs.remove(event.getSelectedIndex());
        }

    }

    public void handleNewCatalogue(ActionEvent event){
        Catalogue cat = EditorPromptFactory.promptNewCatalogue();
        if(cat != null){
            this.catalogue = cat;
            updateTitle();
            editor.enableAll();
        }
    }

    public void handleModifyCatalogue(ActionEvent event){
        Catalogue updated = EditorPromptFactory.promptCatalogue(catalogue);
        if(updated != null){
            this.catalogue = updated;
            updateTitle();
        }
    }

    public void handleSaveCatalogue(ActionEvent event){
        FileChooser saveChooser = createCatalogueFileChooser("Save");
        File file = saveChooser.showSaveDialog(controlledStage);
        try {
            JSONUtils.writeToJSONFile(getCatalogue(), file); // Important to use getCatalogue as the reqs and ms are set there
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileChooser createCatalogueFileChooser(String action){
        FileChooser fc = new FileChooser();
        fc.setTitle(action+" Catalogue");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("Any", "*.*")
        );
        return fc;
    }

    public void handleExportCatalogue(ActionEvent event){

    }

    public void handleOpenCatalogue(ActionEvent event){
        FileChooser openChooser = createCatalogueFileChooser("Open");
        File f = openChooser.showOpenDialog(controlledStage);
        try {
            setCatalogue(JSONUtils.readCatalogueJSONFile(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.enableAll();
    }


    private void updateTitle(){
        StringBuffer sb = new StringBuffer("ReqMan: Editor");
        sb.append(" - ");
        sb.append(catalogue.getName() != null ? catalogue.getName() : "N/A");
        sb.append(" (");
        sb.append(catalogue.getLecture() != null ? catalogue.getLecture() : "N/A" );
        sb.append(" @ ");
        sb.append(catalogue.getSemester() != null ? catalogue.getSemester() : "N/A" );
        sb.append(")");
        controlledStage.setTitle(sb.toString());
    }



}
