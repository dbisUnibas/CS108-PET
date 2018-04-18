package ch.unibas.dmi.dbis.reqman.ui.overview;

import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueOverviewItem {
  
  private final EntityType entityType;
  private String name, type, category;
  private double actualPoints, regularPoints, bonusPoints, malusPoints;
  private UUID entityID;
  
  CatalogueOverviewItem(EntityType entityType, UUID id) {
    this.entityType = entityType;
    this.entityID = id;
  }
  
  public String getName() {
    return name;
  }
  
  void setName(String name) {
    this.name = name;
  }
  
  public String getType() {
    return type;
  }
  
  void setType(String type) {
    this.type = type;
  }
  
  public String getCategory() {
    return category;
  }
  
  void setCategory(String category) {
    this.category = category;
  }
  
  public double getActualPoints() {
    return actualPoints;
  }
  
  void setActualPoints(double actualPoints) {
    this.actualPoints = actualPoints;
  }
  
  public double getRegularPoints() {
    return regularPoints;
  }
  
  void setRegularPoints(double regularPoints) {
    this.regularPoints = regularPoints;
  }
  
  public double getBonusPoints() {
    return bonusPoints;
  }
  
  void setBonusPoints(double bonusPoints) {
    this.bonusPoints = bonusPoints;
  }
  
  public double getMalusPoints() {
    return malusPoints;
  }
  
  void setMalusPoints(double malusPoints) {
    this.malusPoints = malusPoints;
  }
  
  public UUID getEntityID() {
    return entityID;
  }
  
  void setEntityID(UUID entityID) {
    this.entityID = entityID;
  }
  
  public EntityType getEntityType() {
    return entityType;
  }
  
  enum EntityType {
    CATALOGUE,
    MILESTONE,
    REQUIREMENT;
  }
}
