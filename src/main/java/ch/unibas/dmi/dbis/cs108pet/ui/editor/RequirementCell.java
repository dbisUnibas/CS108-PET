package ch.unibas.dmi.dbis.cs108pet.ui.editor;

import ch.unibas.dmi.dbis.cs108pet.data.Requirement;
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
