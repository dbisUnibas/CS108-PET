package ch.unibas.dmi.dbis.reqman.ui.common;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class PromptPopup<T> {

    protected AbstractVisualCreator<T> creator;
    protected PopupStage stage;

    public PromptPopup(AbstractVisualCreator creator){
        this.creator = creator;
        stage = new PopupStage(creator.getPromptTitle(), creator);
    }

    /**
     * Returns a new instance which was created by the Creator.
     * If the user did cancel the creation process null will be returned, so check for this event.
     * @return A newly created instance of T or null, if the user did cancel the creation.
     */
    public T prompt(){
        stage.showAndWait();
        if(creator.isCreatorReady() ){
            return creator.create();
        }else{
            return null;
        }
    }
}
