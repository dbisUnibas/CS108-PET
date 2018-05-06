package ch.unibas.dmi.dbis.reqman.backup;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

/**
 * Data class which holds a uuid and a path.
 * Used to store information on where to find backup file(s) per entity-id.
 *
 * @author loris.sauter
 */
class BackupDescription {
  
  private UUID uuid;
  private String path;
  
  public BackupDescription(UUID uuid, String path) {
    this.uuid = uuid;
    this.path = path;
  }
  
  public static BackupDescription empty() {
    return new BackupDescription(null,null);
  }
  
  @JsonIgnore
  public boolean isEmpty(){
    return uuid == null && path == null;
  }
  
  public UUID getUuid() {
    return uuid;
  }
  
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
  
  public String getPath() {
    return path;
  }
  
  public void setPath(String path) {
    this.path = path;
  }
}
