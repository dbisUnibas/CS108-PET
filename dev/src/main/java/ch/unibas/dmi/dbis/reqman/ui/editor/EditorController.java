package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListHandler;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorController  {

    private Catalogue catalogue;

    private ObservableList<Requirement> observableReqs = FXCollections.observableArrayList();
    private ObservableList<Milestone> observableMs = FXCollections.observableArrayList();

    public EditorController(){

    }

    public void setCatalogue(Catalogue catalogue){
        this.catalogue = catalogue;
        observableReqs = FXCollections.observableArrayList(catalogue.getRequirements() );
        observableMs = FXCollections.observableArrayList(catalogue.getMilestones() );
    }

    public Catalogue getCatalogue(){
        return catalogue;
    }

    public ObservableList<Requirement> getObservableRequirements(){
        return observableReqs;
    }

    public ObservableList<Milestone> getObservableMilestones(){
        return observableMs;
    }

    public void requestAddRequirement(ActionEvent event){
        Requirement r = EditorPromptFactory.promptNewRequirement();
        if(r != null){ // user may cancelled the prompt
            observableReqs.add(r );
        }else{
            System.out.println("nulll");
        }

    }

    public void requestRemoveRequirement(ModifiableListView.RemoveEvent<Requirement> event){
        observableReqs.remove(event.getSelectedIndex() );
    }

    public void requestAddMilestone(ActionEvent event){

    }

    public void requestRemoveMilestone(ModifiableListView.RemoveEvent<Milestone> event){

    }
}
