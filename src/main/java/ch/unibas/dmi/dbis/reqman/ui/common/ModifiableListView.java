package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.ArrayList;

/**
 * Class to represent a {@link ListView} which has a title and buttons to add / remove items.
 *
 * @author loris.sauter
 */
public class ModifiableListView<T> extends BorderPane {
  
  // TODO Allow custom styling
  
  protected ModifiableListController<T> controller = null;
  protected ListView<T> listView = new ListView();
  private ArrayList<ModifiableListHandler<T>> handlers = new ArrayList<>();
  
  public ModifiableListView(String title) {
    super();
    initComponents(title, listView);
    listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    listView.setTooltip(new Tooltip("Right click on a item to modify it"));
  }
  
  public ModifiableListView(String title, ModifiableListController<T> controller) {
    this(title);
    this.controller = controller;
    listView.setItems(controller.getItems());
  }
  
  public void addHandler(ModifiableListHandler<T> handler) {
    handlers.add(handler);
  }
  
  public void removeHandler(ModifiableListHandler<T> handler) {
    handlers.remove(handler);
  }
  
  public ObservableList<T> getItems() {
    return listView.getItems();
  }
  
  public void setItems(ObservableList<T> items) {
    listView.setItems(items);
  }
  
  protected void setOnAddAction(ActionEvent event) {
    AddEvent<T> addEvent = new AddEvent<T>(event);
    if (hasController()) {
      controller.onAdd(addEvent);
    } else {
      handlers.forEach(handler -> handler.onAdd(addEvent));
    }
    
  }
  
  protected void setOnRemoveAction(ActionEvent event) {
    T selected = listView.getSelectionModel().getSelectedItem();
    int index = listView.getSelectionModel().getSelectedIndex();
    RemoveEvent<T> removeEvent = new RemoveEvent<T>(event, selected, index);
    
    if (hasController()) {
      controller.onRemove(removeEvent);
    } else {
      handlers.forEach(handler -> handler.onRemove(removeEvent));
    }
  }
  
  protected boolean hasController() {
    return controller != null;
  }
  
  protected void initComponents(String title, Region content) {
    
    // Border style
    this.setStyle("-fx-border-width: 1; -fx-border-color: silver");
    
    // TitleBar with Add / Remove Buttons
    AnchorPane titleBar = new AnchorPane();
    // TitleBar border style
    titleBar.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: silver;");
    // Button group
    HBox buttons = new HBox();
    buttons.setPadding(new Insets(10));
    buttons.setSpacing(10);
    
    Button buttonAdd = Utils.createPlusButton();
    buttonAdd.setOnAction(this::setOnAddAction);
    Button buttonRemove = Utils.createMinusButton();
    buttonRemove.setOnAction(this::setOnRemoveAction);

        /*Font fontButton = Font.font("sans-serif", FontWeight.EXTRA_BOLD, 12);
        buttonAdd.setFont(fontButton);
        buttonRemove.setFont(fontButton);*/
    
    buttons.getChildren().addAll(buttonAdd, buttonRemove);
    
    // Title
    Label titleText = new Label(title);
    
    titleBar.getChildren().addAll(titleText, buttons);
    AnchorPane.setLeftAnchor(titleText, 10.0);
    AnchorPane.setTopAnchor(titleText, 10.0);
    AnchorPane.setRightAnchor(buttons, 0.0);
    
    // Content
    this.setTop(titleBar);
    this.setCenter(content);
    
  }
  
  public static class RemoveEvent<T> extends ActionEvent {
    public static final EventType<RemoveEvent> REMOVE = new EventType<>(ActionEvent.ACTION, "remove");
    private T selected;
    private int index;
    
    public RemoveEvent(ActionEvent source, T selected, int index) {
      super(source.getSource(), source.getTarget());
      this.selected = selected;
      this.index = index;
    }
    
    @Override
    public EventType<? extends ActionEvent> getEventType() {
      return REMOVE;
    }
    
    public T getSelected() {
      return selected;
    }
    
    public int getSelectedIndex() {
      return index;
    }
  }
  
  /**
   * Rather a flag event
   *
   * @param <T>
   */
  public static class AddEvent<T> extends ActionEvent {
    public static final EventType<AddEvent> ADD = new EventType<>(ActionEvent.ACTION, "add");
    
    public AddEvent(ActionEvent source) {
      super(source.getSource(), source.getTarget());
    }
    
    @Override
    public EventType<? extends ActionEvent> getEventType() {
      return ADD;
    }
  }
  
}
