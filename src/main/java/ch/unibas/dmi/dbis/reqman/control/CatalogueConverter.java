package ch.unibas.dmi.dbis.reqman.control;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueConverter {
  
  /**
   * To which version this converter converts
   */
  
  public static final String VERSION_KEY = "version";
  
  /* === Catalogue properties ===*/
  public static final String LECTURE_KEY = "lecture";
  public static final String LECTURE_DEFAULT = "Lecture";
  public static final String NAME_KEY = "name";
  public static final String NAME_DEFAULT = "CatalogueName";
  public static final String DESC_KEY = "description";
  public static final String SEMESTER_KEY = "semester";
  public static final String MILESTONES_KEY = "milestones";
  public static final String REQUIREMENTS_KEY = "requirements";
  /* === milestone properties ===*/
  public static final String ORDINAL_KEY = "ordinal";
  public static final String DATE_KEY = "date";
  /* === requirement properties ===*/
  public static final String MIN_MS_ORD_KEY = "minMilestoneOrdinal";
  public static final String MAX_MS_ORD_KEY = "maxMilestoneOrdinal";
  public static final String MAX_POINTS_KEY = "maxPoints";
  public static final String BINARY_KEY = "binary";
  public static final String MANDATORY_KEY = "mandatory";
  public static final String PRED_NAMES_KEY = "predecessorNames";
  public static final String PROPS_KEY = "propertiesMap";
  public static final String CAT_KEY = "category";
  public static final String MALUS_KEY = "malus";
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  private Throwable lastException = null;
  private Course course;
  private Catalogue catalogue;
  
  private Version toVersion;
  private Map<Integer, Milestone> ordinalMilestoneMap = new TreeMap<>();
  private Map<String, Requirement> nameRequirementMap = new TreeMap<>();
  
  public Throwable getLastException() {
    return lastException;
  }
  
  public void convert(Version toVersion, File file) {
    LOGGER.info("Conversion from file {}", file);
    this.toVersion = toVersion;
    if (toVersion.isInvalid()) {
      LOGGER.fatal("Cannot convert if an invalid version is given. Abort.");
      return;
    }
    try {
      Map<String, Object> old = JSONUtils.readFromJSONFile(file);
      LOGGER.debug("Extraction of info from {}", old);
      LOGGER.info("Starting conversion");
      try {
        if (checkVersion(old)) {
          try {
            course = extractCourse(old);
            LOGGER.info("Extracted course info {}", course);
            catalogue = extractCatalogueInfo(old);
            LOGGER.info("Extracted catalogue info {}", catalogue);
            extractMilestones(old);
            extractRequirements(old);
            course.setCatalogueUUID(catalogue.getUuid());
            course.setVersion(toVersion.getVersion());
            catalogue.setVersion(toVersion.getVersion());
          } catch (ClassCastException ex) {
            LOGGER.fatal("Error while casting: {}. Aborting.", ex);
            lastException = ex;
          } catch (RuntimeException e){
            LOGGER.fatal("Error while extracting ({}). Aborting.", e);
            lastException = e;
          }
        }else{
          LOGGER.fatal("Version check failed");
        }
      } catch (RuntimeException ex) {
        LOGGER.fatal("Error with catalogue to convert's version {}. Aborting", ex.getMessage());
        lastException = ex;
      }
    } catch (IOException e) {
      LOGGER.fatal("Could not read file for conversion ({}). Aborting...", e.getMessage());
      lastException = e;
    }
  }
  
  public Course getCourse() {
    return course;
  }
  
  public Catalogue getCatalogue() {
    return catalogue;
  }
  
  private void extractMilestones(Map<String, Object> map)  {
    List oldMilestones = (List) map.get(MILESTONES_KEY);
    for (Object obj : oldMilestones) {
      LOGGER.debug("Converting milestone {}", obj);
      Map ms = (Map) obj;
      Integer ordinal = Integer.valueOf(ms.get(ORDINAL_KEY).toString());
      Milestone milestone = new Milestone();
      Time time = new Time();
      time.setDate(Date.from(Instant.ofEpochMilli(Long.valueOf(ms.get(DATE_KEY).toString()))));
      milestone.setTimeUUID(time.getUuid());
      course.addTime(time);
      milestone.setName(ms.get(NAME_KEY) != null ? ms.get(NAME_KEY).toString() : ("Milestone-" + ordinal));
      ordinalMilestoneMap.put(ordinal, milestone);
      catalogue.addMilestone(milestone);
      LOGGER.info("Created Milestone {} and Time {}", milestone, time);
    }
  }
  
  private void extractRequirements(Map<String, Object> map) {
    List oldRequirements = (List) map.get(REQUIREMENTS_KEY);
    for (Object obj : oldRequirements) {
      LOGGER.debug("Converting requirement {}", obj);
      Map req = (Map) obj;
      Requirement requirement = new Requirement();
      String name = req.get(NAME_KEY).toString();
      requirement.setName(name);
      nameRequirementMap.put(name, requirement);
      requirement.setExcerpt(req.get(DESC_KEY) != null ? req.get(DESC_KEY).toString() : null);
      Integer minOrdinal = Integer.valueOf(req.get(MIN_MS_ORD_KEY).toString());
      Integer maxOrdinal = Integer.valueOf(req.get(MAX_MS_ORD_KEY).toString());
      requirement.setMinimalMilestoneUUID(ordinalMilestoneMap.get(minOrdinal).getUuid());
      requirement.setMaximalMilestoneUUID(ordinalMilestoneMap.get(maxOrdinal).getUuid());
      
      Double maxPoints = Double.valueOf(req.get(MAX_POINTS_KEY).toString());
      requirement.setMaxPoints(maxPoints);
      
      boolean binary = Boolean.valueOf(req.get(BINARY_KEY).toString());
      requirement.setBinary(binary);
      
      boolean mandatory = Boolean.valueOf(req.get(MANDATORY_KEY).toString());
      boolean malus = Boolean.valueOf(req.get(MALUS_KEY).toString());
      
      if(mandatory){
        requirement.setType(Requirement.Type.REGULAR);
      }else if(malus){
        requirement.setType(Requirement.Type.MALUS);
      }else{
        requirement.setType(Requirement.Type.BONUS);
      }
      
      extractProperties(req, requirement);
      extractPredecessors(req, requirement);
      
      catalogue.addRequirement(requirement);
      LOGGER.debug("Created Requirement {}", requirement);
    }
  }
  
  private void extractPredecessors(Map req, Requirement requirement) {
    Object preds = req.get(PRED_NAMES_KEY);
    if(preds != null){
      List predsList = (List)preds;
      if(!predsList.isEmpty()){
        for(Object obj : predsList){
          if(nameRequirementMap.containsKey(obj.toString())){
            requirement.addPredecessor(nameRequirementMap.get(obj.toString()));
          }
        }
      }
    }
  }
  
  private void extractProperties(Map req, Requirement requirement) {
    Object obj = req.get(PROPS_KEY);
    Map props = (Map)obj;
    if(props.containsKey(CAT_KEY)){
      requirement.setCategory(props.get(CAT_KEY).toString());
      props.remove(CAT_KEY);
    }
    for(Object key : props.keySet()){
      requirement.addProperty(key.toString(), props.get(key).toString());
    }
    LOGGER.debug("Extracted category {} and properties {}", requirement.getCategory(), requirement.getPropertiesMap());
  }
  
  private Catalogue extractCatalogueInfo(Map<String, Object> old) {
    Catalogue c = new Catalogue();
    c.setName(getOrDefault(old, NAME_KEY, NAME_DEFAULT));
    c.setDescription(getOrDefault(old, DESC_KEY, null));
    return c;
  }
  
  private boolean checkVersion(Map<String, Object> map) {
    if (map != null) {
      if (map.containsKey(VERSION_KEY)) {
        Object vObj = map.get(VERSION_KEY);
        if (vObj instanceof String) {
          Version v = Version.forString((String) vObj);
          if (v == null || v.isInvalid()) {
            throw new RuntimeException("The catalogue to convert has an invalid version");
          }
          return v.compareTo(toVersion) < 0;
        }
      }else{
        return true;
      }
    }
    return false;
  }
  
  private Course extractCourse(Map<String, Object> map) {
    String lecture = getOrDefault(map, LECTURE_KEY, LECTURE_DEFAULT);
    String semester = getOrDefault(map, SEMESTER_KEY, null);
    Course c = new Course();
    c.setName(lecture);
    c.setSemester(semester);
    return c;
  }
  
  private String getOrDefault(Map<String, Object> map, String key, String or) {
    return (String) map.getOrDefault(key, or);
  }
}
