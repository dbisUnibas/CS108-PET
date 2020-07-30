package ch.unibas.dmi.dbis.cs108pet.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Progress {
  
  @Deprecated
  public static final double NO_POINTS = -999;
  
  public static final double NO_PROGRESS = -1d;
  
  private final UUID uuid;
  private double fraction = NO_PROGRESS;
  private UUID requirementUUID;
  private Date assessmentDate = null;
  private UUID progressSummaryUUID;
  private String comment;
  
  
  public Progress() {
    uuid = UUID.randomUUID();
  }
  
  public Progress(Progress progress) {
    this();
    fraction = progress.getFraction();
    requirementUUID = progress.getRequirementUUID();
    assessmentDate = progress.getAssessmentDate();
    progressSummaryUUID = progress.getProgressSummaryUUID();
    comment = progress.getComment();
  }
  
  public double getFraction() {
    return fraction;
  }
  
  public void setFraction(double fraction) {
    this.fraction = fraction;
  }
  
  public UUID getRequirementUUID() {
    return requirementUUID;
  }
  
  public void setRequirementUUID(UUID requirementUUID) {
    this.requirementUUID = requirementUUID;
  }
  
  public Date getAssessmentDate() {
    return assessmentDate;
  }
  
  public void setAssessmentDate(Date assessmentDate) {
    this.assessmentDate = assessmentDate;
  }
  
  public UUID getProgressSummaryUUID() {
    return progressSummaryUUID;
  }
  
  public void setProgressSummaryUUID(UUID progressSummaryUUID) {
    this.progressSummaryUUID = progressSummaryUUID;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  
  /**
   * Returns whether this progress was freshly created.
   * A non-freshly created progress was either loaded from a group file
   * or was modified by a user.
   *
   * @return Whether this progress was freshly created. Then it returns {@code true}, {@code false} otherwise.
   */
  @JsonIgnore
  public boolean isFresh() {
    return fraction == NO_PROGRESS;
  }
  
  
  @JsonIgnore
  public boolean hasProgress() {
    return fraction > 0;
  }
  
  
  public UUID getUuid() {
    return uuid;
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Progress{");
    sb.append("uuid=").append(uuid);
    sb.append(", fraction=").append(fraction);
    sb.append(", requirementUUID=").append(requirementUUID);
    sb.append(", assessmentDate=").append(assessmentDate);
    sb.append(", progressSummaryUUID=").append(progressSummaryUUID);
    sb.append(", comment='").append(comment).append('\'');
    sb.append('}');
    return sb.toString();
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    Progress progress = (Progress) o;
    
    return getUuid().equals(progress.getUuid());
  }
  
  @Override
  public int hashCode() {
    int result;
    long temp;
    result = getUuid().hashCode();
    temp = Double.doubleToLongBits(fraction);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (requirementUUID != null ? requirementUUID.hashCode() : 0);
    result = 31 * result + (assessmentDate != null ? assessmentDate.hashCode() : 0);
    result = 31 * result + (progressSummaryUUID != null ? progressSummaryUUID.hashCode() : 0);
    result = 31 * result + (comment != null ? comment.hashCode() : 0);
    return result;
  }
  
  @JsonIgnore
  public void reset() {
    fraction = NO_PROGRESS;
    assessmentDate = null;
  }
}