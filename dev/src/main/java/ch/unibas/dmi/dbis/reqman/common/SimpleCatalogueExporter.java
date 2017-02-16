package ch.unibas.dmi.dbis.reqman.common;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import j2html.tags.Tag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    private Comparator<Requirement> reqMinMSComparator = (r1, r2) -> {
        if(!checkNoArgNull(r1, r2) ){
            throw new NullPointerException("[Requirement MinMS Comparator] Cannot compare requirements if one is null");
        }
        return Integer.compare(r1.getMinMilestoneOrdinal(), r2.getMinMilestoneOrdinal());
    };

    private Comparator<Requirement> reqMaxMSComparator = (r1, r2) -> {
        if(!checkNoArgNull(r1, r2) ){
            throw new NullPointerException("[Requirement MaxMS Comparator] Cannot compare requirements if one is null");
        }
        return Integer.compare(r1.getMaxMilestoneOrdinal(), r2.getMaxMilestoneOrdinal());
    };

    private Comparator<Requirement> reqBonusComparator = (r1, r2) -> {
        if(!checkNoArgNull(r1, r2) ){
            throw new NullPointerException("[Requirement Bonus Comparator] Cannot compare requirements if one is null");
        }
        return Boolean.compare(!r1.isMandatory(), !r2.isMandatory() );// !mandatory == bonus
    };

    private Comparator<Requirement> reqNameComparator = (r1, r2) -> {
        if(!checkNoArgNull(r1, r2) ){
            throw new NullPointerException("[Requirement Name Comparator] Cannot compare requirements if one is null");
        }
        return r1.getName().compareTo(r2.getName());
    };


    private boolean checkNoArgNull(Object o1, Object o2){
        return o1 != null && o2 != null;
    }

    private Tag[] parseRequirement(){
        ArrayList<Tag> tags = new ArrayList<>();

        List<Requirement> reqs = new ArrayList<>(catalogue.getRequirements());

        // GROUP BY milestone -> GROUP BY bonus -> GROUP BY name
        /*
         * Firstly, create a comparator for requirements which compares the ints return by the provided method.
         * Then compare according a custom comparator, since the isMandatory returns the INVERSE of isBonus
         * Then compare lexicographically based on the string.
         */
        reqs.sort(Comparator.comparingInt(Requirement::getMinMilestoneOrdinal)
                .thenComparing(Requirement::isMandatory,(b1, b2) -> Boolean.compare(!b1, !b2))
                .thenComparing(Requirement::getName));

        reqs.forEach(req -> {
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

    public Tag achievementConatiner(Requirement requirement){
        StringBuilder containerClass = new StringBuilder("achievement z-depth-2 hoverable");
        if(!requirement.isMandatory() ){
            containerClass.append(" bonus");
        }
        StringBuilder points = new StringBuilder(String.valueOf(requirement.getMaxPoints() ));
        if(requirement.isMalus() ){
            points.insert(0, "-");
        }
        Milestone miS = catalogue.getMilestoneByOrdinal(requirement.getMinMilestoneOrdinal());
        String ms = miS != null ? miS.getName() + " ("+miS.getOrdinal()+")" : "N/A";
        return div().withClass(containerClass.toString()).with(
                div().withClass("achievement-img-container").with(img().withSrc("img/placeholder.png")),
                div().withClass("achievement-content-container").with(
                        div().withClass("achievement-header").with(
                                span(requirement.getName()).withClass("achievement-title"),
                                span(points.toString()).withClass("achievement-points"),
                                span(ms).withClass("achievement-date")
                        ),
                        span(requirement.getDescription() ).withClass("achievement-description")
                )
        );
    }

}
