package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
            addGroupToInternalStorage(group);
            addGroupTab(group);
        }
    }

    private Map<String, AssessmentView> groupAVMap = new TreeMap<>();

    private void addGroupToInternalStorage(Group group){
        groups.add(group);
        groupAVMap.put(group.getName(), new AssessmentView(this, group));
    }

    public void addGroupTab(Group active){
        if(evaluator.isGroupTabbed(active)){
            // Dont add another tab of the same group
        }else{
            evaluator.addGroupTab(groupAVMap.get(active.getName()));
        }


    }



    public List<Requirement> getRequirementsByMilestone(int ordinal){
        return catalogue.getRequirementsByMilestone(ordinal);
    }

    public void handleRemoveGroup(ModifiableListView.RemoveEvent<Group> event) {
        if (event.getSelected() != null) {
            groups.remove(event.getSelectedIndex());
        }
    }

    public void handleOpenGroup(ActionEvent event) {
        FileChooser fc = Utils.createGroupFileChooser("Open");
        File f = fc.showOpenDialog(evaluator.getWindow() );
        if(f != null){
            try {
                Group group = JSONUtils.readGroupJSONFile(f);
                addGroupToInternalStorage(group);
                groupFileMap.put(group.getName(), f);
                addGroupTab(group);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, File> groupFileMap = new TreeMap<>();

    public void handleSaveGroup(ActionEvent event) {
        Group active = evaluator.getActiveGroup();
        AssessmentView av = groupAVMap.get(active.getName() );
        File f = groupFileMap.get(active.getName() );
        if(f == null){
            handleSaveAsGroup(event);
        }else{
            try {
                saveGroup(active, av, f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveGroup(Group group, AssessmentView av, File f) throws IOException {
        group.setProgressList(av.getProgressList());
        JSONUtils.writeToJSONFile(group, f);
    }

    public void handleSaveAsGroup(ActionEvent event) {
        Group active = evaluator.getActiveGroup();
        AssessmentView av = groupAVMap.get(active.getName() );
        FileChooser fc = Utils.createGroupFileChooser("Save As");
        File f = fc.showSaveDialog(evaluator.getWindow());
        if(f != null){
            try {
                saveGroup(active, av, f);
                groupFileMap.put(active.getName(), f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleModifyGroup(ActionEvent event) {
        // NEED TO GET THE ACTIVE GROUP
    }

    public ObservableList<Group> getObservableGroups() {
        return groups;
    }
}
