package ch.unibas.dmi.dbis.reqman.data;

import ch.unibas.dmi.dbis.reqman.common.VersionedEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * A {@link Group} tracks progress made in a catalogue.
 * Technically may has any arbitrary size.
 * <p>
 * Group objects are directly serialized using jackson serialization and written as a json file to disk
 *
 * @author loris.sauter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group extends VersionedEntity implements Comparable<Group> {
  
  
  /**
   * The logger instance
   */
  private static final Logger LOG = LogManager.getLogger(Group.class);
  /**
   * The unique identifier of this group
   */
  private final UUID uuid;
  
  /**
   * The unique identifier of the {@link Course} this group belongs to.
   */
  private UUID courseUuid;
  
  /**
   * The unique identifier of the {@link Catalogue}, this group tracks the progress of.
   */
  private UUID catalogueUuid;
  
  
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
   * @return
   */
  public String getExportFileName() {
    return exportFileName;
  }
  
  /**
   * @param exportFileName
   */
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
  
  
  public boolean addMember(Member member) {
    return members.add(member);
  }
  
  public boolean removeMember(Member member) {
    return members.remove(member);
  }
  
  public Member[] getMembers() {
    return members.toArray(new Member[0]);
  }
  
  public void setMembers(List<Member> members) {
    this.members.clear();
    this.members.addAll(members);
  }
  
  public boolean addProgress(Progress progress) {
    return progressList.add(progress);
  }
  
  public List<Progress> getProgressList() {
    return new ArrayList<>(progressList);
  }
  
  /**
   * @param progressList
   */
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
  
  public void setProgressSummaries(List<ProgressSummary> progressSummaryList) {
    this.progressSummaries.clear();
    this.progressSummaries.addAll(progressSummaryList);
  }
  
  public boolean removeProgressSummary(ProgressSummary progressSummary) {
    return progressSummaries.remove(progressSummary);
  }
  
  @Override
  public int compareTo(Group o) {
    return name.compareTo(o.getName());
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
  
  public UUID getCatalogueUuid() {
    return catalogueUuid;
  }
  
  public void setCatalogue(Catalogue catalogue) {
    this.catalogueUuid = catalogue.getUuid();
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Group{");
    sb.append("uuid=").append(uuid);
    sb.append(", courseUuid=").append(courseUuid);
    sb.append(", catalogueUuid=").append(catalogueUuid);
    sb.append(", name='").append(name).append('\'');
    sb.append(", projectName='").append(projectName).append('\'');
    sb.append(", members=").append(members);
    sb.append(", progressList=").append(progressList);
    sb.append(", progressSummaries=").append(progressSummaries);
    sb.append(", exportFileName='").append(exportFileName).append('\'');
    sb.append('}');
    return sb.toString();
  }
  
  void setCourse(Course course) {
    courseUuid = course.getUuid();
  }
}
