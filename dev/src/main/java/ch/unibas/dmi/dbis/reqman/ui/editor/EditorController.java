package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListHandler;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorController  {

    private Catalogue catalogue;

    private Stage controlledStage;

    private ObservableList<Requirement> observableReqs = FXCollections.observableArrayList();
    private ObservableList<Milestone> observableMs = FXCollections.observableArrayList();

    public EditorController(Stage controlledStage){
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
        }else{
            System.out.println("nulll");
        }

    }

    public void handleRemoveRequirement(ModifiableListView.RemoveEvent<Requirement> event){
        observableReqs.remove(event.getSelectedIndex() );
    }

    public void handleAddMilestone(ActionEvent event){

    }

    public void handleRemoveMilestone(ModifiableListView.RemoveEvent<Milestone> event){

    }

    public void handleNewCatalogue(ActionEvent event){
        Catalogue cat = EditorPromptFactory.promptNewCatalogue();
        if(cat != null){
            this.catalogue = cat;
            updateTitle();
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

    }

    public void handleExportCatalogue(ActionEvent event){

    }

    private void updateTitle(){
        StringBuffer sb = new StringBuffer("ReqMan: Editor");
        sb.append(" - ");
        sb.append(catalogue.getName());
        sb.append(" (");
        sb.append(catalogue.getLecture() );
        sb.append(" @ ");
        sb.append(catalogue.getSemester() );
        sb.append(")");
        controlledStage.setTitle(sb.toString());
    }

}
