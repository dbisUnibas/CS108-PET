package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.editor.event.EditorEvent;
import ch.unibas.dmi.dbis.reqman.ui.editor.event.TargetEntity;
import javafx.event.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Layer between (G)UI and internal logic.
 *
 * @author loris.sauter
 */
public class EditorHandler implements EventHandler<EditorEvent> {

    private static final Logger LOGGER = LogManager.getLogger(EditorHandler.class);


    @Override
    public void handle(EditorEvent event) {
        if (event != null) {
            if (EditorEvent.CREATION.equals(event.getEventType())) {
                handleCreation(event);
            } else if (EditorEvent.DELETION.equals(event.getEventType())) {
                handleDeletion(event);
            } else if (EditorEvent.MODIFICATION.equals(event.getEventType())) {
                handleModification(event);
            } else {
                throw new IllegalArgumentException("Cannot handle unknown event type: " + event.getEventType().toString());
            }
        }
        // Silently ignoring null events

    }

    /**
     * It must be guaranteed that the passed event is of type CREATION
     *
     * @param evt
     */
    public void handleCreation(EditorEvent evt) {
        switch (evt.getTargetEntity()) {
            case CATALOGUE:
                Catalogue cat = EditorPromptFactory.promptNewCatalogue();
                break;
            case REQUIREMENT:
                Requirement req = EditorPromptFactory.promptNewRequirement(null);
                break;
            case MILESTONE:

                break;
            default:
                throwInappropriateTargetEntity(evt.getTargetEntity());
        }
    }

    public void handleDeletion(EditorEvent evt) {
        switch(evt.getTargetEntity()){
            case CATALOGUE:
                break;
            case REQUIREMENT:

                break;
            case MILESTONE:

                break;
                default:
                    throwInappropriateTargetEntity(evt.getTargetEntity());
        }
    }

    public void handleModification(EditorEvent evt) {
        switch(evt.getTargetEntity()){
            case CATALOGUE:
                break;
            case REQUIREMENT:

                break;
            case MILESTONE:

                break;
            default:
                throwInappropriateTargetEntity(evt.getTargetEntity());
        }
    }

    private final static void throwInappropriateTargetEntity(TargetEntity entity){
        throw new IllegalArgumentException("EditorHandler can only handle TargetEntity.CATALOGUE, TargetEntity.REQUIREMENT, TargetEntity.MILESTONE, but "+entity.toString()+" was given");
    }
}
