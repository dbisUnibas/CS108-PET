package ch.unibas.dmi.dbis.reqman.backup;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.session.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
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
  
  public List<Group> load() {
    return load(getBackupDescriptionLocation());
  }
  
  public List<Group> load(String path) {
    return load(Paths.get(path));
  }
  
  public List<Group> load(Path path) {
    LOGGER.debug("Loading backups from {}", path);
    File f = path.toFile();
    if (!f.exists()) {
      LOGGER.info("No backups to load");
      return new ArrayList<>();
    }
    try {
      loc = JSONUtils.readFromJSONFile(f, BackupLocations.class);
      LOGGER.debug("Found backup descriptions: {}", JSONUtils.toJSON(loc));
    } catch (IOException e) {
      LOGGER.catching(e);
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
    if (unsavedGroups.isEmpty()) {
      return; // Don't write any backups if nothing is there to write #jodaspeak
    }
    getDefaultBackupLocation().toFile().mkdirs();
    BackupLocations locs = new BackupLocations();
    for (Group g : unsavedGroups) {
      try {
        locs.add(storeBackup(g));
        LOGGER.info("Stored a backup of group {}", g.getName());
      } catch (IOException e) {
        LOGGER.error("Couldn't write backup for group {}. Continuing...", g.getName());
        LOGGER.error(e);
      }
    }
    try {
      LOGGER.debug("Locs before write: {}", JSONUtils.toJSON(locs));
      JSONUtils.writeToJSONFile(locs, getBackupDescriptionLocation().toFile());
      LOGGER.info("Wrote backup locations at {}", getBackupDescriptionLocation());
    } catch (IOException e) {
      LOGGER.error("Couldn't write backuplocations. This is generally bad and may lead to data loss");
      LOGGER.error(e);
    }
    unsavedGroups.clear();
  }
  
  public void clean() {
    try {
      Files.deleteIfExists(getBackupDescriptionLocation());
    } catch (IOException e) {
      LOGGER.error("Couldn't delete backup location");
      LOGGER.error(e);
    }
    try {
      
      try {
        Files.deleteIfExists(getDefaultBackupLocation());
      } catch (DirectoryNotEmptyException ex) {
        for (File f : getDefaultBackupLocation().toFile().listFiles()) {
          f.delete();
        }
        Files.deleteIfExists(getDefaultBackupLocation());
      }
    } catch (IOException e) {
      LOGGER.error("Couldn't delete backup directory. May a manual deletion is required.");
      LOGGER.error(e);
    }
  }
  
  private BackupDescription storeBackup(Group g) throws IOException {
    BackupDescription desc = new BackupDescription(g.getUuid(), getDefaultBackupLocation().resolve(g.getName() + BACKUP_EXTENSION).toFile().getAbsolutePath());
    JSONUtils.writeToJSONFile(g, new File(desc.getPath()));
    LOGGER.info("Stored a backup of group {} at {}", g.getName(), desc.getPath());
    return desc;
  }
  
  private Group readBackup(String path) throws IOException {
    Group g = JSONUtils.readFromJSONFile(new File(path), Group.class);
    LOGGER.debug("Loaded group g {} from {}", g.getName(), path);
    return g;
  }
  
  private Path getDefaultBackupLocation() {
    return Paths.get(System.getProperty("user.home"), SessionManager.REQMAN_DIRECTORY, DEFAULT_BACKUP_DIRECTORY);
  }
  
  private Path getBackupDescriptionLocation() {
    return Paths.get(System.getProperty("user.home"), SessionManager.REQMAN_DIRECTORY, BACKUP_DESCRIPTION);
  }
  
}
