package ch.unibas.dmi.dbis.reqman.data;

import java.util.*;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class Course {
  
  private final UUID uuid;
  
  private String name;
  private String semester;
  private UUID catalogueUUID;
  private Set<UUID> groupUUIDs;
  private Set<Time> timeEntities;
  
  public Course(){
    uuid = UUID.randomUUID();
    groupUUIDs = new HashSet<>();
    timeEntities = new HashSet<>();
  }
  
  public UUID getUuid() {
    return uuid;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getSemester() {
    return semester;
  }
  
  public void setSemester(String semester) {
    this.semester = semester;
  }
  
  public UUID getCatalogueUUID() {
    return catalogueUUID;
  }
  
  public void setCatalogueUUID(UUID catalogueUUID) {
    this.catalogueUUID = catalogueUUID;
  }
  
  
  public boolean isTimeListEmpty() {
    return timeEntities.isEmpty();
  }
  
  public boolean containsTime(Time time) {
    return timeEntities.contains(time);
  }
  
  public boolean addTime(Time time) {
    return timeEntities.add(time);
  }
  
  public boolean removeTime(Time time) {
    return timeEntities.remove(time);
  }
  
  public void clearTimeList() {
    timeEntities.clear();
  }
  
  public boolean isGroupSetEmpty() {
    return groupUUIDs.isEmpty();
  }
  
  public boolean containsGroup(Group group) {
    return groupUUIDs.contains(group.getUuid());
  }
  
  public boolean addGroup(Group group) {
    return groupUUIDs.add(group.getUuid());
  }
  
  public boolean removeGroup(Group group) {
    return groupUUIDs.remove(group.getUuid());
  }
  
  public void clearGroupSet() {
    groupUUIDs.clear();
  }
  
  List<Time> getTimes() {
    return new ArrayList<>(timeEntities);
  }
  
  public void addAllTimes(Time... times) {
    timeEntities.addAll(Arrays.asList(times));
  }
}
