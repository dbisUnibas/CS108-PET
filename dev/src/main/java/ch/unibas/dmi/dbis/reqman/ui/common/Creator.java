package ch.unibas.dmi.dbis.reqman.ui.common;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public interface Creator<T> {

    /**
     * Creates whatever this creator creates. If the creator is not ready, an {@link IllegalStateException} will be thrown.
     *
     * @return A newly created object
     * @throws IllegalStateException If the creator was not ready
     */
    T create() throws IllegalStateException;

    /**
     * Returns whether this creator is ready to use {@link Creator#create()} to create a new instance.
     *
     * @return If {@link Creator#create()} can be called safely.
     */
    boolean isCreatorReady();


}
