package ch.unibas.dmi.dbis.reqman.storage;

import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.data.Group;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The {@link StorageManager} manages {@link SaveFile} for a ReqMan session.
 * <p>
 * For this purpose, the StorageManager is able to open any ReqMan related file and may open related files as well.
 * For instance, if a catalogue file is being scheduled for opening, the manager will automatically try to open a corresponding course file or throw an exception.
 * <p>
 * It keeps track of all opened files and is enables other components of ReqMan to read and write from the files.
 * <p>
 * Furthermore it provides the means for the UI to list and show ReqMan related files.
 *
 * @author loris.sauter
 */
public class StorageManager {
  private Logger LOGGER = LogManager.getLogger();
  
  private SaveFile courseSaveFile;
  private SaveFile catalogueSaveFile;
  private List<SaveFile> groupSaveFileList;
  
  private File dir;
  
  // TODO: openX - check if already opened and check if dependencies were already opened
  
  /**
   * Creates a new StorageManager for the current session, by specifying the savefolder.
   * @param dir The folder in which ReqMan should store all files
   */
  private StorageManager(File dir) {
    this.dir = dir;
    LOGGER.debug("Created with dir={}",dir);
    groupSaveFileList = new ArrayList<>();
  }
  
  private static StorageManager instance = null;
  
  /**
   * Creates a new StorageManager for the current session, by specifying the savefolder.
   * @param dir The folder in which ReqMan should store all files
   */
  public static StorageManager getInstance(File dir){
    if(instance == null){
      instance = new StorageManager(dir);
    }
    return instance;
  }
  
  public static StorageManager getInstance(){
    if(instance == null){
      throw new IllegalStateException("Cannot get instance of StorageManager, due to instance not set");
    }
    return instance;
  }
  
  /**
   * Lists the files found in the current directory.
   * @return
   */
  public List<ReqmanFile> listFiles(){
    ArrayList<ReqmanFile> files = new ArrayList<>();
    Arrays.stream(dir.listFiles(REQMAN_FILE_FILTER)).forEach(f -> {
      LOGGER.debug("Processing file {}", f);
      try {
        ReqmanFile.Type t = ReqmanFile.Type.valueOf(FileUtils.getFileExtension(f).toUpperCase());
        ReqmanFile rf = new ReqmanFile(f, t);
        files.add(rf);
        LOGGER.debug("Added {}", rf);
      } catch (IllegalArgumentException ex) {
        LOGGER.error("Could not find the type of {}. Ignoring this file", ex);
      }
    });
    return files;
  }
  
  public Course openCourse() throws IOException {
    courseSaveFile = SaveFile.createForSaveDir(dir, Course.class);
    courseSaveFile.open();
    LOGGER.debug("Opened course: {}", courseSaveFile.getEntity());
    return (Course)courseSaveFile.getEntity();
  }
  
  public Course openCourse(File file) throws IOException{
    courseSaveFile = SaveFile.createForSaveFile(file, Course.class);
    courseSaveFile.open();
    LOGGER.debug("Opened coruse:{}", courseSaveFile.getEntity());
    return (Course)courseSaveFile.getEntity();
  }
  
  public Catalogue openCatalogue() throws IOException, UuidMismatchException {
    LOGGER.debug("Opening catalogue with dir={}", dir);
    catalogueSaveFile = SaveFile.createForSaveDir(dir, Catalogue.class);
    catalogueSaveFile.open();
    
    Catalogue cat = (Catalogue) catalogueSaveFile.getEntity();
    Course course = openCourse();
    
    if(!matchingUuid(course.getCatalogueUUID(), cat.getUuid())){
      // 'Clean savefiles'
      catalogueSaveFile = null;
      courseSaveFile = null;
      throw new UuidMismatchException(course.getCatalogueUUID(), cat.getUuid());
    }
    LOGGER.debug("Opened catalogue {}",cat);
    return cat;
  }
  
  public Course getCourse(){
    if(courseSaveFile != null){
      return (Course) courseSaveFile.getEntity();
    }
    return null;
  }
  
  public Catalogue getCatalogue(){
    if(catalogueSaveFile != null){
      return (Catalogue) catalogueSaveFile.getEntity();
    }
    return null;
  }
  
