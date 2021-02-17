package ch.unibas.dmi.dbis.cs108pet.analysis;

import ch.unibas.dmi.dbis.cs108pet.data.Requirement;
import org.jetbrains.annotations.NotNull;

/**
 * Combination filter using logical AND.
 *
 * @author loris.sauter
 */
public class AndFilter implements Filter {
  
  private final Filter one;
  private final Filter two;
  
  public AndFilter(@NotNull Filter one, @NotNull Filter two) {
    this.one = one;
    this.two = two;
  }
  
  
  @Override
  public boolean test(Requirement requirement) {
    return one.and(two).test(requirement);
  }
  
  @Override
  public String getDisplayRepresentation() {
    return one.getDisplayRepresentation() + " and " + two.getDisplayRepresentation();
  }
}
