package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Wrapper for easier usage
 *
 * @author loris.sauter
 */
public abstract class AbstractVisualCreator<T> extends AbstractPopulatedGridScene implements Creator<T>{

    public AbstractVisualCreator(){
        super();
    }

    public abstract String getPromptTitle();

    public abstract void handleSaving(ActionEvent event);

}
