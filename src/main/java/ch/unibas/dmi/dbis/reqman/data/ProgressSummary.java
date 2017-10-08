package ch.unibas.dmi.dbis.reqman.data;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressSummary {

    private int milestoneOrdinal;
    private String internalComment;
    private String externalComment;

    public ProgressSummary(int milestoneOrdinal, String internalComment, String externalComment) {

        this.milestoneOrdinal = milestoneOrdinal;
        this.internalComment = internalComment;
        this.externalComment = externalComment;
    }

    public ProgressSummary() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProgressSummary that = (ProgressSummary) o;

        if (getMilestoneOrdinal() != that.getMilestoneOrdinal()) {
            return false;
        }
        if (getInternalComment() != null ? !getInternalComment().equals(that.getInternalComment()) : that.getInternalComment() != null) {
            return false;
        }
        return getExternalComment() != null ? getExternalComment().equals(that.getExternalComment()) : that.getExternalComment() == null;
    }

    @Override
    public int hashCode() {
        int result = getMilestoneOrdinal();
        result = 31 * result + (getInternalComment() != null ? getInternalComment().hashCode() : 0);
        result = 31 * result + (getExternalComment() != null ? getExternalComment().hashCode() : 0);
        return result;
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
}
