package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.PromptPopup;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorPromptFactory {

    private EditorPromptFactory(){
        // no public constructor
    }

    public static Requirement promptNewRequirement(){
        PromptPopup<Requirement> prompt = new PromptPopup<>(new RequirementPropertiesScene());
        return prompt.prompt();
    }

    public static Requirement promptRequirement(Requirement requirement){
        PromptPopup<Requirement> prompt = new PromptPopup<>(new RequirementPropertiesScene(requirement ));
        return prompt.prompt();
    }
}
