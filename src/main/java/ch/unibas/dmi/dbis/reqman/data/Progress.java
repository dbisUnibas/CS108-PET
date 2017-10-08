package ch.unibas.dmi.dbis.reqman.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Progress {
  
  public static final double NO_POINTS = -999;
  private final UUID uuid;
  private String requirementName;
  private int milestoneOrdinal;
  private double points = 0;
  private double percentage = -1d;
  private Date date = null;
  
  public Progress() {
    uuid = UUID.randomUUID();
  }
  
  public Progress(String requirementName, int milestoneOrdinal, double points) {
    this();
    this.requirementName = requirementName;
    this.milestoneOrdinal = milestoneOrdinal;
    this.points = points;
  }
  
  public Progress(Requirement req) {
    this(req.getName(), req.getMinMilestoneOrdinal(), 0);
  }
  
  public Date getDate() {
    return date;
  }
  
  public void setDate(Date date) {
    this.date = date;
  }
  
  public double getPercentage() {
    return percentage;
  }
  
  /**
   * @param percentage
   * @deprecated Since the percentage / fraction is calculated while setting the points.
   */
  @Deprecated
  public void setPercentage(double percentage) {
    this.percentage = percentage;
  }
  
  
  public String getRequirementName() {
    return requirementName;
  }
  
  public void setRequirementName(String requirementName) {
    this.requirementName = requirementName;
  }
  
  public int getMilestoneOrdinal() {
    return milestoneOrdinal;
  }
  
  public void setMilestoneOrdinal(int milestoneOrdinal) {
    this.milestoneOrdinal = milestoneOrdinal;
  }
  
  public double getPoints() {
    return points;
  }
  
  @Deprecated
  public void setPoints(double points) {
    this.points = points;
  }
  
  public void setPoints(double points, double max) {
    if (points == NO_POINTS) {
      percentage = 0d;
      this.points = -1;
      return;
    }
    
    this.points = points;
    if (Double.compare(0d, max) == 0 && Double.compare(0d, points) == 0) {
      // if max points == points == 0 -> progress 100%
      percentage = 1d;
    } else {
      percentage = points / max;
    }
  }
  
  @JsonIgnore
  public double getPointsSensitive(Catalogue catalogue) {
    Requirement r = catalogue.getRequirementByName(requirementName);
    double factor = (r.isMalus() ? -1d : 1d) * percentage;
    return factor * r.getMaxPoints();
  }
  
  @JsonIgnore
  public boolean hasProgress() {
    return percentage > 0;
  }
  
  @JsonIgnore
  public boolean hasDefaultPercentage() {
    return Double.compare(-1d, percentage) == 0;
  }
  
  
  
  public UUID getUuid() {
    return uuid;
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Progress{");
    sb.append("uuid=").append(uuid);
    sb.append(", requirementName='").append(requirementName).append('\'');
    sb.append(", milestoneOrdinal=").append(milestoneOrdinal);
    sb.append(", points=").append(points);
    sb.append(", percentage=").append(percentage);
    sb.append(", date=").append(date);
    sb.append('}');
    return sb.toString();
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    Progress progress = (Progress) o;
    
    return getUuid().equals(progress.getUuid() );
  }
  
  @Override
  public int hashCode() {
    int result;
    long temp;
    result = getUuid() != null ? getUuid().hashCode() : 0;
    result = 31 * result + (getRequirementName() != null ? getRequirementName().hashCode() : 0);
    result = 31 * result + getMilestoneOrdinal();
    temp = Double.doubleToLongBits(getPoints());
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(getPercentage());
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
    return result;
  }
}