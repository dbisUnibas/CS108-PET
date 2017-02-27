package ch.unibas.dmi.dbis.reqman.common;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class StringUtils {

    private StringUtils() {
        // no public constructor
    }

    /**
     * Checks if the provided String is {@code null} or empty.
     *
     * @param str The String to check
     * @return {@code true} if and only if the given {@code str} equals {@code null} and {@link String#isEmpty()} returns {@code true} - returns {@code false} otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
