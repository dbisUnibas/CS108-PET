package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Requirement;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class NotFilter implements Filter {
  
  private final Filter filter;
  
  public NotFilter(@NotNull Filter filter) {
    this.filter = filter;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("NotFilter{");
    sb.append("filter=").append(filter);
    sb.append('}');
    return sb.toString();
  }
  
  @Override
  public boolean test(Requirement requirement) {
    return filter.negate().test(requirement);
  }
  
  @Override
  public String getDisplayRepresentation() {
    return "Must not " + filter.getDisplayRepresentation();
  }
}
