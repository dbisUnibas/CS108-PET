package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Member;
import ch.unibas.dmi.dbis.reqman.data.ProgressSummary;
import ch.unibas.dmi.dbis.reqman.ui.common.PromptPopup;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorPromptFactory {
  
  private EvaluatorPromptFactory() {
    // no objects
  }
  
  public static Member promptMember() {
    PromptPopup<Member> popup = new PromptPopup<>(new MemberScene());
    return popup.prompt();
  }
  
  public static Group promptSplit() {
    PromptPopup<Group> popup = new PromptPopup<>(new SplitGroupScene());
    return popup.prompt();
  }
  
  static Group promptGroup() {
    PromptPopup<Group> popup = new PromptPopup<>(new GroupPropertiesScene());
    return popup.prompt();
  }
  
  static Group promptGroup(Group group) {
    PromptPopup<Group> popup = new PromptPopup<>(new GroupPropertiesScene(group));
    return popup.prompt();
  }
  
  
  public static void showProgressSummary(Group group, ProgressSummary selectedItem) {
    PromptPopup<ProgressSummary> popup = new PromptPopup<>(new ProgressSummaryScene(group, selectedItem));
    popup.showPrompt();
  }
}
