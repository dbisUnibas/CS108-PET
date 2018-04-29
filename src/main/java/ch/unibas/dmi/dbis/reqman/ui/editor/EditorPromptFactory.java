package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.PromptPopup;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
class EditorPromptFactory {
  
  private EditorPromptFactory() {
    // no public constructor
  }
  
  static Requirement promptNewRequirement(EditorHandler handler) {
    PromptPopup<Requirement> prompt = new PromptPopup<>(new RequirementPropertiesScene(handler));
    return prompt.prompt();
  }
  
  static Requirement promptRequirement(EditorHandler handler, Requirement requirement) {
    PromptPopup<Requirement> prompt = new PromptPopup<>(new RequirementPropertiesScene(handler, requirement));
    return prompt.prompt();
  }
  
  static Catalogue promptNewCatalogue() {
    PromptPopup<Catalogue> popup = new PromptPopup<>(new CataloguePropertiesScene());
    return popup.prompt();
  }
  
  static Catalogue promptCatalogue(Catalogue catalogue) {
    PromptPopup<Catalogue> popup = new PromptPopup<>(new CataloguePropertiesScene(catalogue));
    return popup.prompt();
  }
  
  static Milestone promptNewMilestone() {
    PromptPopup<Milestone> popup = new PromptPopup<>(new MilestonePropertiesScene());
    return popup.prompt();
  }
  
  static Milestone promptMilestone(Milestone milestone) {
    PromptPopup<Milestone> popup = new PromptPopup<>(new MilestonePropertiesScene(milestone));
    return popup.prompt();
  }
  
  static Course promptNewCourse() {
    PromptPopup<Course> popup = new PromptPopup<Course>(new CoursePropertiesScene());
    return popup.prompt();
  }
  
  static Course promptCourse(Course course) {
    PromptPopup<Course> popup = new PromptPopup<Course>(new CoursePropertiesScene(course));
    return popup.prompt();
  }
  
  static RequirementPropertiesScene.MetaKeyValuePair promptMetaKeyValuePair() {
    PromptPopup<RequirementPropertiesScene.MetaKeyValuePair> popup = new PromptPopup<>(new MetaKeyValuePairScene());
    return popup.prompt();
  }
}
