package ch.unibas.dmi.dbis.reqman.ui.event;

import javafx.event.EventHandler;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface CreationHandler extends EventHandler<CUDEvent> {

    void handleCreation(CUDEvent event);

    /**
     * Handles only {@link CUDEvent} with their {@link javafx.event.EventType} set to {@link CUDEvent#CREATION}
     * This default implementation calls {@link CreationHandler#handleCreation(CUDEvent)} if conditions are met
     *
     * @param event The event to handle
     */
    @Override
    default void handle(CUDEvent event) {
        if (CUDEvent.CREATION.equals(event.getEventType())) {
            handleCreation(event);
            event.consume();
        }

    }
}
