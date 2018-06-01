package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import ch.unibas.dmi.dbis.reqman.data.Requirement.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Factory to build {@link RequirementOverviewItem}s.
 *
 * @author silvan.heller
 */
public class RequirementOverviewItemFactory {

  private final Catalogue catalogue;
  private final Course course;
  private final CatalogueAnalyser catalogueAnalyser;


  public RequirementOverviewItemFactory(Course course, Catalogue catalogue) {
    this.catalogue = catalogue;
    this.course = course;
    catalogueAnalyser = new CatalogueAnalyser(course, catalogue);
  }

  public RequirementOverviewItem createForRequirement(Requirement req, List<Group> groups) {
    RequirementOverviewItem item = new RequirementOverviewItem(req);
    GroupAnalyser analyser;
    for (Group g : groups) {
      analyser = new GroupAnalyser(course, catalogue, g);
      item.markResult(g, analyser.getProgressFor(req));
    }
    return item;
  }

  public RequirementOverviewItem createForRequirements(List<Requirement> requirements) {
    Requirement requirement = new Requirement();
    return new RequirementOverviewItem(requirement);
  }
}
