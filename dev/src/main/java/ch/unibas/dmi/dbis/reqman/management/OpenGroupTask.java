package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Group;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class OpenGroupTask extends ThrowingManagementTask<Group> {

    private static final Logger LOGGER = LogManager.getLogger(OpenGroupTask.class);

    private final File file;

    public OpenGroupTask(File file) {
        this.file = file;
    }

    @Override
    protected Group call() throws Exception {
        updateAll("Opening group " + file.getName() + "...", 0.2);
        Group gr = JSONUtils.readGroupJSONFile(file);
        updateAll("Succsessfully opened group " + gr.getName() + ".", 1.0);
        return gr;
    }
}
