package ch.unibas.dmi.dbis.reqman.core;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Progress {

    private String requirementName;
    private String milestoneName;
    private double points;


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

        if (Double.compare(progress.getPoints(), getPoints()) != 0) {
            return false;
        }
        if (getRequirementName() != null ? !getRequirementName().equals(progress.getRequirementName()) : progress.getRequirementName() != null) {
            return false;
        }
        return getMilestoneName() != null ? getMilestoneName().equals(progress.getMilestoneName()) : progress.getMilestoneName() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getRequirementName() != null ? getRequirementName().hashCode() : 0;
        result = 31 * result + (getMilestoneName() != null ? getMilestoneName().hashCode() : 0);
        temp = Double.doubleToLongBits(getPoints());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public Progress(String requirementName, String milestoneName, double points) {

        this.requirementName = requirementName;
        this.milestoneName = milestoneName;
        this.points = points;
    }

    public String getRequirementName() {
        return requirementName;
    }

    public void setRequirementName(String requirementName) {
        this.requirementName = requirementName;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }
}
