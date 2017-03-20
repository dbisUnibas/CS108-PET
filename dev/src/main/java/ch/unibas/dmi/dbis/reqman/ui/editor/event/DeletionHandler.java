package ch.unibas.dmi.dbis.reqman.ui.editor.event;

import javafx.event.EventHandler;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface DeletionHandler extends EventHandler<EditorEvent> {

    void handleDeletion(EditorEvent event);

    /**
     * Handles only {@link EditorEvent} if the {@link javafx.event.EventType} is {@link EditorEvent#DELETION}
     * This default implementation calls {@link DeletionHandler#handleDeletion(EditorEvent)} if conditions are met
     *
     * @param event
     */
    default void handle(EditorEvent event) {
        if (EditorEvent.DELETION.equals(event.getEventType())) {
            handleDeletion(event);
        }
    }
}
