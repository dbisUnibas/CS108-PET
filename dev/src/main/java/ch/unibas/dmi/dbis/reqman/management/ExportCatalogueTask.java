package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.configuration.Templates;
import ch.unibas.dmi.dbis.reqman.configuration.TemplatingConfigurationManager;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.templating.RenderManager;

import java.io.File;
import java.io.PrintWriter;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class ExportCatalogueTask extends ManagementTask<Boolean> {

    private final Catalogue catalogue;
    private final File file;

    public ExportCatalogueTask(Catalogue catalogue, File file) {
        LOGGER.entry(catalogue, file);
        this.catalogue = catalogue;
        this.file = file;
    }

    @Override
    protected Boolean call() throws Exception {
        updateAll("Started catalogue export...",0.1);

        RenderManager renderManager = new RenderManager(catalogue); // assembles the catalogue
        TemplatingConfigurationManager configManager = new TemplatingConfigurationManager();
        configManager.loadConfig();
        Templates templates = configManager.getTemplates();
        String extension = configManager.getTemplatesExtension();

        updateAll("Successfully loaded templating config...", 0.2);

        renderManager.parseRequirementTemplate(templates.getRequirementTemplate());
        renderManager.parseMilestoneTemplate(templates.getMilestoneTemplate());
        renderManager.parseCatalogueTemplate(templates.getCatalogueTemplate());

        updateAll("Successfully parsed templates... ", 0.4);

        String export = renderManager.renderCatalogue();

        updateAll("Successfully rendered catalogue...", 0.6);

        // Appends the configured extension if none is present
        String exportFile = file.getPath();
        if (!exportFile.substring(exportFile.lastIndexOf(System.getProperty("file.separator"))).contains(".")) {
            exportFile += "." + extension;
        }
        File eFile = new File(exportFile);
        PrintWriter pw = new PrintWriter(eFile);
        pw.write(export);
        pw.close();
        pw.flush();

        updateAll("Successfully wrote export to disk", 0.9);

        LOGGER.info("==============================");
        LOGGER.info(" D O N E   Catalogue Export @ " + StringUtils.prettyPrintTimestamp(System.currentTimeMillis()));
        LOGGER.info(" " + eFile.getPath() );
        LOGGER.info("==============================");
        updateAll("Finished exporting catalogue ("+ eFile.getPath()+")", 1.0);
        return null;
    }
}
