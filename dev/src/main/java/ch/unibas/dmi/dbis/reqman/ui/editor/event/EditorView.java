package ch.unibas.dmi.dbis.reqman.ui.editor.event;

import ch.unibas.dmi.dbis.reqman.ui.common.TitleProvider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorView extends HBox implements TitleProvider{


    private SplitPane horizontalSplitter;
    

    private String title = "Editor";

    @Override
    public String getTitle() {
        return title;
    }
}
