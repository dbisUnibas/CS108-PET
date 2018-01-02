package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Member;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import ch.unibas.dmi.dbis.reqman.data.ProgressSummary;
import ch.unibas.dmi.dbis.reqman.ui.common.PromptPopup;

import java.util.function.Consumer;

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
  
  public static ProgressSummary promptSummary(Milestone ms, String groupName) {
    PromptPopup<ProgressSummary> popup = new PromptPopup<>(new ProgressSummaryScene(ms, groupName));
    return popup.prompt();
  }
  
  public static ProgressSummary promptSummary(Milestone ms, String groupName, ProgressSummary summary) {
    PromptPopup<ProgressSummary> popup = new PromptPopup<>(new ProgressSummaryScene(ms, groupName, summary));
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
  
  static void showSummary(Milestone ms, String groupName, Consumer<ProgressSummary> acceptor) {
    PromptPopup<ProgressSummary> popup = new PromptPopup<>(new ProgressSummaryScene(ms, groupName), acceptor);
    popup.showPrompt();
  }
  
  static void showSummary(Milestone ms, String groupName, Consumer<ProgressSummary> acceptor, ProgressSummary summary) {
    PromptPopup<ProgressSummary> popup = new PromptPopup<>(new ProgressSummaryScene(ms, groupName, summary), acceptor);
    popup.showPrompt();
  }
}
