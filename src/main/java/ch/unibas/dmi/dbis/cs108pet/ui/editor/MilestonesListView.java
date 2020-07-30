package ch.unibas.dmi.dbis.cs108pet.ui.editor;

import ch.unibas.dmi.dbis.cs108pet.data.Milestone;
import ch.unibas.dmi.dbis.cs108pet.ui.common.ModifiableListHandler;
import ch.unibas.dmi.dbis.cs108pet.ui.common.ModifiableListView;
import ch.unibas.dmi.dbis.cs108pet.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.cs108pet.ui.event.TargetEntity;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MilestonesListView extends ModifiableListView<Milestone> implements ModifiableListHandler<Milestone> {
  private EditorHandler handler;
  
  public MilestonesListView(EditorHandler handler) {
    super("Milestones");
    this.handler = handler;
    addHandler(this);
    listView.setCellFactory((ListView<Milestone> l) -> new ch.unibas.dmi.dbis.cs108pet.ui.editor.MilestoneCell());
    listView.setOnMouseClicked(this::handleModifyRequest);
    listView.setTooltip(new Tooltip("Double-click on Milestone to modify"));
    
  }
  
  @Override
  public void onRemove(RemoveEvent<Milestone> event) {
    CUDEvent evt = CUDEvent.generateDeletionEvent(event, TargetEntity.MILESTONE, event.getSelectedIndex(), event.getSelected());
    handler.handleDeletion(evt);
  }
  
  @Override
  public void onAdd(AddEvent<Milestone> event) {
    CUDEvent evt = CUDEvent.generateCreationEvent(event, TargetEntity.MILESTONE);
    handler.handleCreation(evt);
  }
  
  public Milestone getSelectedMS() {
    return listView.getSelectionModel().getSelectedItem();
  }
  
  public void updateMilestone(Milestone mod) {
    // by: https://stackoverflow.com/a/23141150
    int i = listView.getSelectionModel().getSelectedIndex();
    EventType<? extends ListView.EditEvent<Milestone>> type = ListView.editCommitEvent();
    Event event = new ListView.EditEvent<>(listView, type, mod, i);
    listView.fireEvent(event);
  }
  
  private void handleModifyRequest(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 2) {
      Milestone ms = listView.getSelectionModel().getSelectedItem();
      if (ms != null) {
        CUDEvent mod = CUDEvent.generateModificationEvent(new ActionEvent(mouseEvent.getSource(), mouseEvent.getTarget()), TargetEntity.MILESTONE, ms);
        handler.handleModification(mod);
      }
    }
  }
  
}
