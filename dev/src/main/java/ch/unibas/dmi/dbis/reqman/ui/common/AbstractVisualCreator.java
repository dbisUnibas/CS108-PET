package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.event.ActionEvent;

/**
 * Wrapper for easier usage
 *
 * @author loris.sauter
 */
public abstract class AbstractVisualCreator<T> extends AbstractPopulatedGridScene implements Creator<T> {

    protected SaveCancelPane buttons = new SaveCancelPane();

    public AbstractVisualCreator() {
        super();
        setupButtonHandling();
    }

    public abstract String getPromptTitle();

    public abstract void handleSaving(ActionEvent event);



    /**
     * Dismisses the visual creator: It hides the parental window und thus cancels the creation.
     */
    public void dismiss() {
        getWindow().hide();
    }

    protected void setupButtonHandling() {
        buttons.setOnSave(this::handleSaving);
        buttons.setOnCancel(event -> dismiss());
    }
}
