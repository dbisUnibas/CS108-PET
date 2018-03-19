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
 * The {@link TemplatingConfigurationManager} loads and parses template configuration files.
 *
 * @author loris.sauter
 */
public class TemplatingConfigurationManager {

    /**
     * The template configuration file name
     */
    public static final String CONFIG_FILE_NAME = "templating" + "." + ConfigUtils.CONFIG_EXTENSION;

    /**
     * The template extension
     */
    public static final String TEMPLATE_EXTENSION = "template";

    /**
     * The value to indicate that no such template exists
     */
    public static final String IGNORE_TEMPLATE = "#";

    private final Logger LOGGER = LogManager.getLogger(TemplatingConfigurationManager.class);

    /**
     * The templates object
     */
    private Templates templates = null;
    private TemplatingConfiguration config;
    private File configFile = null;

    /**
     * Loads the config from the given string.
     * The string must be a JSON formatted representation of {@link TemplatingConfiguration}
     * @param configJSON The string to load the config from
     */
    public void loadConfig(String configJSON) {
        try {
            setConfig(JSONUtils.readFromString(configJSON, TemplatingConfiguration.class));
        } catch (IOException ioe) {
            handleExceptionDuringLoading(ioe);
        }
        loadTemplateConfig();
    }

    /**
     * Returns the templates if already loaded.
     * @return Returns the templates (and thus the template file location for the registered entities)
     * @throws IllegalStateException If no template configuration was previously loaded
     */
    public Templates getTemplates() throws IllegalStateException {
        if (templates == null) {
            throw new IllegalStateException("Cannot provide the templates when configuration was not yet loaded");
        }
        return templates;
    }

    /**
     * Returns the export file extension
     * @return The file extension for export files if not elsewhere specified
     * @throws IllegalStateException It fno template configuration was previously loaded
     */
    public String getExportExtension() throws IllegalStateException {
        if (config == null) {
            throw LOGGER.throwing(new IllegalStateException("Cannot provide a config which was not yet loaded"));
        }
        return config.getExtension();
    }

    /**
     * Loads the template configuration from the given file.
     * The passed file must exist and be a JSON formatted representation of {@link TemplatingConfiguration}
     * @param config The configuration file
     */
    public void loadConfig(File config) {
        if (config == null) {
            throw LOGGER.throwing(new NullPointerException("Cannot load config if the specified file is null"));
        }
        LOGGER.info("Loading templating config for file: " + config.getPath());
        TemplatingConfiguration cnfg = null;
        try {
            cnfg = JSONUtils.readFromJSONFile(config, TemplatingConfiguration.class);
            LOGGER.debug("JSON read");
            setConfig(cnfg);
            configFile = config;
        } catch (IOException ioe) {
            handleExceptionDuringLoading(ioe);
        }
        loadTemplateConfig();
        LOGGER.info("Finished loading templating config.");
    }

    /**
     * Loads the template configuration assuming default location
     * The default location of the template configuration file is
     * next to the executing JAR (which is often equally to being a child of the current working dir)
     */
    public void loadConfig() {
        loadConfig(getConfigFile());
    }

    /**
     * Returns the loaded configuration
     * @return the loaded configuration
     */
    TemplatingConfiguration getConfig() {
        if (config == null) {
            throw LOGGER.throwing(new IllegalStateException("Cannot provide a config which was not yet loaded"));
        }
        return config;
    }

    /**
     * Returns the default path to the template configuration.
     * If the application is executed from within a JAR, the configuration file is expected
     * to be next to the JAR (a.k.a. being a silbing of the JAR).
     * Otherwise, if the applicaiton is executed without being packed into a JAR,
     * it is expected to be within the classpath.
     * @return
     */
    File getConfigFile() {
        if (!ConfigUtils.isJARexecuted()) {
            return new File(getClass().getClassLoader().getResource(CONFIG_FILE_NAME).getPath());
        } else {
            File dir = ConfigUtils.getCodeSourceLocation().getParentFile();
            return new File(dir.getPath() + ConfigUtils.getFileSeparator() + CONFIG_FILE_NAME);
        }
    }

    /**
     * Reads the template file specified and returns the file's contents.
     * If the hash symbol is used for a file name, the template is set to be the empty string.
     * @param file The file name of the template file. If {@link TemplatingConfigurationManager#IGNORE_TEMPLATE} is used
     *             as file name, the empty string <code>""</code> is returned.
     * @return The contents of the template file
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

    /**
     * Effectively loads the contents of the template configuration
     */
    private void loadTemplateConfig() {
        try {
            templates = createTemplateConfig();
        } catch (FileNotFoundException e) {
            throw new ConfigurationException("Could not find template file: ", e);
        }
    }

    /**
     * Handles exception during loading.
     * Most exceptions are logged and re-thrown as {@link ConfigurationException}
     * @param e
     */
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

    /**
     * Sets the {@link TemplatingConfiguration} while validating it.
     * @param config The config to set
     * @return If the configuration file had missing / invalid entries which had to be fixed
     */
    private boolean setConfig(TemplatingConfiguration config) {
        boolean result = config.validateTemplatesAndFix();
        this.config = config;
        return result; // if true: had to fix the config.
    }

    /**
     * Creates the {@link File} of a template file.
     * The entries of the template configuration could be absolute or relative paths and this
     * resolving of locations is handled within this method.
     * @param file
     * @return
     */
    private File buildTemplateFile(String file) {
        LOGGER.debug("Building template file for: " + file);
        File template = new File(file);
        if (template.isAbsolute()) {
            LOGGER.debug("Absolute path: " + template.getPath());
            return template;
        } else {
            if (configFile != null) {
                LOGGER.debug("Config file directly specified. Resolving relative path of " + configFile.getPath());
                File f = configFile.toPath().resolveSibling(file).toFile(); // assuming relative to config file
                LOGGER.debug("Resolved template file: " + f.getPath());
                return f;
            } else if (ConfigUtils.isJARexecuted()) {
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

    /**
     * Creates the {@link Templates} container for the read template entices.
     * @return
     * @throws FileNotFoundException
     */
    private Templates createTemplateConfig() throws FileNotFoundException {
        LOGGER.traceEntry();
        TemplatingConfiguration config = getConfig();

        Templates tc = new Templates();
        tc.setRequirementTemplate(readTemplateFile(config.getRequirementEntry()));
        tc.setMilestoneTemplate(readTemplateFile(config.getMilestoneEntry()));
        tc.setCatalogueTemplate(readTemplateFile(config.getCatalogueEntry()));

        tc.setProgressTemplate(readTemplateFile(config.getProgressEntry()));
        tc.setProgressSummaryTemplate(readTemplateFile(config.getProgressSummaryEntry()));
        tc.setGroupTemplate(readTemplateFile(config.getGroupEntry()));

        return tc;
    }


}
