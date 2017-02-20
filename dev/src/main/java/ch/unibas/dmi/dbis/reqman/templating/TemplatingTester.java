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

        RenderManager manager = new RenderManager(cat);

        manager.parseCatalogueTemplate(catTemplate);
        manager.parseMilestoneTemplate(msTemplate);
        manager.parseRequirementTemplate(reqTemplate);

        String export = manager.renderCatalogue();

        System.out.println("====\n"+export);
    }
}
