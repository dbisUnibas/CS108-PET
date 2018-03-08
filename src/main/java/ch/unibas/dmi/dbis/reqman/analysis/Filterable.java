package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.ProgressSummary;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public interface Filterable {
  
  void applyFilter(Filter filter);
  
  void applyProgressSummary(ProgressSummary ps);
  
  void clearFilter();
}
