package ch.unibas.dmi.dbis.reqman.data;

import ch.unibas.dmi.dbis.reqman.ui.common.MandatoryFieldsMissingException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

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
  
  // TODO Decide whether singleton or not (currently: Not)
  
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
  @NotNull
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
  @NotNull
  public static EntityFactory createFactoryFor(Course course, Catalogue catalogue) {
    return new EntityFactory(course, catalogue);
  }
  
  /**
   * Creates a new EntityFactory and a new course, for which this factory will create entities.
   * <p>
   * The factory also needs to have its catalogue set, in order to function properly
   * <p>
   * The given course is used to resolve dates via the course's Time entities.
   *
   * @param courseName The name for the course
   * @param semester   The semester abbreviation
   * @return A new EntityFactory with the newly created course, based on the given arguments
   */
  @NotNull
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
   * @throws IllegalStateException Iff no course / catalogue is set
   */
  public Requirement createBinaryRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
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
   * @throws IllegalStateException Iff no course / catalogue is set
   */
  public Requirement createRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
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
   * @throws IllegalStateException Iff no course / catalogue is set
   */
  public Requirement createMalusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return createRequirement(name, excerpt, maxPoints, minMS, maxMS, true, Requirement.Type.MALUS);
  }
  
  public Requirement createMalusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS, boolean binary) {
    return createRequirement(name, excerpt, maxPoints, minMS, maxMS, binary, Requirement.Type.MALUS);
  }
  
  public Requirement createBonusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS, boolean binary) {
    return createRequirement(name, excerpt, maxPoints, minMS, maxMS, binary, Requirement.Type.BONUS);
  }
  
  /**
   * Creates a group of given members and links it with the current course.
   *
   * @param groupName
   * @param members
   * @return
   */
  public Group createGroup(String groupName, Member... members) {
    Group g = new Group();
    g.setName(groupName);
    g.setCourse(course);
    g.setCatalogue(catalogue);
    g.addAllMembers(Arrays.asList(members));
    return g;
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
   * @throws IllegalStateException Iff no course / catalogue is set
   */
  public Requirement createBonusRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS) {
    return createRequirement(name, excerpt, maxPoints, minMS, maxMS, true, Requirement.Type.BONUS);
  }
  
  /**
   * Creates a new {@link Milestone} with given name and {@link Time}.
   * <p>
   * This operation requires the course and catalogue of this factory set.
   * <p>
   * If the given time is not already part of the {@link Course}, it will be added to it.
   * <p>
   * The resulting milestone will be automatically added to the catalogue.
   *
   * @param name The name of the milestone. Must not be null
   * @param time The {@link Time} object of the milestone's date. Must not be null
   * @return A new milestone, linked with the given Time entity and added to this factory's catalogue.
   * @throws IllegalArgumentException If the name or time argument is null (or both)
   * @throws IllegalStateException    Iff no course / catalogue is set
   */
  @NotNull
  public Milestone createMilestone(String name, Time time) {
    ensureCourseAndCatalogueSet("Create Milestone");
    if (name == null || time == null) {
      throw new IllegalArgumentException("Cannot create milestone if name or time is null");
    }
    if (!course.containsTime(time)) {
      course.addTime(time);
    }
    Milestone ms = new Milestone();
    ms.setName(name);
    ms.setTimeUUID(time.getUuid());
    catalogue.addMilestone(ms);
    return ms;
  }
  
  /**
   * Creates a new {@link Milestone} with given name and {@link Date}.
   * <p>
   * Note that a {@link Time} entity for the given date will be created, if none exists in the namespace of the course.
   *
   * @param name The name of the milestone. Must not be null
   * @param date The date on which the milestone is set to. Must not be null and will cause the creation of a {@link
   *             Time} entity for this date, if none exists
   * @return A new milestone, linked with a newly created {@link Time} entity
   * @throws IllegalArgumentException Iff either the name or date is null (or both).
   * @throws IllegalStateException    Iff no course / catalogue is set
   */
  @NotNull
  public Milestone createMilestone(String name, Date date) {
    ensureCourseAndCatalogueSet("Create Milestone");
    if (name == null || date == null) {
      throw MandatoryFieldsMissingException.createWithFormattedMessage("Mandatory fields for entity Milestone:\n\t1. Name\n\t2. Date");
    }
    Time t = createTime(date);
    return createMilestone(name, t);
  }
  
  /**
   * @param date
   * @return
   * @throws IllegalStateException Iff no course / catalogue is set
   */
  public Time createTime(Date date) {
    ensureCourseSet("Create Time");
    if (date == null) {
      throw new IllegalArgumentException("Cannot create a time with date null");
    }
    Time t = new Time(date);
    if (course.containsTime(t)) {
      return course.getTimeFor(date);
    } else {
      course.addTime(t);
      return t;
    }
  }
  
  /**
   * @param name
   * @return
   * @throws IllegalStateException Iff no course set
   */
  public Catalogue createCatalogue(String name) {
    ensureCourseSet("Create Catalogue");
    Catalogue cat = new Catalogue();
    cat.setName(name);
    course.setCatalogueUUID(cat.getUuid());
    this.catalogue = cat;
    manager = new CourseManager(course, catalogue);
    return cat;
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
  
  public void link(Group group, Course course) {
    group.setCourse(course);
  }
  
  public List<ProgressSummary> createProgressSummaries() {
    ensureCatalogueSet("Create ProgressSummary List");
    return catalogue.getMilestones().stream().map(EntityFactory::createProgressSummary).collect(Collectors.toList());
  }
  
  public List<ProgressSummary> copyProgressSummaries(Group source) {
    return source.getProgressSummaries().stream().map(ProgressSummary::new).collect(Collectors.toList());
  }
  
  public List<Progress> copyProgressList(Group source) {
    return source.getProgressList().stream().map(Progress::new).collect(Collectors.toList());
  }
  
  public List<Progress> createProgressList() {
    return catalogue.getRequirements().stream().map(this::createProgressFor).collect(Collectors.toList());
  }
  
  public int appendMissingProgresses(Group group) {
    List<Progress> progressList = group.getProgressList();
    Set<UUID> reqUuids = catalogue.getRequirements().stream().map(Requirement::getUuid).collect(Collectors.toSet());
    Map<UUID, Requirement> reqMap = new TreeMap<>();
    catalogue.getRequirements().forEach(r -> reqMap.put(r.getUuid(), r));
    Set<UUID> progReqUuids = progressList.stream().map(Progress::getRequirementUUID).collect(Collectors.toSet());
    boolean hasMissing = reqUuids.removeAll(progReqUuids);
    int missing = reqUuids.size();
    if (hasMissing) {
      reqUuids.stream().map(reqUuid -> createProgressFor(reqMap.get(reqUuid))).forEach(progressList::add);
    }
    group.setProgressList(progressList);
    return missing;
  }
  
  public int appendMissingProgressSummaries(Group group) {
    List<ProgressSummary> summaries = group.getProgressSummaries();
    Set<UUID> msIds = catalogue.getMilestones().stream().map(Milestone::getUuid).collect(Collectors.toSet());
    Map<UUID, Milestone> msMap = new TreeMap<>();
    catalogue.getMilestones().forEach(ms -> msMap.put(ms.getUuid(), ms));
    Set<UUID> summariesIds = summaries.stream().map(ProgressSummary::getMilestoneUUID).collect(Collectors.toSet());
    boolean hasMissing = msIds.removeAll(summariesIds);
    int missing = msIds.size();
    if (hasMissing) {
      msIds.stream().map(msId -> createProgressSummary(msMap.get(msId))).forEach(summaries::add);
    }
    group.setProgressSummaries(summaries);
    return missing;
  }
  
  /**
   * Creates a new progress
   *
   * @param requirement
   * @param ps
   * @param points      the amount of points achieved
   * @return
   */
  Progress createProgressFor(Requirement requirement, ProgressSummary ps, double points) {
    Progress p = new Progress();
    p.setAssessmentDate(new Date());
    double fraction = points / requirement.getMaxPoints();
    if (Double.isNaN(fraction) && requirement.getMaxPoints() == 0) {
      fraction = 1;
    }
    p.setFraction(fraction);
    p.setRequirementUUID(requirement.getUuid());
    p.setProgressSummaryUUID(ps.getUuid());
    
    // TODO Linking to group?
    
    return p;
  }
  
  public static ProgressSummary createProgressSummary(Milestone ms) {
    ProgressSummary ps = new ProgressSummary();
    ps.setMilestoneUUID(ms.getUuid());
    return ps;
  }
  
  private Progress createProgressFor(Requirement requirement) {
    Progress p = new Progress();
    p.setRequirementUUID(requirement.getUuid());
    return p;
  }
  
  private Requirement createRequirement(String name, String excerpt, double maxPoints, Milestone minMS, Milestone maxMS, boolean binary, Requirement.Type type) {
    ensureCourseAndCatalogueSet("CreateRequirement");
    if (manager.compare(minMS, maxMS) > 0) { // General compare contract
      throw new IllegalArgumentException("MinMS cannot have a date greater than maxMS (" + manager.getMilestoneDate(minMS) + " > " + manager.getMilestoneDate(maxMS) + ")");
    }
    
    Requirement r = new Requirement();
    r.setName(name);
    r.setExcerpt(excerpt);
    r.setMaxPoints(maxPoints);
    r.setMinimalMilestoneUUID(minMS.getUuid());
    r.setMaximalMilestoneUUID(maxMS.getUuid());
    r.setBinary(binary);
    r.setType(type);
    
    catalogue.addRequirement(r);
    
    // TODO add contains check: reqs, ms' are now all set (based on equals, thus based on UUID)
    
    return r;
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
  
  private void ensureCourseAndCatalogueSet(String operation) {
    ensureCourseSet(operation);
    ensureCatalogueSet(operation);
  }
  
  private void ensureCourseAndAcatalogueSet() {
    ensureCourseSet();
    ensureCatalogueSet();
  }
  
  private void ensureNonNullArgument(Object o) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument not accepted");
    }
  }
}
