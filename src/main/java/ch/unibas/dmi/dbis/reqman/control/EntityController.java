package ch.unibas.dmi.dbis.reqman.control;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.reqman.data.*;
import ch.unibas.dmi.dbis.reqman.storage.StorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * The {@link EntityController} controls all entities during a session.
 *
 * In fact, it has several delegates for such a purpose, these are:
 * <ul>
 *   <li>{@link ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser} for analyses of the {@link ch.unibas.dmi.dbis.reqman.data.Catalogue}</li>
 *   <li>{@link ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser} for analyses of a {@link ch.unibas.dmi.dbis.reqman.data.Group}</li>
 *   <li>{@link ch.unibas.dmi.dbis.reqman.data.CourseManager} for managing the {@link ch.unibas.dmi.dbis.reqman.data.Course}</li>
 *   <li>{@link ch.unibas.dmi.dbis.reqman.data.EntityFactory} for the creation of new entities</li>
 *   <li>{@link ch.unibas.dmi.dbis.reqman.storage.StorageManager} for IO operations</li>
 * </ul>
 *
 * @author loris.sauter
 */
public class EntityController {
  
  private static EntityController instance = null;
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  private CatalogueAnalyser catalogueAnalyser;
  
  private CourseManager courseManager;
  
  private EntityFactory entityFactory;
  
  private StorageManager storageManager;
  
  private HashMap<UUID, GroupAnalyser> groupAnalyserMap = new HashMap<>();
  
  private EntityController() {
  }
  
  public static EntityController getInstance(){
    if(instance == null){
      instance = new EntityController();
    }
    return instance;
  }
  
  public Course createCourse(String courseName, String semester){
    entityFactory = EntityFactory.createFactoryAndCourse(courseName, semester);
    return entityFactory.getCourse();
  }
  
  public Requirement createBinaryRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return entityFactory.createBinaryRequirement(name, excerpt, maxPoints, minMS, maxMS);
  }
  
  public Requirement createRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return entityFactory.createRequirement(name, excerpt, maxPoints, minMS, maxMS);
  }
  
  public Requirement createMalusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return entityFactory.createMalusRequirement(name, excerpt, maxPoints, minMS, maxMS);
  }
  
  public Requirement createBonusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return entityFactory.createBonusRequirement(name, excerpt, maxPoints, minMS, maxMS);
  }
  
  @NotNull
  public Milestone createMilestone(String name, Date date) {
    return entityFactory.createMilestone(name, date);
  }
  
  public Catalogue createCatalogue(String name) {
    return entityFactory.createCatalogue(name);
  }
  
  
  
  public CatalogueAnalyser getCatalogueAnalyser() {
    return catalogueAnalyser;
  }
  
  
  public CourseManager getCourseManager() {
    return courseManager;
  }
  
  
  public EntityFactory getEntityFactory() {
    return entityFactory;
  }
  
  
  public StorageManager getStorageManager() {
    return storageManager;
  }
  
  
  public boolean isEmpty() {
    return groupAnalyserMap.isEmpty();
  }
  
  public GroupAnalyser getGroupAnalyser(Object key) {
    return groupAnalyserMap.get(key);
  }
  
  public boolean containsGroupAnalyserFor(Object key) {
    return groupAnalyserMap.containsKey(key);
  }
  
  public GroupAnalyser addGroupAnalyser(UUID key, GroupAnalyser value) {
    return groupAnalyserMap.put(key, value);
  }
  
  public GroupAnalyser removeGroupAnalyser(Object key) {
    return groupAnalyserMap.remove(key);
  }
}
