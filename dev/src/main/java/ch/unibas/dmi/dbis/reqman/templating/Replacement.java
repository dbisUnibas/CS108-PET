package ch.unibas.dmi.dbis.reqman.templating;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class Replacement<E> implements Renderer<E> {

    private Field<E, ?> field;
    private int start;
    private int end;
    private String targetRegex;


    @Override
    public String render(E instance) {
        return field.render(instance);
    }
}
