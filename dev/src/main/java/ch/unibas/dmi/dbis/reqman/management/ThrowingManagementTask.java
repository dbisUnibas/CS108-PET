package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.ThrowingCallback;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public abstract class ThrowingManagementTask<R> extends ManagementTask<R> {

    private CatchingHandler handler;

    public Exception getLastException() {
        if(handler != null){
            return handler.getLast();
        }else{
            return null;
        }
    }

    public void setThrowingOnSucceeded(ThrowingEventHandler handler){
        this.handler = new CatchingHandler(handler);
        setOnSucceeded(this.handler);
    }

    private ThrowingCallback throwBack = null;

    public void setOnCaught(ThrowingCallback callback) throws Exception {
        throwBack = callback;
    }

    private class CatchingHandler implements EventHandler<WorkerStateEvent>{

        private Exception last = null;

        private final ThrowingEventHandler handler;

        public CatchingHandler(ThrowingEventHandler handler) {
            this.handler = handler;
        }

        public Exception getLast() {
            return last;
        }

        public boolean hasException(){
            return last != null;
        }

        @Override
        public void handle(WorkerStateEvent event) {
            try{
                handler.handleThrowing(event);
            }catch(Exception e){
                last = e;
            }
        }
    }
}
