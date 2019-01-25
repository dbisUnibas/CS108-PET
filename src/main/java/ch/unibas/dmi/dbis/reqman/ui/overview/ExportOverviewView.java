package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.export.CSVOverviewGroupExporter;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class ExportOverviewView extends VBox {
  
  private final static Logger LOGGER = LogManager.getLogger();
  
  private Label titleLabel;
  private Label info1Label;
  private Label info2Label;
  private Label formulaLabel;
  private Label formulaHintLabel;
  private TextField formulaTextField;
  
  private HBox formulaContainer;
  private HBox buttonContainer;
  
  private Button okButton;
  private Button memberButton;
  
  private void initComponents(){
    titleLabel = new Label("Export Groups Overview");
    titleLabel.setStyle("-fx-font-size: 12pt;-fx-font-weight: bold;");
    
    info1Label = new Label("Export the progress of the opened groups as a CSV file in long table format.");
    info1Label.setWrapText(true);
    info2Label = new Label("Grading: Enter a grading formula if the last column should be the group's grade.");
    info2Label.setWrapText(true);
    
    formulaLabel = new Label("Grade Formula: ");
    formulaTextField = new TextField();
    formulaHintLabel = new Label("Hint: Use the variable 'p' for the group's points and the variable 'max' for the maximal amount of points.");
    formulaHintLabel.setWrapText(true);
    formulaHintLabel.setStyle("-fx-font-style: italic;");
    
    formulaContainer = new HBox();
    Utils.applyDefaultSpacing(this);
    buttonContainer = new HBox();
    Utils.applyDefaultSpacing(buttonContainer);
    okButton = new Button("Export Groups");
    memberButton = new Button("Export Members");
  }
  
  private void layoutComponents(){
    formulaContainer.getChildren().addAll(formulaLabel, formulaTextField);
    buttonContainer.getChildren().addAll(Utils.createHFill(), memberButton, okButton);
    getChildren().addAll(titleLabel,info1Label,info2Label,formulaContainer,formulaHintLabel,buttonContainer);
  }
  
  public ExportOverviewView(){
    initComponents();
    layoutComponents();
    okButton.setOnAction(event -> {
      handleExport(false);
    });
    memberButton.setOnAction(event -> {
      handleExport(true);
    });
  }
  
  private void handleExport(boolean members) {
    CSVOverviewGroupExporter exporter;
    if(StringUtils.isNotBlank(formulaTextField.getText())){
      exporter = CSVOverviewGroupExporter.createGradedOverviewExporter(formulaTextField.getText(), EntityController.getInstance().getCatalogue(), EntityController.getInstance().getCourse(), EntityController.getInstance().groupList());
    }else{
      exporter = CSVOverviewGroupExporter.createOverviewExporter(EntityController.getInstance().getCatalogue(),EntityController.getInstance().getCourse(), EntityController.getInstance().groupList());
    }
    FileChooser fc = Utils.createFileChooser("CSV Location");
    File f = fc.showSaveDialog(null);
    if(f == null){
      return; // USER ABORT
    }
    try {
      if(members){
        exporter.exportLongMemberOverviewTable(f);
      }else{
        exporter.exportLongOverviewTable(f);
      }
      Utils.showInfoDialog("Export Successfull","Exported the CSV overview to "+f.getAbsolutePath());
      if(getScene() != null && getScene().getWindow() != null){
        getScene().getWindow().hide();
      }
    } catch (IOException e) {
      LOGGER.error("While csv export: {}", e);
      LOGGER.catching(e);
      Utils.showErrorDialog("Couldn't export to CSV", e.getMessage());
    }
  }
  
  
  
}
