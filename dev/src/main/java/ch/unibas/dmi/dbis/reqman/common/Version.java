package ch.unibas.dmi.dbis.reqman.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * The {@link Version} singleton is used to inform the application reqman about its own
 * version. Upon building the version gets set.
 *
 * @author loris.sauter
 */
public class Version {

    public static final String NO_VERSION = "N/A";
    private static final String PROPERTIES_FILE = "reqman.properties";

    private static final String REQMAN_KEY = "reqman";

    private static final String VERSION_KEY = REQMAN_KEY + "." + "version";
    private static final Logger LOGGER = LogManager.getLogger(Version.class);
    private static Version instance = null;
    private final Properties props;
    private String version;

    private Version() {
        props = new Properties();
        try {
            props.load(Version.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
            version = (String) props.get(VERSION_KEY);
            if (!Character.isDigit(version.charAt(0))) {
                LOGGER.error("Version invalid. Are we in a dev environemnt?" + String.format(" (version=%s)", version));
                handleInvalidVersion();
            }
        } catch (Exception e) {
            LOGGER.error("Could not load reqman.properties.", e);
            handleInvalidVersion();
        }
    }

    /**
     * Returns the single version instance, so the application has access to its own verison.
     *
     * @return The single version instance
     */
    public static Version getInstance() {
        if (instance == null) {
            instance = new Version();
        }
        return instance;
    }

    /**
     * Returns the full version String. This matches the version suffix of the executed jar, when reqman was correctly built.
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
