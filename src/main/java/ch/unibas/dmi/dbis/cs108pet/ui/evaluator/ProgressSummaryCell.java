package ch.unibas.dmi.dbis.cs108pet.ui.evaluator;

import ch.unibas.dmi.dbis.cs108pet.control.EntityController;
import ch.unibas.dmi.dbis.cs108pet.data.Milestone;
import ch.unibas.dmi.dbis.cs108pet.data.ProgressSummary;
import javafx.scene.control.ListCell;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressSummaryCell extends ListCell<ProgressSummary> {
  
  @Override
  public void updateItem(ProgressSummary item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null) {
      return;
    }
    if (!empty) {
      Milestone ms = EntityController.getInstance().getCatalogueAnalyser().getMilestoneOf(item);
      setText(ms != null ? ms.getName() : "[ERROR] No such milestone");
    } else {
      setText("");
    }
  }
}
