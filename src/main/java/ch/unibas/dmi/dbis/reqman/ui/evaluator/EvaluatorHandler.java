package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.Callback;
import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Progress;
import ch.unibas.dmi.dbis.reqman.ui.MenuManager;
import ch.unibas.dmi.dbis.reqman.ui.StatusBar;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorHandler implements EventHandler<CUDEvent> {
  
  private final static Logger LOGGER = LogManager.getLogger(EvaluatorHandler.class);
  
  /**
   * A reference to the actual UI this class controls
   */
  private EvaluatorView evaluator;
  
  /**
   * A map of group ids - assessment UI
   */
  private HashMap<UUID, AssessmentView> assessmentViewMap = new HashMap<>();
  
  /**
   * A map of group ids - booleans to indicate if it was saved or not
   */
  private HashMap<UUID, Boolean> unsavedGroupsMap = new HashMap<>();
  
  @Deprecated
  private HashMap<String, AssessmentView> groupViewMap = new HashMap<>();
  @Deprecated
  private HashMap<String, Boolean> dirtyMap = new HashMap<>();
  
  private Milestone activeMS = null;
  
  private StatusBar statusBar;
  
  private Callback firstGroupCallback = null;
  
  public EvaluatorHandler() {
  
  }
  
  public void setStatusBar(StatusBar statusBar) {
    this.statusBar = statusBar;
  }
  
  public List<Milestone> getMilestones() {
    return new ArrayList<>(EntityController.getInstance().getObservableMilestones());
  }
  
  
  public boolean isCatalogueLoaded() {
    return EntityController.getInstance().hasCatalogue();
  }
  
  public Catalogue getCatalogue() {
    return EntityController.getInstance().getCatalogue();
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
    LOGGER.traceEntry();
    // TODO
    return;
    /*
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
        CUDEvent del = CUDEvent.generateDeletionEvent(event, TargetEntity.GROUP, -1, gr);
        handleDeletion(del);
        CUDEvent add = CUDEvent.generateCreationEvent(event, TargetEntity.GROUP, mod);
        handleCreation(add);
        LOGGER.trace(":handleModification - Done");
        break;
      default:
        // Ignoring
    }*/
  }
  
  public void handleDeletion(CUDEvent event) {
    return;
    // TODO
    /*
    switch (event.getTargetEntity()) {
      case GROUP:
        // DELETE GROUP
        LOGGER.trace(":handleDeletion");
        if (event.getDelivery() != null && event.getDelivery() instanceof Group) {
          Group del = (Group) event.getDelivery();
          if (manager.removeGroup(del)) {
            removeGroupFromMap(del);
          }
          LOGGER.debug(":handleDeletion - Remaining: " + manager.groupList().toString());
        }
        break;
      default:
        // Ignoring
    }*/
  }
  
  public void setOnFirstGroup(Callback callback) {
    this.firstGroupCallback = callback;
  }
  
  public void handleCreation(CUDEvent event) {
    switch (event.getTargetEntity()) {
      case GROUP:
        Group gr;
        LOGGER.trace(":handleCreation");
        if (event.getDelivery() instanceof Group) {
          LOGGER.trace(":handleCreation - re-create");
          gr = (Group) event.getDelivery();
        } else {
          LOGGER.trace(":handleCreation - new create");
          gr = EvaluatorPromptFactory.promptGroup(this);
        }
        handleAddGroup(gr);
        break;
      default:
        // Ignoring
    }
  }
  
  public void handleOpenGroups(ActionEvent actionEvent) {
    if (!EntityController.getInstance().hasCatalogue()) {
      return;
    }
    throw new UnsupportedOperationException("Not implemented yet");
    /*
    FileChooser fc = Utils.createGroupFileChooser("Open");
    if (manager.hasLastOpenLocation()) {
      fc.setInitialDirectory(manager.getLastOpenLocation());
    }
    List<File> files = fc.showOpenMultipleDialog(evaluator.getScene().getWindow());
    if (files == null) {
      return; // USER ABORT
    }
    if (files.size() == 1) {
      if (!manager.isAnyGroupFilePresent(files).isEmpty()) {
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
        loadGroupUI(list);
        handleFirstGroupPresent();
      }, this::handleOpenGroupException);
    }
    // USER ABORT
    */
  }
  
  public void handleSaveGroup(ActionEvent actionEvent) {
    throw new UnsupportedOperationException("Not implemented yet");
    /*
    Group active = evaluator.getActiveGroup();
    if (manager.hasGroupFile(active)) {
      assemble(active);
      manager.saveGroup(active);
    } else {
      handleSaveGroupAs(actionEvent);
    }
    */
  }
  
  public void handleSaveGroupAs(ActionEvent event) {
    throw new UnsupportedOperationException("Not implemented yet");
    /*
    FileChooser fc = Utils.createGroupFileChooser("Save As");
    if (manager.hasLastSaveLocation()) {
      fc.setInitialDirectory(manager.getLastSaveLocation());
    }
    File f = fc.showSaveDialog(evaluator.getScene().getWindow());
    if (f != null) {
      assemble(evaluator.getActiveGroup());
      manager.saveGroupAs(evaluator.getActiveGroup(), f);
    }
    */
  }
  
  public void reloadRequirements() {
    groupViewMap.values().forEach(av -> {
      av.reloadRequirements(true);
    });
  }
  
  public void processCatalogueOpened(Catalogue cat) {
    LOGGER.traceEntry("Param: {}", cat);
    LOGGER.info("Opened catalogue ");
    evaluator.enableAll();
    MenuManager.getInstance().setupGlobalMilestoneMenu(this.getMilestones());
    MenuManager.getInstance().enableCatalogueNeeded();
  }
  
  public void setGlobalMilestoneChoice(Milestone ms) {
    LOGGER.traceEntry("Ms: {}", ms);
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
  
  public ObservableList<Progress> progressList(Group g) {
    return EntityController.getInstance().getObservableProgress(g);
  }
  
  
  public Group getGroupByName(String name) {
    for (Group g : EntityController.getInstance().groupList()) {
      if (g.getName().equals(name)) {
        return g;
      }
    }
    return null;
  }
  
  public void exportAllGroups() {
    throw new UnsupportedOperationException("Not implemented yet");
    /*
    LOGGER.traceEntry();
    if (!isCatalogueLoaded()) {
      LOGGER.debug(":exportAllGroups - No catalogue set. Returning");
      return;
    }
    DirectoryChooser dc = new DirectoryChooser();
    if (manager.hasLastExportLocation()) {
      dc.setInitialDirectory(manager.getLastExportLocation());
    }
    dc.setTitle("Choose an export folder");
    File dir = dc.showDialog(evaluator.getScene().getWindow());
    
    for (Group g : manager.groupList()) {
      assemble(g);
    }
    
    manager.exportAllGroups(dir);
    */
  }
  
  public void showOverview() {
    throw new UnsupportedOperationException("Not implemented yet");
    /*
    if (!manager.isCatalogueLoaded() || (manager.groupList() == null || manager.groupList().isEmpty())) {
      return;
    }
    SimpleOverviewBuilder overview = new SimpleOverviewBuilder(manager.getCatalogue(), manager.groupList());
    String export = overview.exportOverviewHTML();
    WebView view = new WebView();
    WebEngine engine = view.getEngine();
    engine.loadContent(export);
    HBox box = new HBox();
    view.prefWidthProperty().bind(box.widthProperty());
    view.prefHeightProperty().bind(box.heightProperty());
    Scene webScene = new Scene(box, evaluator.getWidth(), evaluator.getHeight());
    box.prefWidthProperty().bind(webScene.widthProperty());
    box.prefHeightProperty().bind(webScene.heightProperty());
    box.getChildren().add(view);
    PopupStage popupStage = new PopupStage("Overview", webScene);
    popupStage.showAndWait();
    */
  }
  
  public void stop() {
    return;
    // Silently ignoring
    // TODO
    /*
    LOGGER.traceEntry();
    manager.groupList().forEach(g -> {
      if (isDirty(g)) {
        manager.saveAsBackup(g);
      }
    });*/
  }
  
  public boolean isDirty(Group group) {
    return dirtyMap.containsKey(group.getName()) && dirtyMap.get(group.getName());
  }
  
  public boolean isGroupLoaded() {
    return EntityController.getInstance().hasGroups();
  }
  
  public void enableEvaluator() {
    evaluator.enableAll();
  }
  
  public void refreshCourseInfoView() {
    evaluator.refreshCourseInfoView();
  }
  
  @Deprecated
  boolean isGroupNameUnique(String name) {
    return true;
  }
  
  @Deprecated
  Milestone getMilestoneByOrdinal(int ordinal) {
    return null;
  }
  
  void setEvaluatorView(EvaluatorView view) {
    this.evaluator = view;
  }
  
  ObservableList<Group> groupList() {
    return EntityController.getInstance().groupList();
  }
  
  void markDirty(Group activeGroup) {
    dirtyMap.put(activeGroup.getName(), true);
    evaluator.markDirty(activeGroup);
  }
  
  void unmarkDirty(Group activeGroup) {
    if (dirtyMap.containsKey(activeGroup.getName())) {
      dirtyMap.put(activeGroup.getName(), false);
    }
    evaluator.unmarkDirty(activeGroup);
  }
  
  void openGroupTab(Group group) {
    addTab(group, false);
  }
  
  private void handleAddGroup(Group gr) {
    LOGGER.traceEntry();
    if (gr == null) {
      LOGGER.trace(":handleAddGroup - new create");
      gr = EvaluatorPromptFactory.promptGroup(this);
    } else {
      LOGGER.trace(":handleAddGroup - re-create");
    }
    LOGGER.entry(gr);
    // ADD GROUP
    loadGroupUI(gr);
    
    handleFirstGroupPresent();
  }
  
  private void handleFirstGroupPresent() {
    LOGGER.traceEntry();
    if (EntityController.getInstance().groupList().size() >= 1) {
      LOGGER.trace(":handleFirstGroupPresent" + " - First group");
      if (firstGroupCallback != null) {
        firstGroupCallback.call();
      } else {
        LOGGER.debug(":handleFirstGroupPresent - No callback set");
      }
    }
  }
  
  private void handleOpenGroupException(Exception ex) {
    Utils.showErrorDialog("Open group(s) failed", "Could not open group due to: \n" + ex.getMessage());
  }
  
  private void loadGroupUI(List<Group> groups) {
    groups.forEach(this::loadGroupUI);
  }
  
  private void loadGroupUI(Group g) {
    LOGGER.traceEntry("Group: {}", g);
    /*
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
    */
    addGroupToMap(g, null);
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
  
  private void removeGroupFromMap(Group group) {
    groupViewMap.remove(group.getName());
    removeTab(group);
    dirtyMap.remove(group.getName());
  }
  
  private void removeTab(Group g) {
    evaluator.removeTab(g);
  }
  
  private void addTab(Group active, boolean fresh) {
    if (evaluator.isGroupTabbed(active)) {
      // Do not open another tab for already tabbed group.
    } else {
      evaluator.addGroupTab(groupViewMap.get(active.getName()), fresh);
    }
    evaluator.setActiveTab(active.getName());
  }
  
  private void assemble(Group g) {
    AssessmentView v = groupViewMap.get(g.getName());
    LOGGER.trace(":assemble");
    LOGGER.entry(g);
    g.setProgressList(v.getProgressListForSaving(true)); // TODO not trimming on export?
    LOGGER.debug("Set progress list");
    g.setProgressSummaryList(v.getSummaries());
    LOGGER.debug("Set summaries");
    g.setVersion(Version.getInstance().getVersion());
  }
}
