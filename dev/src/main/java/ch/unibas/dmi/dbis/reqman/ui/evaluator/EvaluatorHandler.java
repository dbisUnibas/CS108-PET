package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.Callback;
import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.core.*;
import ch.unibas.dmi.dbis.reqman.management.CatalogueNameMismatchException;
import ch.unibas.dmi.dbis.reqman.management.EntityManager;
import ch.unibas.dmi.dbis.reqman.management.NonUniqueGroupNameException;
import ch.unibas.dmi.dbis.reqman.ui.MenuManager;
import ch.unibas.dmi.dbis.reqman.ui.StatusBar;
import ch.unibas.dmi.dbis.reqman.ui.common.PopupStage;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.reqman.ui.event.TargetEntity;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
    private HashMap<String, Boolean> dirtyMap = new HashMap<>();
    private Milestone activeMS = null;
    private StatusBar statusBar;

    public EvaluatorHandler() {
        LOGGER.trace("<init>");
        openBackups();
    }

    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    public StatusBar getStatusBar() {
        return statusBar;
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

    private Callback firstGroupCallback = null;

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
                assemble(gr);
                // DONT FORGET TO UPDATE ALL NAME REFERNECES, IF NAME CHANGED!
                Group mod = EvaluatorPromptFactory.promptGroup(gr, this);
                mod.setProgressList(gr.getProgressList());
                mod.setVersion(gr.getVersion());
                mod.setProgressSummaryList(gr.getProgressSummaries());
                //manager.replaceGroup(gr, mod);
                CUDEvent del =CUDEvent.generateDeletionEvent(event, TargetEntity.GROUP,-1, gr);
                handleDeletion(del);
                CUDEvent add =CUDEvent.generateCreationEvent(event, TargetEntity.GROUP, mod);
                handleCreation(add);
                LOGGER.trace(":handleModification - Done");
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
                    if(manager.removeGroup(del) ){
                        removeGroupFromMap(del);
                    }
                    LOGGER.debug(":handleDeletion - Remaining: "+manager.groupList().toString());
                }
                break;
            default:
                // Ignoring
        }
    }


    private void handleAddGroup(Group gr){
        LOGGER.trace(":handleAddGroup");
        if(gr == null){
            LOGGER.trace(":handleAddGroup - new create");
            gr = EvaluatorPromptFactory.promptGroup(this);
        }else{
            LOGGER.trace(":handleAddGroup - re-create");
        }
        LOGGER.entry(gr);
        // ADD GROUP
        manager.addGroup(gr);
        loadGroupUI(gr);

        handleFirstGroupPresent();
    }

    private void handleFirstGroupPresent(){
        LOGGER.trace(":handleFirstGroupPresent");
        if(manager.groupList().size() == 1){
            LOGGER.trace(":handleFirstGroupPresent"+" - First group");
            if(firstGroupCallback != null){
                firstGroupCallback.call();
            }else{
                LOGGER.debug(":handleFirstGroupPresent - No callback set");
            }
        }
    }

    public void setOnFirstGroup(Callback callback){
        this.firstGroupCallback = callback;
    }

    public void handleCreation(CUDEvent event) {
        switch (event.getTargetEntity()) {
            case GROUP:
                Group gr;
                LOGGER.trace(":handleCreation");
                if(event.getDelivery() instanceof  Group){
                    LOGGER.trace(":handleCreation - re-create");
                    gr = (Group)event.getDelivery();
                }else{
                    LOGGER.trace(":handleCreation - new create");
                    gr = EvaluatorPromptFactory.promptGroup(this);
                }
                handleAddGroup(gr);
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
            if(!manager.isAnyGroupFilePresent(files).isEmpty()){
                Utils.showErrorDialog("Duplicate group files", "Cannot open a group twice");
                return;
            }
            manager.openGroup(files.get(0), (g) -> {
                handleFirstGroupPresent();
                loadGroupUI(g);
            }, this::handleOpenGroupException);

        } else if (files.size() >= 2) {
            List<File> dupes = manager.isAnyGroupFilePresent(files);
            List<File> noDupes = new ArrayList<>(files);
            noDupes.removeAll(dupes);
            manager.openGroups(noDupes, (list) -> {
                handleFirstGroupPresent();
                loadGroupUI(list);
            }, this::handleOpenGroupException);
        }
        // USER ABORT
    }

    private void handleOpenGroupException(Exception ex){
        Utils.showErrorDialog("Open group(s) failed", "Could not open group due to: \n"+ex.getMessage());
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
        FileChooser fc = Utils.createCatalogueFileChooser("Load");
        if (manager.hasLastOpenLocation()) {
            fc.setInitialDirectory(manager.getLastOpenLocation());
        }
        File f = fc.showOpenDialog(evaluator.getScene().getWindow());
        if (f != null) {
            manager.openCatalogue(f, this::processCatalogueOpened);
        }
    }

    public void processCatalogueOpened(Catalogue cat) {
        LOGGER.info("Opened catalogue " + manager.getCatalogueFile().getPath());
        LOGGER.trace("Enabling all");
        evaluator.enableAll();
        MenuManager.getInstance().setupGlobalMilestoneMenu(this.getMilestones());
        MenuManager.getInstance().enableCatalogueNeeded();
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

    private void removeGroupFromMap(Group group){
        groupViewMap.remove(group.getName() );
        removeTab(group);
        dirtyMap.remove(group.getName() );
    }

    private void removeTab(Group g){
        evaluator.removeTab(g);
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
        dirtyMap.put(activeGroup.getName(), true);
        evaluator.markDirty(activeGroup);
    }

    void unmarkDirty(Group activeGroup) {
        if(dirtyMap.containsKey(activeGroup.getName() ) ){
            dirtyMap.put(activeGroup.getName(), false);
        }
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

    public Progress getProgressForRequirement(Group group, Requirement requirement) {
        return manager.getProgressForRequirement(group, requirement);
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
        LOGGER.trace(":assemble");
        LOGGER.entry(g);
        g.setProgressList(v.getProgressListForSaving(true)); // TODO not trimming on export?
        LOGGER.debug("Set progress list");
        g.setProgressSummaryList(v.getSummaries() );
        LOGGER.debug("Set summaries");
        g.setVersion(Version.getInstance().getVersion());
    }

    public void showOverview(){
        if(!manager.isCatalogueLoaded() || (manager.groupList() == null || manager.groupList().isEmpty())){
            return;
        }
        SimpleOverviewBuilder overview = new SimpleOverviewBuilder(manager.getCatalogue(), manager.groupList());
        String export = overview.exportOverviewHTML();
        WebView view = new WebView();
        WebEngine engine = view.getEngine();
        engine.loadContent(export);
        HBox box = new HBox();
        view.prefWidthProperty().bind(box.widthProperty() );
        view.prefHeightProperty().bind(box.heightProperty());
        Scene webScene = new Scene(box, evaluator.getWidth(), evaluator.getHeight());
        box.prefWidthProperty().bind(webScene.widthProperty());
        box.prefHeightProperty().bind(webScene.heightProperty());
        box.getChildren().add(view);
        PopupStage popupStage = new PopupStage("Overview", webScene);
        popupStage.showAndWait();
    }

    public void stop(){
        manager.groupList().forEach(g -> {
            if(isDirty(g)){
                manager.saveAsBackup(g);
            }
        });
    }

    public boolean isDirty(Group group){
        return dirtyMap.containsKey(group.getName() ) && dirtyMap.get(group.getName());
    }

    public void openBackups(){
        manager.openBackupsIfExistent(list -> {
            if(!list.isEmpty()){
                MenuManager.getInstance().enableGroupNeeded();
                evaluator.enableAll();
            }
            list.forEach(obj -> {
                LOGGER.trace(":openBackups - Aftermath: Processing: "+obj.toString());
                if(!obj.isCatalogue()){
                    loadGroupUI(obj.getGroup());
                    markDirty(obj.getGroup() );
                }
            });
        });
    }

    public boolean isGroupLoaded() {
        return !manager.groupList().isEmpty();
    }

    public void enableEvalautor() {
        evaluator.enableAll();
    }
}
