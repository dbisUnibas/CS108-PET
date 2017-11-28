package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.storage.StorageManager;

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
    updateAll("Saving catalogue into (" + StorageManager.getInstance().getSaveDir().getAbsolutePath() + ")", 0.2);
    if (saveFile == null) {
      StorageManager.getInstance().saveCatalogue();
    } else {
      StorageManager.getInstance().saveCatalogue(catalogue);
    }
    updateAll("Successfully saved catalogue to (" + StorageManager.getInstance().getCataloguePath() + ")", 1.0);
    return LOGGER.traceExit(true);
  }
}
