package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.storage.StorageManager;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class OpenCourseTask extends ManagementTask<Course> {
  
  OpenCourseTask() {

  }
  
  
  @Override
  protected Course call() throws Exception {
    LOGGER.trace(":call");
    updateAll("Opening Course from (" + StorageManager.getInstance().getSaveDir().getAbsolutePath() + ")", 0.2);
    Course course = StorageManager.getInstance().openCourse();
    LOGGER.info("Successfully read course file " + StorageManager.getInstance().getCoursePath());
    updateAll("Successfully read catalogue from (" + StorageManager.getInstance().getCoursePath() + ")", 1.0);
    return LOGGER.traceExit(course);
  }
}
