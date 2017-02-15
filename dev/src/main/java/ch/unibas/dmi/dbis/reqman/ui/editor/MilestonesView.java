package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListHandler;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class MilestonesView extends ModifiableListView<Milestone> implements ModifiableListHandler<Milestone> {
    private EditorController controller;

    public MilestonesView(EditorController controller){
        super("Milestones");
        this.controller = controller;
        listView.setItems(controller.getObservableMilestones() );
        addHandler(this);
        listView.setCellFactory((ListView<Milestone> l) -> new MilestoneCell());
    }

    @Override
    public void onRemove(RemoveEvent<Milestone> event) {
        controller.handleRemoveMilestone(event);
    }

    @Override
    public void onAdd(AddEvent<Milestone> event) {
        controller.handleAddMilestone(event);

    }

    static class MilestoneCell extends ListCell<Milestone> {

        @Override
        public void updateItem(Milestone item, boolean empty){
            super.updateItem(item, empty);
            if(!empty){
                setText(item.getName() + " " + item.getOrdinal() );
            }else{
                setText("");
            }
        }
    }
}
