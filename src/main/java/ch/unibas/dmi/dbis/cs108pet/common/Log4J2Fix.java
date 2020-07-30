package ch.unibas.dmi.dbis.cs108pet.common;

/**
 * This class has a single use: Fixing https://issues.apache.org/jira/browse/LOG4J2-1799
 * <p>
 * No instance of this class needed. The call must be the very first within the main method.
 *
 * @author loris.sauter
 */
public final class Log4J2Fix {
  
  /**
   * Private constructor
   */
  private Log4J2Fix() {
    // no instances needed.
  }
  
  /**
   * Fixes the log4j issue LOG4J2-1799
   * by removing the system properites 'sun.stdout.encoding' and 'sun.stderr.encoding'
   */
  public static final void applyHotFix() {
        /*
        Workaround for:
        https://issues.apache.org/jira/browse/LOG4J2-1799
         */
    System.getProperties().remove("sun.stdout.encoding");
    System.getProperties().remove("sun.stderr.encoding");
  }
}
