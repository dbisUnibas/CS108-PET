package ch.unibas.dmi.dbis.reqman.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Milestone {
  
  private final UUID uuid;
  private String name;
  private UUID timeUUID;
  
  /**
   * @deprecated Since SNAPSHOT-2.0.0: Replaced by Time's natural ordering
   */
  @Deprecated
  @JsonIgnore
  private int ordinal = 0;
  /**
   * @deprecated Since SNAPSHOT-2.0.0: Repalced by reference to time
   */
  @Deprecated  @JsonIgnore
  private Date date;
  
  public Milestone() {
    uuid = UUID.randomUUID();
  }
  
  public Milestone(String name, int ordinal, Date date) {
    this();
    this.name = name;
    this.ordinal = ordinal;
    this.date = date;
  }
  
  public UUID getTimeUUID() {
    return timeUUID;
  }
  
  public void setTimeUUID(UUID timeUUID) {
    this.timeUUID = timeUUID;
  }
  
  @Deprecated
  public int getOrdinal() {
    return ordinal;
  }
  
  @Deprecated
  public void setOrdinal(int ordinal) {
    this.ordinal = ordinal;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  @Deprecated
  public Date getDate() {
    return date;
  }
  
  @Deprecated
  public void setDate(Date date) {
    this.date = date;
  }
  
  
  public UUID getUuid() {
    return uuid;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    Milestone milestone = (Milestone) o;
    return getUuid().equals(milestone.getUuid());
  }
  
  @Override
  public int hashCode() {
    int result = getUuid().hashCode();
    result = 31 * result + (getName() != null ? getName().hashCode() : 0);
    result = 31 * result + (getTimeUUID() != null ? getTimeUUID().hashCode() : 0);
    return result;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Milestone{");
    sb.append("uuid=").append(uuid);
    sb.append(", name='").append(name).append('\'');
    sb.append(", timeUUID='").append(timeUUID).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
