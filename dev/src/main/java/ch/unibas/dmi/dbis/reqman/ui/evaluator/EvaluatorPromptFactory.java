package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.ProgressSummary;
import ch.unibas.dmi.dbis.reqman.ui.common.PromptPopup;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorPromptFactory {

    private EvaluatorPromptFactory(){
        // no objects
    }

    public static GroupPropertiesScene.Member promptMember(){
        PromptPopup<GroupPropertiesScene.Member> popup = new PromptPopup<>(new MemberScene() );
        return popup.prompt();
    }

    public static Group promptNewGroup(String catalogueName){
        PromptPopup<Group> popup = new PromptPopup<>(new GroupPropertiesScene(catalogueName));
        return popup.prompt();
    }

    public static ProgressSummary promptSummary(Milestone ms, String groupName){
        PromptPopup<ProgressSummary> popup = new PromptPopup<>(new ProgressSummaryScene(ms, groupName));
        return popup.prompt();
    }
}
