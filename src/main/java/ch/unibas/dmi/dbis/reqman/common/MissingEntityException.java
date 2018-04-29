package ch.unibas.dmi.dbis.reqman.common;

/**
 * Raised whenever an entity is missing.
 *
 * @author loris.sauter
 */
public class MissingEntityException extends Exception {
  
  private final NamedEntity entity;
  private final String missing;
  
  public MissingEntityException(NamedEntity entity, String missing){
    super();
    this.entity = entity;
    this.missing = missing;
  }
  
  public MissingEntityException(String message, NamedEntity entity, String missing){
    super(message);
    this.entity = entity;
    this.missing = missing;
  }
  
  public MissingEntityException(String message, Throwable cause, NamedEntity entity, String missing){
    super(message, cause);
    this.entity = entity;
    this.missing = missing;
  }
  
  public NamedEntity getEntity() {
    return entity;
  }
  
  public String getMissing() {
    return missing;
  }
}
