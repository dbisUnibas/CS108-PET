package ch.unibas.dmi.dbis.reqman.ui.event;

import javafx.event.EventHandler;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface ModificationHandler extends EventHandler<CUDEvent> {

    void handleModification(CUDEvent event);

    @Override
    default void handle(CUDEvent event) {
        if (CUDEvent.MODIFICATION.equals(event.getEventType())) {
            handleModification(event);
        }
    }
}
