package ch.unibas.dmi.dbis.reqman.configuration;

/**
 * Container class to hold the template(-string)s.
 *
 * All the returning templates / templates to set are considered to be formatted in RMTL.
 *
 * @author loris.sauter
 */
public class Templates {

    private String requirementTemplate;
    private String milestoneTemplate;
    private String catalogueTemplate;
    private String progressTemplate;
    private String groupMilestoneTemplate;
    private String groupTemplate;
    private String overviewTemplate;

    public Templates() {

    }

    public String getRequirementTemplate() {
        return requirementTemplate;
    }

    public void setRequirementTemplate(String requirementTemplate) {
        this.requirementTemplate = requirementTemplate;
    }

    public String getMilestoneTemplate() {
        return milestoneTemplate;
    }

    public void setMilestoneTemplate(String milestoneTemplate) {
        this.milestoneTemplate = milestoneTemplate;
    }

    public String getCatalogueTemplate() {
        return catalogueTemplate;
    }

    public void setCatalogueTemplate(String catalogueTemplate) {
        this.catalogueTemplate = catalogueTemplate;
    }

    public String getProgressTemplate() {
        return progressTemplate;
    }

    public void setProgressTemplate(String progressTemplate) {
        this.progressTemplate = progressTemplate;
    }

    public String getGroupMilestoneTemplate() {
        return groupMilestoneTemplate;
    }

    public void setGroupMilestoneTemplate(String groupMilestoneTemplate) {
        this.groupMilestoneTemplate = groupMilestoneTemplate;
    }

    public String getGroupTemplate() {
        return groupTemplate;
    }

    public void setGroupTemplate(String groupTemplate) {
        this.groupTemplate = groupTemplate;
    }

    public String getOverviewTemplate() {
        return overviewTemplate;
    }

    public void setOverviewTemplate(String template) {
        this.overviewTemplate = template;
    }
}
