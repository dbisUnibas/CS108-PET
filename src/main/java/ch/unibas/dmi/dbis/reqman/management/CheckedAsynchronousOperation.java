package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.ui.StatusBar;
import javafx.concurrent.WorkerStateEvent;

import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class CheckedAsynchronousOperation<T> {
  
  private static final Consumer<Exception> DEFAULT_EXCEPTION_HANDLER = Throwable::printStackTrace;
  private final ManagementTask<T> task;
  private final Thread worker;
  private TreeMap<Integer, Predicate<T>> validators = new TreeMap<>();
  private TreeMap<Integer, Consumer<T>> processors = new TreeMap<>();
  private Consumer<Exception> exceptionHandler;
  private StatusBar statusBar;
  private String message = null;
  
  public CheckedAsynchronousOperation(final ManagementTask<T> task) {
    this.worker = new Thread(task);
    this.task = task;
    
    prepare();
  }
  
  public CheckedAsynchronousOperation(final ManagementTask<T> task, boolean background) {
    this(task);
    this.worker.setDaemon(background);
  }
  
  public void setExceptionHandler(Consumer<Exception> handler) {
    this.exceptionHandler = handler;
  }
  
  public void setExceptionMessage(String message) {
    this.message = message;
  }
  
  public void start() {
    worker.start();
  }
  
  public void addValidator(Predicate<T> validator) {
    validators.put(10, validator);
  }
  
  public void addProcessor(Consumer<T> processor) {
    processors.put(10, processor);
  }
  
  /**
   * @param validator
   * @param weight,   the lower the higher priorty
   */
  public void addValidator(Predicate<T> validator, int weight) {
    validators.put(weight, validator);
  }
  
  public void addProcessor(Consumer<T> processor, int weight) {
    processors.put(weight, processor);
  }
  
  ManagementTask<T> getTask() {
    return task;
  }
  
  void setStatusBar(StatusBar bar) {
    this.statusBar = bar;
    statusBar.messageProperty().bind(task.messageProperty());
  }
  
  private void prepare() {
    task.setOnSucceeded(event -> {
      try {
        done(event);
      } catch (IllegalStateException ex) {
        if (exceptionHandler != null) {
          exceptionHandler.accept(ex);
        } else {
          DEFAULT_EXCEPTION_HANDLER.accept(ex);
        }
      }
    });
    
  }
  
  private void done(WorkerStateEvent event) throws IllegalStateException {
    T result = task.getValue();
    
    validators.values().forEach(v -> {
      if (!v.test(result)) {
        throw new IllegalStateException(message);
      }
    });
    
    processors.values().forEach(p -> p.accept(result));
  }
  
}
