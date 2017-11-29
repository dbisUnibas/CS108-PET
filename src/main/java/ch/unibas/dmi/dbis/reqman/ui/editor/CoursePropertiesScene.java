package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.reqman.ui.common.SaveCancelPane;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class CoursePropertiesScene extends AbstractVisualCreator<Course> {
  
  private Course course = null;
  private TextField tfName = new TextField("Course"); // Default name
  private TextField ftSemester = new TextField();
  
  public CoursePropertiesScene() {
    super();
    populateScene();
  }
  
  public CoursePropertiesScene(Course course) {
    this();
    this.course = course;
    loadCourse();
  }
  
  public void handleSaving(ActionEvent event) {
    String name = (tfName.getText() == null || tfName.getText().isEmpty()) ? "Course" : tfName.getText(); // Default name
    String semester = (ftSemester.getText() == null || ftSemester.getText().isEmpty()) ? "" : ftSemester.getText();
    
    if(course == null){
      course = EntityController.getInstance().createCourse(name, semester);
    }else{
      course.setName(name);
      course.setSemester(semester);
    }
    
    getWindow().hide();
  }
  
  @Override
  public Course create() throws IllegalStateException {
    if (!isCreatorReady()) {
      throw new IllegalStateException("Creation of Milestone failed: Creator not ready");
    }
    return course;
  }
  
  @Override
  public boolean isCreatorReady() {
    return course != null;
  }
  
  @Override
  public String getPromptTitle() {
    return "Milestone Properties";
  }
  
  @Override
  protected void populateScene() {
    Label lblName = new Label("Name");
    Label lblSemester = new Label("Semester");
    
    loadCourse();
    
    SaveCancelPane buttonWrapper = new SaveCancelPane();
    
    buttonWrapper.setOnSave(this::handleSaving);
    
    buttonWrapper.setOnCancel(event -> getWindow().hide());
    
    int rowIndex = 0;
    
    grid.add(lblName, 0, rowIndex);
    grid.add(tfName, 1, rowIndex++);
    
    
    grid.add(lblSemester, 0, rowIndex);
    grid.add(ftSemester, 1, rowIndex++);
    
    grid.add(buttonWrapper, 0, ++rowIndex, 2, 1);
  }
  
  private void loadCourse() {
    if (course != null) {
      if (course.getName() != null && !course.getName().isEmpty()) {
        tfName.setText(course.getName());
      }
      if (course.getSemester() != null) {
        ftSemester.setText(course.getSemester());
      }
    }
  }
}