package ch.unibas.dmi.dbis.reqman.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

/**
 * A catalogue is a collection of {@link Milestone}s and {@link Requirement}s.
 *
 * It represents the logical superset of associated milestones and requirements, forming a namespace.
 * A catalogue must have a name and may is associated with a lecture and date.
 *
 * The catalogue class will be written serialized as a json object with jackson library.
 *
 * @author loris.sauter
 */
public class Catalogue {

    private String lecture;
    private String name;
    private String description;
    private String semester;

    /**
     * The very list of milestones
     */
    private List<Milestone> milestones = new Vector<Milestone>();
    /**
     * The very list of requirements
     */
    private List<Requirement> requirements = new Vector<Requirement>();

    @JsonIgnore
    private Map<Integer, List<Requirement>> reqsPerMinMS = new TreeMap<>();

    /**
     * The default constructor
     */
    public Catalogue() {

    }

    /**
     * A constructor for creating a new catalogue with specified name, lecture and semester as well as a description provided.
     * @param lecture The name of the lecture
     * @param name The  name of the catalogue
     * @param description A description of a catalogue
     * @param semester The semester for which this catalogue was designed
     */
    public Catalogue(String lecture, String name, String description, String semester) {
        this.lecture = lecture;
        this.name = name;
        this.description = description;
        this.semester = semester;
    }

    /**
     * Returns the lecture name this catalogue is associated with
     * @return The lecture name this catalogue is associated with
     */
    public String getLecture() {
        return lecture;
    }

    /**
     * Sets the lecture name for which this catalogue is made
     * @param lecture The lecture name
     */
    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    /**
     * Returns this catalogue's name.
     * It will be referenced in {@link Group}s with this name.
     * @return The  name of this catalogue
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this catalogue
     * @param name The (new) name of the catalogue
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of this catalogue
     * @return The description of this catalogue
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets (new) the description of this catalogue
     * @param description The (new) description of the catalogue
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the semester of this catalogue
     * @return The semester of this catalogue
     */
    public String getSemester() {
        return semester;
    }

    /**
     * Sets the semester of this catalogue
     * @param semester The semester represented as a string
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * Checks if the given object and this object are equal.
     * In case the specified object is a catalogue, then the catalogue name's are checked if they are equal
     * @param o The object to test
     * @return TRUE if the specified catalogue's name is similar to this catalogue's name
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Catalogue catalogue = (Catalogue) o;

        return getName().equals(catalogue.getName() );
    }

    /**
     * Returns the hashcode of this object
     * IntelliJ default implementation,
     * @return the haschode of this object
     * @see Object#hashCode()
     */
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

    /**
     * Adds the given milestone to the list of milestones.
     * @param milestone The milestone to add
     * @return the result: TRUE if the addition successfully has been performed
     */
    public boolean addMilestone(Milestone milestone) {
        return milestones.add(milestone);
    }

    public boolean removeMilestone(Milestone milestone) {
        return milestones.remove(milestone);
    }

    public List<Milestone> getMilestones() {
        return new ArrayList<>(milestones);
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public boolean addRequirement(Requirement requirement) {

        if (reqsPerMinMS.get(requirement.getMinMilestoneOrdinal()) != null) {
            reqsPerMinMS.get(requirement.getMinMilestoneOrdinal()).add(requirement);
        } else {
            List<Requirement> list = new ArrayList<>(Arrays.asList(requirement));
            reqsPerMinMS.put(requirement.getMinMilestoneOrdinal(), list);
        }
        return requirements.add(requirement);
    }

    public boolean removeRequirement(Requirement requirement) {
        if (reqsPerMinMS.get(requirement.getMinMilestoneOrdinal()) != null) {
            reqsPerMinMS.get(requirement.getMinMilestoneOrdinal()).remove(requirement);
        }
        return requirements.remove(requirement);

    }

    public List<Requirement> getRequirements() {
        return new ArrayList<>(requirements);
    }

    @JsonIgnore
    public List<Requirement> requirementList(){
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
        this.reqsPerMinMS = new TreeMap<>();
        requirements.forEach(this::addRequirementToMSMap);
    }

    public void addAllRequirements(Requirement... requirements) {
        List<Requirement> list = new ArrayList<>(Arrays.asList(requirements));

        this.requirements.addAll(list);

        list.forEach(this::addRequirementToMSMap);

    }

    public void addAllMilestones(Milestone... milestones) {
        this.milestones.addAll(Arrays.asList(milestones));
    }

    public Milestone getMilestoneByOrdinal(int ordinal) {
        Milestone result = null;
        for (Milestone ms : milestones) {
            if (ms.getOrdinal() == ordinal) {
                result = ms;
            }
        }
        return result;
    }

    public List<Requirement> getRequirementsByMilestone(int ordinal) {
        ArrayList<Requirement> reqs = new ArrayList<>();
        for (Requirement r : requirements) {
            if (r.getMinMilestoneOrdinal() <= ordinal && ordinal <= r.getMaxMilestoneOrdinal()) {
                reqs.add(r);
            }
        }
        return reqs;
    }

    public List<Requirement> getRequirementsWithMinMS(int ordinal) {
        if (reqsPerMinMS.containsKey(ordinal)) {
            return new ArrayList<Requirement>(reqsPerMinMS.get(ordinal));
        } else {
            return null;
        }
    }

    @JsonIgnore
    public double getSum(int msOrdinal) {
        List<Requirement> reqs = reqsPerMinMS.get(msOrdinal);
        if (reqs == null) {
            return 0;
        } else {
            List<Double> points = new ArrayList<>();
            reqs.forEach(req -> points.add(!req.isMandatory() || req.isMalus() ? 0 : req.getMaxPoints()));
            return points.stream().mapToDouble(Double::doubleValue).sum();
        }
    }

    @JsonIgnore
    public double getSum() {
        List<Double> points = new ArrayList<>();
        reqsPerMinMS.keySet().forEach(ordinal -> {
            points.add(getSum(ordinal));
        });
        return points.stream().mapToDouble(Double::doubleValue).sum();
    }

    @JsonIgnore
    public Requirement getRequirementByName(String name) {
        for (Requirement r : requirements) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    @JsonIgnore
    public boolean containsRequirement(String name) {
        for (Requirement r : requirements) {
            if (r.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public Requirement getRequirementForProgress(Progress progress) {
        return getRequirementByName(progress.getRequirementName());
    }

    @JsonIgnore
    public Milestone getMilestoneForProgress(Progress progress) {
        return getMilestoneByOrdinal(progress.getMilestoneOrdinal());
    }

    private void addRequirementToMSMap(Requirement requirement) {
        int ordinal = requirement.getMinMilestoneOrdinal();
        if (reqsPerMinMS.get(ordinal) != null) {
            reqsPerMinMS.get(ordinal).add(requirement);

        } else {
            reqsPerMinMS.put(ordinal, new ArrayList<>(Arrays.asList(requirement)));
        }
    }

    @JsonIgnore
    public int getLastOrdinal() {
        if(milestones.isEmpty() ){
            return 0;
        }
        ArrayList<Milestone> list = new ArrayList<>(getMilestones());
        list.sort(Comparator.comparingInt(Milestone::getOrdinal));
        return list.get(list.size()-1).getOrdinal();
    }

    public List<Milestone> milestoneList() {
        return milestones;
    }
}
