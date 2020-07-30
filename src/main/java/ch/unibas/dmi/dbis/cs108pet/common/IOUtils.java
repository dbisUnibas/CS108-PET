package ch.unibas.dmi.dbis.cs108pet.common;

import org.apache.logging.log4j.core.util.FileUtils;

import java.io.File;

/**
 * General I/O related utilities.
 * Mostly file-extension related
 *
 * @author loris.sauter
 */
public class IOUtils {
  
  public static final String FILE_SEPARATOR = "/";
  public static final String EXTENSION_SEPARATOR = ".";
  
  private IOUtils() {
    // no objects needed
  }
  
  public static boolean hasExtension(File file) {
    if (!file.isFile()) {
      throw new IllegalArgumentException("Cannot check for existence of file extension, if no file is given");
    }
    int index = file.getName().lastIndexOf(EXTENSION_SEPARATOR);
    return index > 0;
  }
  
  public static boolean hasExpectedExtension(File file, String extension) {
    return FileUtils.getFileExtension(file).equals(extension);
  }
  
  
}
