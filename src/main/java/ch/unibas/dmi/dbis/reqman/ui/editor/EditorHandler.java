package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import ch.unibas.dmi.dbis.reqman.storage.ReqmanFile;
import ch.unibas.dmi.dbis.reqman.ui.StatusBar;
import ch.unibas.dmi.dbis.reqman.ui.common.FilterActionHandler;
import ch.unibas.dmi.dbis.reqman.ui.common.FilterBar;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.reqman.ui.event.TargetEntity;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * Layer between (G)UI and internal logic.
 *
 * @author loris.sauter
 */
public class EditorHandler implements EventHandler<CUDEvent>, FilterActionHandler {
  
  private static final Logger LOGGER = LogManager.getLogger(EditorHandler.class);
  
  private EditorView editor = null;
  private StatusBar statusBar;
  
  private final static void throwInappropriateTargetEntity(TargetEntity entity) {
    throw new IllegalArgumentException("EditorHandler can only handle TargetEntity.CATALOGUE, TargetEntity.REQUIREMENT, TargetEntity.MILESTONE, but " + entity.toString() + " was given");
  }
  
  public void setStatusBar(StatusBar statusBar) {
    this.statusBar = statusBar;
  }
  
  @Override
  public void handle(CUDEvent event) {
    if (event != null) {
      if (CUDEvent.CREATION.equals(event.getEventType())) {
        handleCreation(event);
      } else if (CUDEvent.DELETION.equals(event.getEventType())) {
        handleDeletion(event);
      } else if (CUDEvent.MODIFICATION.equals(event.getEventType())) {
        handleModification(event);
      } else {
        throw new IllegalArgumentException("Cannot handle unknown event type: " + event.getEventType().toString());
      }
    }
    // Silently ignoring null events
    
  }
  
  /**
   * It must be guaranteed that the passed event is of type CREATION
   *
   * @param evt
   */
  public void handleCreation(CUDEvent evt) {
    if (!CUDEvent.CREATION.equals(evt.getEventType())) {
      throw new RuntimeException("Something went very bad: handleCreation handles non-creation event. Event: " + evt.toString());
    }
    switch (evt.getTargetEntity()) {
      case CATALOGUE:
        Catalogue cat = EditorPromptFactory.promptNewCatalogue();
        if (cat != null) {
          setupEditor();
        }
        break;
      case REQUIREMENT:
        Requirement req = EditorPromptFactory.promptNewRequirement(this);
        if (req == null) {
          return;
        }
        EntityController.getInstance().getObservableRequirements().add(req);
        LOGGER.debug("Created Req={}", req);
        LOGGER.debug("All Cat.Reqs={}", EntityController.getInstance().getCatalogue().getRequirements());
        LOGGER.debug("All ObsReqs={}", EntityController.getInstance().getObservableRequirements());
        break;
      case MILESTONE:
        Milestone ms = EditorPromptFactory.promptNewMilestone();
        if (ms == null) {
          return;
        }
        EntityController.getInstance().getObservableMilestones().add(ms);
        LOGGER.debug("Created MS={}", ms);
        LOGGER.debug("All Cat.MS={}", EntityController.getInstance().getCatalogue().getMilestones());
        LOGGER.debug("All ObsMS={}", EntityController.getInstance().getObservableMilestones());
        break;
      case COURSE:
        Course course = EditorPromptFactory.promptNewCourse();
        if (course != null) {
          setupEditor();
        }
        break;
      default:
        throwInappropriateTargetEntity(evt.getTargetEntity());
    }
  }
  
