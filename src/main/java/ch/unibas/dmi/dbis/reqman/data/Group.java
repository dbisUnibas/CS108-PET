package ch.unibas.dmi.dbis.reqman.data;

import ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.reqman.common.LoggingUtils;
import ch.unibas.dmi.dbis.reqman.common.VersionedEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * A {@link Group} tracks progress made in a catalogue.
 * Technically may has any arbitrary size.
 * <p>
 * Group objects are directly serialized using jackson serialization and written as a json file to disk
 *
 * @author loris.sauter
 */
public class Group extends VersionedEntity implements Comparable<Group> {
  
  
  /**
   * The logger instance
   */
  @Deprecated
  private static final Logger LOG = LogManager.getLogger(Group.class);
  /**
   * The unique identifier of this group
   */
  private final UUID uuid;
  private UUID courseUuid;
  /**
   * The name of the group, which must be unique
   */
  private String name;
  
  /**
   * The name of the group's project.
   * This is designed so groups may have artifical names such as group1, group2 etc and can have customized names on the
   * same time.
   */
  private String projectName;
  
  
  /**
   * The list of members.
   * This list may be of any size, since groups may vary in size.
   */
  private List<Member> members;
  
  /**
   * The group legacyMembers as a list of strings.
   * A member is a string in a format as follows:
   * <ul>
   * <li>Variant 1: <code>name</code></li>
   * <li>Variant 2: <code>name,surname</code></li>
   * <li>Variant 3: <code>name,surname,email</code></li>
   * </ul>
   *
   * @deprecated Got replaced by {@link Group#members}
   */
  @Deprecated
  private List<String> legacyMembers;
  /**
   * The reference name of the catalogue this group tracks the progress of
   *
   * @deprecated Got replaced by Group{@link #courseUuid}
   */
  @Deprecated
  private String catalogueName;
  /**
   * The list of {@link Progress} of this group.
   */
  private List<Progress> progressList = new ArrayList<>();
  /**
   * The list of {@link ProgressSummary} of this group
   */
  private List<ProgressSummary> progressSummaries = new ArrayList<>();
  /**
   * The name of the file the default export goes to.
   * Contains only the name of the file, location will be set by user upon export
   */
  private String exportFileName;
  
  /**
   * Creates a new group with the specified arguments
   *
   * @param name          The unique name of the group
   * @param projectName   The optional project name
   * @param legacyMembers The optional list of legacyMembers
   * @param catalogueName The name of the catalogue this group tracks progress of
   * @deprecated Got replaced by {@link EntityFactory#createGroup(String, Member...)}
   */
  @Deprecated
  public Group(String name, String projectName, List<String> legacyMembers, String catalogueName) {
    this();
    this.name = name;
    this.projectName = projectName;
    this.legacyMembers = legacyMembers;
    this.catalogueName = catalogueName;
  }
  
  /**
   * Creates a group without any property set.
   * Default constructor
   */
  public Group() {
    uuid = UUID.randomUUID();
    members = new ArrayList<>();
  }
  
  /**
   * Returns the list of {@link Progress}
   *
   * @return The list of {@link Progress}
   */
  public List<Progress> progressList() {
    return progressList;
  }
  
  public void setVersion(String version) {
    this.setVersionInternally(version);
  }
  
  /**
   * @param ms
   * @param catalogue
   * @return
   * @deprecated Got replaced by {@link ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser#getSumFor(ProgressSummary)}
   */
  @Deprecated
  public double getSumForMilestone(Milestone ms, Catalogue catalogue) {
    ArrayList<Double> points = new ArrayList<>();
    
    for (Progress p : getProgressByMilestoneOrdinal(ms.getOrdinal())) {
      double summand = p.hasProgress() ? p.getPointsSensitive(catalogue) : 0;
      LOG.debug(LoggingUtils.SUM_MARKER, String.format("[%s] Has progress: %b, points: %f, sensitive=%f, summand=%g", catalogue.getRequirementForProgress(p).getName(), p.hasProgress(), p.getPoints(), p.getPointsSensitive(catalogue), summand));
      points.add(summand);// only add points if progress
    }
    
    return points.stream().mapToDouble(Double::doubleValue).sum();
  }
  
  @Deprecated
  public List<Milestone> getMilestonesForGroup(Catalogue catalogue) {
    ArrayList<Milestone> list = new ArrayList<>();
    
    for (Progress p : getProgressList()) {
      Milestone ms = catalogue.getMilestoneForProgress(p);
      if (!list.contains(ms)) {
        list.add(ms);
      } else {
        // Milestone already in list.
      }
    }
    list.sort(Comparator.comparingInt(Milestone::getOrdinal));
    return list;
  }
  
  /**
   * @return
   * @deprecated Will be handled externally
   */
  @Deprecated
  public String getExportFileName() {
    return exportFileName;
  }
  
  /**
   * @param exportFileName
   * @deprecated Will be handled externally
   */
  @Deprecated
  public void setExportFileName(String exportFileName) {
    this.exportFileName = exportFileName;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getProjectName() {
    return projectName;
  }
  
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }
  
  
  public String getCatalogueName() {
    return catalogueName;
  }
  
  public void setCatalogueName(String catalogueName) {
    this.catalogueName = catalogueName;
  }
  
  public boolean addMember(Member member) {
    return members.add(member);
  }
  
  public boolean removeMember(Member member) {
    return members.remove(member);
  }
  
  /**
   * @param name
   * @return
   * @deprecated Replaced by {@link #addMember(Member)}
   */
  @Deprecated
  public boolean addMember(String name) {
    return legacyMembers.add(name);
  }
  
