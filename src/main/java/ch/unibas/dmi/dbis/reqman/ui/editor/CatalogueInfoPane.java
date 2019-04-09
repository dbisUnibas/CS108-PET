package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.ui.common.CourseInfoView;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 * @deprecated Since it got replaced by {@link CourseInfoView}
 */
@Deprecated
public class CatalogueInfoPane extends VBox {
  
  private final SimpleStringProperty catName;
  private final SimpleStringProperty catLecture;
  private final SimpleStringProperty catSemester;
  private final SimpleDoubleProperty maxPoints;
  
  private String title = "Catalogue Information";
  
  
  private TilePane container;
  private Label lblTitle;
  private Label lblName;
  private Label lblLecture;
  private Label lblSemester;
  private Label lblPoints;
  
  private Label dispName;
  private Label dispLecture;
  private Label dispSemester;
  private Label dispPoints;
  
  
  public CatalogueInfoPane() {
    super();
    catName = new SimpleStringProperty();
    catLecture = new SimpleStringProperty();
    catSemester = new SimpleStringProperty();
    maxPoints = new SimpleDoubleProperty();
    EditorView.LOGGER_UI.trace("<init> CatalogueInfoPane");
    
    
    initComponents();
    layoutComponents();
  }
  
  public CatalogueInfoPane(String name, String lecture, String semester, double points) {
    this();
    setCatName(name);
    setCatLecture(lecture);
    setCatSemester(semester);
    setMaxPoints(points);
    
  }
  
  public String getCatName() {
    return catName.get();
  }
  
  public void setCatName(String name) {
    catName.set(name);
  }
  
  public String getCatLecture() {
    return catLecture.get();
  }
  
  public void setCatLecture(String lecture) {
    catLecture.set(lecture);
  }
  
  public String getCatSemester() {
    return catSemester.get();
  }
  
  public void setCatSemester(String semester) {
    catSemester.set(semester);
  }
  
  public double getMaxPoints() {
    return maxPoints.get();
  }
  
  public void setMaxPoints(double points) {
    maxPoints.set(points);
  }
  
  public StringProperty catNameProperty() {
    return catName;
  }
  
  public StringProperty catLectureProperty() {
    return catLecture;
  }
  
  public StringProperty catSemesterProperty() {
    return catSemester;
  }
  
  public DoubleProperty maxPointsProperty() {
    return maxPoints;
  }
  
  String debugStyle() {
    return dispName.getStyleClass().toString();
  }
  
  private void initComponents() {
    container = new TilePane();
    //container.setVgap(5);
    //container.setHgap(5);
    container.setPrefColumns(8);
    
    
    lblTitle = new Label(title);
    lblName = new Label("Name: ");
    lblLecture = new Label("Lecture: ");
    lblSemester = new Label("Semester: ");
    lblPoints = new Label("Max. Points: ");
    
    dispName = new Label();
    dispName.textProperty().bind(catName);
    dispName.getStyleClass().add("display");
    dispLecture = new Label();
    dispLecture.textProperty().bind(catLecture);
    dispLecture.getStyleClass().add("display");
    dispSemester = new Label();
    dispSemester.textProperty().bind(catSemester);
    dispSemester.getStyleClass().add("display");
    dispPoints = new Label();
    dispPoints.textProperty().bindBidirectional(maxPoints, new StringConverter<Number>() {
      @Override
      public String toString(Number object) {
        return StringUtils.prettyPrint(object != null ? object.doubleValue() : 0);
      }
      
      @Override
      public Number fromString(String string) {
        try {
          return Double.valueOf(string);
        } catch (NumberFormatException ex) {
          EditorView.LOGGER_UI.warn("Caught NumberFormatException and setting value to 0.", ex);
          return 0;
        }
      }
    });
    dispPoints.getStyleClass().add("display");
    
    lblName.setLabelFor(dispName);
    lblLecture.setLabelFor(dispLecture);
    lblSemester.setLabelFor(dispSemester);
    lblPoints.setLabelFor(dispPoints);
    
    container.setAlignment(Pos.CENTER_LEFT);
    
    setSpacing(10);
    setPadding(new Insets(10));
  }
  
  private void layoutComponents() {
    container.getChildren().addAll(lblName, dispName, lblLecture, dispLecture, lblSemester, dispSemester, lblPoints, dispPoints);
    container.getChildren().forEach(n -> TilePane.setAlignment(n, Pos.CENTER_LEFT));
    getChildren().addAll(lblTitle, container);
  }
}
