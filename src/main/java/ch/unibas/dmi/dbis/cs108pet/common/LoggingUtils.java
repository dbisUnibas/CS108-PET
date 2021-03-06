package ch.unibas.dmi.dbis.cs108pet.common;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * A collection of logging markers
 * <p>
 * This class cannot be instantiated and its single purpose is to have all markers in a single spot
 *
 * @author loris.sauter
 */
public class LoggingUtils {
  
  /**
   * The root marker for application
   */
  public static final Marker ROOT_APP_MARKER = MarkerManager.getMarker("cs108");
  /**
   * The parent marker for all 'evaluator mode' related things.
   * Is a child of {@link LoggingUtils#ROOT_APP_MARKER}
   */
  public static final Marker EVALUATOR_MARKER = MarkerManager.getMarker("EVALUATOR").setParents(ROOT_APP_MARKER);
  /**
   * The parent marker for all 'assessment' related actions
   * Is a child of {@link LoggingUtils#EVALUATOR_MARKER}
   */
  public static final Marker ASSESSMENT_MARKER = MarkerManager.getMarker("ASSESSMENT").setParents(EVALUATOR_MARKER);
  /**
   * The marker for 'predecessor-check' actions
   * Is a child of {@link LoggingUtils#ASSESSMENT_MARKER}
   */
  public static final Marker PREDECESSOR_CHECK_MARKER = MarkerManager.getMarker("PREDECESSOR_CHECK").setParents(ASSESSMENT_MARKER);
  /**
   * The marker for the 'load progress map' action
   * Is a child of {@link LoggingUtils#ASSESSMENT_MARKER}
   */
  public static final Marker LOAD_PROGRESS_MAP = MarkerManager.getMarker("LOAD_PROGRESS_MAP").setParents(ASSESSMENT_MARKER);
  /**
   * The marker for 'dirty' actions
   * Is a child of {@link LoggingUtils#ASSESSMENT_MARKER}
   */
  public static final Marker DIRTY_MARKER = MarkerManager.getMarker("DIRTY").setParents(ASSESSMENT_MARKER);
  /**
   * The marker for 'sum' calculation actions
   * Is a child of {@link LoggingUtils#ASSESSMENT_MARKER}
   */
  public static final Marker SUM_MARKER = MarkerManager.getMarker("SUM").setParents(ASSESSMENT_MARKER);
  
  private LoggingUtils() {
    // no costructor needed
  }
}
