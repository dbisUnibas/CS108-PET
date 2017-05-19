package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.configuration.Templates;
import ch.unibas.dmi.dbis.reqman.configuration.TemplatingConfigurationManager;
import ch.unibas.dmi.dbis.reqman.templating.RenderManager;

import java.io.File;
import java.io.PrintWriter;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
class ExportOverviewTask extends ManagementTask<Boolean> {

    private final OverviewSnapshot snapshot;
    private final File file;

    ExportOverviewTask(OverviewSnapshot snapshot, File file) {
        this.snapshot = snapshot;
        this.file = file;
    }

    @Override
    protected Boolean call() throws Exception {
        updateAll("Started overview export...", 0.1);
        RenderManager manager = new RenderManager(snapshot);

        TemplatingConfigurationManager configManager = new TemplatingConfigurationManager();
        configManager.loadConfig();
        String extension = configManager.getTemplatesExtension();
        Templates templates = configManager.getTemplates();

        updateAll("Loaded templating config...", 0.2);

        manager.parseOverviewTemplate(templates.getOverviewTemplate());

        updateAll("Parsed templates...", 0.3);

        String export = manager.renderOverview(snapshot);

        updateAll("Rendered overview...", 0.8);

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

        updateAll("Wrote export to disk (" + eFile.getPath() + ")", 1.0);

        return true;
    }
}
