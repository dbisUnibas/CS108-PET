package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListHandler;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import ch.unibas.dmi.dbis.reqman.ui.editor.event.EditorEvent;
import ch.unibas.dmi.dbis.reqman.ui.editor.event.TargetEntity;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MilestonesListView  extends ModifiableListView<Milestone> implements ModifiableListHandler<Milestone> {
    private EditorHandler handler;

    public MilestonesListView(EditorHandler handler) {
        super("Milestones");
        this.handler = handler;
        addHandler(this);
        listView.setCellFactory((ListView<Milestone> l) -> new MilestonesView.MilestoneCell());
        listView.setOnMouseClicked(this::handleModifyRequest);
        listView.setTooltip(new Tooltip("Double-click on Milestone to modify"));

    }

    @Override
    public void onRemove(RemoveEvent<Milestone> event) {
        EditorEvent evt = EditorEvent.generateDeletionEvent(event, TargetEntity.MILESTONE, event.getSelectedIndex(), event.getSelected());
        handler.handleDeletion(evt);
    }

    @Override
    public void onAdd(AddEvent<Milestone> event) {
        EditorEvent evt = EditorEvent.generateCreationEvent(event, TargetEntity.MILESTONE);
        handler.handleCreation(evt);
    }

    private void handleModifyRequest(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            Milestone ms = listView.getSelectionModel().getSelectedItem();
            if(ms != null){
                EditorEvent mod = EditorEvent.generateModificationEvent(new ActionEvent(mouseEvent.getSource(), mouseEvent.getTarget()), TargetEntity.MILESTONE, ms);
                handler.handleModification(mod);
            }
        }
    }

    void setMilestones(ObservableList<Milestone> observableList){
        setItems(observableList);
    }

    public static class MilestoneCell extends ListCell<Milestone> {

        @Override
        public void updateItem(Milestone item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setText(item.getName() + " (" + item.getOrdinal() + ")");
            } else {
                setText("");
            }
        }
    }
}
