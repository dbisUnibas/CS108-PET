package ch.unibas.dmi.dbis.reqman.storage;

import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.data.Group;

import java.io.File;
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
    return null;
  }
  
  public Course openCourse(){
    return null;
  }
  
  public Catalogue openCatalogue(){
    return null;
  }
  
  public Group openGroup(){
    return null;
  }
  
  public void saveCourse(Course course){
  
  }
  
  public void saveCatalogue(Catalogue catalogue){
  
  }
  
  public void saveGroup(Group group){
  
  }
  
  
  
}
