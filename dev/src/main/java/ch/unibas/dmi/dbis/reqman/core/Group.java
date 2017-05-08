package ch.unibas.dmi.dbis.reqman.core;

import ch.unibas.dmi.dbis.reqman.common.LoggingUtils;
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
public class Group implements Comparable<Group> {

    /**
     * The logger instance
     */
    private static final Logger LOG = LogManager.getLogger(Group.class);

    /**
     * The name of the group, which must be unique
     */
    private String name;

    /**
     * The name of the group's project.
     * This is designed so groups may have artifical names such as group1, group2 etc and can have customized names on the same time.
     */
    private String projectName;

    /**
     * The group members as a list of strings.
     * A member is a string in a format as follows:
     * <ul>
     * <li>Variant 1: <code>name</code></li>
     * <li>Variant 2: <code>name,surname</code></li>
     * <li>Variant 3: <code>name,surname,email</code></li>
     * </ul>
     */
    private List<String> members;
    /**
     * The reference name of the catalogue this group tracks the progress of
     */
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
     * The ReqMan version with which this group was last saved
     */
    private String version;

    /**
     * Creates a new group with the specified arguments
     *
     * @param name          The unique name of the group
     * @param projectName   The optional project name
     * @param members       The optional list of members
     * @param catalogueName The name of the catalogue this group tracks progress of
     */
    public Group(String name, String projectName, List<String> members, String catalogueName) {

        this.name = name;
        this.projectName = projectName;
        this.members = members;
        this.catalogueName = catalogueName;
    }

    /**
     * Creates a group without any property set.
     * Default constructor
     */
    public Group() {

    }

    /**
     * Returns the list of {@link Progress}
     *
     * @return The list of {@link Progress}
     */
    public List<Progress> progressList() {
        return progressList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public double getSumForMilestone(Milestone ms, Catalogue catalogue) {
        ArrayList<Double> points = new ArrayList<>();

        for (Progress p : getProgressByMilestoneOrdinal(ms.getOrdinal())) {
            double summand = p.hasProgress() ? p.getPointsSensitive(catalogue) : 0;
            LOG.debug(LoggingUtils.SUM_MARKER, String.format("[%s] Has progress: %b, points: %f, sensitive=%f, summand=%g", catalogue.getRequirementForProgress(p).getName(), p.hasProgress(), p.getPoints(), p.getPointsSensitive(catalogue), summand));
            points.add(summand);// only add points if progress
        }

        return points.stream().mapToDouble(Double::doubleValue).sum();
    }

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


    public String getExportFileName() {
        return exportFileName;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Group group = (Group) o;

        return getName().equals(group.getName());
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getProjectName() != null ? getProjectName().hashCode() : 0);
        result = 31 * result + (members != null ? members.hashCode() : 0);
        result = 31 * result + (getCatalogueName() != null ? getCatalogueName().hashCode() : 0);
        result = 31 * result + (progressList != null ? progressList.hashCode() : 0);
        result = 31 * result + (progressSummaries != null ? progressSummaries.hashCode() : 0);
        return result;
    }

    public String getCatalogueName() {
        return catalogueName;
    }

    public void setCatalogueName(String catalogueName) {
        this.catalogueName = catalogueName;
    }

    public boolean addMember(String name) {
        return members.add(name);
    }

    public List<String> getMembers() {
        return new Vector<String>(members);
    }

    public boolean removeMember(String name) {
        return members.remove(name);
    }

    public boolean addProgress(Progress progress) {
        return progressList.add(progress);
    }

    public List<Progress> getProgressList() {
        return new ArrayList<>(progressList);
    }

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

    public ProgressSummary getProgressSummaryForMilestone(Milestone ms) {
        for (ProgressSummary ps : progressSummaries) {
            if (ps.getMilestoneOrdinal() == ms.getOrdinal()) {
                return ps;
            }
        }
        return null;
    }

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

    public double getTotalSum(Catalogue catalogue) {
        ArrayList<Double> points = new ArrayList<>();
        getMilestonesForGroup(catalogue).forEach(ms -> {
            points.add(getSumForMilestone(ms, catalogue));
        });
        return points.stream().mapToDouble(Double::doubleValue).sum();
    }

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

}
