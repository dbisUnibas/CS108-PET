package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListHandler;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupView extends ModifiableListView<Group> implements ModifiableListHandler<Group> {

    private final EvaluatorController controller;


    public GroupView(EvaluatorController controller) {
        super("Groups");
        this.controller = controller;
        addHandler(this);
        listView.setCellFactory((ListView<Group> lv) -> new GroupCell());
        listView.setItems(controller.getObservableGroups());
        listView.setOnMouseClicked(this::handleMouseClick);
    }

    @Override
    public void onRemove(RemoveEvent<Group> event) {
        controller.handleRemoveGroup(event);
    }

    @Override
    public void onAdd(AddEvent<Group> event) {
        controller.handleAddGroup(event);
    }

    private void handleMouseClick(MouseEvent event) {
        if (MouseButton.PRIMARY.equals(event.getButton())) {// 'left' mousebutton?
            if (event.getClickCount() == 2) {// double click?
                Group g = listView.getSelectionModel().getSelectedItem();
                controller.addGroupTab(g);
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
