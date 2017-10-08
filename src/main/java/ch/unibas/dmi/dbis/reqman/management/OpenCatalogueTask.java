package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;

import java.io.File;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
class OpenCatalogueTask extends ManagementTask<Catalogue> {

    private final File openFile;

    OpenCatalogueTask(File openFile) {
        LOGGER.entry(openFile);
        this.openFile = openFile;
    }


    @Override
    protected Catalogue call() throws Exception {
        LOGGER.trace(":call");
        updateAll("Started to read... (" + openFile.getPath() + ")", 0.2);
        Catalogue cat = JSONUtils.readCatalogueJSONFile(openFile);
        LOGGER.info("Successfully read catalogue file " + openFile.getPath());
        updateAll("Successfully read catalogue from (" + openFile.getPath() + ")", 1.0);
        return LOGGER.traceExit(cat);
    }

}