  /**
   * @return
   * @deprecated Replaced by {@link #getMembers()}
   */
  @Deprecated
  public List<String> getLegacyMembers() {
    return new Vector<String>(legacyMembers);
  }
  
  public Member[] getMembers() {
    return members.toArray(new Member[0]);
  }
  
  /**
   * @param name
   * @return
   * @deprecated Replaced by {@link #removeMember(Member)}
   */
  @Deprecated
  public boolean removeMember(String name) {
    return legacyMembers.remove(name);
  }
  
  public boolean addProgress(Progress progress) {
    return progressList.add(progress);
  }
  
  public List<Progress> getProgressList() {
    return new ArrayList<>(progressList);
  }
  
  /**
   * @param progressList
   * @deprecated Removed due to different architecture
   */
  @Deprecated
  public void setProgressList(List<Progress> progressList) {
    this.progressList.clear();
    this.progressList.addAll(progressList);
  }
  
  public boolean removeProgress(Progress progress) {
    return progressList.remove(progress);
  }
  
  public boolean addProgressSummary(ProgressSummary progressSummary) {
    return progressSummaries.add(progressSummary);
  }
  
  public List<ProgressSummary> getProgressSummaries() {
    return new ArrayList<ProgressSummary>(progressSummaries);
  }
  
  public boolean removeProgressSummary(ProgressSummary progressSummary) {
    return progressSummaries.remove(progressSummary);
  }
  
  public void setProgressSummaryList(List<ProgressSummary> progressSummaryList) {
    this.progressSummaries.clear();
    this.progressSummaries.addAll(progressSummaryList);
  }
  
  @Override
  public int compareTo(Group o) {
    return name.compareTo(o.getName());
  }
  
  /**
   * @param ms
   * @return
   * @deprecated Replaced by {@link ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser#getProgressSummaryFor(Milestone)}
   */
  @Deprecated
  public ProgressSummary getProgressSummaryForMilestone(Milestone ms) {
    for (ProgressSummary ps : progressSummaries) {
      if (ps.getMilestoneOrdinal() == ms.getOrdinal()) {
        return ps;
      }
    }
    return null;
  }
  
  /**
   * @param ordinal
   * @return
   * @deprecated Replaced by {@link ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser#getProgressFor(ProgressSummary)}
   */
  @Deprecated
  public List<Progress> getProgressByMilestoneOrdinal(int ordinal) {
    HashSet<Progress> set = new HashSet<>();
    for (Progress p : getProgressList()) {
      if (p.getMilestoneOrdinal() == ordinal) {
        if (!set.add(p)) {
          LOG.debug("WARN: " + p.getRequirementName() + " already in set");
        }
      }
    }
    return new ArrayList<>(set);
  }
  
  /**
   * @param catalogue
   * @return
   * @deprecated Replaced by  {@link GroupAnalyser#getSum()}
   */
  @Deprecated
  public double getTotalSum(Catalogue catalogue) {
    ArrayList<Double> points = new ArrayList<>();
    getMilestonesForGroup(catalogue).forEach(ms -> {
      points.add(getSumForMilestone(ms, catalogue));
    });
    return points.stream().mapToDouble(Double::doubleValue).sum();
  }
  
  /**
   * @param requirement
   * @return
   * @deprecated Replaced by {@link GroupAnalyser#getProgressFor(Requirement)}
   */
  @Deprecated
  public Progress getProgressForRequirement(Requirement requirement) {
    if (requirement == null) {
      throw new IllegalArgumentException("Requirement cannot be null, if progress for it should be provided");
    }
    for (Progress p : progressList) {
      if (p.getRequirementName().equals(requirement.getName())) {
        return p;
      }
    }
    return null;
  }
  
  /**
   * @param catalogue
   * @param progress
   * @return
   * @deprecated Replaced by {@link GroupAnalyser#isProgressUnlocked(Progress)}
   */
  @Deprecated
  public boolean isProgressUnlocked(Catalogue catalogue, Progress progress) {
    int predecessorsAchieved = 0;
    for (String name : catalogue.getRequirementForProgress(progress).getPredecessorNames()) {
      Progress pred = getProgressForRequirement(catalogue.getRequirementByName(name));
      if (pred != null && pred.hasProgress()) {
        predecessorsAchieved++;
      }
    }
    return predecessorsAchieved == catalogue.getRequirementForProgress(progress).getPredecessorNames().size();
  }
  
  public UUID getUuid() {
    return uuid;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    Group group = (Group) o;
    return getUuid().equals(group.getUuid());
  }
  
  @Override
  public int hashCode() {
    int result = getUuid() != null ? getUuid().hashCode() : 0;
    result = 31 * result + (getName() != null ? getName().hashCode() : 0);
    result = 31 * result + (getProjectName() != null ? getProjectName().hashCode() : 0);
    result = 31 * result + (getCatalogueName() != null ? getCatalogueName().hashCode() : 0);
    result = 31 * result + (getProgressList() != null ? getProgressList().hashCode() : 0);
    result = 31 * result + (getProgressSummaries() != null ? getProgressSummaries().hashCode() : 0);
    result = 31 * result + (getVersion() != null ? getVersion().hashCode() : 0);
    return result;
  }
  
  public boolean addAllMembers(Collection<Member> members) {
    return this.members.addAll(members);
  }
  
  public UUID getCourseUuid() {
    return courseUuid;
  }
  
  void setCourse(Course course) {
    courseUuid = course.getUuid();
  }
}
