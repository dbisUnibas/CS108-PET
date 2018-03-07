package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.ProgressSummary;

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
  
  private ProgressSummary active = null;
  private Filter activeFilter = null;
  
  public boolean hasActiveProgressSummary(){
    return active != null;
  }
  
  public ProgressSummary getActiveProgressSummary() {
    return active;
  }
  
  public void setActiveProgressSummary(ProgressSummary active) {
    this.active = active;
    // TODO fire event to things to only show this milestone related stuff
  }
  
  public void clearFilter(){
    activeFilter = null;
    // TODO Clear filter in all active assessment views
  }
  
  public boolean hasActiveFilter(){
    return activeFilter != null;
  }
  
  public Filter getActiveFilter() {
    return activeFilter;
  }
  
  public void setActiveFilter(Filter activeFilter) {
    this.activeFilter = activeFilter;
    // TODO fire event to filtered things, to show only filtered stuff
  }
  
}
