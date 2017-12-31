package ch.unibas.dmi.dbis.reqman.control;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.data.*;
import ch.unibas.dmi.dbis.reqman.session.SessionManager;
import ch.unibas.dmi.dbis.reqman.session.SessionStorage;
import ch.unibas.dmi.dbis.reqman.storage.StorageManager;
import ch.unibas.dmi.dbis.reqman.storage.UuidMismatchException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The {@link EntityController} controls all entities during a session.
 * <p>
 * In fact, it has several delegates for such a purpose, these are:
 * <ul>
 * <li>{@link ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser} for analyses of the {@link
 * ch.unibas.dmi.dbis.reqman.data.Catalogue}</li>
 * <li>{@link ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser} for analyses of a {@link
 * ch.unibas.dmi.dbis.reqman.data.Group}</li>
 * <li>{@link ch.unibas.dmi.dbis.reqman.data.CourseManager} for managing the {@link
 * ch.unibas.dmi.dbis.reqman.data.Course}</li>
 * <li>{@link ch.unibas.dmi.dbis.reqman.data.EntityFactory} for the creation of new entities</li>
 * <li>{@link ch.unibas.dmi.dbis.reqman.storage.StorageManager} for IO operations</li>
 * </ul>
 *
 * @author loris.sauter
 */
public class EntityController {
  
  private static final Logger LOGGER = LogManager.getLogger();
  private static EntityController instance = null;
  private CatalogueAnalyser catalogueAnalyser;
  
  private CourseManager courseManager;
  
  private EntityFactory entityFactory;
  
  private StorageManager storageManager;
  
  private SessionManager sessionManager;
  
  private HashMap<UUID, GroupAnalyser> groupAnalyserMap = new HashMap<>();
  private ObservableList<Group> observableGroups = FXCollections.observableArrayList();
  private HashMap<UUID, ObservableList<Progress>> progressGroupMap = new HashMap<>();
  private HashMap<UUID, ObservableList<ProgressSummary>> summaryGroupMap = new HashMap<>();
  private ObservableList<Requirement> observableRequirements;
  private ObservableList<Milestone> observableMilestones;
  
  
  private EntityController() {
    sessionManager = new SessionManager();
    loadSession();
  }
  
  public static EntityController getInstance() {
    if (instance == null) {
      instance = new EntityController();
    }
    return instance;
  }
  
  public Course createCourse(String courseName, String semester) {
    LOGGER.debug("Creating course");
    entityFactory = EntityFactory.createFactoryAndCourse(courseName, semester);
    LOGGER.debug("Course created");
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
    Milestone ms = entityFactory.createMilestone(name, date);
    LOGGER.debug("Created the ms={}", ms);
    return ms;
  }
  
  public Catalogue createCatalogue(String name) {
    LOGGER.debug("Creating catalogue");
    Catalogue cat = entityFactory.createCatalogue(name);
    /*
     Since the EntityFactory handles the linking and adding of reqs/ ms in catalogue.requirements /catalogue.milestones,
     this redundancy is not very beautiful, but the first way I thought about to still get the UI notified.
     This leads to the contract, that the EditorHandler needs to add the req/ms on its own, to the obs.list
     
     TODO Another way would be to add the entity while creating it. So that this keeps track of the redundant lists
      */
    setupObservableCatalogueLists();
    loadedCatalogue();
    LOGGER.debug("Setup catalogueAnalyser and courseManager");
    return cat;
  }
  
  public boolean hasGroups() {
    return !observableGroups.isEmpty();
  }
  
  public ObservableList<Group> groupList() {
    return observableGroups;
  }
  
  public ObservableList<Progress> getObservableProgress(Group g) {
    return progressGroupMap.get(g.getUuid());
  }
  
  public ObservableList<ProgressSummary> getObservableProgressSummaries(Group g){
    return summaryGroupMap.get(g.getUuid());
  }
  
  public List<ProgressSummary> createProgressSummaries() {
    return entityFactory.createProgressSummaries();
  }
  
  public ObservableList<Progress> getObservableProgressOf(@NotNull Group group, @NotNull ProgressSummary progressSummary) {
    LOGGER.debug("ObservableProgressOf {} @ {}", group, progressSummary);
    GroupAnalyser analyser = getGroupAnalyser(group);
    ObservableList<Progress> list = FXCollections.observableList(analyser.getProgressFor(progressSummary) );
    return list;
  }
  
