package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.ProgressSummary;
import javafx.scene.control.ListCell;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupCell extends ListCell<Group> {
  
  @Override
  public void updateItem(Group item, boolean empty){
    super.updateItem(item,empty);
    if(item == null){
      return;
    }
    if(!empty){
      setText(item.getName());
    }else{
      setText("");
    }
  }
}
