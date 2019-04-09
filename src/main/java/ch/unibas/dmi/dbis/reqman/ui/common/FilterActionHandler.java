package ch.unibas.dmi.dbis.reqman.ui.common;

import ch.unibas.dmi.dbis.reqman.data.Requirement;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
@Deprecated
public interface FilterActionHandler {
  
  /**
   * Returns how many items are found
   *
   * @param pattern
   * @return
   */
  @Deprecated
  int applyFilter(String pattern, FilterBar.Mode mode);
  
  @Deprecated
  int applyFilter(Requirement.Type type);
  
  @Deprecated
  void resetFilter();
}
