package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.storage.StorageManager;

import java.io.File;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SaveCourseTask extends ManagementTask<Boolean> {
  
  private final File saveFile;
  private final Course course;
  
  SaveCourseTask(Course course, File saveFile) {
    LOGGER.entry(saveFile, course);
    this.saveFile = saveFile;
    this.course = course;
  }
  
  @Override
  protected Boolean call() throws Exception {
    LOGGER.trace(":call");
    updateAll("Saving course into (" + StorageManager.getInstance().getSaveDir().getAbsolutePath() + ")", 0.2);
    if (saveFile == null) {
      StorageManager.getInstance().saveCourse();
    } else {
      StorageManager.getInstance().saveCourse(course);
    }
    updateAll("Successfully saved course to (" + StorageManager.getInstance().getCoursePath() + ")", 1.0);
    return LOGGER.traceExit(true);
  }
}