package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.common.Callback;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class EvaluatorHandler implements EventHandler<CUDEvent>, FilterActionHandler{
  
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
    return;
    // TODO Re-Implemet deletion of group
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
          LOGGER.warn(":handleCreation - re-create. THIS SHOULD NOT BE CALLED");
          return;
        } else {
          LOGGER.debug(":handleCreation - new create");
          gr = EvaluatorPromptFactory.promptGroup();
          if(gr == null){
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
    
    switch(mode){
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
  
  private void handleAddGroup(Group group){
    handleAddGroup(group, false);
  }
  
  public void handleSplit(ActionEvent event){
    LOGGER.debug("Handling splitting");
    Group split = EvaluatorPromptFactory.promptSplit();
    if(split == null){
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
    if(EntityController.getInstance().getStorageManager() != null && EntityController.getInstance().getStorageManager().getSaveDir() != null){
      fc.setInitialDirectory(EntityController.getInstance().getStorageManager().getSaveDir());
    }
    fc.getExtensionFilters().add(ReqmanFile.Type.GROUP.getExtensionFilter());
    List<File> files = fc.showOpenMultipleDialog(evaluator.getScene().getWindow() );
    if(files == null){
      return; // user abort
    }else{
      List<Group> groups = null;
      try {
        groups = EntityController.getInstance().openGroups(files);
      } catch (UuidMismatchException | IOException e) {
        LOGGER.catching(e);
        Utils.showErrorDialog("Exception while opening groups", "The following exception was caught:\n\t"+e.getMessage());
        return;
      }
      groups.forEach(this::loadGroupUIAndRefresh);
    }
  }
  
  public void handleSaveGroup(ActionEvent actionEvent) {
    UUID groupID = evaluator.getActiveGroupUUID();
    LOGGER.debug("Saving group with id {}", groupID);
    if(EntityController.getInstance().getStorageManager().hasGroupSaveFile(groupID)){
      EntityController.getInstance().saveGroup(groupID);
      LOGGER.info("Saved group {}", groupID);
    }else{
      LOGGER.debug("No save file for group {} found. Using current save dir");
      Group g = EntityController.getInstance().getGroup(groupID);
      EntityController.getInstance().saveGroupAs(g);
      LOGGER.info("Group {} saved.", g.getName());
    }
    
  }
  
  public void handleSaveGroupAs(ActionEvent event) {
    UUID groupID = evaluator.getActiveGroupUUID();
    Group g = EntityController.getInstance().getGroup(groupID);
    if(g == null){
      LOGGER.error("Cannot save a null-group. Ignoring.");
    }else{
      LOGGER.debug("Saving group ({}) as...", g.getName());
      DirectoryChooser dc = Utils.createDirectoryChooser("Save as");
      File dir = dc.showDialog(evaluator.getScene().getWindow());
      LOGGER.debug("Chosen dir={}", dir);
      EntityController.getInstance().setupSaveDirectory(dir);
      EntityController.getInstance().saveGroupAs(g);
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
    // TODO Re-Implement backups
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
    if(evaluator.isGroupTabbed(group)){
      LOGGER.debug("Not re-adding groupt tab if group {} already open", group.getName());
    }else{
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
  
  private void loadGroupUI(Group g){
    loadGroupUI(g, false);
  }
  
  private void loadGroupUIAndRefresh(Group g){
    loadGroupUI(g, true);
  }
  
  private void loadGroupUI(Group g, boolean recalculate) {
    LOGGER.traceEntry("Group: {}", g);
    if(recalculate){
      addTabAndRefresh(g);
    }else{
      addTab(g, false);
    }
    
  }
  
  private void addGroupToMap(Group group, AssessmentView view) {
    if (view != null) {
      LOGGER.trace(":addGroupToMap - Adding pre-existing AV");
      groupViewMap.put(group.getName(), view);
      if (activeMS != null) {
        LOGGER.trace(":addGroupToInternalStorage - Selecting activeMS: " + activeMS.getName());
        
      }
    } else {
      if (activeMS != null) {
        LOGGER.trace(":addGroupToMap - Creating AV with pre-set MS: " + activeMS.getName());
        
      } else {
        LOGGER.trace(":addGroupToMap - Creating AV without pre-set MS");
        
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
  
  private void addTabAndRefresh(Group group){
    AssessmentView av = getAssessmentView(group);
    av.recalculatePoints();
    if(evaluator.isGroupTabbed(group)){
      // Do not open another tab for alraedy tabbed group
      av.recalculatePoints();
    }else{
      evaluator.addGroupTab(av, false);
    }
    evaluator.setActiveTab(group);
  }
  
  @NotNull
  private AssessmentView getAssessmentView(Group group) {
    AssessmentView av = assessmentViewMap.get(group.getUuid());
    if(av == null){
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
