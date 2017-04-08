package ch.unibas.dmi.dbis.reqman.common;

import java.util.function.Function;

/**
 * The {@link Callback} is literally a callback.
 *
 * This is entirely syntactic sugar and hides a {@link Function} whose
 * type parameters are both {@link Void}.
 *
 * <h2>Usage - Caller</h2>
 * To use this callback in a method which needs a call back.
 *
 * Call
 * <code>callback.apply(null)</code>
 * and discard the result.
 *
 * <h2>Usage - lambda</h2>
 * Since the method {@link Callback#call()} is what the {@link FunctionalInterface} refers to,
 * one must simply implement this / provide a lambda for it.
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface Callback extends Function<Void, Void> {

    void call();


    @Override
    default Void apply(Void aVoid) {
        call();
        return null;
    }
}
