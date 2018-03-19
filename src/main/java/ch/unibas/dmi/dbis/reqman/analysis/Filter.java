package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Requirement;

import java.util.function.Predicate;

/**
 * This is just a shortcut interface to increase readability.
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface Filter extends Predicate<Requirement> {
}
