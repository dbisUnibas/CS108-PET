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

    public static final String CONFIG_FILE_NAME = "templating" + "." + ConfigUtils.CONFIG_EXTENSION;

    public static final String TEMPLATE_EXTENSION = "template";

    public static final String IGNORE_TEMPLATE = "#";

    private final Logger LOGGER = LogManager.getLogger(TemplatingConfigurationManager.class);
    private Templates templates = null;
    private TemplatingConfiguration config;

    public void loadConfig(String configJSON) {
        try {
            setConfig(JSONUtils.readFromString(configJSON, TemplatingConfiguration.class));
        } catch (IOException ioe) {
            handleExceptionDuringLoading(ioe);
        }
        loadTemplateConfig();
    }

    public Templates getTemplates() throws IllegalStateException {
        if (templates == null) {
            throw new IllegalStateException("Cannot provide the templates when configuration was not yet loaded");
        }
        return templates;
    }

    public String getTemplatesExtension() throws IllegalStateException {
        if (config == null) {
            throw LOGGER.throwing(new IllegalStateException("Cannot provide a config which was not yet loaded"));
        }
        return config.getExtension();
    }

    public void loadConfig(File config) {
        if(config == null){
            throw LOGGER.throwing(new NullPointerException("Cannot load config if the specified file is null"));
        }
        LOGGER.info("Loading templating config for file: "+config.getPath() );
        TemplatingConfiguration cnfg = null;
        try {
            cnfg = JSONUtils.readFromJSONFile(config, TemplatingConfiguration.class);
            setConfig(cnfg);
        } catch (IOException ioe) {
            handleExceptionDuringLoading(ioe);
        }
        loadTemplateConfig();
        LOGGER.info("Finished loading templating config.");
    }

    public void loadConfig() {
        loadConfig(getConfigFile());
    }

    private void loadTemplateConfig() {
        try {
            templates = createTemplateConfig();
        } catch (FileNotFoundException e) {
            throw new ConfigurationException("Could not find template file: ", e);
        }
    }


    private void handleExceptionDuringLoading(IOException e) {
        if (e instanceof UnrecognizedPropertyException) {
            LOGGER.warn("Read an unexpected property. Ignoring it.", e);
        } else if (e instanceof JsonParseException) {
            throw LOGGER.throwing(Level.ERROR, new ConfigurationException("The config file could not be parsed", e));
        } else if (e instanceof JsonMappingException) {
            throw LOGGER.throwing(Level.ERROR, new ConfigurationException("The config object is corrupt.", e));
        } else {
            throw LOGGER.throwing(Level.ERROR, new ConfigurationException("An error occurred while reading the configuration.", e));
        }
    }

    private boolean setConfig(TemplatingConfiguration config) {
        boolean result = config.validateTemplatesAndFix();
        this.config = config;
        return result; // if true: had to fix the config.
    }

    private File buildTemplateFile(String file) {
        File template = new File(file);
        if (template.isAbsolute()) {
            return template;
        } else {
            if (ConfigUtils.isJARexecuted()) {
                // The environemnt is a jar.
                File jarFile = ConfigUtils.getCodeSourceLocation();
                // May add check if jarFile really is a file?
                File dir = jarFile.getParentFile();
                return new File(dir.getPath() + ConfigUtils.getFileSeparator() + file);
            } else {
                // Mostly IDE or console with command java -cp ...
                return new File(getClass().getClassLoader().getResource(file).getPath());
            }
        }
    }

    private Templates createTemplateConfig() throws FileNotFoundException {
        TemplatingConfiguration config = getConfig();

        Templates tc = new Templates();
        tc.setRequirementTemplate(readTemplateFile(config.getRequirementEntry()));
        tc.setMilestoneTemplate(readTemplateFile(config.getMilestoneEntry()));
        tc.setCatalogueTemplate(readTemplateFile(config.getCatalogueEntry()));

        tc.setProgressTemplate(readTemplateFile(config.getProgressEntry()));
        tc.setGroupMilestoneTemplate(readTemplateFile(config.getGroupMilestoneEntry()));
        tc.setGroupTemplate(readTemplateFile(config.getGroupEntry()));
        return tc;
    }

    TemplatingConfiguration getConfig() {
        if (config == null) {
            throw LOGGER.throwing(new IllegalStateException("Cannot provide a config which was not yet loaded"));
        }
        return config;
    }

    File getConfigFile() {
        if (!ConfigUtils.isJARexecuted()) {
            return new File(getClass().getClassLoader().getResource(CONFIG_FILE_NAME).getPath());
        } else {
            File dir = ConfigUtils.getCodeSourceLocation().getParentFile();
            return new File(dir.getPath() + ConfigUtils.getFileSeparator() + CONFIG_FILE_NAME);
        }
    }

    /**
     * @param file If the hash symbol is used for a file name, the template is set to be the empty string.
     * @return
     * @throws FileNotFoundException
     */
    String readTemplateFile(String file) throws FileNotFoundException {
        if (IGNORE_TEMPLATE.equals(file)) {
            return "";
        }
        BufferedReader br = new BufferedReader(new FileReader(buildTemplateFile(file)));
        StringBuilder sb = new StringBuilder();
        br.lines().forEach(line -> {
            sb.append(line);
            sb.append("\n");
        });

        return sb.toString();
    }


}
