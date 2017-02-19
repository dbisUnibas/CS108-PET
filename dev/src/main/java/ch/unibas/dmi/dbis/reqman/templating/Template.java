package ch.unibas.dmi.dbis.reqman.templating;

import java.util.List;

/**
 * Represents a template file as a java object.
 * It parses the fields used to easily oldRender the provided entity then.
 *
 * @param <E> The entity this template is for
 */
public class Template<E> {

    private String template;

    private List<Replacement<E>> replacements;

    private Entity entity;

    public Template(String template, List<Replacement<E>> replacements, Entity entity) {
        this.template = template;
        this.replacements = replacements;
        this.entity = entity;
    }



    public String getTemplate() {
        return template;
    }

    public List<Replacement<E>> getReplacements() {
        return replacements;
    }

    public Entity getEntity() {
        return entity;
    }
}
