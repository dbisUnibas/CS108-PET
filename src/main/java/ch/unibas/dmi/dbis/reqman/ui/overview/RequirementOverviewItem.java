package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Progress;
import ch.unibas.dmi.dbis.reqman.data.Requirement;

import java.util.HashMap;
import java.util.Map;

/**
 * Analysis methods do not really work on non-binary achievements, but who cares.
 * If you want to make this work on non-binary achievements, you probably need to include some sort of averaging / probability distributions.
 *
 * @author silvan.heller
 */
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
    int count = 0;
    for (Progress progress : groupResults.values()) {
      if (progress.getFraction() > 0.5) {
        count += 1;
      }
    }
    return count;
  }
}
