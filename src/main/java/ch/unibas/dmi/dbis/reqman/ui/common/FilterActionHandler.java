package ch.unibas.dmi.dbis.reqman.ui.common;

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
  
  void resetFilter();
}
