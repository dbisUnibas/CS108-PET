package ch.unibas.dmi.dbis.reqman.management;

import javafx.concurrent.WorkerStateEvent;

import java.util.PriorityQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class CheckedAsynchronousOperation<T> {

    private static final Consumer<Exception> DEFAULT_EXCEPTION_HANDLER = System.err::println;
    private final ManagementTask<T> task;

    private PriorityQueue<Predicate<T>> validators = new PriorityQueue<>();
    private PriorityQueue<Consumer<T>> processors = new PriorityQueue<>();

    private Consumer<Exception> exceptionHandler;

    private final Thread worker;

    public CheckedAsynchronousOperation(final ManagementTask<T> task){
        this.worker = new Thread(task);
        this.task = task;

        prepare();
    }


    private void prepare(){
        task.setOnSucceeded( event -> {
            try{
                done(event);
            }catch(IllegalStateException ex){
                if(exceptionHandler != null){
                    exceptionHandler.accept(ex);
                }else{
                    DEFAULT_EXCEPTION_HANDLER.accept(ex);
                }
            }
        });

    }

    private void done(WorkerStateEvent event) throws IllegalStateException{
        T result = task.getValue();

        validators.forEach( v -> {
            if(!v.test(result)){
                throw new IllegalStateException();
            }
        });

        processors.forEach(p -> p.accept(result));
    }

    public CheckedAsynchronousOperation(final ManagementTask<T> task, boolean background){
        this(task);
        this.worker.setDaemon(background);
    }

    public void start(){
        worker.start();
    }

    public void addValidator(Predicate<T> validator){

    }
}
