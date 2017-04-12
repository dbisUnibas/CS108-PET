package ch.unibas.dmi.dbis.reqman.ui;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.editor.EditorHandler;
import ch.unibas.dmi.dbis.reqman.ui.evaluator.EvaluatorHandler;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.reqman.ui.event.TargetEntity;
import javafx.event.ActionEvent;
import javafx.scene.control.RadioMenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public MainHandler(EvaluatorHandler evaluatorHandler, EditorHandler editorHandler) {
        this.evaluatorHandler = evaluatorHandler;
        this.evaluatorHandler.setOnFirstGroup(() -> manager.enableGroupNeeded());
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
        evaluatorHandler.handleOpenCatalogue(event);


    }

    @Override
    public void handleOpenGroups(ActionEvent event) {
        manager.enableGroupNeeded(); // TODO
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
        // TODO ensure correct mode
        editorHandler.handleExportCatalogue(event);
    }

    @Override
    public void handleExportGroups(ActionEvent event) {
        if (!evaluatorHandler.isGroupLoaded()) {
            return;
        }
        evaluatorHandler.exportAllGroups();
    }

    @Override
    public void handleExportGroup(ActionEvent event) {
        if (!evaluatorHandler.isGroupLoaded()) {
            return;
        }
        // TODO ensure correct mode
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public void handleQuit(ActionEvent event) {
        // TODO ensure correct mode
        throw new UnsupportedOperationException("NYI");
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
        if (mainScene.isEvaluatorActive()) {
            if (evaluatorHandler.isGroupLoaded()) {
                evaluatorHandler.showOverview();
            }
        }
    }

    @Override
    public void handleShowEditor(ActionEvent event) {
        mainScene.setActive(MainScene.Mode.EDITOR);
    }

    @Override
    public void handleShowEvaluator(ActionEvent event) {
        mainScene.setActive(MainScene.Mode.EVALUATOR);
    }

    @Override
    public void resetGlobalMilestoneChoice() {
        if (mainScene.isEvaluatorActive()) {
            if (evaluatorHandler.isGroupLoaded()) {
                evaluatorHandler.resetGlobalMilestoneChoice();
            }
        }
    }

    @Override
    public void setGlobalMilestoneChoice(Milestone ms) {
        LOGGER.trace(":setGlobalMilestoneChoice");
        if (mainScene.isEvaluatorActive()) {
            evaluatorHandler.setGlobalMilestoneChoice(ms);
        }

    }

    @Override
    public void handlePresentationMode(ActionEvent event) {
        if(event.getSource() instanceof RadioMenuItem){
            RadioMenuItem rmi = (RadioMenuItem)event.getSource();
            if(rmi.isSelected()){
                if(!mainScene.getRoot().getStyleClass().contains("presentation") ){
                    mainScene.getRoot().getStyleClass().add("presentation");
                }
            }else{
                mainScene.getRoot().getStyleClass().remove("presentation");
            }
        }
    }

    public void setMainScene(MainScene mainScene) {
        this.mainScene = mainScene;
    }

    void stop() {
        evaluatorHandler.stop();
    }
}
