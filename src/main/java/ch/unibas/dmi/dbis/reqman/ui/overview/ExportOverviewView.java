package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class ExportOverviewView extends VBox {
  
  private Label titleLabel;
  private Label info1Label;
  private Label info2Label;
  private Label formulaLabel;
  private Label formulaHintLabel;
  private TextField formulaTextField;
  
  private HBox formulaContainer;
  private HBox buttonContainer;
  
  private Button okButton;
  
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
    okButton = new Button("Export");
  }
  
  private void layoutComponents(){
    
  }
  
  private void layoutComonents(){
  
  }
  
}
