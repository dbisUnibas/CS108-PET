package ch.unibas.dmi.dbis.reqman.data;

import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Time;

import java.util.Date;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CourseManager {
  
  private final Course course;
  private final Catalogue catalogue;
  
  public CourseManager(Course course, Catalogue catalogue) {
    this.course = course;
    this.catalogue = catalogue;
  }
  
  public Date getMilestoneDate(Milestone milestone){
    Time time = null;
    for(Time t : course.getTimes() ){
      if(milestone.getTimeUUID().equals(t.getUuid())){
        time = t;
      }
    }
    if(time != null){
      return time.getDate();
    }
    return null;
  }
  
  public Milestone getMinimalMilestone(Requirement requirement){
    for(Milestone ms : catalogue.getMilestones()){
      if(ms.getUuid().equals(requirement.getMinimalMilestoneUUID() )){
        return ms;
      }
    }
    return null;
  }
  
  public Milestone getMaximalMilestone(Requirement requirement){
    for(Milestone ms : catalogue.getMilestones()){
      if(ms.getUuid().equals(requirement.getMaximalMilestoneUUID())){
        return ms;
      }
    }
    return null;
  }
}
