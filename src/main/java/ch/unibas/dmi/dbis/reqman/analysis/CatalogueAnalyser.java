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
   * Returns only those requirements, which firstly occur on the specified milestone.
   *
   * @param milestone
   * @return
   */
  public List<Requirement> getRequirementsFor(Milestone milestone) {
    return catalogue.getRequirements().stream().filter(r -> matchesMinimalMilestone(r, milestone)).collect(Collectors.toList());
  }
  
  /**
   * Returns the sum of all regular requirements
   *
   * @return
   */
  public double getMaximalRegularSum() {
    return catalogue.getRequirements().stream().filter(Requirement::isRegular).mapToDouble(Requirement::getMaxPoints).sum();
  }
  
  /**
   * Returns the sum of all regular requirements with the min ms set to the given ms.
   * This is because, the sum for a milestone is denoted by the 'freshly occurring' requirements,
   * and not for such that are still available.
   *
   * @return
   */
  public double getMaximalRegularSumFor(Milestone ms) {
    return getRequirementsFor(ms).stream().filter(Requirement::isRegular).mapToDouble(Requirement::getMaxPoints).sum();
  }
  
  /**
   * Returns the maximal sum of all bonus requirements.
   * In other words, the resulting sum is the maximal available bonus points to get.
   *
   * @return
   */
  public double getMaximalBonusSum() {
    return catalogue.getRequirements().stream().filter(Requirement::isBonus).mapToDouble((Requirement::getMaxPoints)).sum();
  }
  
  public double getMaximalBonusSumFor(Milestone ms) {
    return getRequirementsFor(ms).stream().filter(Requirement::isBonus).mapToDouble(Requirement::getMaxPoints).sum();
  }
  
  public double getMaximalMalusSum() {
    return catalogue.getRequirements().stream().filter(Requirement::isMalus).mapToDouble(Requirement::getMaxPoints).sum();
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