  public void saveGroup(UUID groupUuid){
    if(groupUuid != null){
      LOGGER.debug("Progress-size: {}", getGroup(groupUuid).getProgressList().size());
      if(storageManager != null){
        try {
          storageManager.saveGroup(groupUuid);
        } catch (IOException e) {
          LOGGER.catching(e);
          // TODO WHat to do
        }
      }else{
        throw LOGGER.throwing(new IllegalStateException("Cannot save group if no StorageManager is available"));
      }
    }
  }
  
  public void saveGroupAs(Group g) {
    if (g != null) {
      if (storageManager != null) {
        LOGGER.debug("Saving group with name {}", g.getName());
        try {
          storageManager.saveGroup(g);
          
        } catch (IOException e) {
          LOGGER.catching(e);
          // TODO What to do
        }
      } else {
        throw LOGGER.throwing(new IllegalStateException("Cannot save group as if no StorageManager is available"));
      }
    }
  }
  
  public List<Group> openGroups(List<File> files) throws UuidMismatchException, IOException {
    LOGGER.debug("Opening group files {}", files);
    List<Group> groupList = new ArrayList<>();
    for (File g : files) {
      Group group = storageManager.openGroup(g);
      addGroup(group);
      groupList.add(group);
    }
    return groupList;
  }
  
  private void setupObservableCatalogueLists(){
    observableRequirements = FXCollections.observableArrayList(); // Actually not very beautiful, but since the catalogue.getRequirements returns a copy / changes to it catalgoue.requirements are not reported, this is the only way.
    observableMilestones = FXCollections.observableArrayList();
    
    observableMilestones.addAll(getCatalogue().getMilestones());
    observableRequirements.addAll(getCatalogue().getRequirements());
  }
  
  private void loadedCatalogue(){
    catalogueAnalyser = new CatalogueAnalyser(getCourse(), getCatalogue());
    courseManager = new CourseManager(getCourse(), getCatalogue());
  }
  
  public Course getCourse() {
    if (entityFactory == null) {
      return null;
    }
    return entityFactory.getCourse();
  }
  
  
  public CatalogueAnalyser getCatalogueAnalyser() {
    return catalogueAnalyser;
  }
  
  
  public CourseManager getCourseManager() {
    return courseManager;
  }
  
  // TODO make private
  public EntityFactory getEntityFactory() {
    return entityFactory;
  }
  
  public void setupSaveDirectory(File dir) {
    if (storageManager == null) {
      storageManager = StorageManager.getInstance(dir);
      LOGGER.debug("Created StorageManager, dir={}", storageManager.getSaveDir());
    } else {
      storageManager.setSaveDir(dir);
      LOGGER.debug("Re-set savedir={}", storageManager.getSaveDir());
    }
  }
  
  public Group createGroup(String name, Member... members){
    Group g = entityFactory.createGroup(name, members);
    entityFactory.link(g, getCourse());
    g.setProgressSummaries(entityFactory.createProgressSummaries());
    g.setProgressList(entityFactory.createProgressList());
    addGroup(g);
    LOGGER.debug("Created {}", g);
    return g;
  }
  
  public void addGroup(Group g){
    observableGroups.add(g);
    addGroupAnalyser(g, new GroupAnalyser(getCourse(), getCatalogue(), g));
    progressGroupMap.put(g.getUuid(), FXCollections.observableList(g.getProgressList()));
    summaryGroupMap.put(g.getUuid(), FXCollections.observableList(g.getProgressSummaries()));
  }
  
  public void removeGroup(Group g){
    observableGroups.remove(g);
    removeGroupAnalyser(g);
    progressGroupMap.remove(g.getUuid());
    summaryGroupMap.remove(g.getUuid());
  }
  
  public Group getGroup(UUID id){
    for(Group g : observableGroups){
      if(g.getUuid().equals(id)){
        return g;
      }
    }
    return null;
  }
  
  public boolean isEmpty() {
    return groupAnalyserMap.isEmpty();
  }
  
  public GroupAnalyser getGroupAnalyser(Group key) {
    return groupAnalyserMap.get(key.getUuid());
  }
  
  public boolean containsGroupAnalyserFor(Group key) {
    return groupAnalyserMap.containsKey(key.getUuid());
  }
  
  public GroupAnalyser addGroupAnalyser(Group key, GroupAnalyser value) {
    return groupAnalyserMap.put(key.getUuid(), value);
  }
  
  public GroupAnalyser removeGroupAnalyser(Group key) {
    return groupAnalyserMap.remove(key.getUuid());
  }
  
