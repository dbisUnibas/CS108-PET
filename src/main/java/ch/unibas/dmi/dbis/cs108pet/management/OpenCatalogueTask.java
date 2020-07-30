package ch.unibas.dmi.dbis.cs108pet.management;

import ch.unibas.dmi.dbis.cs108pet.data.Catalogue;
import ch.unibas.dmi.dbis.cs108pet.storage.StorageManager;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
class OpenCatalogueTask extends ManagementTask<Catalogue> {
  
  
  OpenCatalogueTask() {
  
  }
  
  
  @Override
  protected Catalogue call() throws Exception {
    LOGGER.trace(":call");
    updateAll("Opening Catalogue in (" + StorageManager.getInstance().getSaveDir().getAbsolutePath() + ")", 0.2);
    Catalogue cat = StorageManager.getInstance().openCatalogue();
    LOGGER.info("Successfully read catalogue file " + StorageManager.getInstance().getCataloguePath());
    updateAll("Successfully read catalogue from (" + StorageManager.getInstance().getCataloguePath() + ")", 1.0);
    return LOGGER.traceExit(cat);
  }
  
}
