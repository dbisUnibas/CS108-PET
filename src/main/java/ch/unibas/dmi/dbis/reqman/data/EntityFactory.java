package ch.unibas.dmi.dbis.reqman.data;

/**
 * Creates entities that are linked with each other.
 * <p>
 * Since the {@link Catalogue} entity and the {@link Course} entity are heavily dependent on each other,
 * the factory needs both of them in order to work properly.
 * </p>
 * Basically, the factory creates the entities and links them in a structured and safe way.
 *
 * @author loris.sauter
 */
public class EntityFactory {
  
  /**
   * The internal manager instance for link resolving
   */
  private CourseManager manager;
  
  /**
   * The internal course, used for resolving milestone dates
   */
  private Course course = null;
  /**
   * The internal catalogue
   */
  private Catalogue catalogue = null;
  
  /**
   * Legacy
   *
   * @deprecated Replaced by parametrized constructors
   */
  private EntityFactory() {
  }
  
  /**
   * Creates an EntityFactory for the given course
   *
   * @param course The course, must not be null
   */
  private EntityFactory(Course course) {
    ensureNonNullArgument(course);
    this.course = course;
  }
  
  /**
   * Creates an EntityFactory for the given course and catalogue
   *
   * @param course    the course, must not be null
   * @param catalogue the catalogue, must not be null
   */
  private EntityFactory(Course course, Catalogue catalogue) {
    this(course);
    ensureNonNullArgument(catalogue);
    this.catalogue = catalogue;
    manager = new CourseManager(this.course, this.catalogue);
  }
  
  /**
   * Creates a new EntityFactory for the given course.
   * <p>
   * The factory also needs to have its catalogue set, in order to function properly
   * <p>
   * The given course is used to resolve dates via the course's Time entities.
   *
   * @param course The course to use when creating new entities with this factory. Must not be null
   * @return A new EntityFactory which will create entities in the namespace of the specified course.
   * @throws IllegalArgumentException If the specified course object is null
   */
  public static EntityFactory createFactoryFor(Course course) {
    return new EntityFactory(course);
  }
  
  /**
   * Creates a new EntityFactory for the given course and given catalogue.
   * <p>
   * The given course is used to resolve dates via the course's Time entities.
   * <p>
   * The given catalogue is used to link milestones and requirements on the fly.
   * Besides this, requirements and milestones are automatically added to the catalogue on the fly.
   *
   * @param course    The course to use when creating new entities with this factory. Must not be null
   * @param catalogue The catalogue to use when creating new entities with this factory. Must not be null
   * @return A new EntityFactory which will create entities in the namespace of the specified course/catalogue.
   * @throws IllegalArgumentException If either the course or catalogue are null
   */
  public static EntityFactory createFactoryFor(Course course, Catalogue catalogue) {
    return new EntityFactory(course, catalogue);
  }
  
  public static EntityFactory createFactoryAndCourse(String courseName, String semester) {
    return new EntityFactory(new Course(courseName, semester));
  }
  
  /**
   * Creates a new binary requirement with the specified properties.
   * <p>
   * The resulting requirement is of type {@link ch.unibas.dmi.dbis.reqman.data.Requirement.Type#REGULAR}
   *
   * @param name
   * @param excerpt
   * @param maxPoints
   * @param minMS
   * @param maxMS
   * @return
   */
  public static Requirement createBinaryRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return createRequirement(name, excerpt, maxPoints, minMS, maxMS, true, Requirement.Type.REGULAR);
  }
  
  /**
   * Creates a new requirement with the specified properties.
   * <p>
   * The resulting requirement is of type {@link ch.unibas.dmi.dbis.reqman.data.Requirement.Type#REGULAR} and is
   * non-binary.
   *
   * @param name
   * @param excerpt
   * @param maxPoints
   * @param minMS
   * @param maxMS
   * @return
   */
  public static Requirement createRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return createRequirement(name, excerpt, maxPoints, minMS, maxMS, false, Requirement.Type.REGULAR);
  }
  
  /**
   * Creates a new malus requirement with the specified properties.
   * <p>
   * THe resulting requirement is of type {@link ch.unibas.dmi.dbis.reqman.data.Requirement.Type#MALUS} and is binary.
   *
   * @param name
   * @param excerpt
   * @param maxPoints
   * @param minMS
   * @param maxMS
   * @return
   */
  public static Requirement createMalusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return createRequirement(name, excerpt, maxPoints, minMS, maxMS, true, Requirement.Type.MALUS);
  }
  
  /**
   * Creates a new bonus requirement with the specified properties.
   * <p>
   * THe resulting requirement is of type {@link ch.unibas.dmi.dbis.reqman.data.Requirement.Type#BONUS} and is binary.
   *
   * @param name
   * @param excerpt
   * @param maxPoints
   * @param minMS
   * @param maxMS
   * @return
   */
  public static Requirement createBonusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return createRequirement(name, excerpt, maxPoints, minMS, maxMS, true, Requirement.Type.BONUS);
  }
  
  public static Milestone createMilestone(String name, Time time) {
    Milestone ms = new Milestone();
    ms.setName(name);
    ms.setTimeUUID(time.getUuid());
    return ms;
  }
  
  public static Catalogue createCatalogue(String name) {
    Catalogue cat = new Catalogue();
    cat.setName(name);
    return cat;
  }
  
  private static Requirement createRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS, boolean binary, Requirement.Type type) {
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
  
  public Catalogue getCatalogue() {
    return catalogue;
  }
  
  public void setCatalogue(Catalogue catalogue) {
    this.catalogue = catalogue;
    manager = new CourseManager(this.course, this.catalogue);
  }
  
  public Course getCourse() {
    return course;
  }
  
  private void ensureCourseSet(String operation) {
    if (course == null) {
      throw new IllegalStateException("The factory's course must be set for this operation: " + operation);
    }
  }
  
  private void ensureCourseSet() {
    ensureCourseSet("");
  }
  
  private void ensureCatalogueSet(String operation) {
    if (catalogue == null) {
      throw new IllegalStateException("The factory's catalogue must be ste for this operation: " + operation);
    }
  }
  
  private void ensureCatalogueSet() {
    ensureCatalogueSet("");
  }
  
  private void ensureNonNullArgument(Object o) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument not accepted");
    }
  }
}
