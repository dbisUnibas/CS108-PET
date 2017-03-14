package ch.unibas.dmi.dbis.reqman.configuration;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class TemplatingConfigurationManager {

    public static final String CONFIG_EXTENSION = "config";

    public static final String CONFIG_FILE_NAME = "templating"+"."+CONFIG_EXTENSION;

    public static final String TEMPLATE_EXTENSION = "template";

    private final Logger LOGGER = LogManager.getLogger(TemplatingConfigurationManager.class);

    private TemplatingConfiguration config;

    public void loadConfig(String configJSON) {
        try {
            config = JSONUtils.readFromString(configJSON, TemplatingConfiguration.class);
        } catch (JsonParseException pe) {
            // Throw again or new exception?
        } catch(JsonMappingException me){
            // handle missing mandatory extension?
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public TemplatingConfiguration getConfig() {
        return config;
    }

    public void loadConfig(File config){
        try {
            this.config = JSONUtils.readFromJSONFile(config, TemplatingConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
