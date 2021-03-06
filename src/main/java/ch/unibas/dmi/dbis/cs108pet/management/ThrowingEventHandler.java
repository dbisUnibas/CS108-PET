package ch.unibas.dmi.dbis.cs108pet.management;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface ThrowingEventHandler extends EventHandler<WorkerStateEvent> {
  
  String MARKER = "Marks that this RuntimeException was thrown on purpose.";
  
  void handleThrowing(WorkerStateEvent event) throws Exception;
  
  default void handle(WorkerStateEvent event) {
    try {
      handleThrowing(event);
    } catch (Exception e) {
      throw new RuntimeException(MARKER, e);
    }
  }
}
