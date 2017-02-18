package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.common.SimpleCatalogueExporter;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

    public void openCatalogue(Catalogue catalogue){
        this.catalogue = catalogue;
        observableReqs = FXCollections.observableArrayList(catalogue.getRequirements() );
        observableMs = FXCollections.observableArrayList(catalogue.getMilestones() );
        editor.passRequirementsToView(observableReqs);
        editor.passMilestonesToView(observableMs);
        updateTitle();
    }

    public Catalogue getCatalogue(){
        catalogue.setRequirements(observableReqs);
        catalogue.setMilestones(observableMs);
        return catalogue;
    }

    public ObservableList<Requirement> getObservableRequirements(){
        return observableReqs;
    }

    public ObservableList<Milestone> getObservableMilestones(){
        return observableMs;
    }

    public void handleAddRequirement(ActionEvent event){
        if(!isCatalogueSet() ){
            return; // Prevent open prompt from accelerator even if no catalogue is set
        }
        Requirement r = EditorPromptFactory.promptNewRequirement(this);
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
        if(!isCatalogueSet() ){
            return; // Prevent open prompt from accelerator even if no catalogue is set
        }
        Milestone m = EditorPromptFactory.promptNewMilestone();
        if(m != null){
            m.setOrdinal(getNextMsOrdinal() );
            observableMs.add(m);
        }
    }

    private boolean isCatalogueSet(){
        return catalogue != null;
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

    private File catalogueFile = null;

    public void handleSaveAsCatalogue(ActionEvent event){
        FileChooser saveChooser = createCatalogueFileChooser("Save As");
        File catalogueFile = saveChooser.showSaveDialog(controlledStage);
        if(catalogueFile == null){
            return; // No file was selected
        }
        try {
            JSONUtils.writeToJSONFile(getCatalogue(), catalogueFile); // Important to use getCatalogue as the reqs and ms are set there
            this.catalogueFile = catalogueFile; // Only when saving was done
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSaveCatalogue(ActionEvent event){
        if(catalogueFile == null){
            handleSaveAsCatalogue(event);
        }
        try {
            JSONUtils.writeToJSONFile(getCatalogue(), catalogueFile);
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
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Catalogue");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HTML", "*.html"));
        File f = fc.showSaveDialog(controlledStage);
        if(f == null){
            return; // no file was selected
        }
        SimpleCatalogueExporter exporter = new SimpleCatalogueExporter(getCatalogue() );
        String html = exporter.exportHTML();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(html);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleOpenCatalogue(ActionEvent event){
        FileChooser openChooser = createCatalogueFileChooser("Open");
        File catalogueFile = openChooser.showOpenDialog(controlledStage);
        if(catalogueFile == null){
            return; // No file selected
        }
        this.catalogueFile = catalogueFile;
        try {
            openCatalogue(JSONUtils.readCatalogueJSONFile(catalogueFile));
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


    public void handleModifyRequirement(Requirement sel) {
        Requirement update = EditorPromptFactory.promptRequirement(this, sel);
        int index = observableReqs.indexOf(sel);
        if(update != null){
            observableReqs.remove(index);
            observableReqs.add(index, update);
        }
    }

    public void handleModifyMilestone(Milestone sel){
        Milestone update = EditorPromptFactory.promptMilestone(sel);
        int index = observableMs.indexOf(sel);
        if(update != null){
            observableMs.remove(index);
            observableMs.add(index, update);
        }
    }

    public Milestone getMilestoneByOrdinal(int ordinal) {
        Milestone result = null;
        for(Milestone ms : observableMs){
            if(ms.getOrdinal() == ordinal){
                result = ms;
            }
        }
        return result;
    }

    public Requirement findRequirementByName(String name){
        Requirement result = null;
        for(Requirement req : observableReqs){
            if(req.getName().equals(name)){
                result = req;
            }
        }
        return result;
    }
}
