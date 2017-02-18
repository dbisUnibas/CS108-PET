package ch.unibas.dmi.dbis.reqman.templating;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a template file as a java object.
 * It parses the fields used to easily render the provided entity then.
 *
 * @param <E> The entity this template is for
 */
public class Template<E> {

    private String template;

    private Map<String, Field<E,?>> fields = new TreeMap<>();



}
