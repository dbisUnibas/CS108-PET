package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

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
  private SimpleStringProperty courseNameProperty;
  private Label semesterKeyLbl;
  private Label semesterLbl;
  private SimpleStringProperty semesterProperty;
  
  private Label catalogueKeyLbl;
  private Label catalogueLbl;
  private SimpleStringProperty catalogueNameProperty;
  private SimpleStringProperty catalogueDescProperty;
  
  public CourseInfoView(){
    initComponents();
    layoutComponents();
  }
  
  public CourseInfoView(Course course, Catalogue catalogue){
    this.catalogue = catalogue;
    this.course = course;
  
    initComponents();
    layoutComponents();
  }
  
  public void refresh() {
    course = EntityController.getInstance().getCourse();
    catalogue = EntityController.getInstance().getCatalogue();
    populateInfo();
  }
  
  private void initComponents(){
    courseKeyLbl = new Label("Course:");
    courseNameProperty = new SimpleStringProperty();
    courseLbl = new Label();
    courseLbl.textProperty().bind(courseNameProperty);
    
    
    semesterKeyLbl = new Label("Semester:");
    semesterProperty = new SimpleStringProperty();
    semesterLbl = new Label();
    semesterLbl.textProperty().bind(semesterProperty);
    
    catalogueKeyLbl = new Label("Catalogue:");
    catalogueNameProperty = new SimpleStringProperty();
    catalogueDescProperty = new SimpleStringProperty();
    catalogueLbl = new Label();
    catalogueLbl.textProperty().bind(catalogueNameProperty);
    catalogueLbl.setTooltip(new Tooltip(""));
    catalogueLbl.getTooltip().textProperty().bind(catalogueDescProperty);
    
  }
  
  private void layoutComponents(){
    leftWrapper.getChildren().addAll(courseKeyLbl, courseLbl, semesterKeyLbl, semesterLbl);
    rightWrapper.getChildren().addAll(catalogueKeyLbl, catalogueLbl);
    
    splitPane.setDividerPositions(0.5);
    splitPane.getItems().addAll(leftWrapper, rightWrapper);
    
    splitPane.prefHeightProperty().bind(heightProperty());
    splitPane.prefWidthProperty().bind(widthProperty());
    
    this.getChildren().clear();
    getChildren().add(splitPane);
    
    HBox.setHgrow(this, Priority.ALWAYS);
  }
  
  private void populateInfo(){
    if(course != null){
      String name = course.getName();
      String semester = course.getSemester();
      courseNameProperty.set(name != null ? name : "");
      semesterProperty.set(semester != null ? semester : "");
    }
    if(catalogue != null){
      String name = catalogue.getName();
      String desc = catalogue.getDescription();
      catalogueNameProperty.set(name != null ? name : "");
      catalogueDescProperty.set(desc != null ? desc : "No description");
    }
  }
  
  
}
