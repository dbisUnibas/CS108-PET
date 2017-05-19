package ch.unibas.dmi.dbis.reqman.ui;

import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.ui.common.TitledScene;
import ch.unibas.dmi.dbis.reqman.ui.editor.EditorHandler;
import ch.unibas.dmi.dbis.reqman.ui.editor.EditorView;
import ch.unibas.dmi.dbis.reqman.ui.evaluator.EvaluatorHandler;
import ch.unibas.dmi.dbis.reqman.ui.evaluator.EvaluatorView;
import javafx.scene.Cursor;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MainScene extends TitledScene {

    private BorderPane root;

    private MenuManager menuManager = MenuManager.getInstance();

    private VBox topContainer;

    private EditorView editor;
    private EditorHandler editorHandler;
    private EvaluatorView evaluator;
    private EvaluatorHandler evaluatorHandler;
    private StatusBar statusBar;

    private MainHandler mainHandler;

    private Mode active;

    MainScene() {
        super(new Region(), 800, 600);
        initComponents();
        layoutComponents();
        getRoot().getStylesheets().add("style.css");
    }

    @Override
    public String getTitle() {
        String mode = active.equals(Mode.EDITOR) ? "Editor" : "Evaluator";
        return String.format("ReqMan %s (%s)", mode, Version.getInstance().getVersion());
    }

    boolean isEditorActive() {
        return active == Mode.EDITOR;
    }

    boolean isEvaluatorActive() {
        return active == Mode.EVALUATOR;
    }

    void stop() {
        mainHandler.stop();
    }

    Mode getActiveMode() {
        return active;
    }

    void setActive(Mode mode) {
        switch (mode) {
            case EDITOR:
                root.setCenter(editor);
                editor.refresh();
                menuManager.disableEvaluatorItems();
                menuManager.enableEditorItems();
                break;
            case EVALUATOR:
                root.setCenter(evaluator);
                // Should reload catalogue also since may Reqs have changed
                menuManager.disableEditorItems();
                menuManager.enableEvaluaotrItems();
                break;
        }
        active = mode;
    }

    void indicateWaiting(boolean waiting) {
        getRoot().setCursor(waiting ? Cursor.WAIT : Cursor.DEFAULT);
    }

    private void initComponents() {
        root = new BorderPane();
        topContainer = new VBox();

        editorHandler = new EditorHandler();
        editor = new EditorView(editorHandler);
        evaluatorHandler = new EvaluatorHandler();
        evaluator = new EvaluatorView(evaluatorHandler);

        statusBar = new StatusBar();

        mainHandler = new MainHandler(evaluatorHandler, editorHandler);
        mainHandler.setMainScene(this);
        mainHandler.setStatusBar(statusBar);

        menuManager.setMenuHandler(mainHandler);


        setActive(Mode.EVALUATOR);
        menuManager.disableGroupNeeded();
    }

    private void layoutComponents() {
        setRoot(root);
        root.setTop(topContainer);
        topContainer.getChildren().add(menuManager.getMenuBar());
        root.setBottom(statusBar);
        mainHandler.checkGroupsPresent();
    }

    enum Mode {
        EDITOR,
        EVALUATOR
    }
}
