package ch.unibas.dmi.dbis.reqman.ui.common;

import static j2html.TagCreator.*;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;

import java.io.File;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueExporter {

    private Catalogue cat;

    @Deprecated
    public CatalogueExporter(){

    }

    public static void main(String[] args){
        System.out.println(new CatalogueExporter().exportHTML());
    }

    public CatalogueExporter(Catalogue cat){
        this.cat = cat;
    }

    public String exportHTML(){
        return "<!DOCTYPE html>"+html().with(
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
                                div().withClass("achievement achieved z-depth-2 hoverable").with(
                                        div().withClass("achievement-img-container").with(img().withSrc("img/placeholder.png")),
                                        div().withClass("achievement-content-container").with(
                                                div().withClass("achievement-header").with(
                                                        span("First TEST").withClass("achievement-title"),
                                                        span("2").withClass("achievement-points"),
                                                        span("Milestone 1").withClass("achievement-date")
                                                ),
                                                span("SomeDescription").withClass("achievement-description")
                                        )
                                )
                        )
                )
        ).render();
    }
}
