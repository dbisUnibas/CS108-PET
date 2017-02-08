package ch.unibas.dmi.dbis.reqman.core;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressSummary {

    private String milestoneName;
    private String internalComment;
    private String externalComment;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProgressSummary that = (ProgressSummary) o;

        if (getMilestoneName() != null ? !getMilestoneName().equals(that.getMilestoneName()) : that.getMilestoneName() != null) {
            return false;
        }
        if (getInternalComment() != null ? !getInternalComment().equals(that.getInternalComment()) : that.getInternalComment() != null) {
            return false;
        }
        return getExternalComment() != null ? getExternalComment().equals(that.getExternalComment()) : that.getExternalComment() == null;
    }

    @Override
    public int hashCode() {
        int result = getMilestoneName() != null ? getMilestoneName().hashCode() : 0;
        result = 31 * result + (getInternalComment() != null ? getInternalComment().hashCode() : 0);
        result = 31 * result + (getExternalComment() != null ? getExternalComment().hashCode() : 0);
        return result;
    }

    public String getMilestoneName() {

        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
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

    public ProgressSummary(String milestoneName, String internalComment, String externalComment) {

        this.milestoneName = milestoneName;
        this.internalComment = internalComment;
        this.externalComment = externalComment;
    }

    public ProgressSummary() {

    }
}
