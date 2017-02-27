package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorController {

    private EvaluatorScene evaluator;


    private Catalogue catalogue;

    private ObservableList<Group> groups;


    public EvaluatorController(EvaluatorScene evaluator) {
        this.evaluator = evaluator;
        init();
    }

    private void init() {
        // groups etc
        groups = FXCollections.observableArrayList();
    }

    public List<Milestone> getMilestones(){
        return catalogue.getMilestones();
    }


    public void handleLoadCatalogue(ActionEvent event) {
        FileChooser fc = Utils.createCatalogueFileChooser("Load");
        File f = fc.showOpenDialog(evaluator.getWindow());
        if (f != null) {
            try {
                catalogue = JSONUtils.readCatalogueJSONFile(f);
                evaluator.getCatalogueInfoView().displayData(catalogue);
                // TODO Enable group adding etc
            } catch (IOException e) {
                // TODO Handle exception
                e.printStackTrace();
            }
        }
    }

    public void handleAddGroup(ActionEvent event) {
        Group group = EvaluatorPromptFactory.promptNewGroup(catalogue.getName());
        if (group != null) {
            groups.add(group);
            addGroupTab(group);
        }
    }

    public void addGroupTab(Group active){
        if(evaluator.isGroupTabbed(active)){
            // Dont add another tab of the same group
        }else{
            evaluator.addGroupTab(active);
        }


    }

    public void handleRemoveGroup(ModifiableListView.RemoveEvent<Group> event) {
        if (event.getSelected() != null) {
            groups.remove(event.getSelectedIndex());
        }
    }

    public void handleOpenGroup(ActionEvent event) {
        // NEED TO GET THE ACTIVE GROUP
    }

    public void handleSaveGroup(ActionEvent event) {
    }

    public void handleSaveAsGroup(ActionEvent event) {
    }

    public void handleModifyGroup(ActionEvent event) {
    }

    public void handleOpenAssessment(ActionEvent event) {

    }

    public ObservableList<Group> getObservableGroups() {
        return groups;
    }
}
