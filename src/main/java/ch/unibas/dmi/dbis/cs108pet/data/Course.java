package ch.unibas.dmi.dbis.cs108pet.data;

import ch.unibas.dmi.dbis.cs108pet.common.VersionedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course extends VersionedEntity {
  
  private final UUID uuid;
  
  private String name;
  private String semester;
  private UUID catalogueUUID;
  private boolean negativeReminderAllowed;
  private Set<UUID> groupUUIDs;
  private Set<Time> timeEntities;
  
  public Course() {
    uuid = UUID.randomUUID();
    groupUUIDs = new HashSet<>();
    timeEntities = new HashSet<>();
  }
  
  Course(String name, String semester) {
    this();
    this.name = name;
    this.semester = semester;
  }
  
  public UUID getUuid() {
    return uuid;
  }
  
  public String getName() {
    return name;
  }
  
  @NotNull
  public void setName(String name) {
    this.name = name;
  }
  
  public String getSemester() {
    return semester;
  }
  
  @NotNull
  public void setSemester(String semester) {
    this.semester = semester;
  }
  
  public UUID getCatalogueUUID() {
    return catalogueUUID;
  }
  
  @NotNull
  public void setCatalogueUUID(UUID catalogueUUID) {
    this.catalogueUUID = catalogueUUID;
  }
  
  @JsonIgnore
  public boolean isTimeListEmpty() {
    return timeEntities.isEmpty();
  }
  
  /**
   * Returns {@code true} if this course contains the given {@link Time}.
   * <p>
   * The time entity is identified by its {@link UUID}, but also by its {@link Date}.
   * So if either the time's UUID ({@link Time#getUuid()}) or the time's date ({@link Time#getDate()}), represented by
   * another time entity,
   * already is in the course's time entity set, this method will return {@code true} - otherwise {@code false}
   *
   * @param time
   * @return
   */
  @NotNull
  public boolean containsTime(Time time) {
    if (timeEntities.contains(time)) {
      return true;
    } else {
      for (Time t : timeEntities) {
        if (t.getDate().equals(time.getDate())) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean isNegativeReminderAllowed() {
    return negativeReminderAllowed;
  }
  
  public void setNegativeReminderAllowed(boolean negativeReminderAllowed) {
    this.negativeReminderAllowed = negativeReminderAllowed;
  }
  
  /**
   * Adds the given {@link Time} entity to the set of time entities.
   * <p>
   * If the given time entity is already part of the set, this method returns {@code false},
   * otherwise -on success- it returns {@code true}.
   * More formally, the return value of this method is either {@code false}, iff Course{@link #containsTime(Time)}
   * returns {@code true}. Otherwise the return value is the one of the operation {@link Set#add(Object)}, where the
   * set is the internal set of time entities, and the object the given time entity.
   *
   * @param time The time to add. Must not be null
   * @return {@code true} on success, so the given time entity was added to the set of time entities, or {@code false}
   * if this time entity (or the entity's date) were already in the set.
   */
  @NotNull
  public boolean addTime(Time time) {
    if (containsTime(time)) {
      return false;
    }
    return timeEntities.add(time);
  }
  
  @NotNull
  public boolean removeTime(Time time) {
    return timeEntities.remove(time);
  }
  
  public void clearTimeList() {
    timeEntities.clear();
  }
  
  @JsonIgnore
  public boolean isGroupSetEmpty() {
    return groupUUIDs.isEmpty();
  }
  
  @NotNull
  public boolean containsGroup(Group group) {
    return groupUUIDs.contains(group.getUuid());
  }
  
  @NotNull
  public boolean addGroup(Group group) {
    return groupUUIDs.add(group.getUuid());
  }
  
  @NotNull
  public boolean removeGroup(Group group) {
    return groupUUIDs.remove(group.getUuid());
  }
  
  public void clearGroupSet() {
    groupUUIDs.clear();
  }
  
  @NotNull
  public void addAllTimes(Time... times) {
    timeEntities.addAll(Arrays.asList(times));
  }
  
  @NotNull
  @JsonIgnore
  public Time getTimeFor(Date date) {
    for (Time t : timeEntities) {
      if (t.getDate().equals(date)) {
        return t;
      }
    }
    return null;
  }
  
  List<Time> getTimes() {
    return new ArrayList<>(timeEntities);
  }
  
  public Time[] getTimeEntities() {
    return timeEntities.toArray(new Time[0]);
  }
  
  void setTimeEntities(Time[] entities) {
    timeEntities.addAll(Arrays.asList(entities));
  }
  
  public UUID[] getGroupUUIDs() {
    return groupUUIDs.toArray(new UUID[0]);
  }
  
  void setGroupUUIDs(UUID[] uuids) {
    groupUUIDs.addAll(Arrays.asList(uuids));
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Course{");
    sb.append("uuid=").append(uuid);
    sb.append(", name='").append(name).append('\'');
    sb.append(", semester='").append(semester).append('\'');
    sb.append(", catalogueUUID=").append(catalogueUUID);
    sb.append(", groupUUIDs=").append(groupUUIDs);
    sb.append(", timeEntities=").append(timeEntities);
    sb.append('}');
    return sb.toString();
  }
}
