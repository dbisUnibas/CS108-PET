package ch.unibas.dmi.dbis.reqman.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Properties;

/**
 * The {@link Version} singleton is used to inform the application reqman about its own
 * version. Upon building the version gets set.
 *
 * @author loris.sauter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Version implements Comparable<Version> {
  
  public static final String NO_VERSION = "N/A";
  public static final String VERSION_NOT_SET = "!!VERSION!!";
  private static final String PROPERTIES_FILE = "reqman.properties";
  
  private static final String REQMAN_KEY = "reqman";
  
  private static final String VERSION_KEY = REQMAN_KEY + "." + "version";
  private static final Logger LOGGER = LogManager.getLogger(Version.class);
  public static final String ERROR_MSG = " This is crucial, version based features won't work!";
  private static Version instance = null;
  private Properties props;
  private String version;
  
  private Version() {
    props = new Properties();
    try {
      props.load(Version.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
      version = (String) props.get(VERSION_KEY);
      parseVersion();
      if (!Character.isDigit(version.charAt(0))) {
        LOGGER.error("Version invalid. Are we in a dev environemnt?" + String.format(" (version=%s)", version));
        handleInvalidVersion();
      }
    } catch (Exception e) {
      LOGGER.error("Could not load reqman.properties.", e);
      handleInvalidVersion();
    }
  }
  
  private Version(String version) {
    this.version = version;
    parseVersion();
  }
  
  private int major = -1;
  private int minor = -1;
  private int patch = -1;
  private String suffix = null;
  
  @Override
  public int compareTo(@NotNull Version o) {
    return getFullVersion().compareTo(o.getFullVersion());
  }
  
  public boolean isInvalid() {
    return major == -1;
  }
  
  private void parseVersion() {
    LOGGER.debug("Parsing version: {}", version);
    String[] first = version.split("-");
    if (first.length > 1) {
      suffix = first[1];
    } else if (first == null || first.length == 0) {
      LOGGER.fatal("No real version set." + ERROR_MSG);
      // Possible if in ide and not loaded / manually set version
      return;
    }
    String[] majorMinorPatch = first[0].split("\\.");
    LOGGER.debug("Version parsing: {}", Arrays.toString(majorMinorPatch));
    if (majorMinorPatch.length < 3) {
      LOGGER.fatal("Invalid version format!" + ERROR_MSG);
      return;
    }
    try {
      major = Integer.parseInt(majorMinorPatch[0]);
      minor = Integer.parseInt(majorMinorPatch[1]);
      patch = Integer.parseInt(majorMinorPatch[2]);
    } catch (NumberFormatException e) {
      LOGGER.warn("Illegal version format! ({}). " + ERROR_MSG, e);
      return;
    }
  }
  
  public boolean hasSuffix() {
    return suffix != null;
  }
  
  public int getMajor() {
    return major;
  }
  
  public int getMinor() {
    return minor;
  }
  
  public int getPatch() {
    return patch;
  }
  
  public String getSuffix() {
    return suffix;
  }
  
  /**
   * Returns the single version instance, so the application has access to its own version.
   * Technically this is not a singleton class, since the version can also be used to compare read versions from
   * entities.
   *
   * @return The single version instance
   */
  public static Version getInstance() {
    if (instance == null) {
      instance = new Version();
    }
    return instance;
  }
  
  public static Version forString(String version) {
    return new Version(version);
  }
  
  /**
   * Returns the full version String. This matches the version suffix of the executed jar, when reqman was correctly
   * built.
   *
   * @return The full version String.
   */
  public String getFullVersion() {
    return version;
  }
  
  /**
   * Returns the version string.
   * Since ReqMan uses semantic versioning, this returns a string in format:
   * MAJOR.MINOR.FIX
   *
   * @return the version string in format semantic versioning format
   */
  public String getVersion() {
    if (version.equals(NO_VERSION)) {
      return NO_VERSION;
    }
    int index = version.indexOf("-");
    if (index > 0) {
      return version.substring(0, index);
    }
    return version;
  }
  
  
  private void handleInvalidVersion() {
    LOGGER.error("Setting verstion to N/A, which is in general pretty bad. Check your build!");
    version = NO_VERSION;
  }
}
