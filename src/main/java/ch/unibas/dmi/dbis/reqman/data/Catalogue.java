package ch.unibas.dmi.dbis.reqman.data;

import ch.unibas.dmi.dbis.reqman.common.VersionedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Catalogue extends VersionedEntity {
  
  private final UUID uuid;
  private String name;
  private String description;
  
  /**
   * The very list of milestones
   */
  private List<Milestone> milestones = new ArrayList<>();
  /**
   * The very list of requirements
   */
  private List<Requirement> requirements = new ArrayList<>();
  
  /**
   * The default constructor
   */
  public Catalogue() {
    uuid = UUID.randomUUID();
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
   * Returns the list of milestones
   *
   * @return The list of milestones
   */
  public List<Milestone> milestoneList() {
    return milestones;
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
