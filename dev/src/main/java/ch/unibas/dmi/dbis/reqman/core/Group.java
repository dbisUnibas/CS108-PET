package ch.unibas.dmi.dbis.reqman.core;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Group {

    private String name;
    private String projectName;
    private List<String> members;
    private String catalogueName;
    private List<Progress> progressList = new ArrayList<>();
    private List<ProgressSummary> progressSummaries = new ArrayList<>();

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

        if (getName() != null ? !getName().equals(group.getName()) : group.getName() != null) {
            return false;
        }
        if (getProjectName() != null ? !getProjectName().equals(group.getProjectName()) : group.getProjectName() != null) {
            return false;
        }
        if (members != null ? !members.equals(group.members) : group.members != null) {
            return false;
        }
        if (getCatalogueName() != null ? !getCatalogueName().equals(group.getCatalogueName()) : group.getCatalogueName() != null) {
            return false;
        }
        if (progressList != null ? !progressList.equals(group.progressList) : group.progressList != null) {
            return false;
        }
        return progressSummaries != null ? progressSummaries.equals(group.progressSummaries) : group.progressSummaries == null;
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

    public Group(String name, String projectName, List<String> members, String catalogueName) {

        this.name = name;
        this.projectName = projectName;
        this.members = members;
        this.catalogueName = catalogueName;
    }

    public Group() {

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
        return new Vector<Progress>(progressList);
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

    public void setProgressList(List<Progress> progressList){
        this.progressList = progressList;
    }
}
