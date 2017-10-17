package ch.unibas.dmi.dbis.reqman.data;

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
    if(course == null || catalogue == null){
      throw new IllegalArgumentException("Cannot create CourseManager if course or catalogue is null");
    }
    if(! course.getCatalogueUUID().equals(catalogue.getUuid()) ){
      throw new IllegalArgumentException("Nonmatching course/catalogue pair. Expected catalogue "+course.getCatalogueUUID()+", but "+catalogue.getUuid()+" given");
    }
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
