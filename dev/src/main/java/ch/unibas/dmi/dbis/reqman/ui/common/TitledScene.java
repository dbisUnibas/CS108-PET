package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public abstract class TitledScene extends Scene implements TitleProvider {

    public TitledScene(Parent root, int width, int height){
        super(root, width, height);
    }
}
