package ch.unibas.dmi.dbis.reqman.control;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.reqman.data.CourseManager;
import ch.unibas.dmi.dbis.reqman.data.EntityFactory;
import ch.unibas.dmi.dbis.reqman.storage.StorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  private CatalogueAnalyser catalogueAnalyser;
  
  private CourseManager courseManager;
  
  private EntityFactory entityFactory;
  
  private StorageManager storageManager;
  
  private HashMap<UUID, GroupAnalyser> groupAnalyserMap = new HashMap<>();
  
  public EntityController() {
  }
  
  public CatalogueAnalyser getCatalogueAnalyser() {
    return catalogueAnalyser;
  }
  
  public void setCatalogueAnalyser(CatalogueAnalyser catalogueAnalyser) {
    this.catalogueAnalyser = catalogueAnalyser;
  }
  
  public CourseManager getCourseManager() {
    return courseManager;
  }
  
  public void setCourseManager(CourseManager courseManager) {
    this.courseManager = courseManager;
  }
  
  public EntityFactory getEntityFactory() {
    return entityFactory;
  }
  
  public void setEntityFactory(EntityFactory entityFactory) {
    this.entityFactory = entityFactory;
  }
  
  public StorageManager getStorageManager() {
    return storageManager;
  }
  
  public void setStorageManager(StorageManager storageManager) {
    this.storageManager = storageManager;
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
