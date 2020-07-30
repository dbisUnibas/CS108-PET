package ch.unibas.dmi.dbis.cs108pet.ui.overview;

import ch.unibas.dmi.dbis.cs108pet.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.cs108pet.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.cs108pet.data.Catalogue;
import ch.unibas.dmi.dbis.cs108pet.data.Course;
import ch.unibas.dmi.dbis.cs108pet.data.Group;
import ch.unibas.dmi.dbis.cs108pet.data.Milestone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Factory to build {@link GroupOverviewItem}s.
 *
 * @author loris.sauter
 */
public class GroupOverviewItemFactory {
  
  private final Catalogue catalogue;
  private final Course course;
  private final CatalogueAnalyser catalogueAnalyser;
  
  
  public GroupOverviewItemFactory(Course course, Catalogue catalogue) {
    this.catalogue = catalogue;
    this.course = course;
    catalogueAnalyser = new CatalogueAnalyser(course, catalogue);
  }
  
  public GroupOverviewItem createForCatalogue(List<Group> groups) {
    Map<UUID, Double> map = new HashMap<>();
    map.put(catalogue.getUuid(), catalogueAnalyser.getMaximalRegularSum());
    GroupAnalyser analyser;
    for (Group g : groups) {
      analyser = new GroupAnalyser(course, catalogue, g);
      map.put(g.getUuid(), analyser.getSum());
    }
    return new GroupOverviewItem(catalogue.getName(), catalogue.getUuid(), map);
  }
  
  public GroupOverviewItem createFor(Milestone milestone, List<Group> groups) {
    Map<UUID, Double> map = new HashMap<>();
    map.put(catalogue.getUuid(), catalogueAnalyser.getMaximalRegularSumFor(milestone));
    GroupAnalyser analyser;
    for (Group g : groups) {
      analyser = new GroupAnalyser(course, catalogue, g);
      map.put(g.getUuid(), analyser.getSumFor(analyser.getProgressSummaryFor(milestone)));
    }
    return new GroupOverviewItem(milestone.getName(), milestone.getUuid(), map);
  }
}
