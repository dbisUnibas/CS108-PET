package ch.unibas.dmi.dbis.reqman.management;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface ThrowingEventHandler extends EventHandler<WorkerStateEvent> {

    void handleThrowing(WorkerStateEvent event) throws Exception;

    default void handle(WorkerStateEvent event){
        try{
            handleThrowing(event);
        }catch(Exception e){
            throw new RuntimeException(MARKER,e);
        }
    }

    String MARKER = "Marks that this RuntimeException was thrown on purpose.";
}
