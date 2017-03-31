package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;

import java.util.Date;
import java.util.Map;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class ParserSandbox {

    public static void main(String[] args) {
        Milestone ms = new Milestone("Milestone", 0, new Date());
        Requirement r = new Requirement("first", "desc", ms.getOrdinal(), ms.getOrdinal(), 3, true, true, false);
        Catalogue cat = new Catalogue("Lecture", "name", "desc", "fs");
        cat.addAllMilestones(ms);
        cat.addRequirement(r);

        String reqTemplate = "Name: ${requirement.name}\n" +
                "Desc: ${requirement.description}\n" +
                "Milestone MIN: ${requirement.minMS.name}\n" +
                "Another: ${requirement.maxPoints}";

        String msTemplate = "Name: ${milestone.name} (${milestone.ordinal}) @ ${milestone.date}";
        String msTem1 = "${milestone.ordinal}";
        String msTem2 = "${milestone.ordinal} @ OTHER";

        TemplateParser parser = new TemplateParser();
        RenderManager manager = new RenderManager(cat);
        parser.setupFor(manager.MILESTONE_ENTITY);
        Map<String, Field<Milestone, ?>> map = parser.oldParse(msTemplate); // Would be in template


        TemplateRenderer renderer = new TemplateRenderer(parser);

        System.out.println(renderer.oldRender(msTemplate, ms, map));

        parser.setupFor(manager.REQUIREMENT_ENTITY);
        Map<String, Field<Requirement, ?>> reqMap = parser.oldParse(reqTemplate);

        /*
        reqMap.forEach((key, field) -> {
            System.out.println("Key: "+key+", field: "+field);
        });
        */

        System.out.println(renderer.oldRender(reqTemplate, r, reqMap));
    }
}
