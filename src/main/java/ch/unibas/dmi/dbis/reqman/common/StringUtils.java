package ch.unibas.dmi.dbis.reqman.common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * {@link StringUtils} contains random utily functions related to strings.
 * This class is not designed to be instanciated and has no public constructor for that particular reason.
 *
 * @author loris.sauter
 */
public class StringUtils {
  
  private StringUtils() {
    // no public constructor
  }
  
  /**
   * Rounds the provided value to 2 digits.
   *
   * @param value The value to round
   * @return A string representing the new value
   */
  public static String roundTo2Digits(double value) {
    DecimalFormat df = new DecimalFormat("#.##");
    return df.format(value);
  }
  
  /**
   * Pretty prints a double value (with maximal necessary precision or as integer, if so).
   *
   * @param value
   * @return
   */
  public static String prettyPrint(double value) {
        /*
        To remove - sign if value is zero
         */
    if (Double.compare(-0d, value) == 0) {
      value *= -1d;
    }
        /*
        Solution by: http://stackoverflow.com/a/25308216
         */
    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
    return df.format(value);
  }
  
  /**
   * Concatenates the given strings with the specified delimiter in between.
   * <p>
   * This method is the reverse of {@link String#split(String)} as demonstrated by this example:
   * <p>
   * <pre><code>String all = concatWithDelimiter("#", "foo", "bar", "alice", "bob"); // all equals: "foo#bar#alice#bob"
   * String[] parts = all.split("#");// parts is similar to: new String[]{"foo", "bar", "alice"; "bob"}
   * </code></pre>
   *
   * @param delimiter The delimiter with which separates the strings.
   * @param strings   The variable list of strings to concatenate.
   * @return A single string with all the strings from the given variable list of strings, separated by the {@code delimiter}.
   */
  public static String concatWithDelimiter(String delimiter, String... strings) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < strings.length - 1; i++) {
      sb.append(strings[i]);
      sb.append(delimiter);
    }
    sb.append(strings[strings.length - 1]);
    return sb.toString();
  }
  
  /**
   * Concatenates the given strings with a period (.) as delimiter.
   *
   * @param strings The variable list of strings to concatenate
   * @return A single string with the all elements of {@code strings}, separated by {@code .}
   * @see StringUtils#concatWithDelimiter(String, String...)
   */
  public static String concatWithPeriodDelimeter(String... strings) {
    return concatWithDelimiter(".", strings);
  }
  
  /**
   * Prints a given timestamp with the {@link SimpleDateFormat} {@code dd.MM.YYYY HH:mm:ss:SSS}
   *
   * @param timestamp The timestamp to pretty print
   * @return The formatted timestamp, as a string.
   */
  public static String prettyPrintTimestamp(long timestamp) {
    return prettyPrintTimestamp(timestamp, "dd.MM.YYYY HH:mm:ss:SSS");
  }
  
  /**
   * Prints a given timestamp with the specified format.
   * The format ist then used to setup a {@link SimpleDateFormat} which formats the timestamp.
   *
   * @param timestamp The timestamp to pretty print.
   * @param format    The format expression to pass to the {@link SimpleDateFormat} constructor.
   * @return The formatted timestamp in its string representation
   */
  public static String prettyPrintTimestamp(long timestamp, String format) {
    Date d = new Date(timestamp);
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(d);
  }
  
  /**
   * Returns whether the given {@link String} is {@code null} or empty.
   *
   * @param str The string to test.
   * @return {@code true} if the string is {@code null} or the result of {@linkplain String#isEmpty()}
   * @see String#isEmpty()
   */
  public static boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty();
  }
  
  /**
   * A null-safe version of {@link String#contains(CharSequence)}.
   *
   * @param str     The string to search in
   * @param pattern The pattern to search for
   * @return {@code true} iff {@code str} is not {@code null} and {@link String#contains(CharSequence)} returns {@code
   * true}, {@code false} oderwise.
   * @see String#contains(CharSequence)
   */
  public static boolean containsNullSafe(String str, CharSequence pattern) {
    if (str == null) {
      return false;
    }
    return str.contains(pattern);
  }
}
