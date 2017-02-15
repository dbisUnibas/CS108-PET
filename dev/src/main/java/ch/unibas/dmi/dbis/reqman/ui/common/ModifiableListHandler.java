package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.event.ActionEvent;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public interface ModifiableListHandler<T> {
    void onRemove(ModifiableListView.RemoveEvent<T> event);

    void onAdd(ActionEvent event);
}
