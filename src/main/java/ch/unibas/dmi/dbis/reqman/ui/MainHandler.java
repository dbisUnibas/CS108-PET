package ch.unibas.dmi.dbis.reqman.ui;

import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.management.OperationFactory;
import ch.unibas.dmi.dbis.reqman.storage.UuidMismatchException;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import ch.unibas.dmi.dbis.reqman.ui.editor.EditorHandler;
import ch.unibas.dmi.dbis.reqman.ui.evaluator.EvaluatorHandler;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.reqman.ui.event.TargetEntity;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.RadioMenuItem;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MainHandler implements MenuHandler {
  
  public static final String EXPORT_DISABLED_REASON = "The export feature is currently being re-written.\n" +
      "In particular, the complete export language is subject to change.";
  private static final Logger LOGGER = LogManager.getLogger(MainHandler.class);
  private static MainHandler instance = null;
  private final EvaluatorHandler evaluatorHandler;
  private final EditorHandler editorHandler;
  private MainScene mainScene;
  private MenuManager manager = MenuManager.getInstance();
  private StatusBar statusBar;
  
  public MainHandler(EvaluatorHandler evaluatorHandler, EditorHandler editorHandler) {
    LOGGER.traceEntry();
    this.evaluatorHandler = evaluatorHandler;
    this.evaluatorHandler.setOnFirstGroup(() -> {
      manager.enableGroupNeeded();
      evaluatorHandler.enableEvaluator();
    });
    this.editorHandler = editorHandler;
    manager.enableOpenItems();
    
  }
  
  public static MainHandler getInstance(EvaluatorHandler evaluatorHandler, EditorHandler editorHandler) {
    if (instance == null) {
      instance = new MainHandler(evaluatorHandler, editorHandler);
    }
    return instance;
  }
  
  @Override
  public void handleNewCatalogue(ActionEvent event) {
    editorHandler.handle(CUDEvent.generateCreationEvent(event, TargetEntity.CATALOGUE));
    if (!editorHandler.isCatalogueLoaded()) {
      return;
    }
    mainScene.setActive(MainScene.Mode.EDITOR);
    manager.enableCatalogueNeeded();
  }
  
  @Override
  public void handleNewGroup(ActionEvent event) {
    if (EntityController.getInstance().hasCourse() && EntityController.getInstance().hasCatalogue()) {
      boolean changeMode = false;
      if (!mainScene.isEvaluatorActive()) {
        changeMode = true;
      }
      evaluatorHandler.handle(CUDEvent.generateCreationEvent(event, TargetEntity.GROUP));
      manager.enableGroupNeeded();
      if (changeMode) {
        mainScene.setActive(MainScene.Mode.EVALUATOR);
      }
    }
  }
  
  @Override
  public void handleOpenCat(ActionEvent event) {
    if (EntityController.getInstance().hasCatalogue()) {
      LOGGER.warn("Cannot handle re-opening of catalogue. Silently ignoring");
      /*
      TODO re-open / new-open catalogue:
      * Reset EntityController
      * All Handlers
      * Save all open files
      * perform open.
       */
      return;
    }
    try {
      
      if (EntityController.getInstance().isStorageManagerReady()) {
        LOGGER.debug("Open Catalogue - course loaded");
        EntityController.getInstance().openCatalogue();
        mainScene.setActive(MainScene.Mode.EDITOR);
        editorHandler.setupEditor();
        manager.enableCatalogueNeeded();
      } else if (event.isConsumed()) {
        LOGGER.warn("Something went very wrong");
      } else {
        LOGGER.debug("Open catalogue and course");
        handleOpenCourse(event);
        event.consume();
        handleOpenCat(event);
      }
    } catch (IllegalStateException ex) {
      LOGGER.catching(ex);
      Utils.showErrorDialog("Error on loading catalgoue", ex.getMessage());
    } catch (UuidMismatchException e) {
      LOGGER.catching(e);
      Utils.showErrorDialog("ID Mismatch", e.getMessage());
    } catch (IOException e) {
      LOGGER.catching(e);
      Utils.showErrorDialog("IOException during Open Catalogue", e.getLocalizedMessage());
    }
    
  }
  
  @Override
  public void handleOpenGroups(ActionEvent event) {
    if (mainScene.isEditorActive()) {
      handleShowEvaluator(event);
    }
    boolean catCoursNeeded = false;
    if (EntityController.getInstance().hasCourse()) {
      LOGGER.debug("Opening group(s) with course set...");
      if (EntityController.getInstance().hasCatalogue()) {
        LOGGER.debug("... and catalogue set");
        evaluatorHandler.handleOpenGroups(event);
        manager.enableGroupNeeded();
      } else {
        // No cat set
        catCoursNeeded = true;
      }
    } else if (event.isConsumed()) {
      LOGGER.warn("Open Groups: Already consumed event. Ignoring");
    } else {
      // No course set
      catCoursNeeded = true;
    }
    if (catCoursNeeded) {
      LOGGER.debug("Opening groups and loading cat/course");
      ActionEvent catEvent = event.copyFor(event, Event.NULL_SOURCE_TARGET);
      handleOpenCat(catEvent); // Loads course as well
      event.consume();
      handleOpenGroups(event);
    }
    LOGGER.debug("Opening performed");
  }
  
  @Override
  public void handleSaveCat(ActionEvent event) {
    editorHandler.saveCatalogue();
  }
  
  @Override
  public void handleSaveGroup(ActionEvent event) {
    if (!evaluatorHandler.isGroupLoaded()) {
      return;
    }
    evaluatorHandler.handleSaveGroup(event);
  }
  
  @Override
  public void handleSaveCatAs(ActionEvent event) {
    editorHandler.saveAsCatalogue();
  }
  
  @Override
  public void handleSaveGroupAs(ActionEvent event) {
    if (!evaluatorHandler.isGroupLoaded()) {
      return;
    }
    evaluatorHandler.handleSaveGroupAs(event);
  }
  
  @Override
  public void handleExportCat(ActionEvent event) {
    Utils.showFeatureDisabled("Export Catalogue", EXPORT_DISABLED_REASON);
    return;
    // TODO Re-Implement export catalogue / course
    /*
    if (!EntityManager.getInstance().isCatalogueLoaded()) {
      return;
    }
    FileChooser fc = new FileChooser();
    fc.setTitle("Export Catalogue");
    if (EntityManager.getInstance().hasLastExportLocation()) {
      fc.setInitialDirectory(EntityManager.getInstance().getLastExportLocation());
    }
    File f = fc.showSaveDialog(mainScene.getWindow());
    if (f != null) {
      mainScene.indicateWaiting(true);
      EntityManager.getInstance().exportCatalogue(f);
      mainScene.indicateWaiting(false);
    }
    */
  }
  
  @Override
  public void handleExportGroups(ActionEvent event) {
    Utils.showFeatureDisabled("Export multiple Groups", EXPORT_DISABLED_REASON);
    return;
    // TODO Re-Implement export groups
    /*
    if (!evaluatorHandler.isGroupLoaded()) {
      return;
    }
    evaluatorHandler.exportAllGroups();
    */
  }
  
  @Override
  public void handleExportGroup(ActionEvent event) {
    Utils.showFeatureDisabled("Export single Group", EXPORT_DISABLED_REASON);
    // TODO Re-Implement export group
  }
  
  @Override
  public void handleQuit(ActionEvent event) {
    Platform.exit();
  }
  
  @Override
  public void handleNewReq(ActionEvent event) {
    if (mainScene.isEditorActive()) {
      if (editorHandler.isCatalogueLoaded()) {
        editorHandler.handle(CUDEvent.generateCreationEvent(event, TargetEntity.REQUIREMENT));
      }
    }
  }
  
  @Override
  public void handleNewMS(ActionEvent event) {
    if (mainScene.isEditorActive()) {
      if (editorHandler.isCatalogueLoaded()) {
        editorHandler.handle(CUDEvent.generateCreationEvent(event, TargetEntity.MILESTONE));
      }
    }
    
  }
  
  @Override
  public void handleModCat(ActionEvent event) {
    if (mainScene.isEditorActive()) {
      if (editorHandler.isCatalogueLoaded()) {
        editorHandler.handle(CUDEvent.generateModificationEvent(event, TargetEntity.CATALOGUE, null));// By design, can be null
      }
    }
    
  }
  
  @Override
  public void handleModReq(ActionEvent event) {
    if (mainScene.isEditorActive()) {
      if (editorHandler.isCatalogueLoaded()) {
        editorHandler.handleModification(CUDEvent.generateModificationEvent(event, TargetEntity.REQUIREMENT, editorHandler.getSelectedRequirement()));
      }
    }
  }
  
  @Override
  public void handleModMS(ActionEvent event) {
    if (mainScene.isEditorActive()) {
      if (editorHandler.isCatalogueLoaded()) {
        editorHandler.handleModification(CUDEvent.generateModificationEvent(event, TargetEntity.MILESTONE, editorHandler.getSelectedMS()));
      }
    }
  }
  
  @Override
  public void handleModGroup(ActionEvent event) {
    if (mainScene.isEvaluatorActive()) {
      if (evaluatorHandler.isGroupLoaded()) {
        evaluatorHandler.handle(CUDEvent.generateModificationEvent(event, TargetEntity.GROUP, null));
      }
    }
  }
  
  @Override
  public void handleShowOverview(ActionEvent event) {
    Utils.showFeatureDisabled("Points Overview", "The old overview is being replaced by a new one");
    return;
    // TODO Re-Implement show overview
    /*
    if (mainScene.isEvaluatorActive()) {
      if (evaluatorHandler.isGroupLoaded()) {
        evaluatorHandler.showOverview();
      }
    }
    */
  }
  
  @Override
  public void handleShowEditor(ActionEvent event) {
    if (mainScene.isEvaluatorActive()) {
      mainScene.setActive(MainScene.Mode.EDITOR);
    }
  }
  
  @Override
  public void handleShowEvaluator(ActionEvent event) {
    if (mainScene.isEditorActive()) {
      mainScene.setActive(MainScene.Mode.EVALUATOR);
    }
    
  }
  
  @Override
  public void handleExportOverview(ActionEvent event) {
    throw new UnsupportedOperationException("Not implemented yet");
    // TODO Re-Implement export Overview
    /*
    LOGGER.traceEntry();
    if (mainScene.isEvaluatorActive()) {
      if (evaluatorHandler.isGroupLoaded()) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Overview");
        if (EntityManager.getInstance().hasLastExportLocation()) {
          fc.setInitialDirectory(EntityManager.getInstance().getLastExportLocation());
        }
        File f = fc.showSaveDialog(mainScene.getWindow());
        if (f != null) {
          mainScene.indicateWaiting(true);
          EntityManager.getInstance().exportOverview(f);
          mainScene.indicateWaiting(false);
        }
      } else {
        Utils.showErrorDialog("Export Failed", "Cannot export an overview if no groups are loaded");
      }
    }
    */
  }
  
  @Override
  public void resetGlobalMilestoneChoice() {
    if (mainScene.isEvaluatorActive()) {
      if (evaluatorHandler.isGroupLoaded()) {
        LOGGER.debug("Resetting global milestone choice");
      }
    }
  }
  
  @Override
  public void setGlobalMilestoneChoice(Milestone ms) {
    LOGGER.traceEntry();
    if (mainScene.isEvaluatorActive()) {
      LOGGER.debug("Setting global milestone to {}", ms);
    }
    
  }
  
  @Override
  public void handlePresentationMode(ActionEvent event) {
    if (event.getSource() instanceof RadioMenuItem) {
      RadioMenuItem rmi = (RadioMenuItem) event.getSource();
      if (rmi.isSelected()) {
        if (!mainScene.getRoot().getStyleClass().contains("presentation")) {
          mainScene.getRoot().getStyleClass().add("presentation");
        }
      } else {
        mainScene.getRoot().getStyleClass().remove("presentation");
      }
    }
  }
  
  @Override
  public void handleNewCourse(ActionEvent event) {
    editorHandler.handle(CUDEvent.generateCreationEvent(event, TargetEntity.COURSE));
    if (!EntityController.getInstance().hasCourse()) {
      return;
    }
    mainScene.setActive(MainScene.Mode.EDITOR);
    editorHandler.setupEditor();
    manager.enableCatalogueNeeded();
  }
  
  @Override
  public void handleOpenCourse(ActionEvent event) {
    editorHandler.openCourse();
  }
  
  @Override
  public void handleSaveCourse(ActionEvent event) {
    editorHandler.saveCourse();
  }
  
  @Override
  public void handleSaveCourseAs(ActionEvent event) {
    editorHandler.saveAsCourse();
  }
  
  @Override
  public void handleClearFilter(ActionEvent event) {
    LOGGER.debug("Clearing filter");
    switch (mainScene.getActiveMode()) {
      case EDITOR:
        if (EntityController.getInstance().hasCatalogue()) {
          editorHandler.displayAllRequirements();
        }
        break;
      case EVALUATOR:
        if (EntityController.getInstance().hasGroups()) {
          evaluatorHandler.resetFilterForAll();
        }
        break;
    }
    
  }
  
  @Override
  public void handleShowFilterBar(ActionEvent event) {
    LOGGER.debug("Showing filter bar");
    switch (mainScene.getActiveMode()) {
      case EDITOR:
        LOGGER.debug("Showing editor filter");
        if (EntityController.getInstance().hasCatalogue()) {
          editorHandler.showFilterBar();
        } else {
          LOGGER.debug("Not showing filter bar because no catalogue available");
        }
        break;
      case EVALUATOR:
        LOGGER.debug("Showing evaluator filter");
        if (EntityController.getInstance().hasGroups()) {
          evaluatorHandler.showFilterBar();
        } else {
          LOGGER.debug("Not showing filter bar because no groups available");
        }
        break;
    }
    
  }
  
  @Override
  public void handleSplitGroup(ActionEvent event) {
    if (EntityController.getInstance().hasGroups()) {
      mainScene.setActive(MainScene.Mode.EVALUATOR);
      evaluatorHandler.handleSplit(event);
    } else {
      LOGGER.debug("Cannot split group if there is no group available");
    }
  }
  
  @Override
  public void handleCatalogueStatistics(ActionEvent event) {
    editorHandler.showStatistics();
  }
  
  @Override
  public void handleImport(ActionEvent event) {
    if(Utils.showConfirmationDialog("Import Catalogue", "You will lose unsafed changes on both, groups and catalogue / course.\nAre you sure to continue?")){
      mainScene.setActive(MainScene.Mode.EDITOR);
      FileChooser fc = Utils.createFileChooser("Import Catalogue");
      File f = fc.showOpenDialog(mainScene.getWindow());
      evaluatorHandler.closeAll();
      editorHandler.closeAll();
      EntityController.getInstance().reset();
      try {
        if (EntityController.getInstance().convertOld(f)) {
          Utils.showInfoDialog("Conversion Finished", "The conversion finished and will be displayed.");
          editorHandler.setupEditor();
          manager.enableCatalogueNeeded();
          manager.enableEditorItems();
        }
      } catch (RuntimeException ex) {
        LOGGER.fatal("Exception in conversion");
        LOGGER.catching(Level.FATAL, ex);
        Utils.showErrorDialog("Error - " + ex.getClass().getSimpleName(),
            "An exception occurred",
            "An uncaught exception occurred. The exception is of type " + ex.getClass().getSimpleName() + ".\n" +
                "The exception's message is as follows:\n\t" + ex.getMessage() + "\n" +
                "ReqMan probably would still work, but re-start is recommended.\n");
      }
    }
    
  }
  
  public void setMainScene(MainScene mainScene) {
    this.mainScene = mainScene;
  }
  
  public void setStatusBar(StatusBar statusBar) {
    this.statusBar = statusBar;
    evaluatorHandler.setStatusBar(statusBar);
    editorHandler.setStatusBar(statusBar);
    OperationFactory.registerStatusBar(statusBar);
  }
  
  void stop() {
    evaluatorHandler.stop();
    try {
      EntityController.getInstance().saveSession();
    } catch (IOException e) {
      LOGGER.warn("Couldn't store session for reason, {}", e);
      LOGGER.catching(Level.WARN, e);
    }
  }
  
  void checkGroupsPresent() {
    if (evaluatorHandler.isGroupLoaded()) {
      MenuManager.getInstance().enableGroupNeeded();
    }
  }
}
