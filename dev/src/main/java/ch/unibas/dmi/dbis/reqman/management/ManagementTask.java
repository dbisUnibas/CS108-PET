package ch.unibas.dmi.dbis.reqman.management;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public abstract class ManagementTask<R> extends Task<R> {
    protected static final Logger LOGGER = LogManager.getLogger(ManagementTask.class);

    protected void updateAll(String message, double progress) {
        LOGGER.trace(message + String.format("(Progress: %f)", progress / 1.0));
        updateMessage(message);
        updateProgress(progress, 1.0);
    }
}
