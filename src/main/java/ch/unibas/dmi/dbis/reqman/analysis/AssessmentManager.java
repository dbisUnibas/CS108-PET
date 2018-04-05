package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Milestone;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class AssessmentManager {
  
  private static AssessmentManager instance = null;
  
  public static AssessmentManager getInstance() {
    if(instance == null){
      instance = new AssessmentManager();
    }
    return instance;
  }
  
  private AssessmentManager() {
  
  }
  
  private ArrayList<Filterable> listeners = new ArrayList<>();
  private Milestone activeMilestone = null;
  private Filter activeFilter = null;
  
  public void addFilterable(Filterable filterable){
    listeners.add(filterable);
  }
  
  public void removeFilterable(Filterable filterable){
    listeners.remove(filterable);
  }
  
  public boolean hasActiveProgressSummary(){
    return activeMilestone != null;
  }
  
  public Milestone getActiveMilestone() {
    return activeMilestone;
  }
  
  public void setActiveMilestone(Milestone active) {
    this.activeMilestone = active;
    listeners.forEach(f -> f.applyActiveMilestone(active));
  }
  
  public void clearFilter(){
    activeFilter = null;
    listeners.forEach(Filterable::clearFilter);
  }
  
  public boolean hasActiveFilter(){
    return activeFilter != null;
  }
  
  public Filter getActiveFilter() {
    return activeFilter;
  }
  
  private void setActiveFilter(@NotNull Filter activeFilter) {
    this.activeFilter = activeFilter;
    listeners.forEach(f -> f.applyFilter(activeFilter));
  }
  
  /**
   * Sets (and thus overrides) any existing filter.
   * @param filter
   */
  public void setFilter(@NotNull Filter filter){
    clearFilter();
    setActiveFilter(filter);
  }
  
  /**
   * Creates a concatenated filter with the concatenation AND
   * between the active filter and the new one
   * @param filter
   */
  public void addFilterAnd(@NotNull Filter filter){
    if(hasActiveFilter() ){
      setActiveFilter(new AndFilter(getActiveFilter(), filter));
    }else{
      setActiveFilter(filter);
    }
  }
  
  /**
   * Creates a concatenated filter with the concatenation OR
   *    * between the active filter and the new one
   * @param filter
   */
  public void addFilterOr(@NotNull Filter filter){
    if(hasActiveFilter() ){
      setActiveFilter(new OrFilter(getActiveFilter(), filter));
    }else{
      setActiveFilter(filter);
    }
  }
}
