package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.scene.Scene;

/**
 * Wrapper for easier usage
 *
 * @author loris.sauter
 */
public abstract class AbstractVisualCreator<T> extends AbstractPopulatedGridScene implements Creator<T>{

    public abstract String getPromptTitle();
}
