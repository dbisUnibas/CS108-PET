package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This is a PopupStage witch could be used as a pseudo dialog.
 * It has no buttons which handle closing, so the passed {@link Scene} must implement the handling of that.
 * <p>
 * Anything that wants to display such a {@link PopupStage} must call {@link PopupStage#showAndWait()} to show it
 * and the passed {@link Scene} must invoke {@link PopupStage#hide()} to close it again. If said {@code scene} is not
 * attached to another {@code stage}, it could easily be achieved by invoking {@code scene.getWindow().hide()}
 *
 * @author loris.sauter
 */
public class PopupStage extends Stage {

    protected String title;

    protected Scene content;

    public PopupStage() {
        this("N/A", null, true);
    }

    public PopupStage(String title, Scene content, boolean modality) {
        this.content = content;
        this.title = title;

        Utils.applyLogoIcon(this);
        initComponents(modality);
    }

    public PopupStage(String title, Scene content) {
        this(title, content, true);
    }

    protected void initComponents(boolean modal) {
        if (content != null) {
            setScene(content);
        }
        if (title != null) {
            setTitle(title);
        }
        if (modal) {
            initModality(Modality.APPLICATION_MODAL);
        }

    }
}
