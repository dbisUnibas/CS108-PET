package ch.unibas.dmi.dbis.cs108pet.analysis;

import ch.unibas.dmi.dbis.cs108pet.data.Requirement;

/**
 * To pass this filter, the requirement's type must match the required one.
 *
 * @author loris.sauter
 */
public class TypeFilter implements Filter {
  
  private final Requirement.Type type;
  
  public TypeFilter(Requirement.Type type) {
    this.type = type;
  }
  
  @Override
  public boolean test(Requirement requirement) {
    return requirement.getType().equals(type);
  }
  
  @Override
  public String getDisplayRepresentation() {
    return String.format("Type is '%s'", type);
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("TypeFilter{");
    sb.append("type=").append(type);
    sb.append('}');
    return sb.toString();
  }
}
