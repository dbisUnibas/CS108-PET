package ch.unibas.dmi.dbis.reqman.backup;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.session.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class BackupManager {
  
  public static final String DEFAULT_BACKUP_DIRECTORY = "backups";
  private static final String BACKUP_DESCRIPTION = "backup.json";
  private static final String BACKUP_EXTENSION = ".backup";
  private static final Logger LOGGER = LogManager.getLogger();
  private static BackupManager ourInstance = new BackupManager();
  private BackupLocations loc;
  private ArrayList<Group> unsavedGroups = new ArrayList<>();
  
  private BackupManager() {
  }
  
  public static BackupManager getInstance() {
    return ourInstance;
  }
  
  public List<Group> load(){
    return load(getBackupDescriptionLocation());
  }
  
  public List<Group> load(String path) {
    return load(Paths.get(path));
  }
  
  public List<Group> load(Path path) {
    File f = path.toFile();
    try {
      loc = JSONUtils.readFromJSONFile(f, BackupLocations.class);
    } catch (IOException e) {
      loc = BackupLocations.empty();
    }
    ArrayList<Group> out = new ArrayList<>();
    loc.getLocations().forEach(l -> {
      try {
        out.add(readBackup(l.getPath()));
      } catch (IOException e) {
        LOGGER.error("Couldn't read backup file {}. Ignoring it", l.getPath());
        LOGGER.error(e);
      }
    });
    return out;
  }
  
  public void addUnsavedGroup(Group g) {
    if (!unsavedGroups.contains(g)) {
      unsavedGroups.add(g);
    }
  }
  
  public void removeUnsavedGroup(Group g) {
    unsavedGroups.remove(g);
  }
  
  public void storeBackups() {
    BackupLocations locs = new BackupLocations();
    for(Group g : unsavedGroups){
      try {
        locs.add(storeBackup(g));
      } catch (IOException e) {
        LOGGER.error("Couldn't write backup for group {}. Continuing...", g.getName());
        LOGGER.error(e);
      }
    }
    try {
      JSONUtils.writeToJSONFile(locs, getDefaultBackupLocation().toFile());
    } catch (IOException e) {
      LOGGER.error("Couldn't write backuplocations. This is generally bad and may lead to data loss");
    }
    unsavedGroups.clear();
  }
  
  private BackupDescription storeBackup(Group g) throws IOException {
    BackupDescription desc = new BackupDescription(g.getUuid(), getDefaultBackupLocation().resolve(g.getName()+"."+BACKUP_EXTENSION).toFile().getAbsolutePath());
    JSONUtils.writeToJSONFile(g, new File(desc.getPath()));
    return desc;
  }
  
  private Group readBackup(String path) throws IOException {
    return JSONUtils.readFromJSONFile(new File(path), Group.class);
  }
  
  private Path getDefaultBackupLocation() {
    return Paths.get(System.getProperty("user.dir"), SessionManager.REQMAN_DIRECTORY, DEFAULT_BACKUP_DIRECTORY);
  }
  
  private Path getBackupDescriptionLocation() {
    return Paths.get(System.getProperty("user.dir"), SessionManager.REQMAN_DIRECTORY, BACKUP_DESCRIPTION);
  }
  
}
