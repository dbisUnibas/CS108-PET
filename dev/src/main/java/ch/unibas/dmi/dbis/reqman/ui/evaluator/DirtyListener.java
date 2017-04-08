package ch.unibas.dmi.dbis.reqman.ui.evaluator;

/**
 * The {@link DirtyListener} will be invoked if something changes that has to be saved.
 *
 * @author loris.sauter
 */
public interface DirtyListener {

    default void mark(boolean dirty){
        if(dirty){
            markDirty();
        }else{
            unmarkDirty();
        }
    }

    void markDirty();

    void unmarkDirty();
}
