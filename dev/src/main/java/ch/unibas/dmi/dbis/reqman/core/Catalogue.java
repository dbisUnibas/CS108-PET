package ch.unibas.dmi.dbis.reqman.core;

import java.util.List;
import java.util.Vector;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Catalogue {

    private String lecture;
    private String name;
    private String description;
    private String semester;

    private List<Milestone> milestones = new Vector<Milestone>();
    private List<Requirement> requirements = new Vector<Requirement>();

    public Catalogue() {

    }

    public Catalogue(String lecture, String name, String description, String semester) {
        this.lecture = lecture;
        this.name = name;
        this.description = description;
        this.semester = semester;
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSemester() {
        return semester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Catalogue catalogue = (Catalogue) o;

        if (getLecture() != null ? !getLecture().equals(catalogue.getLecture()) : catalogue.getLecture() != null) {
            return false;
        }
        if (getName() != null ? !getName().equals(catalogue.getName()) : catalogue.getName() != null) {
            return false;
        }
        if (getDescription() != null ? !getDescription().equals(catalogue.getDescription()) : catalogue.getDescription() != null) {
            return false;
        }
        if (getSemester() != null ? !getSemester().equals(catalogue.getSemester()) : catalogue.getSemester() != null) {
            return false;
        }
        if (milestones != null ? !milestones.equals(catalogue.milestones) : catalogue.milestones != null) {
            return false;
        }
        return requirements != null ? requirements.equals(catalogue.requirements) : catalogue.requirements == null;
    }

    @Override
    public int hashCode() {
        int result = getLecture() != null ? getLecture().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getSemester() != null ? getSemester().hashCode() : 0);
        result = 31 * result + (milestones != null ? milestones.hashCode() : 0);
        result = 31 * result + (requirements != null ? requirements.hashCode() : 0);
        return result;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public boolean addMilestone(Milestone milestone) {
        return milestones.add(milestone);
    }

    public boolean removeMilestone(Milestone milestone) {
        return milestones.remove(milestone);
    }

    public boolean addRequirement(Requirement requirement) {
        return requirements.add(requirement);
    }

    public boolean removeRequirement(Requirement requirement) {
        return requirements.remove(requirement);
    }

}
