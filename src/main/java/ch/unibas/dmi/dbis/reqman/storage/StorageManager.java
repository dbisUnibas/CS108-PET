package ch.unibas.dmi.dbis.reqman.storage;

import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.data.Group;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

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
  
  private SaveFile<Course> courseSaveFile;
  private SaveFile<Catalogue> catalogueSaveFile;
  private List<SaveFile<Group>> groupSaveFileList;
  
  private final File dir;
  
  /**
   * Creates a new StorageManager for the current session, by specifying the savefolder.
   * @param dir The folder in which ReqMan should store all files
   */
  public StorageManager(File dir) {
    this.dir = dir;
  }
  
  /**
   * Lists the files found in the current directory.
   * @return
   */
  public List<ReqmanFile> listFiles(){
    ArrayList<ReqmanFile> files = new ArrayList<>();
    for(File f : dir.listFiles(REQMAN_FILE_FILTER)){
      LOGGER.debug("Processing file {}", f);
      try{
        ReqmanFile.Type t = ReqmanFile.Type.valueOf(FileUtils.getFileExtension(f).toUpperCase() );
        ReqmanFile rf = new ReqmanFile(f, t);
        files.add(rf);
        LOGGER.debug("Added {}", rf);
      }catch(IllegalArgumentException ex){
        LOGGER.error("Could not find the type of {}. Ignoring this file", ex);
      }
    }
    return files;
  }
  
  public Course openCourse(){
    // TODO simply open .course file in dir
    return null;
  }
  
  public Catalogue openCatalogue(){
    // TODO open .catalogue file in dir, then open .course file and check matching id
    return null;
  }
  
  public Group openGroup(){
    // TODO open .group file in dir, then open .course file in dir and check matching id, then open .catalogue file and check matching id
    return null;
  }
  
  public void saveCourse(Course course){
  
  }
  
  public void saveCatalogue(Catalogue catalogue){
  
  }
  
  public void saveGroup(Group group){
  
  }
  
  
  public static List<String> getKnownExtensions(){
    ArrayList<String> list = new ArrayList<>();
    for(ReqmanFile.Type type : ReqmanFile.Type.values()){
      list.add(type.getExtension() );
    }
    return list;
  }
  
  public static final FileFilter REQMAN_FILE_FILTER = pathname -> getKnownExtensions().contains(FileUtils.getFileExtension(pathname));
}
