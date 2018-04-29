package ch.unibas.dmi.dbis.reqman.common;

/**
 * A {@link VersionedEntity} is an abstract class, which provides the means to store and retrieve the version.
 *
 * This is an abstract class (if Java 1.9 would be target, this could be replaced by an interface), which simply provies
 * getter and setter for its version field.
 *
 * @author loris.sauter
 */
public abstract class VersionedEntity implements NamedEntity {
  
  private String version = Version.NO_VERSION;
  
  public String getVersion(){
    return version;
  }
  
  public void setVersion(String version){
    this.version = version;
  }
  
  protected void setVersionInternally(String version){
    this.version = version;
  }
}
