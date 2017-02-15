package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
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

    public static Requirement promptNewRequirement(EditorController controller){
        PromptPopup<Requirement> prompt = new PromptPopup<>(new RequirementPropertiesScene(controller));
        return prompt.prompt();
    }

    public static Requirement promptRequirement(EditorController controller, Requirement requirement){
        PromptPopup<Requirement> prompt = new PromptPopup<>(new RequirementPropertiesScene(controller, requirement ));
        return prompt.prompt();
    }

    public static Catalogue promptNewCatalogue(){
        PromptPopup<Catalogue> popup = new PromptPopup<>(new CataloguePropertiesScene() );
        return popup.prompt();
    }

    public static Catalogue promptCatalogue(Catalogue catalogue){
        PromptPopup<Catalogue> popup = new PromptPopup<>(new CataloguePropertiesScene(catalogue));
        return popup.prompt();
    }

    public static Milestone promptNewMilestone(){
        PromptPopup<Milestone> popup = new PromptPopup<>(new MilestonePropertiesScene() );
        return popup.prompt();
    }

    public static Milestone promptMilestone(Milestone milestone){
        PromptPopup<Milestone> popup = new PromptPopup<>(new MilestonePropertiesScene(milestone));
        return popup.prompt();
    }
}
