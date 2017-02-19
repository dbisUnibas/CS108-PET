package ch.unibas.dmi.dbis.reqman.templating;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public interface Renderer<E> {

    /**
     * Renders the given instance.
     * @param instance
     * @return
     */
    String render(E instance);
}
