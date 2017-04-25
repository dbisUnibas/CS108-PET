package ch.unibas.dmi.dbis.reqman.ui;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import javafx.event.ActionEvent;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public interface MenuHandler {

    void handleNewCatalogue(ActionEvent event);
    void handleNewGroup(ActionEvent event);
    void handleOpenCat(ActionEvent event);
    void handleOpenGroups(ActionEvent event);
    void handleSaveCat(ActionEvent event);
    void handleSaveGroup(ActionEvent event);
    void handleSaveCatAs(ActionEvent event);
    void handleSaveGroupAs(ActionEvent event);
    void handleExportCat(ActionEvent event);
    void handleExportGroups(ActionEvent event);
    void handleExportGroup(ActionEvent event);
    void handleQuit(ActionEvent event);
    void handleNewReq(ActionEvent event);
    void handleNewMS(ActionEvent event);
    void handleModCat(ActionEvent event);
    void handleModReq(ActionEvent event);
    void handleModMS(ActionEvent event);
    void handleModGroup(ActionEvent event);
    void handleShowOverview(ActionEvent event);
    void handleShowEditor(ActionEvent event);
    void handleShowEvaluator(ActionEvent event);
    void handleExportOverview(ActionEvent event);

    void resetGlobalMilestoneChoice();

    void setGlobalMilestoneChoice(Milestone ms);

    void handlePresentationMode(ActionEvent event);
}
