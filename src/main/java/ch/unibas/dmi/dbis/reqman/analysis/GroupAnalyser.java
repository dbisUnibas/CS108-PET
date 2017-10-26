package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.*;

import java.util.List;
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
  
  
  
  public Requirement getRequirementOf(Progress progress) {
    for (Requirement r : catalogue.getRequirements()) {
      if (r.getUuid().equals(progress.getUuid())) {
        return r;
      }
    }
    return null;
  }
  
  boolean matchesProgressSummary(Progress p, ProgressSummary ps) {
    return p.getProgressSummaryUUID().equals(ps.getUuid());
  }
}
