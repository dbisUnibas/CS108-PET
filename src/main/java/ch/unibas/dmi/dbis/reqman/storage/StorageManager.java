package ch.unibas.dmi.dbis.reqman.storage;

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
  // TODO Decide: Singleton or not
  private final Logger LOGGER = LogManager.getLogger();
  
  private SaveFile courseSaveFile;
  private SaveFile catalogueSaveFile;
  private List<SaveFile> groupSaveFileList;
  
  private final File dir;
  
  /**
   * Creates a new StorageManager for the current session, by specifying the savefolder.
   * @param dir The folder in which ReqMan should store all files
   */
  public StorageManager(File dir) {
    this.dir = dir;
    groupSaveFileList = new ArrayList<>();
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
    return (Course)courseSaveFile.getEntity();
  }
  
  public Catalogue openCatalogue() throws IOException, UuidMismatchException {
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
    // TODO open .group file in dir, then open .course file in dir and check matching id, then open .catalogue file and check matching id
    SaveFile groupFile = SaveFile.createForSaveFile(file, Group.class);
    groupFile.open();
    
    Group group = (Group) groupFile.getEntity();
    
    Catalogue cat = openCatalogue(); // Checks if catalogue is matchin on its own.
    Course course = getCourse(); // Loaded / opened due call of openCatalogue()
    
    if(!matchingUuid(group.getCourseUuid(), course.getUuid())){
      // Resetting the other opened files
      catalogueSaveFile = null;
      courseSaveFile=null;
      throw new UuidMismatchException(group.getCourseUuid(), course.getUuid());
    }
    
    groupSaveFileList.add(groupFile);
    return group;
  }
  
  public void saveCourse(Course course) throws IOException {
    checkIfDirSet();
    courseSaveFile = SaveFile.createForEntity(course);
    courseSaveFile.setSaveDirectory(dir);
    courseSaveFile.save();
  }
  
  public void saveCourse() throws IOException{
    courseSaveFile.save();
  }
  
  public void saveCatalogue(Catalogue catalogue) throws IOException {
    checkIfDirSet();
    catalogueSaveFile = SaveFile.createForEntity(catalogue);
    catalogueSaveFile.setSaveDirectory(dir);
    catalogueSaveFile.save();
  }
  
  public void saveCatalogue() throws IOException {
    catalogueSaveFile.save();
  }
  
  public void saveGroup(Group group) throws IOException {
    // TODO handle existing group
    checkIfDirSet();
    SaveFile groupFile = SaveFile.createForEntity(group);
    groupFile.setSaveDirectory(dir);
    groupFile.save();
    groupSaveFileList.add(groupFile);
  }
  
  public void saveGroup(UUID groupUuid) throws IOException {
    for (SaveFile sf : groupSaveFileList) {
      if (groupUuid.equals(((Group) sf.getEntity()).getUuid())){
        sf.save();
      }
    }
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
  
  private void checkIfDirSet() throws RuntimeException{
    if(dir == null){
      throw new RuntimeException("Save Directory not set");
    }
  }
  
  public static final FileFilter REQMAN_FILE_FILTER = pathname -> getKnownExtensions().contains(FileUtils.getFileExtension(pathname));
}
