package ch.unibas.dmi.dbis.cs108pet.ui.overview;

import ch.unibas.dmi.dbis.cs108pet.analysis.CatalogueAnalyser;
import ch.unibas.dmi.dbis.cs108pet.data.Catalogue;
import ch.unibas.dmi.dbis.cs108pet.data.Milestone;
import ch.unibas.dmi.dbis.cs108pet.data.Requirement;

import static ch.unibas.dmi.dbis.cs108pet.ui.overview.CatalogueOverviewItem.EntityType.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueOverviewItemFactory {
  
  private CatalogueAnalyser analyser;
  
  public CatalogueOverviewItemFactory(CatalogueAnalyser analyser) {
    this.analyser = analyser;
  }
  
  public CatalogueOverviewItem createFor(Catalogue catalogue) {
    CatalogueOverviewItem item = new CatalogueOverviewItem(CATALOGUE, catalogue.getUuid());
    
    item.setName(catalogue.getName());
    item.setActualPoints(analyser.getMaximalRegularSum());
    item.setType("Catalogue");
    item.setRegularPoints(analyser.getMaximalRegularSum());
    item.setBonusPoints(analyser.getMaximalBonusSum());
    item.setMalusPoints(analyser.getMaximalMalusSum());
    return item;
  }
  
  public CatalogueOverviewItem createFor(Milestone ms) {
    CatalogueOverviewItem item = new CatalogueOverviewItem(MILESTONE, ms.getUuid());
    
    item.setName(ms.getName());
    item.setType("Milestone");
    item.setActualPoints(analyser.getMaximalRegularSumFor(ms));
    item.setRegularPoints(analyser.getMaximalRegularSumFor(ms));
    item.setBonusPoints(analyser.getMaximalBonusSumFor(ms));
    item.setMalusPoints(analyser.getMaximalMalusSumFor(ms));
    return item;
  }
  
  public CatalogueOverviewItem createFor(Requirement requirement) {
    CatalogueOverviewItem item = new CatalogueOverviewItem(REQUIREMENT, requirement.getUuid());
    
    item.setName(requirement.getName());
    item.setType(requirement.getType().toString());
    item.setActualPoints((requirement.isMalus() ? -1d : 1d) * requirement.getMaxPoints());
    item.setCategory(requirement.getCategory());
    switch (requirement.getType()) {
      case REGULAR:
        item.setRegularPoints(requirement.getMaxPoints());
        break;
      case MALUS:
        item.setMalusPoints(requirement.getMaxPoints());
        break;
      case BONUS:
        item.setBonusPoints(requirement.getMaxPoints());
        break;
    }
    return item;
  }
}
