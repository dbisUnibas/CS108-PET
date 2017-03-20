package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class PromptPopup<T> {

    protected AbstractVisualCreator<T> creator;
    protected PopupStage stage;

    public PromptPopup(AbstractVisualCreator creator) {
        this.creator = creator;
        stage = new PopupStage(creator.getPromptTitle(), creator);

        //creator.getRoot().setOnKeyReleased(this::handleKeyEvent);

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

    private void handleKeyEvent(KeyEvent event) {
        // TODO Move to abstract visual creator and filter
        if (KeyCode.ESCAPE.equals(event.getCode())) {
            stage.hide();
        } else if (KeyCode.ENTER.equals(event.getCode())) {
            System.out.println("Source: " + event.getSource().toString());
            if (event.getTarget() == this) {
                creator.handleSaving(new ActionEvent(event.getSource(), event.getTarget())); // Create own event?
            }

        }
    }
}
