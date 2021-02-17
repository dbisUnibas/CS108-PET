package ch.unibas.dmi.dbis.cs108pet.common;

import java.util.UUID;

/**
 * Caused when an entity is tried to be opened again.
 *
 * @author loris.sauter
 */
public class EntityAlreadyOpenException extends RuntimeException {
  
  private final UUID uuid;
  private final String entityType;
  
  
  public EntityAlreadyOpenException(UUID uuid, String entityType) {
    super("Entity of type (" + entityType + ") and uuid=" + uuid.toString() + " already opened");
    this.uuid = uuid;
    this.entityType = entityType;
  }
  
  public EntityAlreadyOpenException(String msg, UUID uuid, String entityType) {
    super(msg);
    this.uuid = uuid;
    this.entityType = entityType;
  }
  
  public UUID getUuid() {
    return uuid;
  }
  
  public String getEntityType() {
    return entityType;
  }
}
