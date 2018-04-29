package ch.unibas.dmi.dbis.reqman.ui;

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
  
  void handleShowEditor(ActionEvent event);
  
  void handleShowEvaluator(ActionEvent event);
  
  void handlePresentationMode(ActionEvent event);
  
  void handleNewCourse(ActionEvent event);
  
  void handleOpenCourse(ActionEvent event);
  
  void handleSaveCourse(ActionEvent event);
  
  void handleSaveCourseAs(ActionEvent event);
  
  void handleClearFilter(ActionEvent event);
  
  void handleShowFilterBar(ActionEvent event);
  
  void handleSplitGroup(ActionEvent event);
  
  void handleCatalogueStatistics(ActionEvent event);
  
  void handleImport(ActionEvent event);
  
  void handleGroupStatistics(ActionEvent event);
  
  void handleShowAbout(ActionEvent event);
  
  void handleShowHelp(ActionEvent event);
  
  void handleModCourse(ActionEvent event);
  
}
