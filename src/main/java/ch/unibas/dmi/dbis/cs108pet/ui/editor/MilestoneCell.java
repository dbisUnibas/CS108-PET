package ch.unibas.dmi.dbis.cs108pet.ui.editor;

import ch.unibas.dmi.dbis.cs108pet.data.Milestone;
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
