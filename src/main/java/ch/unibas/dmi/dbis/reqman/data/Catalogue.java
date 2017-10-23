package ch.unibas.dmi.dbis.reqman.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

/**
 * A catalogue is a collection of {@link Milestone}s and {@link Requirement}s.
 * <p>
 * It represents the logical superset of associated milestones and requirements, forming a namespace.
 * A catalogue must have a name and may is associated with a lecture and date.
 * <p>
 * The catalogue class will be written serialized as a json object with jackson library.
 * <p>
 * <b>Note:</b> Since SNAPSHOT-2.0.0, all of the 'analysis' methods, i.e. {@link #getSum()} are deprecated.
 * There will be dedicated classes for the analysis of ReqMan entities.
 *
 * @author loris.sauter
 */
public class Catalogue {
  
  private final UUID uuid;
  /**
   * @deprecated Since SNAPSHOT-2.0.0: Got moved to entity Course
   */
  @Deprecated
  private String lecture;
  private String name;
  private String description;
  /**
   * @deprecated Since SNAPSHOT-2.0.0: Got moved t entity Course
   */
  @Deprecated
  private String semester;
  
  /**
   * The very list of milestones
   */
  private List<Milestone> milestones = new ArrayList<>();
  /**
   * The very list of requirements
   */
  private List<Requirement> requirements = new ArrayList<>();
  
  @Deprecated
  @JsonIgnore
  private Map<Integer, List<Requirement>> reqsPerMinMS = new TreeMap<>();
  
  /**
   * The default constructor
   */
  public Catalogue() {
    uuid = UUID.randomUUID();
  }
  
  /**
   * A constructor for creating a new catalogue with specified name, lecture and semester as well as a description provided.
   *
   * @param lecture     The name of the lecture
   * @param name        The  name of the catalogue
   * @param description A description of a catalogue
   * @param semester    The semester for which this catalogue was designed
   */
  public Catalogue(String lecture, String name, String description, String semester) {
    this();
    this.lecture = lecture;
    this.name = name;
    this.description = description;
    this.semester = semester;
  }
  
  /**
   * Returns the lecture name this catalogue is associated with
   *
   * @return The lecture name this catalogue is associated with
   */
  @Deprecated
  public String getLecture() {
    return lecture;
  }
  
  /**
   * Sets the lecture name for which this catalogue is made
   *
   * @param lecture The lecture name
   */
  @Deprecated
  public void setLecture(String lecture) {
    this.lecture = lecture;
  }
  
  /**
   * Returns this catalogue's name.
   * It will be referenced in {@link Group}s with this name.
   *
   * @return The  name of this catalogue
   */
  public String getName() {
    return name;
  }
  
  /**
   * Sets the name of this catalogue
   *
   * @param name The (new) name of the catalogue
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Returns the description of this catalogue
   *
   * @return The description of this catalogue
   */
  public String getDescription() {
    return description;
  }
  
  /**
   * Sets (new) the description of this catalogue
   *
   * @param description The (new) description of the catalogue
   */
  public void setDescription(String description) {
    this.description = description;
  }
  
  /**
   * Returns the semester of this catalogue
   *
   * @return The semester of this catalogue
   */
  @Deprecated
  public String getSemester() {
    return semester;
  }
  
  /**
   * Sets the semester of this catalogue
   *
   * @param semester The semester represented as a string
   */
  @Deprecated
  public void setSemester(String semester) {
    this.semester = semester;
  }
  
  
  /**
   * Adds the given milestone to the list of milestones.
   *
   * @param milestone The milestone to add
   * @return the result: TRUE if the addition successfully has been performed
   * @see List#add(Object)
   */
  public boolean addMilestone(Milestone milestone) {
    return milestones.add(milestone);
  }
  
  /**
   * Removes the given milestone from the list of of milestones.
   *
   * @param milestone The milestone to remove
   * @return The result: TRUE if the operation was successful
   * @see List#remove(Object)
   */
  public boolean removeMilestone(Milestone milestone) {
    return milestones.remove(milestone);
  }
  
  /**
   * Returns a copy of the milestone list
   *
   * @return List of milestones
   */
  public List<Milestone> getMilestones() {
    return new ArrayList<>(milestones);
  }
  
  /**
   * Adds a requirement to the list of requirements.
   *
   * @param requirement The requirement to add
   * @return The result of the {@link List#add(Object)} operation
   */
  public boolean addRequirement(Requirement requirement) {
    return requirements.add(requirement);
  }
  
  
  /**
   * Removes the specified requirements
   *
   * @param requirement The requirement to remove
   * @return The result of the {@link List#remove(Object)} operation
   */
  public boolean removeRequirement(Requirement requirement) {
    return requirements.remove(requirement);
    
  }
  
  /**
   * Returns a copy of the requirements list
   *
   * @return The list of requirements
   */
  public List<Requirement> getRequirements() {
    return new ArrayList<>(requirements);
  }
  
  /**
   * Returns the requirements list itself
   *
   * @return The list of requirements
   */
  @JsonIgnore
  public List<Requirement> requirementList() {
    return requirements;
  }
  
  /**
   * Adds all milestones passed to this method to the list of milestones.
   *
   * @param milestones The milestones to add to the list of milestones
   */
  public void addAllMilestones(Milestone... milestones) {
    this.milestones.addAll(Arrays.asList(milestones));
  }
  
