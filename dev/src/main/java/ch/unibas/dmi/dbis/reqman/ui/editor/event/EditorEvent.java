package ch.unibas.dmi.dbis.reqman.ui.editor.event;

import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorEvent extends ActionEvent {

    public static final EventType<EditorEvent> CREATION = new EventType<>(ACTION, "creation");
    public static final EventType<EditorEvent> DELETION = new EventType<>(ACTION, "deletion");
    public static final EventType<EditorEvent> MODIFICATION = new EventType<>(ACTION, "modification");

    private TargetEntity targetEntity;

    private EventType<EditorEvent> eventType;

    @Override
    public EventType<? extends ActionEvent> getEventType(){
        return  eventType == null ? ANY : eventType;
    }

    public EditorEvent(){
        super();
    }


    public EditorEvent(Object source, EventTarget target, EventType<EditorEvent> type, TargetEntity targetEntity){
        super(source, target);
        this.eventType = type;
        this.targetEntity = targetEntity;
    }

    public TargetEntity getTargetEntity() {
        return targetEntity;
    }

    public static EditorEvent generateCreationEvent(Object source, EventTarget target, TargetEntity targetEntity){
        return new EditorEvent(source, target, CREATION, targetEntity);
    }

    public static EditorEvent generateDeletionEvent(Object source, EventTarget target, TargetEntity targetEntity){
        return new EditorEvent(source, target, DELETION, targetEntity);
    }

    public static EditorEvent generateModificationEvent(Object source, EventTarget target, TargetEntity targetEntity){
        return new EditorEvent(source, target, MODIFICATION, targetEntity);
    }
}
