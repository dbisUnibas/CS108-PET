package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.event.ActionEvent;
import javafx.scene.Scene;

/**
 * Wrapper for easier usage
 *
 * @author loris.sauter
 */
public abstract class AbstractVisualCreator<T> extends AbstractPopulatedGridScene implements Creator<T>{

    public abstract String getPromptTitle();

    public abstract void handleSaving(ActionEvent event);
}
