package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;

import java.io.File;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
class SaveCatalogueTask extends ManagementTask<Boolean> {

    private final File saveFile;
    private final Catalogue catalogue;

    SaveCatalogueTask(Catalogue cat, File saveFile) {
        LOGGER.entry(saveFile, cat);
        this.saveFile = saveFile;
        this.catalogue = cat;
    }

    @Override
    protected Boolean call() throws Exception {
        LOGGER.trace(":call");
        updateAll("Saving catalogue (" + saveFile.getPath() + ")", 0.2);
        JSONUtils.writeToJSONFile(catalogue, saveFile);
        updateAll("Successfully saved catalogue to (" + saveFile.getPath() + ")", 1.0);
        return LOGGER.traceExit(true);
    }
}
