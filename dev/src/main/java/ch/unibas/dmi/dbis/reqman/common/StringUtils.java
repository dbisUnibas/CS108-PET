package ch.unibas.dmi.dbis.reqman.common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
     * Pretty prints a double value (with maximal necessary precision or as integer, if so).
     * @param value
     * @return
     */
    public static String prettyPrint(double value) {
        /*
        Algorithm by: http://stackoverflow.com/a/25308216
         */
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
        return df.format(value);
    }
}
