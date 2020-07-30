package ch.unibas.dmi.dbis.cs108pet.ui.overview;

import ch.unibas.dmi.dbis.cs108pet.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.cs108pet.data.Requirement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueOverviewHelper {
  
  private final CatalogueAnalyser analyser;
  
  
  public CatalogueOverviewHelper(CatalogueAnalyser analyser) {
    this.analyser = analyser;
  }
  
  public double getRegularSumOf(List<CatalogueOverviewItem> items) {
    return analyser.getMaximalRegularSumFor(convert(items));
  }
  
  public double getBonusSumOf(List<CatalogueOverviewItem> items) {
    return analyser.getMaximalBonusSumFor(convert(items));
  }
  
  public double getMalusSumOf(List<CatalogueOverviewItem> items) {
    return analyser.getMaximalMalusSumFor(convert(items));
  }
  
  private List<Requirement> convert(List<CatalogueOverviewItem> items) {
    return items.stream()
        .filter(i -> i.getEntityType().equals(CatalogueOverviewItem.EntityType.REQUIREMENT))
        .map(i -> analyser.getRequirementById(i.getEntityID()))
        .collect(Collectors.toList());
  }
}
