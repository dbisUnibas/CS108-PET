package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.configuration.ConfigUtils;
import ch.unibas.dmi.dbis.reqman.configuration.Templates;
import ch.unibas.dmi.dbis.reqman.configuration.TemplatingConfigurationManager;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.templating.RenderManager;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class ExportMultipleGroupTask extends ManagementTask<Boolean> {

    private final File exportDir;
    private final List<Group> groups;
    private final Catalogue catalogue;

    public ExportMultipleGroupTask(File exportDir, List<Group> groups, Catalogue catalogue) {
        this.exportDir = exportDir;
        this.groups = groups;
        this.catalogue = catalogue;
    }

    @Override
    protected Boolean call() throws Exception {
        updateAll("Started groups export...", 0.01);
        RenderManager manager = new RenderManager(catalogue);
        TemplatingConfigurationManager configManager = new TemplatingConfigurationManager();
        configManager.loadConfig();
        String extension = configManager.getTemplatesExtension();
        Templates templates = configManager.getTemplates();

        updateAll("Loaded templating config...", 0.05);

        manager.parseProgressTemplate(templates.getProgressTemplate());
        manager.parseGroupMilestoneTemplate(templates.getGroupMilestoneTemplate());
        manager.parseGroupTemplate(templates.getGroupTemplate());

        updateAll("Parsed templates...", 0.1);

        for (int i = 1; i <= groups.size(); i++) {
            Group g = groups.get(i - 1);
            manager.setGroup(g);
            String export = manager.renderGroup(g);

            updateAll("Rendered group...", calcAfterRender(i));

            String exportName = g.getExportFileName() == null ? g.getName() : g.getExportFileName();
            String exportFile = exportDir.getPath() + ConfigUtils.getFileSeparator() + exportName;
            // If the file has no extension // TODO: REMOVE extension in exportfilename of group
            if (!exportFile.substring(exportFile.lastIndexOf(ConfigUtils.getFileSeparator())).contains(".")) {
                exportFile += "." + extension;
            }
            File eFile = new File(exportFile);
            PrintWriter pw = new PrintWriter(eFile);
            pw.write(export);
            pw.flush();
            pw.close();

            updateAll("Wrote export to disk (" + eFile.getPath() + ")", calcAfterWrite(i));

            LOGGER.info("============================");
            LOGGER.info(" FINISHED : " + g.getName() + " @ " + ch.unibas.dmi.dbis.reqman.common.StringUtils.prettyPrintTimestamp(System.currentTimeMillis()));
            LOGGER.info(" " + eFile.getPath());
            LOGGER.info("============================");
        }

        return true;
    }

    /**
     * Stage must be so first stage == 1
     *
     * @param stage
     * @return
     */
    private double calcAfterRender(int stage) {
        return 0.1 + stage * (0.2 / (double) groups.size()) + (stage - 1) * (0.7 / (double) groups.size());
    }

    private double calcAfterWrite(int stage) {
        return 0.1 + stage * (0.2 / (double) groups.size()) + (stage) * (0.7 / (double) groups.size());
    }
}