  public ObservableList<Requirement> getObservableRequirements() {
    return observableRequirements;
  }
  
  public ObservableList<Milestone> getObservableMilestones() {
    return observableMilestones;
  }
  
  public boolean removeRequirement(Requirement requirement) {
    // TODO Check dependecies
    boolean result = entityFactory.getCatalogue().removeRequirement(requirement);
    LOGGER.debug("Removing req={}", requirement);
    LOGGER.debug("After deletion: {}", entityFactory.getCatalogue().getRequirements());
    observableRequirements.remove(requirement);
    return result;
  }
  
  public boolean removeMilestone(Milestone milestone) {
    // TODO Check dependencies
    boolean result = entityFactory.getCatalogue().removeMilestone(milestone);
    LOGGER.debug("Removing ms={}", milestone);
    LOGGER.debug("After deletion: {}", entityFactory.getCatalogue().getMilestones());
    observableMilestones.remove(milestone);
    return result;
  }
  
  public boolean hasCatalogue() {
    if (entityFactory == null) {
      return false;
    } else {
      return entityFactory.getCatalogue() != null;
    }
  }
  
  public Catalogue getCatalogue() {
    if (entityFactory == null) {
      return null;
    }
    return entityFactory.getCatalogue();
  }
  
  public boolean hasCourse() {
    if (entityFactory == null) {
      return false;
    } else {
      return entityFactory.getCourse() != null;
    }
  }
  
  public void saveCourse() {
    if (hasCourse()) {
      if (storageManager != null) {
        LOGGER.debug("Saving course");
        //OperationFactory.createSaveCourseOperation(getCourse(), null).start();
        try {
          storageManager.saveCourse(getCourse());
        } catch (IOException e) {
          LOGGER.catching(e);
          // TODO what to do
        }
      } else {
        throw LOGGER.throwing(new IllegalStateException("Cannot save the course if no StorageManager is available"));
      }
    }
  }
  
  public void saveCatalogue() {
    if (hasCatalogue()) {
      if (storageManager != null) {
        LOGGER.debug("Saving catalogue");
        //OperationFactory.createSaveCatalogueOperation(getCatalogue(), null).start();
        try {
          storageManager.saveCatalogue(getCatalogue());
          storageManager.saveCourse(getCourse()); // because times could have been added
        } catch (IOException e) {
          LOGGER.catching(e);
          // TODO What to do
        }
      } else {
        throw LOGGER.throwing(new IllegalStateException("Canno save catalogue if no StorageManager is available"));
      }
    }
  }
  
  public StorageManager getStorageManager() {
    return storageManager;
  }
  
  public boolean isStorageManagerReady() {
    return storageManager != null && storageManager.getSaveDir() != null;
  }
  
  public void openCourse(File courseFile) {
    storageManager = StorageManager.getInstance(courseFile.getParentFile());
    try {
      Course c = storageManager.openCourse(courseFile);
      entityFactory = EntityFactory.createFactoryFor(c);
    } catch (IOException e) {
      LOGGER.catching(e);
      //TODO what to do
    }
  }
  
  public void openCatalogue() throws IOException, UuidMismatchException {
    if(storageManager.getCourse() != null){
      Course c = getCourse();
      entityFactory = EntityFactory.createFactoryFor(c, storageManager.openCatalogue() );
    }else{
      Catalogue cat = storageManager.openCatalogue();
      Course c = storageManager.getCourse();
      entityFactory = EntityFactory.createFactoryFor(c,cat);
    }
    setupObservableCatalogueLists();
    loadedCatalogue();
  }
  
  public void loadSession(){
    if(sessionManager.hasSession() ){
      sessionManager.loadSession();
      SessionStorage session = sessionManager.getSessionStorage();
      LOGGER.info("Loaded session {}", session);
      if(storageManager != null){
        storageManager.setSaveDir(new File(session.getLastUsedDir()));
      }else{
        storageManager = StorageManager.getInstance(new File(session.getLastUsedDir()));
      }
    }else{
      LOGGER.info("No session available");
    }
  }
  
  public void saveSession() throws IOException{
    if(storageManager != null){
      LOGGER.info("Storing session...");
      SessionStorage session = new SessionStorage();
      session.setDate(new Date() );
      session.setVersion(Version.getInstance().getVersion());
      session.setLastUsedDir(storageManager.getSaveDir().getAbsolutePath());
      sessionManager.storeSession(session);
      LOGGER.info("Stored session {}",session);
    }else{
      LOGGER.info("No StorageManager found, not writing a sesison");
    }
  }
}
