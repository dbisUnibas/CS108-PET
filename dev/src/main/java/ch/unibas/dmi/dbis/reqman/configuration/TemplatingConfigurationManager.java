package ch.unibas.dmi.dbis.reqman.configuration;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class TemplatingConfigurationManager {

    public static final String CONFIG_FILE_NAME = "templating"+"."+ ConfigUtils.CONFIG_EXTENSION;

    public static final String TEMPLATE_EXTENSION = "template";

    private final Logger LOGGER = LogManager.getLogger(TemplatingConfigurationManager.class);

    private TemplatingConfiguration config;

    public void loadConfig(String configJSON) {
        try {
            setConfig(JSONUtils.readFromString(configJSON, TemplatingConfiguration.class) );
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public TemplatingConfiguration getConfig() {
        if(config == null){
            throw LOGGER.throwing(new IllegalStateException("Cannot provide a config which was not yet loaded") );
        }
        return config;
    }

    private IOException lastIOE = null;

    public void loadConfig(File config){
        TemplatingConfiguration cnfg = null;
        try {
            cnfg = JSONUtils.readFromJSONFile(config, TemplatingConfiguration.class);
            // UnrecognizedPropertyException thrown, when typo in name, unkown key used
            // when extension field missing: no exception
            // when templates field missing: no exception


            // when empty object: no exception
            // when no top-level object: no exception
        }catch (IOException ioe) {
            handleExceptionDuringLoading(ioe);
        }
        if(cnfg != null){
            setConfig(cnfg);
        }else{
            throw new RuntimeException("Exception while loading configuration: ", lastIOE);
        }
    }


    private void handleExceptionDuringLoading(IOException e) {
        if(e instanceof UnrecognizedPropertyException){
            LOGGER.warn("Read an unexpected property. Ignoring it.", e);
        }else if(e instanceof JsonParseException){
            throw LOGGER.throwing(Level.ERROR, new RuntimeException("The config file could not be parsed",e));
        }else if(e instanceof JsonMappingException){
            throw LOGGER.throwing(Level.ERROR, new RuntimeException("The config object is corrupt.", e) );
        }else{
            throw LOGGER.throwing(Level.ERROR, new RuntimeException("An error occurred while reading the configuration.", e));
        }
        lastIOE = e;
    }

    private boolean setConfig(TemplatingConfiguration config){
        boolean result = config.validateTemplatesAndFix();
        this.config = config;
        return result; // if true: had to fix the config.
    }

    public void loadConfig(){
        loadConfig(getConfigFile() );
    }

    public File getConfigFile(){
        if(!ConfigUtils.isJARexecuted()){
            return new File(getClass().getClassLoader().getResource(CONFIG_FILE_NAME).getPath());
        }else{
            File dir = ConfigUtils.getCodeSourceLocation().getParentFile();
            return new File(dir.getPath()+ConfigUtils.getFileSeparator()+CONFIG_FILE_NAME);
        }
    }

    public String readTemplateFile(String file) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(buildTemplateFile(file)));
        StringBuilder sb = new StringBuilder();
        br.lines().forEach(line -> {
            sb.append(line);
            sb.append("\n");
        });

        return sb.toString();
    }

    public File buildTemplateFile(String file){
        File template = new File(file);
        if(template.isAbsolute()){
            return template;
        }else{
            if(ConfigUtils.isJARexecuted() ){
                // The environemnt is a jar.
                File jarFile = ConfigUtils.getCodeSourceLocation();
                // May add check if jarFile really is a file?
                File dir = jarFile.getParentFile();
                return new File(dir.getPath()+ConfigUtils.getFileSeparator()+file);
            }else{
                // Mostly IDE or console with command java -cp ...
                return new File(getClass().getClassLoader().getResource(file).getPath());
            }
        }
    }


}
