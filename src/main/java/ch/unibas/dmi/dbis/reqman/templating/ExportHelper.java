package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.configuration.Templates;
import ch.unibas.dmi.dbis.reqman.configuration.TemplatingConfigurationManager;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Group;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ExportHelper {
  
  public static void exportCatalogue(File config, File target) throws FileNotFoundException {
    Logger logger = LogManager.getLogger(ExportHelper.class);
    
    RenderManager renderManager = new RenderManager(EntityController.getInstance().getCatalogue()); // assembles the catalogue
    TemplatingConfigurationManager configManager = new TemplatingConfigurationManager();
    configManager.loadConfig(config);
    Templates templates = configManager.getTemplates();
    String extension = configManager.getExportExtension();
  
    logger.debug("Successfully loaded templating config");
  
    renderManager.parseRequirementTemplate(templates.getRequirementTemplate());
    renderManager.parseMilestoneTemplate(templates.getMilestoneTemplate());
    renderManager.parseCatalogueTemplate(templates.getCatalogueTemplate());
  
    logger.debug("Successfully parsed templates");
  
    String export = renderManager.renderCatalogue();
  
    logger.debug("Successfully rendered catalogue");
  
    // Appends the configured extension if none is present
    String exportFile = target.getPath();
    if (!exportFile.substring(exportFile.lastIndexOf(System.getProperty("file.separator"))).contains(".")) {
      exportFile += "." + extension;
    }
    File eFile = new File(exportFile);
    PrintWriter pw = new PrintWriter(eFile);
    pw.write(export);
    pw.close();
    pw.flush();
  
    logger.debug("Wrote exportfile");
  
    logger.info("==============================");
    logger.info(" D O N E   Catalogue Export @ " + StringUtils.prettyPrintTimestamp(System.currentTimeMillis()));
    logger.info(" " + eFile.getPath());
    logger.info("==============================");
    
  }
  
  public static void exportGroup(File config, File target, Group group) throws FileNotFoundException {
    Logger logger = LogManager.getLogger(ExportHelper.class);
    
    if(group == null){
      logger.error("Cannot export null-group");
      return;
    }
  
    RenderManager renderManager = new RenderManager(EntityController.getInstance().getCatalogue()); // assembles the catalogue
    renderManager.setGroup(group);
    TemplatingConfigurationManager configManager = new TemplatingConfigurationManager();
    configManager.loadConfig(config);
    Templates templates = configManager.getTemplates();
    String extension = configManager.getExportExtension();
  
    logger.debug("Successfully loaded templating config");
  
    renderManager.parseRequirementTemplate(templates.getRequirementTemplate());
    renderManager.parseMilestoneTemplate(templates.getMilestoneTemplate());
    renderManager.parseCatalogueTemplate(templates.getCatalogueTemplate());
    
    renderManager.parseProgressTemplate(templates.getProgressTemplate());
    renderManager.parseProgressSummaryTemplate(templates.getProgressSummaryTemplate());
    renderManager.parseGroupTemplate(templates.getGroupTemplate());
  
    logger.debug("Successfully parsed templates");
  
    String export = renderManager.renderGroup(group);
  
    logger.debug("Successfully rendered group");
  
    // Appends the configured extension if none is present
    String exportFile = target.getPath();
    if (!exportFile.substring(exportFile.lastIndexOf(System.getProperty("file.separator"))).contains(".")) {
      exportFile += "." + extension;
    }
    File eFile = new File(exportFile);
    PrintWriter pw = new PrintWriter(eFile);
    pw.write(export);
    pw.close();
    pw.flush();
  
    logger.debug("Wrote exportfile");
  
    logger.info("==============================");
    logger.info(" D O N E   Group Export @ " + StringUtils.prettyPrintTimestamp(System.currentTimeMillis()));
    logger.info(" " + eFile.getPath());
    logger.info("==============================");
  }
}
