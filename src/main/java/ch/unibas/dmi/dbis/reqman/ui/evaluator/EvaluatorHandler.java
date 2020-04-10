package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.common.Callback;
import ch.unibas.dmi.dbis.reqman.common.EntityAlreadyOpenException;
import ch.unibas.dmi.dbis.reqman.common.MissingEntityException;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import ch.unibas.dmi.dbis.reqman.storage.ReqmanFile;
import ch.unibas.dmi.dbis.reqman.storage.UuidMismatchException;
import ch.unibas.dmi.dbis.reqman.ui.StatusBar;
import ch.unibas.dmi.dbis.reqman.ui.common.FilterActionHandler;
import ch.unibas.dmi.dbis.reqman.ui.common.FilterBar;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorHandler implements EventHandler<CUDEvent>, FilterActionHandler {
  
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
  
  private StatusBar statusBar;
  
  private Callback firstGroupCallback = null;
  
  public EvaluatorHandler() {
  
  }
  
  public void setStatusBar(StatusBar statusBar) {
    this.statusBar = statusBar;
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
    switch (event.getTargetEntity()) {
      case GROUP:
        LOGGER.trace(":handleModificaiton");
        Group gr = EntityController.getInstance().getGroup(evaluator.getActiveGroupUUID());
        EvaluatorPromptFactory.promptGroup(gr);
        evaluator.updateDisplayOf(gr);
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
          evaluator.removeTab(del);
          EntityController.getInstance().removeGroup(del);
          LOGGER.debug(":handleDeletion<Group> - Remaining: {}", EntityController.getInstance().groupList());
        }
        break;
      default:
        // Ignoring
    }
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
          LOGGER.warn(":handleCreation - re-create. THIS SHOULD NOT BE CALLED");
          return;
        } else {
          LOGGER.debug(":handleCreation - new create");
          gr = EvaluatorPromptFactory.promptGroup();
          if (gr == null) {
            LOGGER.debug("User aborted creation of group");
            return;
          }
        }
        handleAddGroup(gr);
        break;
      default:
        // Ignoring
    }
  }
  
  public void showFilterBar() {
    evaluator.showFilterBar();
  }
  
  @Override
  public int applyFilter(String pattern, FilterBar.Mode mode) {
    CatalogueAnalyser analyser = EntityController.getInstance().getCatalogueAnalyser();
    List<Requirement> requirements = null;
    
    switch (mode) {
      case NAME:
        requirements = analyser.findRequirementsNameContains(pattern);
        break;
      case TEXT:
        requirements = analyser.findRequirementsContaining(pattern);
        break;
      case CATEGORY:
        requirements = analyser.findRequirementsForCategory(pattern);
        break;
      case TYPE:
        // Should not happen
        break;
    }
    
    assessmentViewMap.get(evaluator.getActiveGroupUUID()).displayProgressViews(requirements);
    return requirements.size();
  }
  
  @Override
  public int applyFilter(Requirement.Type type) {
    CatalogueAnalyser analyser = EntityController.getInstance().getCatalogueAnalyser();
    List<Requirement> requirements = analyser.findRequirementsByType(type);
    assessmentViewMap.get(evaluator.getActiveGroupUUID()).displayProgressViews(requirements);
    return requirements.size();
  }
  
  @Override
  public void resetFilter() {
    assessmentViewMap.get(evaluator.getActiveGroupUUID()).displayAll();
  }
  
  public void resetFilterForAll() {
    assessmentViewMap.values().forEach(AssessmentView::displayAll);
  }
  
  public void closeAll() {
    LOGGER.info("Close All");
    reset();
    evaluator.closeAll();
  }
  
  public Group getActiveGroup() {
    return EntityController.getInstance().getGroup(evaluator.getActiveGroupUUID());
  }
  
  private boolean active;
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public void closeFilterBar() {
    evaluator.closeFilterBar();
  }
  
  public void loadBackups() {
  
  }
  
  private void reset() {
    this.assessmentViewMap.clear();
  }
  
  private void handleAddGroup(Group group) {
    handleAddGroup(group, false);
  }
  
  public void handleSplit(ActionEvent event) {
    LOGGER.debug("Handling splitting");
    Group split = EvaluatorPromptFactory.promptSplit();
    if (split == null) {
      // User abort?!
      LOGGER.debug("User abort");
      return;
    }
    handleAddGroup(split, true);
  }
  
  public void handleOpenGroups(ActionEvent actionEvent) {
    if (!EntityController.getInstance().hasCatalogue()) {
      return;
    }
    FileChooser fc = Utils.createFileChooser("Open Groups");
    if (EntityController.getInstance().getStorageManager() != null && EntityController.getInstance().getStorageManager().getSaveDir() != null) {
      fc.setInitialDirectory(EntityController.getInstance().getStorageManager().getSaveDir());
    }
    fc.getExtensionFilters().add(ReqmanFile.Type.GROUP.getExtensionFilter());
    List<File> files = fc.showOpenMultipleDialog(evaluator.getScene().getWindow());
    if (files == null) {
      return; // user abort
    } else {
      List<Group> groups = null;
      try {
        groups = EntityController.getInstance().openGroups(files);
      } catch (EntityAlreadyOpenException ex) {
        Utils.showErrorDialog(ex.getEntityType() + " already open", ex.getEntityType() + " already open", "Cannot open the entity of type " + ex.getEntityType() + " and uuid=" + ex.getUuid().toString() + " more than once.");
      } catch (UuidMismatchException | IOException e) {
        LOGGER.catching(e);
        Utils.showErrorDialog("Exception while opening groups", "The following exception was caught:\n\t" + e.getMessage());
        return;
      } catch (MissingEntityException e) {
        Utils.showErrorDialog("Corrupt group file", "The group file of group (" + e.getEntity().getName() + ") is corrupt.\n" +
            "The entity has no or an empty field '" + e.getMissing() + "'.\n" +
            "Please restart ReqMan to avoid further corrupt files.");
      }
      groups.forEach(this::loadGroupUIAndRefresh);
    }
  }
  
  public void handleSaveGroup(ActionEvent actionEvent) {
    UUID groupID = evaluator.getActiveGroupUUID();
    LOGGER.debug("Saving group with id {}", groupID);
    Group gr = EntityController.getInstance().getGroup(groupID);
    if (EntityController.getInstance().getStorageManager().hasGroupSaveFile(groupID)) {
      EntityController.getInstance().saveGroup(groupID);
      LOGGER.info("Saved group {}", groupID);
    } else {
      LOGGER.debug("No save file for group {} found. Using current save dir");
      Group g = EntityController.getInstance().getGroup(groupID);
      EntityController.getInstance().saveGroupAs(g);
      LOGGER.info("Group {} saved.", g.getName());
    }
    evaluator.unmarkDirty(gr);
    assessmentViewMap.get(gr.getUuid()).unmarkDirty();
    Notifications.create().title("Export successful!").hideAfter(Duration.seconds(5)).text(String.format("Group '%s' saved", EntityController.getInstance().getGroup(groupID).getName())).showInformation();
  }
  
  public void handleSaveGroupAs(ActionEvent event) {
    UUID groupID = evaluator.getActiveGroupUUID();
    Group g = EntityController.getInstance().getGroup(groupID);
    if (g == null) {
      LOGGER.error("Cannot save a null-group. Ignoring.");
    } else {
      LOGGER.debug("Saving group ({}) as...", g.getName());
      DirectoryChooser dc = Utils.createDirectoryChooser("Save as");
      File dir = dc.showDialog(evaluator.getScene().getWindow());
      LOGGER.debug("Chosen dir={}", dir);
      EntityController.getInstance().setupSaveDirectory(dir);
      EntityController.getInstance().saveGroupAs(g);
      evaluator.unmarkDirty(g);
      Notifications.create().title("Export successful!").hideAfter(Duration.seconds(5)).text(String.format("Group '%s' saved", g.getName())).showInformation();
    }
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
  
  public void stop() {
    return;
    // Silently ignoring
    // TODO Re-Implement backups
    /*
    LOGGER.traceEntry();
    manager.groupList().forEach(g -> {
      if (isDirty(g)) {
        manager.saveAsBackup(g);
      }
    });*/
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
  
  void openGroupTab(Group group) {
    if (evaluator.isGroupTabbed(group)) {
      LOGGER.debug("Not re-adding groupt tab if group {} already open", group.getName());
    } else {
      addTab(group, false);
    }
  }
  
  private void handleAddGroup(Group gr, boolean recalulate) {
    LOGGER.traceEntry();
    if (gr == null) {
      LOGGER.trace(":handleAddGroup - new create");
      gr = EvaluatorPromptFactory.promptGroup();
    } else {
      LOGGER.trace(":handleAddGroup - re-create");
    }
    LOGGER.entry(gr);
    // ADD GROUP
    loadGroupUI(gr, recalulate);
    
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
    groups.forEach(this::loadGroupUIAndRefresh);
  }
  
  private void loadGroupUI(Group g) {
    loadGroupUI(g, false);
  }
  
  private void loadGroupUIAndRefresh(Group g) {
    loadGroupUI(g, true);
  }
  
  private void loadGroupUI(Group g, boolean recalculate) {
    LOGGER.traceEntry("Group: {}", g);
    if (recalculate) {
      addTabAndRefresh(g);
    } else {
      addTab(g, false);
    }
    
  }
  
  public void showStatistics() {
    evaluator.showStatistics(assessmentViewMap);
  }
  
  public void addTabAndRefresh(Group group) {
    AssessmentView av = getAssessmentView(group);
    av.recalculatePoints();
    if (evaluator.isGroupTabbed(group)) {
      // Do not open another tab for alraedy tabbed group
      av.recalculatePoints();
    } else {
      evaluator.addGroupTab(av, false);
    }
    evaluator.setActiveTab(group);
  }
  
  @NotNull
  private AssessmentView getAssessmentView(Group group) {
    AssessmentView av = assessmentViewMap.get(group.getUuid());
    if (av == null) {
      av = createAssessmentView(group);
      assessmentViewMap.put(group.getUuid(), av);
    }
    return av;
  }
  
  
  private void addTab(Group active, boolean fresh) {
    AssessmentView av = getAssessmentView(active);
    if (evaluator.isGroupTabbed(active)) {
      // Do not open another tab for already tabbed group.
      av.recalculatePoints();
    } else {
      
      evaluator.addGroupTab(av, fresh);
    }
    evaluator.setActiveTab(active);
  }
  
  private AssessmentView createAssessmentView(Group group) {
    return new AssessmentView(group);
  }
  
}
