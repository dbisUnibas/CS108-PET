package ch.unibas.dmi.dbis.reqman.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EntityFactoryTest {
  
  private EntityFactory factory;
  
  @Before
  public void before(){
    factory = EntityFactory.createFactoryAndCourse("BeforeCourse", "HS17");
  }
  
  @Test
  public void createFactoryAndCourse() throws Exception {
    EntityFactory factory = EntityFactory.createFactoryAndCourse("TestCourse", "HS17");
    assertEquals("TestCourse", factory.getCourse().getName());
  }
  
  
}