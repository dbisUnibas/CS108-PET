package ch.unibas.dmi.dbis.reqman.configuration;

import java.io.File;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ConfigUtils {

    private ConfigUtils(){
        // No instance needed
    }

    public static boolean isJARexecuted(){
        String codeSourceLocation = ConfigUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return codeSourceLocation.endsWith(".jar");
    }

    public static File getCodeSourceLocation(){
        return new File(ConfigUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath() );
    }

    public static final String getFileSeparator(){
        return System.getProperty("file.separator");
    }


}
