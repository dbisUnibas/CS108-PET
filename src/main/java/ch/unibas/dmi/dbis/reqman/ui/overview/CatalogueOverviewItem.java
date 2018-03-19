package ch.unibas.dmi.dbis.reqman.ui.overview;

import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueOverviewItem {
  
  private String name, type, category;
  private double actualPoints, regularPoints, bonusPoints, malusPoints;
  
  private UUID entityID;
  
  private final EntityType entityType;
  
  CatalogueOverviewItem(EntityType entityType, UUID id){
    this.entityType = entityType;
    this.entityID = id;
  }
  
  void setName(String name) {
    this.name = name;
  }
  
  void setType(String type) {
    this.type = type;
  }
  
  void setCategory(String category) {
    this.category = category;
  }
  
  void setActualPoints(double actualPoints) {
    this.actualPoints = actualPoints;
  }
  
  void setRegularPoints(double regularPoints) {
    this.regularPoints = regularPoints;
  }
  
  void setBonusPoints(double bonusPoints) {
    this.bonusPoints = bonusPoints;
  }
  
  void setMalusPoints(double malusPoints) {
    this.malusPoints = malusPoints;
  }
  
  void setEntityID(UUID entityID) {
    this.entityID = entityID;
  }
  
  public String getName() {
    return name;
  }
  
  public String getType() {
    return type;
  }
  
  public String getCategory() {
    return category;
  }
  
  public double getActualPoints() {
    return actualPoints;
  }
  
  public double getRegularPoints() {
    return regularPoints;
  }
  
  public double getBonusPoints() {
    return bonusPoints;
  }
  
  public double getMalusPoints() {
    return malusPoints;
  }
  
  public UUID getEntityID() {
    return entityID;
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
