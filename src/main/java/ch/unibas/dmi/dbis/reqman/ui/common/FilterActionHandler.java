package ch.unibas.dmi.dbis.reqman.ui.common;

import ch.unibas.dmi.dbis.reqman.data.Requirement;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public interface FilterActionHandler {
  
  /**
   * Returns how many items are found
   * @param pattern
   * @return
   */
  int applyFilter(String pattern, FilterBar.Mode mode);
  
  int applyFilter(Requirement.Type type);
  
  void resetFilter();
}
