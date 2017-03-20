package ch.unibas.dmi.dbis.reqman.ui.editor.event;

import javafx.event.EventHandler;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface CreationHandler extends EventHandler<EditorEvent> {

    void handleCreation(EditorEvent event);

    /**
     * Handles only {@link EditorEvent} with their {@link javafx.event.EventType} set to {@link EditorEvent#CREATION}
     * This default implementation calls {@link CreationHandler#handleCreation(EditorEvent)} if conditions are met
     *
     * @param event The event to handle
     */
    default void handle(EditorEvent event) {
        if (EditorEvent.CREATION.equals(event.getEventType())) {
            handleCreation(event);
        }
    }
}
