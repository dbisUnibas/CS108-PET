package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Requirement;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: write JavaDoc
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
