package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Requirement;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class OrFilter implements Filter {
  
  private final Filter one;
  private final Filter two;
  
  public OrFilter(@NotNull Filter one, @NotNull Filter two) {
    this.one = one;
    this.two = two;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("OrFilter{");
    sb.append("one=").append(one);
    sb.append(", two=").append(two);
    sb.append('}');
    return sb.toString();
  }
  
  @Override
  public boolean test(Requirement requirement) {
    return one.or(two).test(requirement);
  }
  
  @Override
  public String getDisplayRepresentation() {
    return one.getDisplayRepresentation() + " or " + two.getDisplayRepresentation();
  }
}
