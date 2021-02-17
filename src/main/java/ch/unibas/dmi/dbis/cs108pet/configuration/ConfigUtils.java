package ch.unibas.dmi.dbis.cs108pet.configuration;

import java.io.File;

/**
 * Utility class for configuration management.
 * <p>
 * Contains utility functions needed during configuration reading.
 *
 * @author loris.sauter
 */
public class ConfigUtils {
  
  /**
   * The file extension for configuration files
   */
  public static final String CONFIG_EXTENSION = "config";
  
  /**
   * The private default constructor since no objects needed
   */
  private ConfigUtils() {
    // No instance needed
  }
  
  /**
   * Checks if the application currently is executed from within a JAR or not
   *
   * @return TRUE if the code source location ends with '.jar' and thus is likely to be from a JAR - false otherwise
   */
  public static boolean isJARexecuted() {
    String codeSourceLocation = ConfigUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    return codeSourceLocation.endsWith(".jar");
  }
  
  /**
   * Convenient method to get the code source location.
   *
   * @return The code source location as a {@link File}
   */
  public static File getCodeSourceLocation() {
    return new File(ConfigUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
  }
  
  /**
   * Convenient method to get the system file separator.
   *
   * @return The file separator string as defined by the system property 'file.separator'
   */
  public static final String getFileSeparator() {
    return System.getProperty("file.separator");
  }
  
  
}
