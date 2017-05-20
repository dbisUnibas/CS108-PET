package ch.unibas.dmi.dbis.reqman.common;

import java.util.function.Function;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
@Deprecated
public interface ThrowingFunction<T, R> extends Function<T, R> {

    R applyThrowing(T input) throws Exception;

    @Override
    default R apply(T input) {
        try {
            return applyThrowing(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
