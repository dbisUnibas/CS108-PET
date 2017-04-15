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
        task.setOnSucceeded( this::done);

    }

    private void done(WorkerStateEvent event){
        T result = task.getValue();

        validators.forEach( v -> v.test(result));

        processors.forEach(p -> p.accept(result));
    }

    public CheckedAsynchronousOperation(final ManagementTask<T> task, boolean background){
        this(task);
        this.worker.setDaemon(background);
    }

    public void start(){
        worker.start();
    }
}
