package ch.unibas.dmi.dbis.cs108pet.analysis;

import ch.unibas.dmi.dbis.cs108pet.data.Requirement;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Filter} for names.
 * <p>
 * To pass this filter, the requirement must contain the given name (case insensitive).
 *
 * @author loris.sauter
 */
public class NameContainsFilter implements Filter {
  
  private final String name;
  
  public NameContainsFilter(@NotNull String name) {
    if (name == null) {
      throw new IllegalArgumentException("Cannot create a NameContainsFilter for name NULL");
    }
    this.name = name.toLowerCase();
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("NameContainsFilter{");
    sb.append("name='").append(name).append('\'');
    sb.append('}');
    return sb.toString();
  }
  
  @Override
  public boolean test(Requirement requirement) {
    try {
      return requirement.getName().toLowerCase().contains(name);
    } catch (NullPointerException ex) {
      return false;
    }
  }
  
  @Override
  public String getDisplayRepresentation() {
    return String.format("Name contains '%s'", name);
  }
}
