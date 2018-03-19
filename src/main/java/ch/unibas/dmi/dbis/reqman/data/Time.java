package ch.unibas.dmi.dbis.reqman.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Time {
  
  private final UUID uuid;
  
  private Date date;
  
  public Time() {
    this.uuid = UUID.randomUUID();
  }
  
  public Time(Date date) {
    this();
    this.date = date;
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
    
    Time time = (Time) o;
    return getUuid().equals(time.getUuid());
  }
  
  @Override
  public int hashCode() {
    int result = getUuid() != null ? getUuid().hashCode() : 0;
    result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
    return result;
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Time{");
    sb.append("uuid=").append(uuid);
    sb.append(", date=").append(date);
    sb.append('}');
    return sb.toString();
  }
}
