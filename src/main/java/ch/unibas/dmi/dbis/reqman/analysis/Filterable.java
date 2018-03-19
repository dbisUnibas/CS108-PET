package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.ProgressSummary;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public interface Filterable {
  
  void applyFilter(Filter filter);
  
  void applyActiveMilestone(Milestone ps);
  
  void clearFilter();
}
