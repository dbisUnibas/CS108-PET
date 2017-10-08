package ch.unibas.dmi.dbis.reqman.data;

import java.util.Date;
import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Milestone {
  
  private final UUID uuid;
  
  private int ordinal = 0;
  
  private String name;
  
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
  
  
  public int getOrdinal() {
    return ordinal;
  }
  
  // TODO reduce visibility
  public void setOrdinal(int ordinal) {
    this.ordinal = ordinal;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Date getDate() {
    return date;
  }
  
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
    int result = getUuid() != null ? getUuid().hashCode() : 0;
    result = 31 * result + getOrdinal();
    result = 31 * result + (getName() != null ? getName().hashCode() : 0);
    result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
    return result;
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Milestone{");
    sb.append("uuid=").append(uuid);
    sb.append(", ordinal=").append(ordinal);
    sb.append(", name='").append(name).append('\'');
    sb.append(", date=").append(date);
    sb.append('}');
    return sb.toString();
  }
}
