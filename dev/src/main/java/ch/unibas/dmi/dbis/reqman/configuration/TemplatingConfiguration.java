package ch.unibas.dmi.dbis.reqman.configuration;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class TemplatingConfiguration {

    /**
     * The requirement entity key
     */
    @JsonIgnore
    public static final String REQUIREMENT = "requirement";
    /**
     * The milestone entity key
     */
    @JsonIgnore
    public static final String MILESTONE = "milestone";
    /**
     * The catalogue entity key
     */
    @JsonIgnore
    public static final String CATALOGUE = "catalogue";
    /**
     * The progress entity key
     */
    @JsonIgnore
    public static final String PROGRESS = "progress";
    /**
     * The group-milestone (progress-summary) entity key
     */
    @JsonIgnore
    public static final String GROUP_MILESTONE = "group-milestone";
    /**
     * The group entity key
     */
    @JsonIgnore
    public static final String GROUP = "group";
    /**
     * The template property key
     */
    @JsonIgnore
    public static final String TEMPLATE = "template";
    /**
     * The overview entity key
     */
    @JsonIgnore
    public static final String OVERVIEW = "overview";
    /**
     * The default templating config
     */
    @JsonIgnore
    public static final Map<String, String> DEFAULT_TEMPLATING_CONFIGURATION = generateDefaultTemplatesMap();
    /**
     * The extension of the export files
     */
    private String extension;
    /**
     * The template locations
     */
    private Map<String, String> templates;

    /**
     * Default constructor for new {@link TemplatingConfiguration}
     */
    public TemplatingConfiguration() {
        templates = new HashMap<>();
    }

    /**
     * Constructs a new {@link TemplatingConfiguration} with specified export extension
     * @param extension
     */
    public TemplatingConfiguration(String extension) {
        this();
        this.extension = extension;
    }

    /**
     * Constructs a new {@link TemplatingConfiguration} with specified export extension and template file locations
     * @param extension
     * @param templates
     */
    private TemplatingConfiguration(String extension, Map<String, String> templates) {
        this();
        this.extension = extension;
        // Don't clear the map, since it cannot have any values in it.
        this.templates.putAll(templates);
    }

    /**
     * Creates the default template configuration.
     * The default template configuration assumes that the template files are named like '<entity>.template' and
     * are located as siblings of the templating configuration file
     * @return
     */
    private static final Map<String, String> generateDefaultTemplatesMap() {
        HashMap<String, String> map = new HashMap<>();

        map.put(REQUIREMENT, StringUtils.concatWithPeriodDelimeter(REQUIREMENT, TEMPLATE));
        map.put(MILESTONE, StringUtils.concatWithPeriodDelimeter(MILESTONE, TEMPLATE));
        map.put(CATALOGUE, StringUtils.concatWithPeriodDelimeter(CATALOGUE, TEMPLATE));
        map.put(PROGRESS, StringUtils.concatWithPeriodDelimeter(PROGRESS, TEMPLATE));
        map.put(GROUP_MILESTONE, StringUtils.concatWithPeriodDelimeter(GROUP_MILESTONE, TEMPLATE));
        map.put(GROUP, StringUtils.concatWithPeriodDelimeter(GROUP, TEMPLATE));
        map.put(OVERVIEW, StringUtils.concatWithPeriodDelimeter(OVERVIEW, TEMPLATE));

        return map;
    }

    public String getExtension() {
        return extension;
    }

    public Map<String, String> getTemplates() {
        return new HashMap<>(templates);
    }

    @JsonIgnore
    public String getTemplatesEntry(String key) {
        return templates.get(key);
    }

    @JsonIgnore
    public String getRequirementEntry() {
        return getTemplatesEntry(REQUIREMENT);
    }

    @JsonIgnore
    public String getMilestoneEntry() {
        return getTemplatesEntry(MILESTONE);
    }

    @JsonIgnore
    public String getCatalogueEntry() {
        return getTemplatesEntry(CATALOGUE);
    }

    @JsonIgnore
    public String getProgressEntry() {
        return getTemplatesEntry(PROGRESS);
    }

    @JsonIgnore
    public String getGroupMilestoneEntry() {
        return getTemplatesEntry(GROUP_MILESTONE);
    }

    @JsonIgnore
    public String getOverviewEntry() {
        return getTemplatesEntry(OVERVIEW);
    }

    @JsonIgnore
    public String getGroupEntry() {
        return getTemplatesEntry(GROUP);
    }

    /**
     * Validates the templates property and fixes missing entries, by setting them to their default value.
     *
     * @return {@code true} if a fix was made, otherwise {@code false}
     */
    public boolean validateTemplatesAndFix() {
        if (extension == null) {
            throw new IllegalArgumentException("Mandatory field is missing:\n\textension"); // TODO May replace with custom exception
        } else if (extension.isEmpty()) {
            throw new IllegalArgumentException("Mandatory field has no value: \n\textension"); // TODO May replace with custom exception
        }
        int nbFixes = 0;
        if (templates.isEmpty()) {
            templates.putAll(generateDefaultTemplatesMap());
            nbFixes++;
        } else {
            nbFixes += validateAndFixEntry(REQUIREMENT);
            nbFixes += validateAndFixEntry(MILESTONE);
            nbFixes += validateAndFixEntry(CATALOGUE);
            nbFixes += validateAndFixEntry(PROGRESS);
            nbFixes += validateAndFixEntry(GROUP_MILESTONE);
            nbFixes += validateAndFixEntry(GROUP);
            nbFixes += validateAndFixEntry(OVERVIEW);
        }

        return nbFixes != 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TemplatingConfiguration{");
        sb.append("extension='").append(extension).append('\'');
        sb.append(", templates=").append(templates);
        sb.append('}');
        return sb.toString();
    }


    /**
     * Validates and fixes the entry of the templates map.
     *
     * @param key The key to validate for
     * @return An integer > 0 if a fix was made, 0 otherwise
     */
    private int validateAndFixEntry(String key) {
        if (!templates.containsKey(key)) {
            templates.put(key, DEFAULT_TEMPLATING_CONFIGURATION.get(key));
            return 1;
        } else {
            return 0;
        }
    }
}
