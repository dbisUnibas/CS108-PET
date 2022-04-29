package ch.unibas.dmi.dbis.cs108pet.session;

import ch.unibas.dmi.dbis.cs108pet.common.JSONUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The session manager stores and reads the session file
 *
 * @author loris.sauter
 */
public class SessionManager {
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  public static final String APP_DIR = ".cs108pet";
  
  public static final String SESSION_FILE_NAME = "session.json";
  
  private SessionStorage sessionStorage = null;
  
  /**
   * Loads the session storage.
   * Loading order is as follows:
   * <ol>
   * <li>A file named like {@link #SESSION_FILE_NAME} in the current working dir</li>
   * <li>A folder named like {@link #APP_DIR} in the current working dir contains a  {@link #SESSION_FILE_NAME}
   * named file</li>
   * <li>A file named like {@link #SESSION_FILE_NAME} in the user's home dir</li>
   * <li>A folder named like {@link #APP_DIR} in the user's home dir contains a {@link #SESSION_FILE_NAME}
   * named file</li>
   * </ol>
   *
   * @return The session file or null if there was none available
   */
  private SessionStorage loadSessionStorage() {
    String home = System.getProperty("user.home");
    String cwd = System.getProperty("user.dir");
    
    SessionStorage session = null;
    
    Path[] paths = new Path[4];
    paths[0] = Paths.get(cwd, SESSION_FILE_NAME);
    paths[1] = Paths.get(cwd, APP_DIR, SESSION_FILE_NAME);
    paths[2] = Paths.get(home, SESSION_FILE_NAME);
    paths[3] = Paths.get(home, APP_DIR, SESSION_FILE_NAME);
    for (Path path : paths) {
      session = loadSessionStorage(path.toFile());
      if (session != null) {
        break;
      }
    }
    return session;
  }
  
  private SessionStorage loadSessionStorage(File file) {
    LOGGER.debug("Loading session from {}", file);
    try {
      SessionStorage session = JSONUtils.readFromJSONFile(file, SessionStorage.class);
      LOGGER.debug("Successfully loaded session file {}", file);
      return session;
    } catch (IOException e) {
      LOGGER.debug("Couldn't read session-file {} for reason {}. Returning null.", file, e);
      return null;
    }
  }
  
  /**
   * Loads the session storage.
   * Loading order is as follows:
   * <ol>
   * <li>A file named like {@link #SESSION_FILE_NAME} in the current working dir</li>
   * <li>A folder named like {@link #APP_DIR} in the current working dir contains a  {@link #SESSION_FILE_NAME}
   * named file</li>
   * <li>A file named like {@link #SESSION_FILE_NAME} in the user's home dir</li>
   * <li>A folder named like {@link #APP_DIR} in the user's home dir contains a {@link #SESSION_FILE_NAME}
   * named file</li>
   * </ol>
   */
  public void loadSession() {
    sessionStorage = loadSessionStorage();
  }
  
  public SessionStorage getSessionStorage() {
    return sessionStorage;
  }
  
  /**
   * Stores the given session file.
   * The location is a file named {@link #SESSION_FILE_NAME} in a folder named {@link #APP_DIR} in the user's
   * home directory.
   * If the application is unable to create that directory, it writes the file named {@link #SESSION_FILE_NAME} to the
   * user's home directory.
   *
   * @param session
   */
  public boolean storeSession(SessionStorage session) {
    Path location = Paths.get(System.getProperty("user.home"), APP_DIR, SESSION_FILE_NAME);
    if (!makeAppDirIfNotExistent()) {
      location = Paths.get(System.getProperty("user.home"), SESSION_FILE_NAME);
    }
    try {
      JSONUtils.writeToJSONFile(session, location.toFile());
      return true;
    } catch (IOException e) {
      LOGGER.warn("Couldn't write session to {} for reason {}", location, e);
      return false;
    }
  }
  
  private boolean makeAppDirIfNotExistent() {
    Path loc = Paths.get(System.getProperty("user.home"), APP_DIR);
    if (!Files.isDirectory(loc)) {
      try {
        Files.createDirectory(loc);
        return true;
      } catch (IOException e) {
        LOGGER.catching(Level.ERROR, e);
        return false;
      }
    } else {
      return true;
    }
  }
  
  
  /**
   * Returns whether there is a session file.
   *
   * @return
   */
  public boolean hasSession() {
    SessionStorage session = loadSessionStorage();
    return session != null && session.isValidDir(session.getLastUsedDir());
  }
  
  
}
