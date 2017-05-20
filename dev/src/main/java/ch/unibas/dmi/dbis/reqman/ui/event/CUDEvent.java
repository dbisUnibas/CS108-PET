package ch.unibas.dmi.dbis.reqman.ui.event;

import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CUDEvent extends ActionEvent {

    public static final EventType<CUDEvent> CREATION = new EventType<>(ACTION, "creation");
    public static final EventType<CUDEvent> DELETION = new EventType<>(ACTION, "delivery");
    public static final EventType<CUDEvent> MODIFICATION = new EventType<>(ACTION, "modification");
    private final EventType<CUDEvent> eventType;
    private TargetEntity targetEntity;
    private ActionEvent parent;
    private int index = -1; // So exception thrown, if not correctly set and used
    private Object delivery = null; // must match TargetEntity

    public CUDEvent(ActionEvent parent, EventType<CUDEvent> type, TargetEntity targetEntity, Object delivery) {
        this(parent, targetEntity, type);
        this.delivery = delivery;
    }

    CUDEvent(Object source, EventTarget target, EventType<CUDEvent> type, TargetEntity targetEntity) {
        super(source, target);
        this.eventType = type;
        this.targetEntity = targetEntity;
    }

    CUDEvent(ActionEvent parent, TargetEntity targetEntity, EventType<CUDEvent> type, int index, Object deleted) {
        this(parent, targetEntity, type);
        this.index = index;
        delivery = deleted;
    }

    CUDEvent(ActionEvent parent, TargetEntity targetEntity, EventType<CUDEvent> type) {
        this(parent.getSource(), parent.getTarget(), type, targetEntity);
        this.parent = parent;
    }

    public static CUDEvent generateCreationEvent(Object source, EventTarget target, TargetEntity targetEntity) {
        return new CUDEvent(source, target, CREATION, targetEntity);
    }

    public static CUDEvent generateDeletionEvent(Object source, EventTarget target, TargetEntity targetEntity) {
        return new CUDEvent(source, target, DELETION, targetEntity);
    }

    public static CUDEvent generateDeletionEvent(ActionEvent parent, TargetEntity targetEntity, int index, Object deletion) {
        CUDEvent evt = new CUDEvent(parent, targetEntity, DELETION, index, deletion);
        return evt;
    }

    public static CUDEvent generateModificationEvent(ActionEvent parent, TargetEntity targetEntity, Object delivery) {
        return new CUDEvent(parent, MODIFICATION, targetEntity, delivery);
    }

    public static CUDEvent generateCreationEvent(ActionEvent event, TargetEntity targetEntity) {
        return new CUDEvent(event, targetEntity, CREATION);
    }

    public static CUDEvent generateCreationEvent(ActionEvent event, TargetEntity target, Object delivery) {
        CUDEvent evt = generateCreationEvent(event, target);
        evt.setDelivery(delivery);
        return evt;
    }

    public Object getDelivery() {
        return delivery;
    }

    private void setDelivery(Object o) {
        this.delivery = o;
    }

    public boolean hasParent() {
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
}
