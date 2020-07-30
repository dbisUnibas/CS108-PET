package ch.unibas.dmi.dbis.cs108pet.ui.evaluator;

import ch.unibas.dmi.dbis.cs108pet.data.Group;
import ch.unibas.dmi.dbis.cs108pet.ui.common.ModifiableListHandler;
import ch.unibas.dmi.dbis.cs108pet.ui.common.ModifiableListView;
import ch.unibas.dmi.dbis.cs108pet.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.cs108pet.ui.event.TargetEntity;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupListView extends ModifiableListView<Group> implements ModifiableListHandler<Group> {
  private final EvaluatorHandler handler;
  
  public GroupListView(EvaluatorHandler handler) {
    super("Groups");
    this.handler = handler;
    addHandler(this);
    listView.setItems(handler.groupList());
    listView.setCellFactory((ListView<Group> l) -> new GroupCell());
    listView.setOnMouseClicked(this::handleOpenRequest);
    listView.setTooltip(new Tooltip("Double-click to open tab"));
  }
  
  @Override
  public void onRemove(RemoveEvent<Group> event) {
    CUDEvent evt = CUDEvent.generateDeletionEvent(event, TargetEntity.GROUP, event.getSelectedIndex(), event.getSelected());
    handler.handleDeletion(evt);
  }
  
  @Override
  public void onAdd(AddEvent<Group> event) {
    CUDEvent evt = CUDEvent.generateCreationEvent(event, TargetEntity.GROUP);
    handler.handleCreation(evt);
  }
  
  public void updateDisplayOf(Group gr) {
    int i = listView.getItems().indexOf(gr);
    EventType<? extends ListView.EditEvent<Group>> type = ListView.editCommitEvent();
    Event event = new ListView.EditEvent<>(listView, type, gr, i);
    listView.fireEvent(event);
  }
  
  void setMilestones(ObservableList<Group> observableList) {
    setItems(observableList);
  }
  
  private void handleOpenRequest(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 2) {
      Group group = listView.getSelectionModel().getSelectedItem();
      if (group != null) {
        handler.openGroupTab(group);
      }
    }
  }
  
  public static class GroupCell extends ListCell<Group> {
    @Override
    public void updateItem(Group item, boolean empty) {
      super.updateItem(item, empty);
      if (!empty) {
        setText(item.getName());
      } else {
        setText("");
      }
    }
  }
}