package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Progress;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RequirementOverviewItem {

  private final Requirement requirement;
  private Map<Group, Progress> groupResults;

  public RequirementOverviewItem(Requirement requirement) {
    this.requirement = requirement;
    groupResults = new HashMap<>();
  }

  public void markResult(Group group, Progress result) {
    groupResults.put(group, result);
  }

  public Requirement getRequirement() {
    return requirement;
  }

  public Integer getAchievedCount() {
    if (!requirement.isBinary()) {
      return -1;
    }
    int count = 0;
    for (Progress progress : groupResults.values()) {
      count += Math.max(0, progress.getFraction());
    }
    return count;
  }

  public Integer getNotAchievedCount() {
    if (!requirement.isBinary()) {
      return -1;
    }
    int count = 0;
    for (Progress progress : groupResults.values()) {
      if (progress.getFraction() == 0) {
        count += 1;
      }
    }
    return count;
  }
}
