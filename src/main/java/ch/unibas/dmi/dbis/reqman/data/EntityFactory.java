package ch.unibas.dmi.dbis.reqman.data;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EntityFactory {

  private EntityFactory(){
    // No instance needed
  }
  
  private static Requirement createRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS, boolean binary, Requirement.Type type){
    Requirement r = new Requirement();
    r.setName(name);
    r.setExcerpt(excerpt);
    r.setMaxPoints(maxPoints);
    r.setMinimalMilestoneUUID(minMS.getUuid());
    r.setMaximalMilestoneUUID(maxMS.getUuid());
    r.setBinary(binary);
    r.setType(type);
    return r;
  }
  
  /**
   * Creates a new binary requirement with the specified properties.
   *
   * The resulting requirement is of type {@link ch.unibas.dmi.dbis.reqman.data.Requirement.Type#REGULAR}
   * @param name
   * @param excerpt
   * @param maxPoints
   * @param minMS
   * @param maxMS
   * @return
   */
  public static Requirement createBinaryRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS){
    return createRequirement(name,excerpt,maxPoints,minMS,maxMS,true,Requirement.Type.REGULAR);
  }
  
  /**
   * Creates a new requirement with the specified properties.
   *
   * The resulting requirement is of type {@link ch.unibas.dmi.dbis.reqman.data.Requirement.Type#REGULAR} and is non-binary.
   * @param name
   * @param excerpt
   * @param maxPoints
   * @param minMS
   * @param maxMS
   * @return
   */
  public static Requirement createRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS){
    return createRequirement(name,excerpt,maxPoints,minMS,maxMS,false, Requirement.Type.REGULAR);
  }
  
  /**
   * Creates a new malus requirement with the specified properties.
   *
   * THe resulting requirement is of type {@link ch.unibas.dmi.dbis.reqman.data.Requirement.Type#MALUS} and is binary.
   * @param name
   * @param excerpt
   * @param maxPoints
   * @param minMS
   * @param maxMS
   * @return
   */
  public static Requirement createMalusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS){
    return createRequirement(name,excerpt,maxPoints,minMS,maxMS, true, Requirement.Type.MALUS);
  }
  
  /**
   * Creates a new bonus requirement with the specified properties.
   *
   * THe resulting requirement is of type {@link ch.unibas.dmi.dbis.reqman.data.Requirement.Type#BONUS} and is binary.
   * @param name
   * @param excerpt
   * @param maxPoints
   * @param minMS
   * @param maxMS
   * @return
   */
  public static Requirement createBonusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS){
    return createRequirement(name,excerpt,maxPoints,minMS,maxMS,true, Requirement.Type.BONUS);
  }
  
  public static Milestone createMilestone(String name, Time time){
    Milestone ms = new Milestone();
    ms.setName(name);
    ms.setTimeUUID(time.getUuid());
    return ms;
  }
  
  public static Catalogue createCatalogue(String name){
    Catalogue cat = new Catalogue();
    cat.setName(name);
    return cat;
  }
}
