package ch.unibas.dmi.dbis.reqman.data;

import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressSummary {
  
  private final UUID uuid;
  
  private int milestoneOrdinal;
  private String internalComment;
  private String externalComment;
  
  public ProgressSummary(int milestoneOrdinal, String internalComment, String externalComment) {
    this();
    this.milestoneOrdinal = milestoneOrdinal;
    this.internalComment = internalComment;
    this.externalComment = externalComment;
  }
  
  public ProgressSummary() {
    this.uuid = UUID.randomUUID();
  }
  
  
  public int getMilestoneOrdinal() {
    
    return milestoneOrdinal;
  }
  
  public void setMilestoneOrdinal(int milestoneOrdinal) {
    this.milestoneOrdinal = milestoneOrdinal;
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
    result = 31 * result + getMilestoneOrdinal();
    result = 31 * result + (getInternalComment() != null ? getInternalComment().hashCode() : 0);
    result = 31 * result + (getExternalComment() != null ? getExternalComment().hashCode() : 0);
    return result;
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ProgressSummary{");
    sb.append("uuid=").append(uuid);
    sb.append(", milestoneOrdinal=").append(milestoneOrdinal);
    sb.append(", internalComment='").append(internalComment).append('\'');
    sb.append(", externalComment='").append(externalComment).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
