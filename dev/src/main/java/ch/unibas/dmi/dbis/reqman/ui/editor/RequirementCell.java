package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Requirement;
import javafx.scene.control.ListCell;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class RequirementCell extends ListCell<Requirement> {

    @Override
    public void updateItem(Requirement item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setText(item.getName());
        } else {
            setText("");
        }
    }
}
