package ch.unibas.dmi.dbis.reqman.common;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class LoggingUtils {


    public static final Marker REQMAN_MARKER = MarkerManager.getMarker("REQMAN");
    public static final Marker EVALUATOR_MARKER = MarkerManager.getMarker("EVALUATOR").setParents(REQMAN_MARKER);
    public static final Marker ASSESSMENT_MARKER = MarkerManager.getMarker("ASSESSMENT").setParents(EVALUATOR_MARKER);


    public static final Marker PREDECESSOR_CHECK_MARKER = MarkerManager.getMarker("PREDECESSOR_CHECK").setParents(ASSESSMENT_MARKER);
    public static final Marker LOAD_PROGRESS_MAP = MarkerManager.getMarker("LOAD_PROGRESS_MAP").setParents(ASSESSMENT_MARKER);
}
