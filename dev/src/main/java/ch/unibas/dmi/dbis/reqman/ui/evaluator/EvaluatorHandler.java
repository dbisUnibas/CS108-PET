package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.core.*;
import ch.unibas.dmi.dbis.reqman.management.CatalogueNameMismatchException;
import ch.unibas.dmi.dbis.reqman.management.EntityManager;
import ch.unibas.dmi.dbis.reqman.management.NonUniqueGroupNameException;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorHandler implements EventHandler<CUDEvent> {

    private final static Logger LOGGER = LogManager.getLogger(EvaluatorHandler.class);

    private final EntityManager manager = EntityManager.getInstance();

    private EvaluatorView evaluator;

    private HashMap<String, AssessmentView> groupViewMap = new HashMap<>();
    private Milestone activeMS = null;

    public EvaluatorHandler() {
        LOGGER.trace("<init>");
    }

    void setEvaluatorView(EvaluatorView view) {
        this.evaluator = view;
    }

    public List<Milestone> getMilestones() {
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
        if (event != null) {
            if (CUDEvent.CREATION.equals(event.getEventType())) {
                // CREATION
                handleCreation(event);
            } else if (CUDEvent.DELETION.equals(event.getEventType())) {
                // DELETION
                handleDeletion(event);
            } else if (CUDEvent.MODIFICATION.equals(event.getEventType())) {
                handleModification(event);
            } else {
                throw new IllegalArgumentException("Cannot handle event type: " + event.getEventType().toString());
            }
        }
        // silently ignoring null events

    }

    public void handleModification(CUDEvent event) {
        switch (event.getTargetEntity()) {
            case GROUP:
                LOGGER.trace(":handleModificaiton");
                Group gr = evaluator.getActiveGroup();
                // DONT FORGET TO UPDATE ALL NAME REFERNECES, IF NAME CHANGED!
                Group mod = EvaluatorPromptFactory.promptGroup(gr, this);
                manager.replaceGroup(gr, mod);
                break;
            default:
                // Ignoring
        }
    }

    public void handleDeletion(CUDEvent event) {
        switch (event.getTargetEntity()) {
            case GROUP:
                // DELETE GROUP
                LOGGER.trace(":handleDeletion");
                if (event.getDelivery() != null && event.getDelivery() instanceof Group) {
                    Group del = (Group) event.getDelivery();
                    manager.removeGroup(del);
                }
                break;
            default:
                // Ignoring
        }
    }

    public void handleCreation(CUDEvent event) {
        switch (event.getTargetEntity()) {
            case GROUP:
                LOGGER.trace(":handleCreation");
                // ADD GROUP
                Group gr = EvaluatorPromptFactory.promptGroup(this);
                manager.addGroup(gr);
                loadGroupUI(gr);
                break;
            default:
                // Ignoring
        }
    }

    public boolean isGroupNameUnique(String name) {
        return manager.isGroupNameUnique(name);
    }

    public void handleOpenGroups(ActionEvent actionEvent) {
        if (!manager.isCatalogueLoaded()) {
            return;
        }
        FileChooser fc = Utils.createGroupFileChooser("Open");
        if (manager.hasLastOpenLocation()) {
            fc.setInitialDirectory(manager.getLastOpenLocation());
        }
        List<File> files = fc.showOpenMultipleDialog(evaluator.getScene().getWindow());
        if (files == null) {
            return; // USER ABORT
        }
        if (files.size() == 1) {
            manager.openGroup(files.get(0), this::loadGroupUI);

        } else if (files.size() >= 2) {
            manager.openGroups(files, this::loadGroupUI);
        }
        // USER ABORT
    }

    private void loadGroupUI(List<Group> groups) {
        groups.forEach(this::loadGroupUI);
    }

    private void loadGroupUI(Group g) {
        LOGGER.trace("Creating UI for group " + g.getName());
        if (manager.getLastOpenException() != null) {
            LOGGER.warn("Caught Exception");
            Exception e = manager.getLastOpenException();
            if (e instanceof CatalogueNameMismatchException) {
                CatalogueNameMismatchException cnme = (CatalogueNameMismatchException) e;
                String message = String.format("Could not finish opening group from file %s.\n" +
                        "The specified group (name: %s)'s catalogue signature is: %s\n" +
                        "Current catalogue name: %s", cnme.getGroupFile().getPath(), cnme.getGroupName(), cnme.getGroupCatName(), cnme.getCatName());
                Utils.showErrorDialog("Catalogue signature mismatch", message);
            } else if (e instanceof NonUniqueGroupNameException) {
                NonUniqueGroupNameException nugne = (NonUniqueGroupNameException) e;
                String message = String.format("Cannot finish opening of group %s, there exists alread a group with that name.", nugne.getName());
                Utils.showErrorDialog("Duplication error", message);
            }
        }

        addGroupToMap(g, null);
    }

    public void handleSaveGroup(ActionEvent actionEvent) {
        Group active = evaluator.getActiveGroup();
        if (manager.hasGroupFile(active)) {
            assemble(active);
            manager.saveGroup(active);
        } else {
            handleSaveGroupAs(actionEvent);
        }
    }

    public void handleSaveGroupAs(ActionEvent event) {
        FileChooser fc = Utils.createGroupFileChooser("Save As");
        if (manager.hasLastSaveLocation()) {
            fc.setInitialDirectory(manager.getLastSaveLocation());
        }
        File f = fc.showSaveDialog(evaluator.getScene().getWindow());
        if (f != null) {
            assemble(evaluator.getActiveGroup());
            manager.saveGroupAs(evaluator.getActiveGroup(), f);
        }
    }

    public void handleOpenCatalogue(ActionEvent event) {
        FileChooser fc = Utils.createCatalogueFileChooser("Open");
        if (manager.hasLastOpenLocation()) {
            fc.setInitialDirectory(manager.getLastOpenLocation());
        }
        File f = fc.showOpenDialog(evaluator.getScene().getWindow());
        if (f != null) {
            manager.openCatalogue(f, this::catalogueLoaded);
        }
    }

    private void catalogueLoaded() {
        LOGGER.info("Opened catalogue " + manager.getCatalogueFile().getPath());
        LOGGER.trace("Enabling all");
        evaluator.enableAll();
        evaluator.displayCatalogueInfo(manager.getCatalogue());
    }

    ObservableList<Group> groupList() {
        return manager.groupList();
    }

    private void addGroupToMap(Group group, AssessmentView view) {
        if (view != null) {
            LOGGER.trace(":addGroupToMap - Adding pre-existing AV");
            groupViewMap.put(group.getName(), view);
            if (activeMS != null) {
                LOGGER.trace(":addGroupToInternalStorage - Selecting activeMS: " + activeMS.getName());
                view.selectMilestone(activeMS);
            }
        } else {
            if (activeMS != null) {
                LOGGER.trace(":addGroupToMap - Creating AV with pre-set MS: " + activeMS.getName());
                groupViewMap.put(group.getName(), new AssessmentView(this, group, activeMS));
            } else {
                LOGGER.trace(":addGroupToMap - Creating AV without pre-set MS");
                groupViewMap.put(group.getName(), new AssessmentView(this, group));
            }
        }
        addTab(group, false);
    }

    public Milestone getMilestoneByOrdinal(int ordinal) {
        return manager.getMilestoneByOrdinal(ordinal);
    }

    List<Requirement> getRequirementsByMilestone(int ordinal) {
        return manager.getRequirementsByMilestone(ordinal);
    }

    public List<Requirement> getRequirementsWithMinMS(int ordinal) {
        return manager.getRequirementsWithMinMS(ordinal);
    }

    void markDirty(Group activeGroup) {
        evaluator.markDirty(activeGroup);
    }

    void unmarkDirty(Group activeGroup) {
        evaluator.unmarkDirty(activeGroup);
    }

    public void setGlobalMilestoneChoice(Milestone ms) {
        LOGGER.debug("Set global milestone choice to: " + ms.getName());
        this.activeMS = ms;
        for (AssessmentView av : groupViewMap.values()) {
            LOGGER.trace("Setting milestone " + ms.getName() + " for AV: " + av.getActiveGroup().getName());
            av.selectMilestone(ms);
        }
    }

    public void resetGlobalMilestoneChoice() {
        LOGGER.debug("Resetting global milestone choice");
        this.activeMS = null;
    }

    private void addTab(Group active, boolean fresh) {
        if (evaluator.isGroupTabbed(active)) {
            // Do not open another tab for already tabbed group.
        } else {
            evaluator.addGroupTab(groupViewMap.get(active.getName()), fresh);
        }
        evaluator.setActiveTab(active.getName());
    }

    void openGroupTab(Group group) {
        addTab(group, false);
    }

    public ObservableList<Progress> progressList(Group g) {
        return manager.getObservableProgress(g);
    }

    public Group getGroupByName(String name) {
        for (Group g : manager.groupList()) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        return null;
    }

    public void exportAllGroups(){
        LOGGER.trace(":exportAllGroups");
        if(!isCatalogueLoaded()){
            LOGGER.debug(":exportAllGroups - No catalogue set. Returning");
            return;
        }
        DirectoryChooser dc = new DirectoryChooser();
        if(manager.hasLastExportLocation() ){
            dc.setInitialDirectory(manager.getLastExportLocation() );
        }
        dc.setTitle("Choose an export folder");
        File dir = dc.showDialog(evaluator.getScene().getWindow() );

        for(Group g : manager.groupList()){
            assemble(g);
        }

        manager.exportAllGroups(dir);
    }

    private void assemble(Group g) {
        AssessmentView v = groupViewMap.get(g.getName() );
        g.setProgressList(v.getProgressListForSaving(true)); // TODO not trimming on export?
        g.setProgressSummaryList(v.getSummaries() );
        g.setVersion(Version.getInstance().getVersion());
    }

    public void showOverview(){

    }
}
