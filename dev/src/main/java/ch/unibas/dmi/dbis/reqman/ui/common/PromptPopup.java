package ch.unibas.dmi.dbis.reqman.ui.common;

import java.util.function.Consumer;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class PromptPopup<T> {

    protected AbstractVisualCreator<T> creator;
    protected PopupStage stage;


    private Consumer<T> consumer = null;

    private PromptPopup(AbstractVisualCreator creator, boolean modality) {
        this.creator = creator;
        stage = new PopupStage(creator.getPromptTitle(), creator, modality);
        stage.setOnHiding(evt -> {
            if (consumer != null) {
                if (getCreation() != null) {
                    consumer.accept(getCreation());
                }
            }
        });
    }

<<<<<<< HEAD
    public PromptPopup(AbstractVisualCreator creator){
        this(creator, true);
    }

    public PromptPopup(AbstractVisualCreator creator, Consumer consumer){
        this(creator, false);
=======
    public PromptPopup(AbstractVisualCreator creator, Consumer consumer) {
        this(creator);
>>>>>>> ae4f5057d1de49a374e94ef0fec6678b21e0f3d0
        this.consumer = consumer;


    }

    /**
     * Returns a new instance which was created by the Creator.
     * If the user did cancel the creation process null will be returned, so check for this event.
     *
     * @return A newly created instance of T or null, if the user did cancel the creation.
     */
    public T prompt() {
        stage.showAndWait();
        return getCreation();
    }

    public void showPrompt() {
        stage.show();
    }

    /**
     * Returns a new instance of which was created by the Creator.
     *
     * @return the created instance OR null, if the user canceled the prompt.
     */
    public T getCreation() {
        if (creator.isCreatorReady()) {
            return creator.create();
        } else {
            return null;
        }
    }

}
