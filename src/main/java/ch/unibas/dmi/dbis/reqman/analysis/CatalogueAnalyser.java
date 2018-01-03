package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.data.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
  
  public Requirement getRequirementById(UUID requirementUuid){
    if(requirementUuid == null){
      throw new IllegalArgumentException("Cannot find requirement by id, if the id is null");
    }
    for(Requirement r : catalogue.getRequirements()){
      if(requirementUuid.equals(r.getUuid())){
        return r;
      }
    }
    return null;
  }
  
  public Milestone getMilestoneById(UUID milestoneUuid){
    if(milestoneUuid == null){
      throw new IllegalArgumentException("Cannot find milestone by id, if the id is null");
    }
    for(Milestone ms : catalogue.getMilestones()){
      if(milestoneUuid.equals(ms.getUuid())){
        return ms;
      }
    }
    return null;
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
  
  public double getMaximalRegularSumFor(List<Requirement> list){
    return list.stream().filter(Requirement::isRegular).mapToDouble(Requirement::getMaxPoints).sum();
  }
  
  public double getMaximalRegularSumForProgressList(List<Progress> list){
    return list.stream().map(this::getRequirementOf).filter(Requirement::isRegular).mapToDouble(Requirement::getMaxPoints).sum();
  }
  
  public double getMaximalRegularSumFor(ProgressSummary ps){
    return getMaximalRegularSumFor(getMilestoneOf(ps));
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
  
  
  public List<Requirement> getPredecessors(Requirement requirement){
    List<Requirement> predecessors = new ArrayList<>();
    for(UUID id : requirement.getPredecessors()){
      predecessors.add(getRequirementById(id));
    }
    return predecessors;
  }
  
  public boolean isPredecessor(Requirement requirement){
    long nbOfDependents= catalogue.getRequirements().stream().filter(r -> getPredecessors(r).contains(requirement)).count();
    return nbOfDependents > 0;
  }
  
  public List<Requirement> findRequirementsNameContains(String search){
    return catalogue.getRequirements().stream().filter(r -> r.getName().contains(search)).collect(Collectors.toList());
  }
  
  public List<Requirement> findRequirementsContaining(String pattern){
    return catalogue.getRequirements().stream().filter(r -> containsRequirementPattern(r,pattern)).collect(Collectors.toList());
  }
  
  public List<Requirement> findRequirementsForCategory(String category){
    return catalogue.getRequirements().stream().filter(r->StringUtils.containsNullSafe(r.getCategory(), category)).collect(Collectors.toList());
  }
  
  public List<Requirement> findRequirementsByType(Requirement.Type type){
    return catalogue.getRequirements().stream().filter(r -> r.getType().equals(type)).collect(Collectors.toList());
  }
  
  public Set<String> getCategories(){
    Set<String> set = new TreeSet<>();
    catalogue.getRequirements().stream().filter(r -> !StringUtils.isNullOrEmpty(r.getCategory())).forEach(r -> set.add(r.getCategory()));
    return set;
  }
  
  public Milestone getMilestoneOf(ProgressSummary progressSummary) {
    return getMilestoneById(progressSummary.getMilestoneUUID());
  }
  
  public List<Requirement> getRequirementsFor(ProgressSummary progressSummary) {
    return getRequirementsFor(getMilestoneOf(progressSummary));
  }
  
  boolean containsRequirementPattern(Requirement requirement, String pattern){
    boolean inName = StringUtils.containsNullSafe(requirement.getName(), pattern);
    boolean inExcerpt = StringUtils.containsNullSafe(requirement.getExcerpt(), pattern);
    boolean inDescription = StringUtils.containsNullSafe(requirement.getDescription(), pattern);
    return inName || inExcerpt || inDescription;
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
  
  public double getActualPoints(@NotNull Progress progress) {
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
  
  public Requirement getRequirementOf(@NotNull Progress progress) {
    for (Requirement r : catalogue.getRequirements()) {
      if (r.getUuid().equals(progress.getRequirementUUID())) {
        return r;
      }
    }
    return null;
  }
}
