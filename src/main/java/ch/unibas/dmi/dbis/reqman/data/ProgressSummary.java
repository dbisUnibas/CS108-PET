package ch.unibas.dmi.dbis.reqman.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgressSummary {
  
  private final UUID uuid;
  private UUID milestoneUUID;
  
  
  private String internalComment;
  private String externalComment;
  
  
  public ProgressSummary() {
    this.uuid = UUID.randomUUID();
  }
  
  public ProgressSummary(ProgressSummary ps) {
    this();
    milestoneUUID = ps.getMilestoneUUID();
    internalComment = ps.getInternalComment();
    externalComment = ps.getExternalComment();
  }
  
  
  public UUID getMilestoneUUID() {
    return milestoneUUID;
  }
  
  public void setMilestoneUUID(UUID milestoneUUID) {
    this.milestoneUUID = milestoneUUID;
  }
  
  public String getInternalComment() {
    return internalComment;
  }
  
  public void setInternalComment(String internalComment) {
    this.internalComment = internalComment;
  }
  
  public String getExternalComment() {
    return externalComment;
  }
  
  public void setExternalComment(String externalComment) {
    this.externalComment = externalComment;
  }
  
  public UUID getUuid() {
    return uuid;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    ProgressSummary that = (ProgressSummary) o;
    
    return getUuid().equals(that.getUuid());
  }
  
  @Override
  public int hashCode() {
    int result = getUuid().hashCode();
    result = 31 * result + (milestoneUUID != null ? milestoneUUID.hashCode() : 0);
    result = 31 * result + (getInternalComment() != null ? getInternalComment().hashCode() : 0);
    result = 31 * result + (getExternalComment() != null ? getExternalComment().hashCode() : 0);
    return result;
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ProgressSummary{");
    sb.append("uuid=").append(uuid);
    sb.append(", milestoneUUID=").append(milestoneUUID);
    sb.append(", internalComment='").append(internalComment).append('\'');
    sb.append(", externalComment='").append(externalComment).append('\'');
    sb.append('}');
    return sb.toString();
  }
  
}
