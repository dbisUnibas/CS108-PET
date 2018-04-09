package ch.unibas.dmi.dbis.reqman.ui.common;

import ch.unibas.dmi.dbis.reqman.analysis.*;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.apache.commons.lang.StringUtils;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class FilterBar extends HBox {
  
  @Deprecated
  private FilterActionHandler handler;
  private final AssessmentManager manager = AssessmentManager.getInstance();
  private Label nameLbl;
  private Label containsLbl;
  private Label infoLbl;
  private ComboBox<Mode> modeCB;
  private ComboBox<Requirement.Type> typeCB;
  private TextField searchInput;
  private Button filterBtn;
  private Button resetBtn;
  private Button closeBtn;
  
  public FilterBar(){
    initComponents();
    layoutComponents();
  }
  
  @Deprecated
  public FilterBar(FilterActionHandler handler) {
    this.handler = handler;
    initComponents();
    layoutComponents();
  }
  
  public void clear() {
    handleReset(null);
  }
  
  public void show() {
    setVisible(true);
  }
  
  private void layoutComponents() {
    getChildren().addAll(nameLbl, modeCB, containsLbl, searchInput, filterBtn, resetBtn, Utils.createHFill(), infoLbl, closeBtn);
    
    Utils.applyDefaultSpacing(this);
    getStyleClass().add("darkened");
  }
  
  private void initComponents() {
    nameLbl = new Label("Filter requirement");
    containsLbl = new Label("containing");
    infoLbl = new Label();
    modeCB = new ComboBox<>();
    modeCB.setItems(FXCollections.observableArrayList(Mode.values()));
    modeCB.getSelectionModel().select(Mode.TEXT);
    
    typeCB = new ComboBox<>();
    typeCB.setItems(FXCollections.observableArrayList(Requirement.Type.values()));
    searchInput = new TextField();
    filterBtn = new Button("Filter");
    filterBtn.setOnAction(this::handleFilter);
    resetBtn = new Button("Reset");
    resetBtn.setOnAction(this::handleReset);
    closeBtn = new Button("Close");
    closeBtn.setOnAction(this::handleClose);
    
    modeCB.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
      if (newValue.equals(Mode.TYPE)) {
        getChildren().remove(3);
        getChildren().add(3, typeCB);
        typeCB.getSelectionModel().selectFirst();
      } else {
        getChildren().remove(3);
        getChildren().add(3, searchInput);
      }
    }));
  }
  
  private void handleClose(ActionEvent actionEvent) {
//    handler.closeFilterBar();
    // TODO Solution with parent passed and reference is stored.
    Parent p = getParent();
    if (p instanceof Pane) {
      ((Pane) p).getChildren().remove(this);
    } else {
      setVisible(false);
    }
  }
  
  private void handleReset(ActionEvent actionEvent) {
    manager.clearFilter();
    infoLbl.setText("");
    searchInput.clear();
  }
  
  /**
   *
   * @return
   * @throws IllegalArgumentException if no filter could be created
   */
  private Filter createFilterFromUI() throws IllegalArgumentException{
    if(StringUtils.isNotBlank(searchInput.getText())){
      switch(modeCB.getSelectionModel().getSelectedItem()){
        case NAME:
          return new NameContainsFilter(searchInput.getText());
        case TEXT:
          return new TextContainsFilter(searchInput.getText());
        case CATEGORY:
          return new CategoryContainsFilter(searchInput.getText());
        case TYPE:
          return new TypeFilter(typeCB.getSelectionModel().getSelectedItem());
      }
    }else if(modeCB.getSelectionModel().getSelectedItem() == Mode.TYPE){
      return new TypeFilter(typeCB.getSelectionModel().getSelectedItem());
    }
    throw new IllegalArgumentException("Couldn't create a filter.");
  }
  
  private void handleFilter(ActionEvent actionEvent) {
    try{
      Filter filter = createFilterFromUI();
      manager.setFilter(filter);
      infoLbl.setText("Showing "+filter.getDisplayRepresentation());
    }catch (IllegalArgumentException ex){
      // Ignore the exception as the user probably unintentionally clicked
    }
  }
  
  public enum Mode {
    NAME,
    TEXT,
    CATEGORY,
    TYPE;
    
    @Override
    public String toString() {
      return StringUtils.capitalize(name().toLowerCase());
    }
  }
}