  /**
   * Returns the milestone which is identified by the given ordinal.
   *
   * @param ordinal The oridnal of the milestone
   * @return The milestone with the ordinal specified or NULL if no such milestone exists
   */
  @Deprecated
  public Milestone getMilestoneByOrdinal(int ordinal) {
    Milestone result = null;
    for (Milestone ms : milestones) {
      if (ms.getOrdinal() == ordinal) {
        result = ms;
      }
    }
    return result;
  }
  
  /**
   * Returns a list of requirements which are associated with the specified milestone ordinal.
   * A requirement is associated to a milestone ordinal iff:
   * <ul>
   * <li>{@link Requirement#getMinMilestoneOrdinal()} is less or equals the specified ordinal <b>and</b></li>
   * <li>{@link Requirement#getMaxMilestoneOrdinal()} is greater or equals the specified ordinal</li>
   * </ul>
   *
   * @param ordinal The ordinal of a milestone to get the requirements for
   * @return The list of requirements associated with this ordinal or an empty list if no such associated requirements exist
   */
  @Deprecated
  public List<Requirement> getRequirementsByMilestone(int ordinal) {
    return null;
  }
  
  /**
   * Returns a list of requirements which are available from the specified milestone ordinal on
   *
   * @param ordinal The oridnal of a milestone to get the requirements for
   * @return The resulting list or NULL if no such milestone with the specified oridnal exists
   */
  @Deprecated
  public List<Requirement> getRequirementsWithMinMS(int ordinal) {
    return null;
  }
  
  /**
   * Returns the sum of maximal available points of the specified milestone.
   * Requirements which are malus or are not mandatory are ignored
   *
   * @param msOrdinal The milestone ordinal to get the sum of
   * @return The sum of maximal available points of the specified milestone or 0 if no such milestone exists
   */
  @JsonIgnore
  @Deprecated
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
  
  /**
   * Returns the sum of total maximal available points.
   * Internally this method sums over all known milestones and their sums.
   *
   * @return The sum of total maximal available points or 0 if no requirements are present
   * @see Catalogue#getSum(int)
   */
  @JsonIgnore
  @Deprecated
  public double getSum() {
    List<Double> points = new ArrayList<>();
    reqsPerMinMS.keySet().forEach(ordinal -> {
      points.add(getSum(ordinal));
    });
    return points.stream().mapToDouble(Double::doubleValue).sum();
  }
  
  /**
   * Returns the requirement with the specified name
   *
   * @param name The name of the requirement to get
   * @return The requirement with the specified name or NULL if no such requirement exists
   */
  @JsonIgnore
  @Deprecated
  public Requirement getRequirementByName(String name) {
    for (Requirement r : requirements) {
      if (r.getName().equals(name)) {
        return r;
      }
    }
    return null;
  }
  
  /**
   * Checks if a requirement with that name already exists
   *
   * @param name The name to test
   * @return TRUE if a requirement with the specified name already exists - FALSE otherwise
   */
  @JsonIgnore
  @Deprecated
  public boolean containsRequirement(String name) {
    for (Requirement r : requirements) {
      if (r.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Returns the requirement for the specified progress
   *
   * @param progress The progress for which the requirement is
   * @return The requirement or NULL if no such requirement exists
   */
  @JsonIgnore
  @Deprecated
  public Requirement getRequirementForProgress(Progress progress) {
    return getRequirementByName(progress.getRequirementName());
  }
  
  /**
   * Returns the milestone the progress was made on
   *
   * @param progress The progress to get the milestone for
   * @return The milestone the progress was made on or NULL if no such milestone exists
   */
  @JsonIgnore
  @Deprecated
  public Milestone getMilestoneForProgress(Progress progress) {
    return getMilestoneByOrdinal(progress.getMilestoneOrdinal());
  }
  
  
  /**
   * Returns the lastly used ordinal
   *
   * @return the lastyl used ordinal
   */
  @JsonIgnore
  @Deprecated
  public int getLastOrdinal() {
    if (milestones.isEmpty()) {
      return 0;
    }
    ArrayList<Milestone> list = new ArrayList<>(getMilestones());
    list.sort(Comparator.comparingInt(Milestone::getOrdinal));
    return list.get(list.size() - 1).getOrdinal();
  }
  
  /**
   * Returns the list of milestones
   *
   * @return The list of milestones
   */
  public List<Milestone> milestoneList() {
    return milestones;
  }
  
  /**
   * Re-syncs the requirements and ensures that all internal references of and to requirements are set correctly.
   * <p>
   * When loading a catalogue from a JSON file the internal structure is not completely set up. Invoking
   * this method after reading from a JSON file it is ensured that those internal structure is correct.
   */
  public void resyncRequirements() {
    // nothing
  }
  
  
  public UUID getUuid() {
    return uuid;
  }
  
  @Override
  public int hashCode() {
    int result = getUuid().hashCode();
    result = 31 * result + (getName() != null ? getName().hashCode() : 0);
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    result = 31 * result + (getMilestones() != null ? getMilestones().hashCode() : 0);
    result = 31 * result + (getRequirements() != null ? getRequirements().hashCode() : 0);
    return result;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    Catalogue catalogue = (Catalogue) o;
    return getUuid().equals(catalogue.getUuid());
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Catalogue{");
    sb.append("uuid=").append(uuid);
    sb.append(", name='").append(name).append('\'');
    sb.append(", description='").append(description).append('\'');
    sb.append(", milestones=").append(milestones);
    sb.append(", requirements=").append(requirements);
    sb.append('}');
    return sb.toString();
  }
  
  public void addAllRequirements(Requirement... requirements) {
    this.requirements.addAll(Arrays.asList(requirements));
  }
}
