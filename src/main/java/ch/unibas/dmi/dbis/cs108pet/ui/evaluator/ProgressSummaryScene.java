package ch.unibas.dmi.dbis.cs108pet.ui.evaluator;

import ch.unibas.dmi.dbis.cs108pet.control.EntityController;
import ch.unibas.dmi.dbis.cs108pet.data.Group;
import ch.unibas.dmi.dbis.cs108pet.data.ProgressSummary;
import ch.unibas.dmi.dbis.cs108pet.ui.common.AbstractVisualCreator;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressSummaryScene extends AbstractVisualCreator<ProgressSummary> {
  
  private Group group;
  private ProgressSummary summary = null;
  
  private TextArea taInternal;
  private TextArea taExternal;
  
  public ProgressSummaryScene(Group group, ProgressSummary progressSummary) {
    this.group = group;
    this.summary = progressSummary;
    populateScene();
    loadSummary();
  }
  
  @Override
  public String getPromptTitle() {
    return group.getName() + "'s Progress Summary of " + EntityController.getInstance().getCatalogueAnalyser().getMilestoneOf(summary).getName();
  }
  
  @Override
  public void handleSaving(ActionEvent event) {
    
    summary.setExternalComment(taExternal.getText());
    summary.setInternalComment(taInternal.getText());
    
    dismiss();
  }
  
  @Override
  public ProgressSummary create() throws IllegalStateException {
    if (!isCreatorReady()) {
      throw new IllegalStateException("Cannot create ProgressSummary since the Creator is not ready");
    }
    return summary;
  }
  
  @Override
  public boolean isCreatorReady() {
    return summary != null;
  }
  
  @Override
  protected void populateScene() {
    Label lblGroup = new Label("Group");
    Label lblGroupName = new Label(group.getName());
    Label lblMilestone = new Label("Milestone");
    Label lblMilestoneName = new Label(EntityController.getInstance().getCatalogueAnalyser().getMilestoneOf(summary).getName());
    Label lblExternal = new Label("External comment");
    Label lblInternal = new Label("Internal comment");
    
    taExternal = new TextArea();
    taInternal = new TextArea();
    
    int rowIndex = 0;
    
    grid.add(lblGroup, 0, rowIndex);
    grid.add(lblGroupName, 1, rowIndex++);
    
    grid.add(lblMilestone, 0, rowIndex);
    grid.add(lblMilestoneName, 1, rowIndex++);
    
    grid.add(lblExternal, 0, rowIndex);
    grid.add(taExternal, 1, rowIndex, 1, 2);
    rowIndex += 2;
    
    grid.add(lblInternal, 0, rowIndex);
    grid.add(taInternal, 1, rowIndex, 1, 2);
    rowIndex += 2;
    
    grid.add(buttons, 0, ++rowIndex, 2, 1);
  }
  
  private void loadSummary() {
    if (summary != null) {
      taInternal.setText(summary.getInternalComment());
      taExternal.setText(summary.getExternalComment());
    }
  }
}
