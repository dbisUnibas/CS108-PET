package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupAnalyser {
  
  private final Group group;
  private final Course course;
  private final Catalogue catalogue;
  private final CatalogueAnalyser catalogueAnalyser;
  private final CourseManager manager;
  
  public GroupAnalyser(Course course, Catalogue catalogue, Group group) {
    this.group = group;
    this.course = course;
    this.catalogue = catalogue;
    this.catalogueAnalyser = new CatalogueAnalyser(course, catalogue);
    this.manager = new CourseManager(course, catalogue);
  }
  
  public List<Progress> getProgressFor(ProgressSummary summary) {
    return group.getProgressList().stream().filter(p -> matchesProgressSummary(p, summary)).collect(Collectors.toList());
  }
  
  public double getActualPoints(Progress progress) {
    Requirement r = getRequirementOf(progress);
    if(r == null){
      throw new IllegalArgumentException("No such requirement "+progress.getRequirementUUID());
    }else{
      switch(r.getType()){
        case REGULAR:
        case BONUS:
          return progress.getFraction() * r.getMaxPoints();
        case MALUS:
          return -1 * progress.getFraction() * r.getMaxPoints();
      }
    }
    return Double.NaN; // unreachable?
  }
  
  public Requirement getRequirementOf(@NotNull  Progress progress) {
    for (Requirement r : catalogue.getRequirements()) {
      if (r.getUuid().equals(progress.getUuid())) {
        return r;
      }
    }
    return null;
  }
  
  public Progress getProgressFor(@NotNull Requirement requirement){
    for(Progress p: group.getProgressList() ){
      if(requirement.getUuid().equals(p.getRequirementUUID())){
        return p;
      }
    }
    return null;
  }
  
  public Progress getProgressById(@NotNull  UUID id){
    if(id == null){
      throw new IllegalArgumentException("Cannot find progress by id, if the id is null");
    }
    for(Progress p : group.getProgressList()){
      if(id.equals(p.getUuid())){
        return p;
      }
    }
    return null;
  }
  
  public ProgressSummary getProgressSummaryById(@NotNull  UUID id){
    if(id == null){
      throw new IllegalArgumentException("Cannot find progress summary by id, if the id is null");
    }
    for(ProgressSummary ps : group.getProgressSummaries()){
      if(id.equals(ps.getUuid())){
        return ps;
      }
    }
    return null;
  }
  
  public ProgressSummary getProgressSummaryFor(@NotNull  Milestone ms){
    if(ms == null){
      throw new IllegalArgumentException("Cannot find a progress summary for a milestone, if the milestone is null");
    }
    for(ProgressSummary ps : group.getProgressSummaries()){
      if(ms.getUuid().equals(ps.getMilestoneUUID())){
        return ps;
      }
    }
    return null;
  }
  
  
  public boolean isProgressUnlocked(Progress progress){
    int predecessorsFulfilled = 0;
    for(Requirement r : catalogueAnalyser.getPredecessors(getRequirementOf(progress))){
      Progress p = getProgressFor(r);
      if(p.getFraction() > 0){
        predecessorsFulfilled++;
      }
    }
    return predecessorsFulfilled == getRequirementOf(progress).getPredecessors().length;
  }
  
  public double getSum(){
    return group.getProgressList().stream().mapToDouble(this::getActualPoints).sum();
  }
  
  public double getSumFor(ProgressSummary ps){
    return getProgressFor(ps).stream().mapToDouble(this::getActualPoints).sum();
  }
  
  boolean matchesProgressSummary(Progress p, ProgressSummary ps) {
    return p.getProgressSummaryUUID().equals(ps.getUuid());
  }
}
