package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Requirement;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class NameContainsFilter implements Filter {
  
  private String name;
  
  public NameContainsFilter(String name){
    this.name = name;
  }
  
  @Override
  public boolean test(Requirement requirement) {
    return requirement.getName().toLowerCase().contains(name.toLowerCase());
  }
}
