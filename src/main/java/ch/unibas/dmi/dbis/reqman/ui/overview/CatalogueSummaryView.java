package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueSummaryView extends GridPane{
  
  private Label lblTitle;
  private Label lblSelectedPoints;
  private Label lblMaximalPoints;
  private Label lblRegular;
  private Label lblBonus;
  private Label lblMalus;
  
  private TextField tfSelectedRegular;
  private TextField tfSelectedBonus;
  private TextField tfSelectedMalus;
  
  private TextField tfMaximalRegular;
  private TextField tfMaximalBonus;
  private TextField tfMaximalMalus;
  
  private final CatalogueAnalyser analyser;
  
  public CatalogueSummaryView(CatalogueAnalyser analyser){
    this.analyser = analyser;
    Utils.applyDefaultGridSetup(this);
    initComponents();
    layoutComponents();
    update();
  }
  
  private void initComponents() {
    lblTitle = new Label("Total Points");
    lblSelectedPoints = new Label("Selected Points");
    lblMaximalPoints = new Label("Maximal Points");
    
    lblRegular = new Label("Regular");
    lblBonus = new Label("Bonus");
    lblMalus = new Label("Malus");
    
    tfSelectedRegular = new TextField();
    setupTextField(tfSelectedRegular);
    tfSelectedBonus = new TextField();
    setupTextField(tfSelectedBonus);
    tfSelectedMalus = new TextField();
    setupTextField(tfSelectedMalus);
    
    tfMaximalRegular = new TextField();
    setupTextField(tfMaximalRegular);
    tfMaximalBonus = new TextField();
    setupTextField(tfMaximalBonus);
    tfMaximalMalus = new TextField();
    setupTextField(tfMaximalMalus);
  }
  
  private void setupTextField(TextField tf){
    tf.setEditable(false);
    tf.setAlignment(Pos.CENTER_RIGHT);
  }
  
  private void layoutComponents() {
    addRow(0, lblTitle, lblRegular, lblBonus, lblMalus);
    addRow(1, lblSelectedPoints, tfSelectedRegular, tfSelectedBonus, tfSelectedMalus);
    addRow(2, lblMaximalPoints, tfMaximalRegular, tfMaximalBonus, tfMaximalMalus);
  }
  
  public void update(){
    if(analyser != null){
      tfMaximalRegular.setText(StringUtils.prettyPrint(analyser.getMaximalRegularSum()));
      tfMaximalBonus.setText(StringUtils.prettyPrint(analyser.getMaximalBonusSum()));
      tfMaximalMalus.setText(StringUtils.prettyPrint(analyser.getMaximalMalusSum()));
    }
  }
  
}
