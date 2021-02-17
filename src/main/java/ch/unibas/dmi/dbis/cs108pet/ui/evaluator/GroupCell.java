package ch.unibas.dmi.dbis.cs108pet.ui.evaluator;

import ch.unibas.dmi.dbis.cs108pet.data.Group;
import javafx.scene.control.ListCell;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupCell extends ListCell<Group> {
  
  @Override
  public void updateItem(Group item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null) {
      return;
    }
    if (!empty) {
      setText(item.getName());
    } else {
      setText("");
    }
  }
}
