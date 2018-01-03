package ch.unibas.dmi.dbis.reqman.ui.common;

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
  
  private final FilterActionHandler handler;
  private Label nameLbl;
  private Label containsLbl;
  private Label infoLbl;
  private ComboBox<Mode> modeCB;
  private ComboBox<Requirement.Type> typeCB;
  private TextField searchInput;
  private Button filterBtn;
  private Button resetBtn;
  private Button closeBtn;
  
  public FilterBar(FilterActionHandler handler) {
    this.handler = handler;
    initComponents();
    layoutComponents();
  }
  
  public void clear() {
    searchInput.clear();
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
    handler.resetFilter();
    infoLbl.setText("");
    searchInput.clear();
  }
  
  private void handleFilter(ActionEvent actionEvent) {
    int amount = -1;
    String pattern = searchInput.getText();
    if (StringUtils.isNotBlank(pattern)) {
      amount = handler.applyFilter(pattern, modeCB.getSelectionModel().getSelectedItem());
    } else if (typeCB.getSelectionModel().getSelectedItem() != null) {
      amount = handler.applyFilter(typeCB.getSelectionModel().getSelectedItem());
    }
    if (amount == 0) {
      infoLbl.setText("No matches found!");
    } else if (amount == -1) {
      // do nothing
    } else {
      infoLbl.setText(String.format("Showing %d matche(s)", amount));
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
