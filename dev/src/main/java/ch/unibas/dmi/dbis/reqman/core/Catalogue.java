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
