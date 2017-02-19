package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;

import java.util.Date;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class RendererTester {

    public static void main(String[] args){
        Milestone ms = new Milestone("Milestone", 0, new Date() );
        Requirement r = new Requirement("first","desc",ms.getOrdinal(),ms.getOrdinal(),3,true,true,false);

        Requirement r2 = new Requirement("optional", "This is a) not binary and second not mandatory.", ms.getOrdinal(), ms.getOrdinal(), 10, false, false, false);

        r.addProperty("img", "path/to/some/image");

        Catalogue cat = new Catalogue("Lecture","name","desc","fs");
        cat.addAllMilestones(ms);
        cat.addRequirement(r);

        String reqTemplate = "Name: ${requirement.name}\n" +
                "Desc: ${requirement.description}\n" +
                "Milestone MIN: ${requirement.minMS.name}\n" +
                "Another: ${requirement.malus[-][+]}${requirement.maxPoints}\n" +
                "This requirement is ${requirement.mandatory[mandatory][optional]}.\n" +
                "Path: ${requirement.meta[img]}";

        String msTemplate = "Name: ${milestone.name} (${milestone.ordinal}) @ ${milestone.date}";
        String msTem1 = "${milestone.ordinal}";
        String msTem2 = "${milestone.ordinal} @ OTHER";

        TemplateParser parser = new TemplateParser(cat);
        parser.setupFor(parser.MILESTONE_ENTITY);
        Template<Milestone> templateMS = parser.parseTemplate(msTemplate);
        TemplateRenderer renderer = new TemplateRenderer();
        System.out.println("Rendered Milestone: \n"+renderer.render(templateMS, ms));
        parser.setupFor(parser.REQUIREMENT_ENTITY);
        Template<Requirement> templateR = parser.parseTemplate(reqTemplate);

        String renderedR1 = renderer.render(templateR, r);
        String renderedR2 = renderer.render(templateR, r2);

        System.out.println("===");
        System.out.println("Rendered Requirement: \n"+renderedR1);

        System.out.println("===");
        System.out.println(renderedR2);
    }
}
