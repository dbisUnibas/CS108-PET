package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 * Displays information about the {@link ch.unibas.dmi.dbis.reqman.data.Course} and {@link ch.unibas.dmi.dbis.reqman.data.Catalogue}
 *
 * @author loris.sauter
 */
public class CourseInfoView extends HBox{
  
  private Course course;
  private Catalogue catalogue;
  
  private HBox leftWrapper = new HBox();
  private HBox rightWrapper = new HBox();
  private SplitPane splitPane = new SplitPane();
  
  private Label courseKeyLbl;
  private Label courseLbl;
  private Label semesterKeyLbl;
  private Label semesterLbl;
  
  private Label catalogueKeyLbl;
  private Label catalogueLbl;
  
  public CourseInfoView(Course course, Catalogue catalogue){
    this.catalogue = catalogue;
    this.course = course;
  
    initComponents();
    layoutComponents();
  }
  
  private void initComponents(){
    courseKeyLbl = new Label("Course:");
    courseLbl = new Label(course.getName());
    
    semesterKeyLbl = new Label("Semester:");
    semesterLbl = new Label(course.getSemester() );
    
    catalogueKeyLbl = new Label("Catalogue:");
    catalogueLbl = new Label(catalogue.getName());
    catalogueLbl.setTooltip(new Tooltip(catalogue.getDescription()));
  }
  
  private void layoutComponents(){
    leftWrapper.getChildren().addAll(courseKeyLbl, courseLbl, semesterKeyLbl, semesterLbl);
    rightWrapper.getChildren().addAll(catalogueKeyLbl, catalogueLbl);
    
    splitPane.setDividerPositions(0.6);
    splitPane.getItems().addAll(leftWrapper, rightWrapper);
    
    this.getChildren().add(splitPane);
  }
  
  
}
