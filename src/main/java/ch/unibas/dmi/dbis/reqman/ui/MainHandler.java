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
import javafx.scene.control.RadioMenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MainHandler implements MenuHandler {
  
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
  }
  
  public static MainHandler getInstance(EvaluatorHandler evaluatorHandler, EditorHandler editorHandler) {
    if (instance == null) {
      instance = new MainHandler(evaluatorHandler, editorHandler);
    }
    return instance;
  }
  
  @Override
  public void handleNewCatalogue(ActionEvent event) {
    // TODO ensure correct mode ?
    editorHandler.handle(CUDEvent.generateCreationEvent(event, TargetEntity.CATALOGUE));
    if (!editorHandler.isCatalogueLoaded()) {
      return;
    }
    mainScene.setActive(MainScene.Mode.EDITOR);
    manager.enableCatalogueNeeded();
  }
  
  @Override
  public void handleNewGroup(ActionEvent event) {
    if (!mainScene.isEvaluatorActive()) {
      return;
    }
    evaluatorHandler.handle(CUDEvent.generateCreationEvent(event, TargetEntity.GROUP));
    manager.enableGroupNeeded();
  }
  
  @Override
  public void handleOpenCat(ActionEvent event) {
    if (EntityController.getInstance().hasCatalogue()) {
      LOGGER.warn("Cannot handle re-opening of catalogue. Silently ignoring");
//      Utils.showErrorDialog("Cannot load another catalogue", "Currently (ReqMan v" + Version.getInstance().getVersion() + ") cannot switch catalgoues during runitme.\n Please save your work and restart the application. ");
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
      
      // Currently not needed:
      /*
      FileChooser fc = Utils.createFileChooser("Open Catalogue");
      fc.getExtensionFilters().add(ReqmanFile.Type.CATALOGUE.getExtensionFilter());
      File f = fc.showOpenDialog(null);
      if (f != null) {
        if (mainScene.isEditorActive()) {
          LOGGER.debug("Opening catalogue in editor");
          // EDITOR
          editorHandler.setupEditor();
        } else {
          // EVALUATOR
          LOGGER.debug("Opening catalogue in evalautor");
          evaluatorHandler.processCatalogueOpened(EntityController.getInstance().getCatalogue());
        }
      }*/
      
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
    evaluatorHandler.handleOpenGroups(event);
    if (!evaluatorHandler.isGroupLoaded()) {
      return;
    }
    manager.enableGroupNeeded();
  }
  
  @Override
  public void handleSaveCat(ActionEvent event) {
    // TODO ensure correct mode ?
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
    // TODO ensure correct mode ?
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
    throw new UnsupportedOperationException("Not implemented yet");
    // TODO
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
    throw new UnsupportedOperationException("Not implemented yet");
    // TODO
    /*
    if (!evaluatorHandler.isGroupLoaded()) {
      return;
    }
    evaluatorHandler.exportAllGroups();
    */
  }
  
  @Override
  public void handleExportGroup(ActionEvent event) {
    throw new UnsupportedOperationException("Not implemented yet");
    // TODO
    /*
    if (!evaluatorHandler.isGroupLoaded()) {
      return;
    }
    // TODO ensure correct mode
    throw new UnsupportedOperationException("NYI");
    */
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
    throw new UnsupportedOperationException("Not implemented yet");
    // TODO
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
    // TODO
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
    manager.enableCatalogueNeeded(); // TODO fix and / or change to courseNeeded
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
  }
  
  void checkGroupsPresent() {
    if (evaluatorHandler.isGroupLoaded()) {
      MenuManager.getInstance().enableGroupNeeded();
    }
  }
}
