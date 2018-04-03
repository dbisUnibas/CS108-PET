package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Requirement;

/**
 * To pass this filter, the requirement's type must match the required one.
 *
 * @author loris.sauter
 */
public class TypeFilter implements Filter {
  
  private final Requirement.Type type;
  
  public TypeFilter(Requirement.Type type){
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
}
