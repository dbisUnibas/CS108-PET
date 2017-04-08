package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
class OpenCatalogueTask extends Task<Catalogue> {

    private static final Logger LOGGER = LogManager.getLogger(OpenCatalogueTask.class);

    private final File openFile;

    OpenCatalogueTask(File openFile) {
        LOGGER.entry(openFile);
        this.openFile = openFile;
    }


    @Override
    protected Catalogue call() throws Exception {
        LOGGER.trace(":call");
        updateAll("Started to read...", 0.2);
        Catalogue cat = JSONUtils.readCatalogueJSONFile(openFile);
        LOGGER.info("Successfully read catalogue file "+openFile.getPath());
        updateAll("Successfully read catalogue.", 1.0);
        return LOGGER.traceExit(cat);
    }

    private void updateAll(String message, double progress){
        updateMessage(message);
        updateProgress(progress, 1.0);
    }

}