  public void handleDeletion(CUDEvent evt) {
    switch (evt.getTargetEntity()) {
      case REQUIREMENT:
        if (evt.getDelivery() instanceof RequirementTableView.ObservableRequirement) {
          RequirementTableView.ObservableRequirement obsReq = (RequirementTableView.ObservableRequirement) evt.getDelivery();
          Requirement req = obsReq.getRequirement();
          LOGGER.debug(String.format("Deletion Requirement: selected=%s, index=%d, result=%s", obsReq, evt.getIndex(), req));
          EntityController.getInstance().removeRequirement(req);
          LOGGER.debug("size: " + editor.getRequirementsView().getRequirementsSize());
        } else {
          // Most probably the event has no delivery -> thus nothing has to happen.
          if (evt.getDelivery() != null) {
            throw new RuntimeException("Something went really bad. DelReq with no non-null req");
          }
        }
        break;
      case MILESTONE:
        if (evt.getDelivery() instanceof Milestone) {
          Milestone milestone = (Milestone) evt.getDelivery();
          LOGGER.debug("Going to remove MS={}", milestone);
          EntityController.getInstance().removeMilestone(milestone);
        }
        break;
      default:
        throwInappropriateTargetEntity(evt.getTargetEntity());
    }
  }
  
  public void handleModification(CUDEvent evt) {
    
    switch (evt.getTargetEntity()) {
      case CATALOGUE:
        Catalogue oldCat = EntityController.getInstance().getCatalogue();
        Catalogue newCat = EditorPromptFactory.promptCatalogue(oldCat);
        LOGGER.debug("Modificaiton of catalaogue: old={}, new={}", oldCat, newCat);
        setupCourseCatalogueInfo();
        break;
      case COURSE:
        Course oldCourse = EntityController.getInstance().getCourse();
        Course newCourse = EditorPromptFactory.promptCourse(oldCourse);
        LOGGER.debug("Modification of course: old={}, new={}", oldCourse, newCourse);
        setupCourseCatalogueInfo();
        break;
      case REQUIREMENT:
        if (evt.getDelivery() instanceof RequirementTableView.ObservableRequirement) {
          RequirementTableView.ObservableRequirement obsReq = (RequirementTableView.ObservableRequirement) evt.getDelivery();
          Requirement r = obsReq.getRequirement();
          Requirement mod = EditorPromptFactory.promptRequirement(this, r);
          editor.getRequirementsView().updateRequirement(mod);
          // modification should happen in the same object, since java objects are always call by reference
          LOGGER.debug("hash(origin)={}, hash(modified)={} (O={}, M={})", r.hashCode(), mod.hashCode(), r, mod);
        }
        break;
      case MILESTONE:
        if (evt.getDelivery() instanceof Milestone) {
          Milestone mod = EditorPromptFactory.promptMilestone((Milestone) evt.getDelivery());
          editor.getMilestoneView().updateMilestone(mod);
          LOGGER.debug("Milestone got modified: {}", mod);
        }
        break;
      default:
        throwInappropriateTargetEntity(evt.getTargetEntity());
    }
  }
  
  public void setupEditor() {
    LOGGER.traceEntry();
    if (EntityController.getInstance().hasCatalogue()) {
      editor.getRequirementsView().setRequirements(EntityController.getInstance().getObservableRequirements(), EntityController.getInstance().getCatalogue());
      editor.getMilestoneView().setItems(EntityController.getInstance().getObservableMilestones());
    }
    
    setupCourseCatalogueInfo();
    
    editor.enableAll();
  }
  
  public void displayOnly(List<Requirement> requirementList) {
    LOGGER.trace("DisplayOnly{}", requirementList);
    if (EntityController.getInstance().hasCatalogue()) {
      editor.getRequirementsView().displayOnly(requirementList);
    }
  }
  
  public void displayAllRequirements() {
    LOGGER.trace("DisplayAllRequirements");
    if (EntityController.getInstance().hasCatalogue()) {
      editor.getRequirementsView().displayAll();
    }
  }
  
