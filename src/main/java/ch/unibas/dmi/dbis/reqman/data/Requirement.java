package ch.unibas.dmi.dbis.reqman.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The class {@link Requirement} represents a requirement as defined by the definitions document.
 *
 * @author loris.sauter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Requirement {

  /**
   * The unique identifier of the requirement
   */
  private final UUID uuid;
  /**
   * The name of the requirement.
   */
  private String name;
  /**
   * The short excerpt of the requirement.
   */
  private String excerpt;
  /**
   * The desciption of the requirement. May be a long-ish string.
   */
  private String description;
  /**
   * The ID of the milestone this requirement is firstly available
   */
  private UUID minimalMilestoneUUID;
  /**
   * The ID of the milestone this requirement is lastly available
   */
  private UUID maximalMilestoneUUID;
  /**
   * The maximal amount of points received upon meeting this requirement
   */
  private double maxPoints;
  /**
   * Whether this requirement is binary or not:
   * A binary requirement is either completely fulfilled - or not.
   * A non-binary requirement may be partially fulfilled.
   */
  private boolean binary;
  /**
   * The type of the requriement
   */
  private Type type;
  /**
   * A list of predecessor requirement UUIDs.
   */
  private List<UUID> predecessors = new ArrayList<>();
  /**
   * A map of key-value-pairs related to export this requirement
   */
  private Map<String, String> propertiesMap = new HashMap<>();
  /**
   * The optional category this requirement belongs to.
   */
  private String category;

  /**
   * The default constructor for a requirement.
   * All the properties of this requirement have to be set manually after this instance is created.
   */
  public Requirement() {
    uuid = UUID.randomUUID();
  }


  @JsonIgnore
  public boolean hasPredecessors() {
    return !predecessors.isEmpty();
  }

  public void clearPropertiesMap() {
    propertiesMap.clear();
  }


  public String addProperty(String key, String value) {
    return propertiesMap.put(key, value);
  }


  @Override
  public int hashCode() {
    int result;
    long temp;
    result = getUuid().hashCode();
    result = 31 * result + (getName() != null ? getName().hashCode() : 0);
    result = 31 * result + (getExcerpt() != null ? getExcerpt().hashCode() : 0);
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    result = 31 * result + (getMinimalMilestoneUUID() != null ? getMinimalMilestoneUUID().hashCode() : 0);
    result = 31 * result + (getMaximalMilestoneUUID() != null ? getMaximalMilestoneUUID().hashCode() : 0);
    temp = Double.doubleToLongBits(getMaxPoints());
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (isBinary() ? 1 : 0);
    result = 31 * result + (getType() != null ? getType().hashCode() : 0);
    result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Requirement that = (Requirement) o;

    return getUuid().equals(that.getUuid());
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Requirement{");
    sb.append("uuid=").append(uuid);
    sb.append(", name='").append(name).append('\'');
    sb.append(", excerpt='").append(excerpt).append('\'');
    sb.append(", description='").append(description).append('\'');
    sb.append(", minimalMilestoneUUID=").append(minimalMilestoneUUID);
    sb.append(", maximalMilestoneUUID=").append(maximalMilestoneUUID);
    sb.append(", maxPoints=").append(maxPoints);
    sb.append(", binary=").append(binary);
    sb.append(", type=").append(type);
    sb.append(", propertiesMap=").append(propertiesMap);
    sb.append(", category='").append(category).append('\'');
    sb.append('}');
    return sb.toString();
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


  public double getMaxPoints() {
    return maxPoints;
  }

  public void setMaxPoints(double maxPoints) {
    this.maxPoints = maxPoints;
  }

  public boolean isBinary() {
    return binary;
  }

  public void setBinary(boolean binary) {
    this.binary = binary;
  }

  @JsonIgnore
  public boolean isMandatory() {
    return type.equals(Type.REGULAR);
  }

  @JsonIgnore
  public boolean isMalus() {
    return type.equals(Type.MALUS);
  }

  public String removeProperty(String key) {
    if(propertiesMap==null){
      return null;
    }
    return propertiesMap.remove(key);
  }

  public Map<String, String> getPropertiesMap() {
    if(propertiesMap == null){
      this.propertiesMap = new HashMap<>();
    }
    return new HashMap<String, String>(propertiesMap);
  }

  public void setPropertiesMap(Map<String, String> propertiesMap) {
    this.propertiesMap = propertiesMap;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public void setExcerpt(String excerpt) {
    this.excerpt = excerpt;
  }

  public UUID getMinimalMilestoneUUID() {
    return minimalMilestoneUUID;
  }

  public void setMinimalMilestoneUUID(UUID minimalMilestoneUUID) {
    this.minimalMilestoneUUID = minimalMilestoneUUID;
  }

  public UUID getMaximalMilestoneUUID() {
    return maximalMilestoneUUID;
  }

  public void setMaximalMilestoneUUID(UUID maximalMilestoneUUID) {
    this.maximalMilestoneUUID = maximalMilestoneUUID;
  }

  public boolean addPredecessor(Requirement requirement) {
    if(predecessors == null){
      this.predecessors = new ArrayList<>();
    }
    if (!predecessors.contains(requirement.getUuid())) {
      return predecessors.add(requirement.getUuid());
    }
    return false;
  }

  public boolean removePredecessor(Requirement requirement) {
    if(predecessors == null){
      return false;
    }
    return predecessors.remove(requirement.getUuid());
  }

  public UUID[] getPredecessors() {
    if(predecessors == null){
      return new UUID[0];
    }
    return predecessors.toArray(new UUID[0]);
  }

  public void setPredecessors(UUID[] predecessors) {
    if(predecessors == null){
      this.predecessors = new ArrayList<>();
    }
    this.predecessors.clear();
    this.predecessors.addAll(Arrays.asList(predecessors));
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Denotes whether this requirement is a regular requirement.
   * Non-regular requirements are either bonus or malus requirements and are not considered for the
   * maximal available points of a catalogue.
   *
   * @return TRUE iff and only if this requirement is of type REGULAR.
   */
  @JsonIgnore
  public boolean isRegular() {
    if(this.type == null){
      return false;
    }
    return this.type.equals(Type.REGULAR);
  }

  @JsonIgnore
  public boolean isBonus() {
    if(this.type == null){
      return false;
    }
    return type.equals(Type.BONUS);
  }

  /**
   * Returns the UUID of this requirement.
   *
   * @return The UUID of this requirement.
   */
  public UUID getUuid() {
    return uuid;
  }

  @JsonIgnore
  public void setAllPredecessors(Set<Requirement> predecessors) {
    if(this.predecessors == null){
      this.predecessors = new ArrayList<>();
    }
    this.predecessors.clear();
    this.predecessors.addAll(predecessors.stream().map(Requirement::getUuid).collect(Collectors.toSet()));
  }

  public enum Type {

    REGULAR,
    MALUS,
    BONUS;

    @Override
    public String toString() {
      return StringUtils.capitalize(name().toLowerCase());
    }
  }
}
