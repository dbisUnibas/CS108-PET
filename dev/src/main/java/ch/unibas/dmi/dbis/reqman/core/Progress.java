package ch.unibas.dmi.dbis.reqman.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.DoubleSummaryStatistics;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Progress {

    private String requirementName;
    private int milestoneOrdinal;
    private double points;

    public double getPercentage() {
        return percentage;
    }

    /**
     *
     * @param percentage
     * @deprecated Since the percentage / fraction is calculated while setting the points.
     */
    @Deprecated
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    private double percentage = -1d;


    public Progress() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Progress progress = (Progress) o;

        if (getMilestoneOrdinal() != progress.getMilestoneOrdinal()) {
            return false;
        }
        if (Double.compare(progress.getPoints(), getPoints()) != 0) {
            return false;
        }
        return getRequirementName() != null ? getRequirementName().equals(progress.getRequirementName()) : progress.getRequirementName() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getRequirementName() != null ? getRequirementName().hashCode() : 0;
        result = 31 * result + getMilestoneOrdinal();
        temp = Double.doubleToLongBits(getPoints());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public Progress(String requirementName, int milestoneOrdinal, double points) {

        this.requirementName = requirementName;
        this.milestoneOrdinal = milestoneOrdinal;
        this.points = points;
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

    public void setPoints(double points, double max){
        this.points = points;
        if(Double.compare(0d, max) == 0 && Double.compare(0d, points)==0){
            // if max points == points == 0 -> progress 100%
            percentage = 1d;
        }else{
            percentage = points / max;
        }
    }

    @JsonIgnore
    public double getPointsSensitive(Catalogue catalogue){
        Requirement r = catalogue.getRequirementByName(requirementName);
        double factor = r.isMalus() ? -1d : 1d;
        return factor * points;
    }

    @JsonIgnore
    public boolean hasProgress(){
        return percentage > 0;
    }

    @JsonIgnore
    public boolean hasDefaultPercentage() {
        return Double.compare(-1d, percentage)==0;
    }
}
