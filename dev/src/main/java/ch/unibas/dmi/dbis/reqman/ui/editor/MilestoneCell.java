package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import javafx.scene.control.ListCell;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MilestoneCell extends ListCell<Milestone> {

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
