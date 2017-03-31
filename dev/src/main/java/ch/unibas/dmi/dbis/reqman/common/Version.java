package ch.unibas.dmi.dbis.reqman.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * The {@link Version} singleton is used to inform the application reqman about its own
 * version. Upon building the version gets set.
 *
 * @author loris.sauter
 */
public class Version {

    private static Version instance = null;

    private static final String PROPERTIES_FILE = "reqman.properties";

    private static final String REQMAN_KEY = "reqman";

    private static final String VERSION_KEY = REQMAN_KEY+"."+"version";

    private final Properties props;

    private String version;

    private static final Logger LOGGER = LogManager.getLogger(Version.class);

    private Version() {
        props = new Properties();
        try {
            props.load(getClass().getResourceAsStream(PROPERTIES_FILE));
            version = (String)props.get(VERSION_KEY);
        } catch (IOException e) {
            LOGGER.error("Could not load reqman.properties.", e);
            LOGGER.error("Setting verstion to N/A, which is in general pretty bad. Check your build!");
            version = "N/A";
        }
    }

    /**
     * Returns the full version String. This matches the version suffix of the executed jar, when reqman was correctly built.
     * @return The full version String.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the single version instance, so the application has access to its own verison.
     * @return The single version instance
     */
    public static Version getInstance() {
        if(instance == null){
            instance = new Version();
        }
        return instance;
    }
}
