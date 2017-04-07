package ch.unibas.dmi.dbis.reqman.ui.editor.event;

import javafx.event.EventHandler;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface ModificationHandler extends EventHandler<EditorEvent> {

    void handleModification(EditorEvent event);

    default void handle(EditorEvent event) {
        if (EditorEvent.MODIFICATION.equals(event.getEventType())) {
            handleModification(event);
        }
    }
}
