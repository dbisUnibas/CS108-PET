package ch.unibas.dmi.dbis.reqman.common;

import org.apache.logging.log4j.core.util.FileUtils;

import java.io.File;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class IOUtils {
  
  private IOUtils(){
    // no objects needed
  }
  
  public static final String EXTENSION_SEPARATOR = ".";
  
  public static boolean hasExtension(File file){
    if(!file.isFile()){
      throw new IllegalArgumentException("Cannot check for existence of file extension, if no file is given");
    }
    int index = file.getName().lastIndexOf(EXTENSION_SEPARATOR);
    return index > 0;
  }
  
  
}
