package ch.unibas.dmi.dbis.cs108pet.ui.event;

import javafx.event.EventHandler;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface DeletionHandler extends EventHandler<CUDEvent> {

  void handleDeletion(CUDEvent event);

  /**
   * Handles only {@link CUDEvent} if the {@link javafx.event.EventType} is {@link CUDEvent#DELETION}
   * This default implementation calls {@link DeletionHandler#handleDeletion(CUDEvent)} if conditions are met
   *
   * @param event
   */
  @Override
  default void handle(CUDEvent event) {
    if (CUDEvent.DELETION.equals(event.getEventType())) {
      handleDeletion(event);
    }
  }
}
