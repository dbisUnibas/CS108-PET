package ch.unibas.dmi.dbis.cs108pet.management;

import ch.unibas.dmi.dbis.cs108pet.data.Group;

import java.io.File;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class SaveGroupBackupTask extends ManagementTask<Boolean> {
  
  private final Group group;
  private final File catalogueFile;
  
  public SaveGroupBackupTask(Group group, File catalogueFile) {
    this.group = group;
    this.catalogueFile = catalogueFile;
  }
  
  @Override
  protected Boolean call() throws Exception {
        /*File location = ConfigUtils.getCodeSourceLocation();
        File dir = location.getParentFile();
//        File backup = new File(dir.getPath() + ConfigUtils.getFileSeparator() + StringUtils.prettyPrintTimestamp(System.currentTimeMillis(), "YYYY-MM-dd-HH-mm-ss-SSS") + "." + EntityManager.BACKUP_EXTENSION);
        updateAll("Located backup file", 0.2);
        HashMap<String, Object> backupObj = new HashMap<>();
//        backupObj.put(EntityManager.CATALOGUE_KEY, catalogueFile != null ? catalogueFile.getAbsolutePath() : null);
//        backupObj.put(EntityManager.GROUP_KEY, JSONUtils.toJSON(group));

        JSONUtils.writeToJSONFile(backupObj, backup);
        updateAll("Wrote backup of group " + group.getName() + " to " + backup.getPath(), 1.0);
        LOGGER.info("Saved backup of group " + group.getName() + " to " + backup.getPath());*/
    return true;
  }
}
