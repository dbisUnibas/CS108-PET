package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Milestone;

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
  
  public void setActiveFilter(Filter activeFilter) {
    this.activeFilter = activeFilter;
    listeners.forEach(f -> f.applyFilter(activeFilter));
  }
  
}
