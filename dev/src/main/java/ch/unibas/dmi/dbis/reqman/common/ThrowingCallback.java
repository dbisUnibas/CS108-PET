package ch.unibas.dmi.dbis.reqman.common;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface ThrowingCallback extends ThrowingFunction<Void, Void> {

    void callThrowing() throws Exception;

    @Override
    default Void applyThrowing(Void aVoid) {
        try {
            callThrowing();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