  public Group openGroup(File file) throws IOException, UuidMismatchException {
    SaveFile groupFile = SaveFile.createForSaveFile(file, Group.class);
    groupFile.open();
    
    Group group = (Group) groupFile.getEntity();
    
    Catalogue cat = getCatalogue(); // Checks if catalogue is matchin on its own.
    Course course = getCourse(); // Loaded / opened due call of openCatalogue()
    
    boolean matchingCourse = matchingUuid(group.getCourseUuid(), course.getUuid());
    boolean matchingCatalogue = matchingUuid(group.getCatalogueUuid(), cat.getUuid());
    
    if(!(matchingCatalogue && matchingCourse)){
      UuidMismatchException ex;
      if(!matchingCatalogue){
        ex = new UuidMismatchException(group.getCourseUuid(), cat.getUuid());
      }else{
        ex = new UuidMismatchException(group.getCourseUuid(), course.getUuid());
      }
      throw ex;
    }
    
    groupSaveFileList.add(groupFile);
    return group;
  }
  
  public void saveCourse(Course course) throws IOException {
    checkIfDirSet();
    courseSaveFile = SaveFile.createForEntity(course);
    courseSaveFile.setSaveDirectory(dir);
    LOGGER.debug("Course savePath={}",courseSaveFile.getSaveFilePath());
    courseSaveFile.save();
    LOGGER.debug("Saved course to {}", courseSaveFile.getSaveFilePath());
  }
  
  public void saveCourse() throws IOException{
    LOGGER.debug("Saving to {}",courseSaveFile.getSaveFilePath());
    courseSaveFile.save();
    LOGGER.debug("Saved course to {}", courseSaveFile.getSaveFilePath());
  }
  
  public void saveCatalogue(Catalogue catalogue) throws IOException {
    checkIfDirSet();
    catalogueSaveFile = SaveFile.createForEntity(catalogue);
    catalogueSaveFile.setSaveDirectory(dir);
    LOGGER.debug("Catalogue savePath={}", catalogueSaveFile.getSaveFilePath());
    catalogueSaveFile.save();
    LOGGER.debug("Saved catalogue to {}", catalogueSaveFile.getSaveFilePath());
  }
  
  public void saveCatalogue() throws IOException {
    catalogueSaveFile.save();
    LOGGER.debug("Saved catalogue to {}", catalogueSaveFile.getSaveFilePath());
  }
  
  public void saveGroup(Group group, boolean sensitively) throws IOException {
    // TODO handle existing group
    LOGGER.debug("Save group with group obj as param");
    checkIfDirSet();
    SaveFile groupFile = SaveFile.createForEntity(group);
    groupFile.setSaveDirectory(dir);
    if(sensitively){
      groupFile.save();
    }else{
      groupFile.save();
    }
    LOGGER.debug("Saved group to {}", groupFile.getSaveFilePath());
    groupSaveFileList.add(groupFile);
  }
  
  public void saveGroup(UUID groupUuid) throws IOException {
    for (SaveFile sf : groupSaveFileList) {
      if (groupUuid.equals(((Group) sf.getEntity()).getUuid())){
        LOGGER.debug("Trying to save at {}", sf.getSaveFilePath());
        sf.save();
        LOGGER.debug("Saved group to {}", sf.getSaveFilePath());
        return;
      }
    }
  }
  
  public boolean hasGroupSaveFile(UUID groupUuid){
    for(SaveFile sf : groupSaveFileList){
      if(groupUuid.equals( ((Group)sf.getEntity()).getUuid())){
        return true;
      }
    }
    return false;
  }
  
  
  private static List<String> getKnownExtensions(){
    ArrayList<String> list = new ArrayList<>();
    for(ReqmanFile.Type type : ReqmanFile.Type.values()){
      list.add(type.getExtension() );
    }
    return list;
  }
  
  private static boolean matchingUuid(UUID expected, UUID actual){
    return expected.equals(actual);
  }
  
  public File getSaveDir() {
    return dir;
  }
  
  public String getCataloguePath() {
    if(catalogueSaveFile == null){
      return null;
    }
    return catalogueSaveFile.getSaveFilePath();
  }
  
  public String getCoursePath() {
    if(courseSaveFile == null){
      return null;
    }
    return courseSaveFile.getSaveFilePath();
  }
  
  public void setSaveDir(File saveDir) {
    LOGGER.debug("Set savedir to {}", saveDir);
    this.dir = saveDir;
  }
  
  public void saveGroupSensitively(Group g) throws IOException {
    LOGGER.debug("Saving group saensitively");
    if(hasGroupSaveFile(g.getUuid())){
      saveGroup(g.getUuid());
    }else{
      saveGroup(g, true);
    }
  }
  
  private void checkIfDirSet() throws RuntimeException{
    if(dir == null){
      throw new RuntimeException("Save Directory not set");
    }
  }
  
  public static final FileFilter REQMAN_FILE_FILTER = pathname -> getKnownExtensions().contains(FileUtils.getFileExtension(pathname));
  
}
