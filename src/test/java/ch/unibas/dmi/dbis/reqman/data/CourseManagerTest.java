package ch.unibas.dmi.dbis.reqman.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class CourseManagerTest {
  
  private Catalogue catalogue;
  private Course course;
  private Time t1;
  private Time t2;
  private Time t3;
  
  
  private Milestone ms1;
  private Milestone ms2;
  private Milestone ms3;
  
  private Requirement r1;
  private Requirement r2;
  private Requirement r3;
  private Requirement r4;
  private Requirement r5;
  private Date d1;
  private Date d2;
  private Date d3;
  
  @Before
  public void setupClasses(){
    setupTimeEntities();
    setupMilestones();
    setupRequirements();
    setupCatalogue();
    catalogue.addAllMilestones(ms1,ms2,ms3);
    catalogue.addAllRequirements(r1,r2,r3,r4,r5);
    setupCourse();
  }
  
  private void setupMilestones(){
    ms1 = EntityFactory.createMilestone("MS1", t1);
    ms2 = EntityFactory.createMilestone("MS2", t2);
    ms3 = EntityFactory.createMilestone("MS3", t3);
  }
  
  private void setupRequirements(){
    r1 = EntityFactory.createRequirement("R1", "ToDos for R1",5,ms1,ms1);
    r2 = EntityFactory.createBinaryRequirement("R2", "ToDos for R2, binary", 1, ms1, ms3);
    r3 = EntityFactory.createRequirement("R3", "ToDos for R3", 4, ms1, ms2);
    r4 = EntityFactory.createBonusRequirement("R4", "ToDos for BONUS R4", 2, ms1, ms3);
    r5 = EntityFactory.createMalusRequirement("R5", "Donts for MALUS R5", 5, ms1, ms3);
  }
  
  private void setupCatalogue(){
    catalogue = new Catalogue();
    catalogue.setName("Test Catalogue");
    catalogue.setDescription("Catalogue created during CourseManagerTest, thus used in unit test environment");
  }
  
  private void setupTimeEntities(){
    t1 = new Time();
    d1 = new Date(1508025600L);
    t1.setDate(d1);
    t2 = new Time();
    d2 = new Date(1508457600L);
    t2.setDate(d2);
    t3 = new Time();
    d3 = new Date(1508889600L);
    t3.setDate(d3);
  }
  
  private void setupCourse(){
    course = new Course();
    course.setName("Test Course");
    course.setSemester("HS17");
    course.setCatalogueUUID(catalogue.getUuid());
    course.addAllTimes(t1,t2,t3);
  }
  
  @Test
  public void testGetMSDate(){
    CourseManager manager = new CourseManager(course, catalogue);
    Date d1 = manager.getMilestoneDate(ms1);
    Date d2 = manager.getMilestoneDate(ms2);
    Date d3 = manager.getMilestoneDate(ms3);
    Assert.assertEquals(this.d1, d1);
    Assert.assertEquals(this.d2, d2);
    Assert.assertEquals(this.d3, d3);
  }
  
}