package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;

import java.util.Date;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class TemplatingTester {

    public static void main(String[] args){
        Milestone ms = new Milestone("Milestone", 0, new Date() );
        Requirement r = new Requirement("first","desc",ms.getOrdinal(),ms.getOrdinal(),3,true,true,false);

        Requirement r2 = new Requirement("optional", "This is a) not binary and second not mandatory.", ms.getOrdinal(), ms.getOrdinal(), 10, false, false, false);

        r.addProperty("img", "path/to/some/image");

        Catalogue cat = new Catalogue("Lecture","name","desc","fs");
        cat.addAllMilestones(ms);
        cat.addRequirement(r);
        cat.addRequirement(r2);

        String reqTemplate = "Name: ${requirement.name}\n" +
                "Desc: ${requirement.description}\n" +
                "Milestone MIN: ${requirement.minMS.name}\n" +
                "Another: ${requirement.malus[-][+]}${requirement.maxPoints}\n" +
                "This requirement is ${requirement.mandatory[mandatory][optional]}.\n" +
                "Path: ${requirement.meta[img]}\n";

        String msTemplate = "Name: ${milestone.name} (${milestone.ordinal}) @ ${milestone.date}\n";
        String msTem1 = "${milestone.ordinal}";
        String msTem2 = "${milestone.ordinal} @ OTHER";

        String catTemplate = "Name: ${catalogue.name}\n" +
                "Description: ${catalogue.description}\n" +
                "Lecture: ${catalogue.lecture}\n" +
                "Semseter: ${catalogue.semester}\n\n" +
                "Requirements:\n${catalogue.requirements}\n\n" +
                "Milestones: \n${catalogue.milestones}";

        String catHTML = "  <!DOCTYPE html>\n" +
                "  <html>\n" +
                "\t<head>\n" +
                "\t\t<link href=\"http://fonts.googleapis.com/icon?family=Material+Icons\" rel=\"stylesheet\">\n" +
                "\t\t<link type=\"text/css\" rel=\"stylesheet\" href=\"css/materialize.min.css\"  media=\"screen,projection\"/>\n" +
                "\t\t<link type=\"text/css\" rel=\"stylesheet\" href=\"css/achievements.css\"/>\n" +
                "\t\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "\t</head>\n" +
                "    <body>\n" +
                "\t\n" +
                "\t<br><br><br>\n" +
                "\t\n" +
                "\t<div class=\"container\">\n" +
                "        \n" +
                "\t\t${catalogue.requirements}\n" +
                "\t\n" +
                "\t\n" +
                "      <!--Import jQuery before materialize.js-->\n" +
                "      <script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-2.1.1.min.js\"></script>\n" +
                "      <script type=\"text/javascript\" src=\"js/materialize.min.js\"></script>\n" +
                "    </body>\n" +
                "  </html>";

        String reqHTML = "<div class=\"achievement ${requirement.mandatory[][bonus]} z-depth-2 hoverable\">\n" +
                "\t<div class=\"achievement-img-container\">\n" +
                "\t\t<img src=\"img/placeholder.png\">\n" +
                "\t</div>\n" +
                "\t<div class=\"achievement-content-container\">\n" +
                "\t\t<div class=\"achievement-header\">\n" +
                "\t\t\t<span class=\"achievement-title\">${requirement.name}</span>\n" +
                "\t\t\t<span class=\"achievement-points\">${requirement.maxPoints}</span>\n" +
                "\t\t\t<span class=\"achievement-date\">${requirement.minMS.name}</span>\n" +
                "\t\t</div>\n" +
                "\t\t<span class=\"achievement-description\">${requirement.description}</span>\n" +
                "\t</div>\n" +
                "</div>";

        RenderManager manager = new RenderManager(cat);

        manager.parseCatalogueTemplate(catTemplate);
        manager.parseMilestoneTemplate(msTemplate);
        manager.parseRequirementTemplate(reqTemplate);

        String export = manager.renderCatalogue();

        System.out.println("====\n"+export);
    }
}
