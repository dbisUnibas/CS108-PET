package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.ProgressSummary;
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

    public static GroupPropertiesScene.Member promptMember() {
        PromptPopup<GroupPropertiesScene.Member> popup = new PromptPopup<>(new MemberScene());
        return popup.prompt();
    }

    public static Group promptNewGroup(EvaluatorController controller) {
        PromptPopup<Group> popup = new PromptPopup<>(new GroupPropertiesScene(controller));
        return popup.prompt();
    }

    public static Group promptGroup(Group group, EvaluatorController controller) {
        PromptPopup<Group> popup = new PromptPopup<>(new GroupPropertiesScene(controller, group));
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


    static void showSummary(Milestone ms, String groupName, Consumer<ProgressSummary> acceptor) {
        PromptPopup<ProgressSummary> popup = new PromptPopup<>(new ProgressSummaryScene(ms, groupName), acceptor);
        popup.showPrompt();
    }

    static void showSummary(Milestone ms, String groupName, Consumer<ProgressSummary> acceptor, ProgressSummary summary) {
        PromptPopup<ProgressSummary> popup = new PromptPopup<>(new ProgressSummaryScene(ms, groupName, summary), acceptor);
        popup.showPrompt();
    }

}
