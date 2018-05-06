package ch.unibas.dmi.dbis.reqman.backup;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Dataclass
 *
 * @author loris.sauter
 */
public class BackupLocations {
  
  private List<BackupDescription> locations = new ArrayList<>();
  
  public BackupLocations() {
  }
  
  public BackupLocations(List<BackupDescription> locations) {
    this.locations = locations;
  }
  
  public static BackupLocations empty() {
    return new BackupLocations();
  }
  
  @JsonIgnore public boolean add(BackupDescription backupDescription) {
    return locations.add(backupDescription);
  }
  
  @JsonIgnore
  public boolean isEmpty() {
    return locations.isEmpty();
  }
  
  public List<BackupDescription> getLocations() {
    return locations;
  }
  
  public void setLocations(List<BackupDescription> locations) {
    this.locations = locations;
  }
}
