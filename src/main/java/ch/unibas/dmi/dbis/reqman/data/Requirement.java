package ch.unibas.dmi.dbis.reqman.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

/**
 * The class {@link Requirement} represents a requirement as defined by the definitions document.
 *
 * @author loris.sauter
 */
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
   * A list of predecessor requirement names this requirement depends on.
   */
  @Deprecated
  private List<String> predecessorNames = new Vector<>();
  /**
   * A map of key-value-pairs related to export this requirement
   */
  private Map<String, String> propertiesMap = new HashMap<>();
  /**
   * The optional category this requirement belongs to.
   */
  private String category;
  
  /**
   * The minimal milestone ordinal this requirement firstly occurs
   *
   * @deprecated Since SNAPSHOT-2.0.0: UUIDs were introduced for cross-references
   */
  @Deprecated
  private int minMilestoneOrdinal;
  /**
   * The maximal milestone ordinal this requirement must be met
   *
   * @deprecated Since SNAPSHOT-2.0.0: UUIDs were introduced for cross-references
   */
  @Deprecated
  private int maxMilestoneOrdinal;
  /**
   * Whether this requirement is mandatory or not.
   *
   * @deprecated Since SNAPSHOT-2.0.0: The requirement type was introduced
   */
  @Deprecated
  private boolean mandatory;
  /**
   * Whether this requirement has a malus role or not.
   * So to speak if maxPoints is negative or not.
   *
   * @deprecated Since SNAPSHOT-2.0.0: The requirement type was introduced
   */
  @Deprecated
  private boolean malus;
  
  /**
   * The default constructor for a requirement.
   * All the properties of this requirement have to be set manually after this instance is created.
   */
  public Requirement() {
    uuid = UUID.randomUUID();
  }
  
  /**
   * Creates a new {@link Requirement} with given properties.
   *
   * @param name                The name of the requirement which shall be short, descriptive and unique
   * @param description         A description of this requirement.
   * @param minMilestoneOrdinal The ordinal of the {@link Milestone} upon this requirement is active
   * @param maxMilestoneOrdinal The ordinal of the {@link Milestone} this requirement is active up to
   * @param maxPoints           The absolute, maximal amount of points this requirement can generate.
   * @param binary              Whether this requirement is binary (achieved: yes/no or partial).
   * @param mandatory           Whether this requirement is mandatory
   * @param malus               Whether this requirement has to be considered as a malus.
   */
  @Deprecated
  public Requirement(String name, String description, int minMilestoneOrdinal, int maxMilestoneOrdinal, double maxPoints, boolean binary, boolean mandatory, boolean malus) {
    this();
    
    this.name = name;
    this.description = description;
    this.minMilestoneOrdinal = minMilestoneOrdinal;
    this.maxMilestoneOrdinal = maxMilestoneOrdinal;
    this.maxPoints = maxPoints;
    this.binary = binary;
    this.mandatory = mandatory;
    this.malus = malus;
  }
  
  public void clearPredecessorNames() {
    predecessorNames.clear();
  }
  
  public void clearPropertiesMap() {
    propertiesMap.clear();
  }
  
  /**
   * Adds the given name of a requirement to the list of requirements this requirement depends on.
   * <p>
   * The corresponding {@link Requirement} with the given name is then a predecessor of this requirement.
   *
   * @param name The name of the requirement this requirement depends on. Must be a valid requirement name.
   * @return {@code true} As specified in {@link List#add(Object)}
   * @see List#add(Object)
   */
  public boolean addPredecessorName(String name) {
    return predecessorNames.add(name);
  }
  
  /**
   * Removes the specified requirement name of the list of
   *
   * @param name The name of the requirement this requirement no longer depends on. Must be a valid requirement name.
   * @return {@code true} If the specified name was in the list of predecessors (and is now not anymore).
   */
  public boolean removePredecessorName(String name) {
    return predecessorNames.remove(name);
  }
  
  /**
   * Returns a copy of the predecessor list.
   * <p>
   * The {@link List} returned is a copy and not referenced within this instance.
   * Thus modifying the returning list <b>will not be synced</b> with the list of this instance.
   * To modify the list of predecessors use the appropriate methods provided by {@link Requirement}
   *
   * @return A copy of the list of predecessor names.
   */
  public List<String> getPredecessorNames() {
    return new ArrayList<>(predecessorNames);
  }
  
  public void setPredecessorNames(List<String> predecessorNames) {
    this.predecessorNames = predecessorNames;
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
    result = 31 * result + (getPredecessorNames() != null ? getPredecessorNames().hashCode() : 0);
    result = 31 * result + (getPropertiesMap() != null ? getPropertiesMap().hashCode() : 0);
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
    sb.append(", predecessorNames=").append(predecessorNames);
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
  
  @Deprecated
  public int getMinMilestoneOrdinal() {
    return minMilestoneOrdinal;
  }
  
  @Deprecated
  public void setMinMilestoneOrdinal(int minMilestoneOrdinal) {
    this.minMilestoneOrdinal = minMilestoneOrdinal;
  }
  
  @Deprecated
  public int getMaxMilestoneOrdinal() {
    return maxMilestoneOrdinal;
  }
  
  @Deprecated
  public void setMaxMilestoneOrdinal(int maxMilestoneOrdinal) {
    this.maxMilestoneOrdinal = maxMilestoneOrdinal;
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
  
  public boolean isMandatory() {
    return type.equals(Type.REGULAR);
  }
  
  @Deprecated
  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }
  
  public boolean isMalus() {
    return type.equals(Type.MALUS);
  }
  
  @Deprecated
  public void setMalus(boolean malus) {
    this.malus = malus;
  }
  
  public String removeProperty(String key) {
    return propertiesMap.remove(key);
    
  }
  
  public Map<String, String> getPropertiesMap() {
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
    return predecessors.add(requirement.getUuid());
  }
  
  public boolean removePRedecessor(Requirement requirement) {
    return predecessors.remove(requirement.getUuid());
  }
  
  public UUID[] getPredecessors() {
    return predecessors.toArray(new UUID[0]);
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
  
  @JsonIgnore
  @Deprecated
  public double getMaxPointsSensitive() {
    double factor = isMalus() ? -1.0 : 1.0;
    return getMaxPoints() * factor;
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
    return this.type.equals(Type.REGULAR);
  }
  
  @JsonIgnore
  public boolean isBonus() {
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
  
  public enum Type {
    
    REGULAR,
    BONUS,
    MALUS;
    
  }
}
