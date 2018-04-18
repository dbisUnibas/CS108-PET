package ch.unibas.dmi.dbis.reqman.ui.common;

import ch.unibas.dmi.dbis.reqman.analysis.*;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class FilterBar extends HBox {
  
  private final AssessmentManager manager = AssessmentManager.getInstance();
  @Deprecated
  private FilterActionHandler handler;
  private Label nameLbl;
  private Label containsLbl;
  private Label infoLbl;
  private ComboBox<Mode> modeCB;
  private ComboBox<Requirement.Type> typeCB;
  private TextField searchInput;
  private Button andBtn;
  private Button orBtn;
  private ToggleButton negBtn;
  private Button rmBtn;
  private Button disposeBtn;
  
  private HBox horizontal;
  private VBox vertical;
  private Stack<Filter> filterStack = new Stack<>();
  private boolean andMode = true;
  
  public FilterBar() {
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
    getChildren().add(vertical);
    vertical.getChildren().add(horizontal);
    horizontal.getChildren().addAll(nameLbl, modeCB, containsLbl, searchInput, andBtn, orBtn, negBtn, Utils.createHFill(), rmBtn,disposeBtn);
    vertical.getChildren().add(infoLbl);
    Utils.applyDefaultSpacing(this);
    Utils.applyDefaultSpacing(horizontal);
    getStyleClass().add("darkened");
  }
  
  private void initComponents() {
    horizontal = new HBox();
    vertical = new VBox();
    
    nameLbl = new Label("Filter: ");
    containsLbl = new Label("contains");
    infoLbl = new Label("Active: n/a");
    modeCB = new ComboBox<>();
    modeCB.setItems(FXCollections.observableArrayList(Mode.values()));
    modeCB.getSelectionModel().select(Mode.TEXT);
    
    typeCB = new ComboBox<>();
    typeCB.setItems(FXCollections.observableArrayList(Requirement.Type.values()));
    searchInput = new TextField();
    andBtn = new Button("and");
    andBtn.setOnAction(this::handleAnd);
    orBtn = new Button("or");
    orBtn.setOnAction(this::handleOr);
    rmBtn = new Button("Remove last");
    rmBtn.setOnAction(this::handleRemoveLast);
    negBtn = new ToggleButton("negate");
    negBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
      handleFilter();
    });
    disposeBtn = new Button("Dispose");
    disposeBtn.setOnAction(this::handleClose);
    
    modeCB.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
      if (newValue.equals(Mode.TYPE)) {
        horizontal.getChildren().remove(3);
        horizontal.getChildren().add(3, typeCB);
        typeCB.getSelectionModel().selectFirst();
        containsLbl.setText("is");
        handleFilter();
      } else {
        horizontal.getChildren().remove(3);
        horizontal.getChildren().add(3, searchInput);
        containsLbl.setText("contains");
        handleFilter();
      }
    }));
    
    searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!oldValue.equals(newValue)) {
        handleFilter();
      }
      if (StringUtils.isBlank(newValue) && filterStack.isEmpty()) {
        handleReset(new ActionEvent());
      }
    });
    
    horizontal.setAlignment(Pos.BASELINE_LEFT);
    vertical.setAlignment(Pos.CENTER_LEFT);
  }
  
  private void handleRemoveLast(ActionEvent actionEvent) {
    searchInput.setText("");
    infoLbl.setText("");
    if(filterStack.size() >= 1){
      setFilter(filterStack.pop());
    }else{
      clear();
    }
  }
  
  private void displayFilter(Filter filter) {
    infoLbl.setText("Active: " + filter.getDisplayRepresentation());
  }
  
  private void handleAnd(ActionEvent evt) {
    filterStack.push(manager.getActiveFilter());
    andMode = true;
    searchInput.setText("");
  }
  
  private void handleOr(ActionEvent evt) {
    filterStack.push(manager.getActiveFilter());
    andMode = false;
    searchInput.setText("");
  }
  
  private void handleClose(ActionEvent actionEvent) {
    handleReset(actionEvent);
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
   * @return
   * @throws IllegalArgumentException if no filter could be created
   */
  private Filter createFilterFromUI() throws IllegalArgumentException {
    Filter f = null;
    if (StringUtils.isNotBlank(searchInput.getText())) {
      switch (modeCB.getSelectionModel().getSelectedItem()) {
        case NAME:
          f = new NameContainsFilter(searchInput.getText());
          break;
        case TEXT:
          f = new TextContainsFilter(searchInput.getText());
          break;
        case CATEGORY:
          f = new CategoryContainsFilter(searchInput.getText());
          break;
        case TYPE:
          f = new TypeFilter(typeCB.getSelectionModel().getSelectedItem());
          break;
      }
    } else if (modeCB.getSelectionModel().getSelectedItem() == Mode.TYPE) {
      f = new TypeFilter(typeCB.getSelectionModel().getSelectedItem());
    }
    
    if (f != null && negBtn.isSelected()) {
      return new NotFilter(f);
    } else if (f != null) {
      return f;
    } else {
      throw new IllegalArgumentException("Couldn't create a filter.");
    }
  }
  
  private void handleFilter() {
    try {
      Filter filter = createFilterFromUI();
      if (filterStack.isEmpty()) {
        manager.setFilter(filter);
      } else if (andMode) {
        // AND
        filter = new AndFilter(filterStack.peek(), filter);
      } else {
        // OR
        filter = new OrFilter(filterStack.peek(), filter);
      }
      setFilter(filter);
    } catch (IllegalArgumentException ex) {
      // Ignore the exception as the user probably unintentionally clicked
    }
  }
  
  private void setFilter(@NotNull Filter filter) {
    manager.setFilter(filter);
    displayFilter(filter);
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
