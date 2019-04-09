package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.reqman.ui.common.SaveCancelPane;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.ZoneId;
import java.util.Date;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class MilestonePropertiesScene extends AbstractVisualCreator<Milestone> {
  
  private Milestone milestone = null;
  private TextField tfName = new TextField("Milestone"); // Default name
  private DatePicker inputDate = new DatePicker();
  
  public MilestonePropertiesScene() {
    super();
    populateScene();
  }
  
  public MilestonePropertiesScene(Milestone milestone) {
    this();
    this.milestone = milestone;
    loadMilestone();
  }
  
  public void handleSaving(ActionEvent event) {
    String name = (tfName.getText() == null || tfName.getText().isEmpty()) ? "Milestone" : tfName.getText(); // Default name
    Date d = null;
    if (inputDate.getValue() != null) {
      d = Date.from(inputDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    if (milestone == null) {
      milestone = EntityController.getInstance().createMilestone(name, d);
    } else {
      milestone.setName(name);
      milestone.setTimeUUID(EntityController.getInstance().getEntityFactory().createTime(d).getUuid());
    }
    
    
    getWindow().hide();
  }
  
  @Override
  public Milestone create() throws IllegalStateException {
    if (!isCreatorReady()) {
      throw new IllegalStateException("Creation of Milestone failed: Creator not ready");
    }
    return milestone;
  }
  
  @Override
  public boolean isCreatorReady() {
    return milestone != null;
  }
  
  @Override
  public String getPromptTitle() {
    return "Milestone Properties";
  }
  
  @Override
  protected void populateScene() {
    Label lblName = new Label("Name*");
    Label lblDate = new Label("Date*");
    
    loadMilestone();
    
    SaveCancelPane buttonWrapper = new SaveCancelPane();
    
    buttonWrapper.setOnSave(this::handleSaving);
    
    buttonWrapper.setOnCancel(event -> getWindow().hide());
    
    int rowIndex = 0;
    
    grid.add(lblName, 0, rowIndex);
    grid.add(tfName, 1, rowIndex++);
    
    grid.add(lblDate, 0, rowIndex);
    grid.add(inputDate, 1, rowIndex++);
    
    grid.add(buttonWrapper, 0, ++rowIndex, 2, 1);
  }
  
  private void loadMilestone() {
    if (milestone != null) {
      if (milestone.getName() != null && !milestone.getName().isEmpty()) {
        tfName.setText(milestone.getName());
      }
      if (milestone.getTimeUUID() != null) {
        Date d = EntityController.getInstance().getCourseManager().getMilestoneDate(milestone);
        inputDate.setValue(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
      }
    }
  }
}
