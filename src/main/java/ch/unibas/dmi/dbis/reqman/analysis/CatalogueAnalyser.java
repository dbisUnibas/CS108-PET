package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueAnalyser {
  
  private final Catalogue catalogue;
  private final Course course;
  
  private final CourseManager courseManager;
  
  public CatalogueAnalyser(Course course, Catalogue catalogue) {
    this.course = course;
    this.catalogue = catalogue;
    if (!course.getCatalogueUUID().equals(catalogue.getUuid())) {
      throw new IllegalArgumentException("Mismatching catalogue id. Expected " + course.getCatalogueUUID() + "but given " + catalogue.getUuid());
    }
    courseManager = new CourseManager(course, catalogue);
  }
  
  public boolean hasRequirements() {
    return !catalogue.getRequirements().isEmpty();
  }
  
  public boolean hasMilestones() {
    return !catalogue.getMilestones().isEmpty();
  }
  
  /**
   * Returns only those requirements, which firstly occur on the specified milstone.
   * @param milestone
   * @return
   */
  public List<Requirement> getRequirementsFor(Milestone milestone) {
    return catalogue.getRequirements().stream().filter(r -> matchesMinimalMilestone(r,milestone)).collect(Collectors.toList());
  }
  
  boolean matchesMinimalMilestone(Requirement requirement, Milestone milestone) {
    return requirement.getMinimalMilestoneUUID().equals(milestone.getUuid());
  }
  
  boolean matchesMaximalMilestone(Requirement requirement, Milestone milestone) {
    return requirement.getMaximalMilestoneUUID().equals(milestone.getUuid());
  }
  
  boolean matchesMilestone(Requirement requirement, Milestone milestone) {
    return getMilestoneDateComparator().compare(courseManager.getMinimalMilestone(requirement), milestone) <= 0 && getMilestoneDateComparator().compare(courseManager.getMaximalMilestone(requirement), milestone) >= 0;
  }
  
  private Comparator<Milestone> getMilestoneDateComparator() {
    return Comparator.comparing(courseManager::getMilestoneDate);
  }
}
