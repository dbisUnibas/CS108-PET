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
    public static final EventType<EditorEvent> DELETION = new EventType<>(ACTION, "delivery");
    public static final EventType<EditorEvent> MODIFICATION = new EventType<>(ACTION, "modification");

    private TargetEntity targetEntity;

    private ActionEvent parent;

    private int index = -1; // So exception thrown, if not correctly set and used
    private Object delivery = null; // must match TargetEntity

    private final EventType<EditorEvent> eventType;

    EditorEvent(Object source, EventTarget target, EventType<EditorEvent> type, TargetEntity targetEntity) {
        super(source, target);
        this.eventType = type;
        this.targetEntity = targetEntity;
    }

    EditorEvent(ActionEvent parent, TargetEntity targetEntity, EventType<EditorEvent> type, int index, Object deleted) {
        this(parent,targetEntity, type );
        this.index = index;
        delivery = deleted;
    }

    public EditorEvent(ActionEvent parent, EventType<EditorEvent> type, TargetEntity targetEntity, Object delivery) {
        this(parent,targetEntity, type);
        this.delivery = delivery;
    }

    public Object getDelivery() {
        return delivery;
    }

    EditorEvent(ActionEvent parent, TargetEntity targetEntity, EventType<EditorEvent> type){
        this(parent.getSource(), parent.getTarget(), type, targetEntity);
        this.parent = parent;
    }

    public static EditorEvent generateCreationEvent(Object source, EventTarget target, TargetEntity targetEntity) {
        return new EditorEvent(source, target, CREATION, targetEntity);
    }

    public static EditorEvent generateDeletionEvent(Object source, EventTarget target, TargetEntity targetEntity) {
        return new EditorEvent(source, target, DELETION, targetEntity);
    }

    public static EditorEvent generateDeletionEvent(ActionEvent parent, TargetEntity targetEntity, int index, Object deletion){
        EditorEvent evt = new EditorEvent(parent, targetEntity, DELETION, index, deletion);
        return evt;
    }

    public static EditorEvent generateModificationEvent(ActionEvent parent, TargetEntity targetEntity, Object delivery) {
        return new EditorEvent(parent, MODIFICATION, targetEntity, delivery);
    }

    public boolean hasParent(){
        return parent != null;
    }

    public ActionEvent getParent() {
        return parent;
    }

    protected void setParent(ActionEvent parent) {
        this.parent = parent;
    }

    public int getIndex() {
        return index;
    }

    protected void setIndex(int index) {
        this.index = index;
    }

    @Override
    public EventType<? extends ActionEvent> getEventType() {
        return eventType == null ? ANY : eventType;
    }

    public TargetEntity getTargetEntity() {
        return targetEntity;
    }

    public static EditorEvent generateCreationEvent(ActionEvent event, TargetEntity targetEntity) {
        return new EditorEvent(event, targetEntity, CREATION);
    }
}
