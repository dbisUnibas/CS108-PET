package ch.unibas.dmi.dbis.reqman.configuration;

import java.io.File;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ConfigUtils {

    public static final String CONFIG_EXTENSION = "config";

    private ConfigUtils() {
        // No instance needed
    }

    public static boolean isJARexecuted() {
        String codeSourceLocation = ConfigUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return codeSourceLocation.endsWith(".jar");
    }

    public static File getCodeSourceLocation() {
        return new File(ConfigUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    public static final String getFileSeparator() {
        return System.getProperty("file.separator");
    }


}
