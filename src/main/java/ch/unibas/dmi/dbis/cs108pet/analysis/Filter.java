package ch.unibas.dmi.dbis.cs108pet.analysis;

import ch.unibas.dmi.dbis.cs108pet.data.Requirement;

import java.util.function.Predicate;

/**
 * This is just a shortcut interface to increase readability.
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface Filter extends Predicate<Requirement> {
  
  default String getDisplayRepresentation() {
    return getClass().getSimpleName();
  }
}
