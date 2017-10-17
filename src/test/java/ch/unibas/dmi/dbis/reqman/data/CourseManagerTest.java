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
  
  private EntityFactory factory;
  
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
    factory = EntityFactory.createFactoryAndCourse("Test Course", "HS17");
    course = factory.getCourse();
    catalogue = factory.createCatalogue("Test Catalogue");
    catalogue.setDescription("Catalogue created during CourseManagerTest, thus used in unit test environment");
    factory.setCatalogue(catalogue);
    setupTimeEntities();
    setupMilestones();
    setupRequirements();
  }
  
  private void setupMilestones(){
    ms1 = factory.createMilestone("MS1", t1);
    ms2 = factory.createMilestone("MS2", t2);
    ms3 = factory.createMilestone("MS3", t3);
  }
  
  private void setupRequirements(){
    r1 = factory.createRequirement("R1", "ToDos for R1",5,ms1,ms1);
    r2 = factory.createBinaryRequirement("R2", "ToDos for R2, binary", 1, ms1, ms3);
    r3 = factory.createRequirement("R3", "ToDos for R3", 4, ms1, ms2);
    r4 = factory.createBonusRequirement("R4", "ToDos for BONUS R4", 2, ms1, ms3);
    r5 = factory.createMalusRequirement("R5", "Donts for MALUS R5", 5, ms1, ms3);
  }
  
  
  private void setupTimeEntities(){
    d1 = new Date(1508025600L);
    d2 = new Date(1508457600L);
    d3 = new Date(1508889600L);
    t1 = factory.createTime(d1);
    t2 = factory.createTime(d2);
    t3 = factory.createTime(d3);
  }
  
  // TODO properly test manager
  
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