package ch.unibas.dmi.dbis.cs108pet.ui.editor;

import ch.unibas.dmi.dbis.cs108pet.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.cs108pet.ui.common.SaveCancelPane;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class MetaKeyValuePairScene extends AbstractVisualCreator<RequirementPropertiesScene.MetaKeyValuePair> {
  
  private RequirementPropertiesScene.MetaKeyValuePair pair = null;
  private TextField tfKey = new TextField();
  private TextField tfValue = new TextField();
  
  public MetaKeyValuePairScene() {
    super();
    populateScene();
  }
  
  @Override
  public String getPromptTitle() {
    return "Add new key value pair";
  }
  
  @Override
  public void handleSaving(ActionEvent event) {
    String key = tfKey.getText();
    String val = tfValue.getText();
    if (key != null && !key.isEmpty()) {
      if (val != null && !val.isEmpty()) {
        pair = new RequirementPropertiesScene.MetaKeyValuePair(key, val);
      }
    }
    getWindow().hide();
  }
  
  @Override
  public RequirementPropertiesScene.MetaKeyValuePair create() throws IllegalStateException {
    return pair;
  }
  
  @Override
  public boolean isCreatorReady() {
    return pair != null;
  }
  
  @Override
  protected void populateScene() {
    
    Label lblKey = new Label("Key");
    Label lblValue = new Label("Value");
    
    
    grid.add(lblKey, 0, 0);
    grid.add(tfKey, 1, 0);
    grid.add(lblValue, 0, 1);
    grid.add(tfValue, 1, 1);
    
    SaveCancelPane buttons = new SaveCancelPane();
    buttons.setOnCancel(event -> getWindow().hide());
    buttons.setOnSave(this::handleSaving);
    
    grid.add(buttons, 0, 3, 2, 1);
    setRoot(grid);
  }
}
