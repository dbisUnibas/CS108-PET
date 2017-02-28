package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupRenderTester {

    public static void main(String[] args) throws IOException {

        String groupTempate = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<link href=\"http://fonts.googleapis.com/icon?family=Material+Icons\" rel=\"stylesheet\">\n" +
                "\t<link type=\"text/css\" rel=\"stylesheet\" href=\"css/materialize.min.css\"  media=\"screen,projection\"/>\n" +
                "\t<link type=\"text/css\" rel=\"stylesheet\" href=\"css/achievements.css\"/>\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "\t<title>${group.name}</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<br><br><br>\n" +
                "\n" +
                "<h1>${group.name}</h1>\n" +
                "<h2>${group.project}</h2>\n" +
                "<div class=\"container\">\n" +
                "\t\n" +
                "\t${group.milestones}\n" +
                "\n" +
                "\n" +
                "  <!--Import jQuery before materialize.js-->\n" +
                "  <script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-2.1.1.min.js\"></script>\n" +
                "  <script type=\"text/javascript\" src=\"js/materialize.min.js\"></script>\n" +
                "</body>\n" +
                "</html>";

        String groupMilestoneTemplate = "<div class=\"milestone-content-container\">\n" +
                "\t<div class=\"milestone-header\">\n" +
                "\t\t<span class=\"milestone-title\">${groupMilestone.name}</span>\n" +
                "\t</div>\n" +
                "\t<div class=\"milestone-content\">\n" +
                "\t\t${groupMilestone.progressList}\n" +
                "\t\t<span class=\"milestone-points\">${groupMilestone.sum}</span>\n" +
                "\t</div>\n" +
                "</div>";
        String progressTemplate = "<div class=\"achievement ${requirement.meta[category]} ${requirement.mandatory[][bonus]} ${progress.hasPoints[achieved][]} z-depth-2 hoverable\">\n" +
                "\t<div class=\"achievement-img-container\">\n" +
                "\t\t<img src=\"${requirement.meta[image]}\">\n" +
                "\t</div>\n" +
                "\t<div class=\"achievement-content-container\">\n" +
                "\t\t<div class=\"achievement-header\">\n" +
                "\t\t\t<span class=\"achievement-title\">${requirement.name}</span>\n" +
                "\t\t\t<span class=\"achievement-points\">${requirement.malus[-][]}${progress.points} of ${requirement.malus[-][]}${requirement.maxPoints}</span>\n" +
                "\t\t\t<span class=\"achievement-date\">${requirement.minMS.name}</span>\n" +
                "\t\t</div>\n" +
                "\t\t<span class=\"achievement-description\">${requirement.description}</span>\n" +
                "\t</div>\n" +
                "</div>";

        File catFile = new File("C:\\Users\\Loris\\uni\\08_fs17\\learningcontract\\reqman\\process\\catalogues\\testcatalogue.json");
        File groupFile = new File("C:\\Users\\Loris\\uni\\08_fs17\\learningcontract\\reqman\\process\\catalogues\\testgroup.json");
        File exportFile = new File("C:\\Users\\Loris\\uni\\08_fs17\\learningcontract\\reqman\\process\\catalogues\\testgroupexport.html");

        Catalogue cat = JSONUtils.readCatalogueJSONFile(catFile);
        Group group = JSONUtils.readGroupJSONFile(groupFile);

        RenderManager manager = new RenderManager(cat, group);
        manager.parseGroupMilestoneTemplate(groupMilestoneTemplate);
        manager.parseGroupTemplate(groupTempate);
        manager.parseProgressTemplate(progressTemplate);

        String html = manager.renderGroup(group);
        PrintWriter pw = new PrintWriter(exportFile);

        pw.print(html);
        pw.flush();
        pw.close();

    }
}