  public void saveCatalogue() {
    LOGGER.traceEntry();
    if (EntityController.getInstance().hasCatalogue() && EntityController.getInstance().isStorageManagerReady()) {
      LOGGER.debug("Save catalogeu");
      EntityController.getInstance().saveCatalogue();
    } else {
      LOGGER.debug("Cannot save [CATALOGUE]. Save as instead");
      saveAsCatalogue();
    }
  }
  
  
  public void saveAsCatalogue() {
    LOGGER.debug("Saving catalgoue as");
    if (EntityController.getInstance().hasCatalogue()) {
      DirectoryChooser dc = Utils.createDirectoryChooser("Save as");
      File dir = dc.showDialog(editor.getScene().getWindow());
      LOGGER.debug("Chosen dir={}", dir);
      if (dir != null) {
        EntityController.getInstance().setupSaveDirectory(dir);
        EntityController.getInstance().saveCatalogue();
      }
    } else {
      LOGGER.warn("Cannot save non-exist logger");
    }
  }
  
  public void saveCourse() {
    LOGGER.debug("SaveCourse");
    if (EntityController.getInstance().hasCourse() && EntityController.getInstance().isStorageManagerReady()) {
      LOGGER.debug("Save course");
      EntityController.getInstance().saveCourse();
    } else {
      LOGGER.debug("Cannot save [COURSE]. SaveAs instead");
      saveAsCourse();
    }
  }
  
  public void openCourse() {
    LOGGER.debug("Open course");
    FileChooser fc = Utils.createFileChooser("Open Course");
    fc.getExtensionFilters().add(ReqmanFile.Type.COURSE.getExtensionFilter());
    
    LOGGER.debug("Filter.desc={}", fc.getExtensionFilters().get(0).getDescription());
    LOGGER.debug("Filter.ext={}", fc.getExtensionFilters().get(0).getExtensions());
    
    LOGGER.debug("fc={}", fc);
    LOGGER.debug("Editor={}", editor);
    Window w = null;
    File f = fc.showOpenDialog(w);
    if (f != null) {
      EntityController.getInstance().openCourse(f);
    }
  }
  
  public void saveAsCourse() {
    LOGGER.debug("SaveAsCourse");
    if (EntityController.getInstance().hasCourse()) {
      DirectoryChooser dc = Utils.createDirectoryChooser("Save as");
      File dir = dc.showDialog(editor.getScene().getWindow());
      if (dir != null) {
        LOGGER.debug("SaveCourse as..");
        EntityController.getInstance().setupSaveDirectory(dir);
        EntityController.getInstance().saveCourse();
      }
    }
  }
  
  public boolean isCatalogueLoaded() {
    return EntityController.getInstance().hasCatalogue();
  }
  
  
  public Milestone getSelectedMS() {
    return editor.getMilestoneView().getSelectedMS();
  }
  
  public Requirement getSelectedRequirement() {
    return editor.getRequirementsView().getSelectedRequirement();
  }
  
  public void closeFilterBar() {
    editor.closeFilterBar();
  }
  
  public void showFilterBar() {
    editor.showFilterBar();
  }
  
  @Override
  public int applyFilter(String pattern, FilterBar.Mode mode) {
    List<Requirement> filtered = null;
    
    switch (mode) {
      case NAME:
        filtered = EntityController.getInstance().getCatalogueAnalyser().findRequirementsNameContains(pattern);
        break;
      case TEXT:
        filtered = EntityController.getInstance().getCatalogueAnalyser().findRequirementsContaining(pattern);
        break;
      case CATEGORY:
        filtered = EntityController.getInstance().getCatalogueAnalyser().findRequirementsForCategory(pattern);
        break;
    }
    if (filtered == null) {
      return 0;
    }
    displayOnly(filtered);
    return filtered.size();
  }
  
  
  @Override
  public void resetFilter() {
    displayAllRequirements();
  }
  
  public void showStatistics() {
    editor.showStatistics();
  }
  
  void setEditorView(EditorView view) {
    this.editor = view;
  }
  
  private void setupCourseCatalogueInfo() {
    LOGGER.debug("setupCatalogueInfo called");
    editor.getCourseInfoView().refresh();
  }
}
