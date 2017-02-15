package ch.unibas.dmi.dbis.reqman.ui.common;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import j2html.tags.Tag;

import java.util.ArrayList;

import static j2html.TagCreator.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class SimpleCatalogueExporter {

    private Catalogue catalogue;

    @Deprecated
    public SimpleCatalogueExporter(){

    }

    public static void main(String[] args){
        Requirement r = new Requirement("Name", "Desc", "Milestone:1", "Milestone:2", 3, true, false, false);
        System.out.println(achievementConatiner(r).render());
    }

    public SimpleCatalogueExporter(Catalogue cat){
        this.catalogue = cat;
    }

    public String exportHTML(){
        Tag[] achievements = parseRequirement();
        Tag page = html().with(
                head().with(
                        title("TEMPLATE TEST"),
                        link().withRel("stylesheet").withHref("http://fonts.googleapis.com/icon?family=Material+Icons"),
                        link().withType("text/css").withRel("stylesheet").withHref("css/materialize.min.css"),//MEDIA is missing!
                        link().withType("text/css").withRel("stylesheet").withHref("css/achievements.css"),
                        meta().withName("viewport").withContent("width=device-width, initial-scale=1.0")
                ),
                body().with(
                        br(),
                        br(),
                        br(),
                        div().withClass("container").with(
                            achievements
                        )
                )
        );

        return prependDoctype(page.render() );
    }

    private Tag[] parseRequirement(){
        ArrayList<Tag> tags = new ArrayList<>();

        catalogue.getRequirements().forEach(req -> {
            tags.add(achievementConatiner(req));
        });

        return tags.toArray(new Tag[0]);
    }

    public static Tag container(){
        return div().withClass("container");
    }

    public static String prependDoctype(String html){
        return "<!DOCTYPE html>"+html;
    }

    public static Tag theHead(String title){
        return head().with(
                title("TEMPLATE TEST"),
                link().withRel("stylesheet").withHref("http://fonts.googleapis.com/icon?family=Material+Icons"),
                link().withType("text/css").withRel("stylesheet").withHref("css/materialize.min.css"),//MEDIA is missing!
                link().withType("text/css").withRel("stylesheet").withHref("css/achievements.css"),
                meta().withName("viewport").withContent("width=device-width, initial-scale=1.0")
        );
    }

    public static Tag achievementConatiner(Requirement requirement){
        StringBuilder containerClass = new StringBuilder("achievement z-depth-2 hoverable");
        if(!requirement.isMandatory() ){
            containerClass.append(" bonus");
        }
        return div().withClass(containerClass.toString()).with(
                div().withClass("achievement-img-container").with(img().withSrc("img/placeholder.png")),
                div().withClass("achievement-content-container").with(
                        div().withClass("achievement-header").with(
                                span(requirement.getName()).withClass("achievement-title"),
                                span(String.valueOf(requirement.getMaxPoints())).withClass("achievement-points"),
                                span(toHumanReadable(requirement.getMinMilestone())).withClass("achievement-date")
                        ),
                        span(requirement.getDescription() ).withClass("achievement-description")
                )
        );
    }

    public static String toHumanReadable(String milestoneFromRequirement){
        StringBuilder sb = new StringBuilder();
        int delimIndex = milestoneFromRequirement.indexOf(":");
        sb.append(milestoneFromRequirement.substring(0, delimIndex) );
        sb.append(" ");
        sb.append(milestoneFromRequirement.substring(delimIndex+1));
        return sb.toString();
    }



}
