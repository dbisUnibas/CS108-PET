package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
  private final CourseManager courseManager;
  
  public GroupAnalyser(Course course, Catalogue catalogue, Group group) {
    this.group = group;
    this.course = course;
    this.catalogue = catalogue;
    this.catalogueAnalyser = new CatalogueAnalyser(course, catalogue);
    this.courseManager = new CourseManager(course, catalogue);
  }
  
  public List<Progress> getProgressFor(ProgressSummary summary) {
    return group.getProgressList().stream().filter(p -> matchesProgressMilestone(p, summary)).collect(Collectors.toList());
  }
  
  
  /**
   * Returns all {@link Progress} whose assessment was made until the specified {@link ProgressSummary}.
   * Until means, the given progress was assessed at the given progress summary.
   * @param ps
   * @return
   */
  public List<Progress> getProgressMadeUntilOrOpen(ProgressSummary ps){
    Set<Progress> progressMade = new HashSet<>(getProgressFor(ps));
    Milestone ms = catalogueAnalyser.getMilestoneOf(ps);
    if(catalogueAnalyser.hasPredecessor(ms)){
      Milestone predecessor = catalogueAnalyser.getPredecessorOf(ms);
      ProgressSummary predPS = getProgressSummaryFor(predecessor);
      progressMade.addAll(getOpenProgressAt(predPS));
    }
    return new ArrayList<>(progressMade);
  }
  
  /**
   * Returns a list of {@link Progress} objects that are associated with the given {@link ProgressSummary}, but are unassessed.
   * @param ps
   * @return
   */
  public List<Progress> getOpenProgressAt(ProgressSummary ps){
    return getProgressFor(ps).stream().filter(p -> !isSingularProgress(p)).filter(Progress::isFresh).collect(Collectors.toList());
  }
  
  public boolean isSingularProgress(Progress progress){
    Requirement r = getRequirementOf(progress);
    if(r != null){
      return r.getMinimalMilestoneUUID().equals(r.getMaximalMilestoneUUID());
    }else{
      throw new RuntimeException("Couldn't find requirement for p="+progress.toString());
    }
  }
  
  public double getActualPoints(Progress progress) {
    return catalogueAnalyser.getActualPoints(progress);
  }
  
  public Requirement getRequirementOf(@NotNull  Progress progress) {
    return catalogueAnalyser.getRequirementOf(progress);
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
  
  public Milestone getMilestoneOf(Progress p){
    if(p == null){
      throw new IllegalArgumentException("Cannot find a progress' milestone, if the milestone is null");
    }
    return catalogueAnalyser.getMilestoneById(getProgressSummaryOf(p).getMilestoneUUID());
  }
  
  public ProgressSummary getProgressSummaryOf(Progress p){
    if(p == null){
      throw new IllegalArgumentException("Cannot find a progress' progress summary, if the milestone is null");
    }
    return getProgressSummaryById(p.getProgressSummaryUUID());
  }
  
  
  public boolean isProgressUnlocked(Progress progress){
    int predecessorsFulfilled = 0;
    for(Requirement r : catalogueAnalyser.getPredecessors(getRequirementOf(progress))){
      Progress p = getProgressFor(r);
      if(p.hasProgress()){
        predecessorsFulfilled++;
      }
    }
    return predecessorsFulfilled == getRequirementOf(progress).getPredecessors().length;
  }
  
  public double getSum(){
    return group.getProgressSummaries().stream().mapToDouble(this::getSumFor).sum();
  }
  
  public double getSumFor(ProgressSummary ps){
    double sum = getProgressForProgressSummary(ps).stream().mapToDouble(this::getActualPoints).sum();
    if(!course.isNegativeReminderAllowed()){
      return sum < 0 ? 0d : sum;
    }
    return sum;
  }
  
  public double getCumulativeSumFor(ProgressSummary ps){
    double sum = 0;
    
    int pos = catalogueAnalyser.getCourseManager().getMilestoneOrdinal(catalogueAnalyser.getMilestoneOf(ps));
    for(int i=0; i<=pos;i++){
      sum += getSumFor(getProgressSummaryFor(catalogueAnalyser.getMilestoneByPosition(i)));
    }
    
    return sum;
  }
  
  public double getBonusSumFor(ProgressSummary ps){
    return getSumFor(ps, Requirement.Type.BONUS);
  }
  
  public double getRegularSumFor(ProgressSummary ps){
    return getSumFor(ps,Requirement.Type.REGULAR);
  }
  
  public double getMalusSumFor(ProgressSummary ps){
    return getSumFor(ps, Requirement.Type.MALUS);
  }
  
  public double getSumFor(ProgressSummary ps, Requirement.Type type){
    return getProgressForProgressSummary(ps).stream().filter(p -> getRequirementOf(p).getType().equals(type)).mapToDouble(this::getActualPoints).sum();
  }
  
  public List<Progress> getProgressForProgressSummary(ProgressSummary ps){
    return group.getProgressList().stream().filter( p -> matchesProgressSummary(p, ps)).collect(Collectors.toList());
  }
  
  public List<Progress> getProgressFor(List<Requirement> list){
    return list.stream().map(this::getProgressFor).collect(Collectors.toList());
  }
  
  public List<Progress> getProgressFor(List<Requirement> list, ProgressSummary ps){
    return getProgressFor(list).stream().filter(p->matchesProgressMilestone(p, ps)).collect(Collectors.toList());
  }
  
  public double getSumFor(List<Progress> list){
    return list.stream().filter(Progress::hasProgress).mapToDouble(this::getActualPoints).sum();
  }
  
  public boolean equals(@NotNull Milestone ms, @NotNull ProgressSummary ps){
    return ps.getMilestoneUUID().equals(ms.getUuid());
  }
  
  public boolean equals(@NotNull ProgressSummary ps, @NotNull Milestone ms){
    return equals(ms, ps);
  }
  
  /**
   * Checks if the given {@link Progress} matches the given {@link ProgressSummary}.
   *
   * Matching is defined as follows:
   * Either the {@link Progress} has already progress associated with (e.g. {@link Progress#hasProgress()} returns true),
   * then the {@link ProgressSummary}'s Uuid must be equal to the one returned by {@link Progress#getProgressSummaryUUID()}.
   * Or the {@link Progress} is <i>fresh</i>, then it is checked if the progress' {@link Requirement}'s {@link Milestone}
   * equals the milestone, represented by the progress summary.
   *
   * @param p
   * @param ps
   * @return
   */
  boolean matchesProgressMilestone(@NotNull Progress p, @NotNull ProgressSummary ps) {
    Requirement req = getRequirementOf(p);
    if(req == null){
      //throw new RuntimeException("Progress "+p.toString()+" has no requirement.");
      // TODO Log deprecated progress ?
      // Most probable reason for this case is, the requirement got removed.
      return false;
    }
    Milestone ms = catalogueAnalyser.getMilestoneOf(ps);
    if(!p.isFresh()){
      ProgressSummary assessmentMilestone = getProgressSummaryById(p.getProgressSummaryUUID());
      if(assessmentMilestone == null){
        throw new RuntimeException(String.format("Progress (%s) is not fresh, but there is no ProgressSummary with this uuid", p.toString()));
      }
      return equals(assessmentMilestone, ms);
    }
    return catalogueAnalyser.matchesMilestone(req, ms);
  }
  
  boolean matchesProgressSummary(@NotNull Progress p, @NotNull ProgressSummary ps){
    if(p.getProgressSummaryUUID() == null){
      return false; // Then the case, if the progress was not yet assessed
    }
    return p.getProgressSummaryUUID().equals(ps.getUuid());
  }
  
  public Comparator<Progress> getProgressComparator(){
    return Comparator.comparing(this::getRequirementOf, catalogueAnalyser.getRequirementComparator());
  }
  
  
  
}
