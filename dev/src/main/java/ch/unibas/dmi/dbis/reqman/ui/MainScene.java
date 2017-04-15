package ch.unibas.dmi.dbis.reqman.ui;

import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.ui.common.TitledScene;
import ch.unibas.dmi.dbis.reqman.ui.editor.EditorHandler;
import ch.unibas.dmi.dbis.reqman.ui.editor.EditorView;
import ch.unibas.dmi.dbis.reqman.ui.evaluator.EvaluatorHandler;
import ch.unibas.dmi.dbis.reqman.ui.evaluator.EvaluatorView;
import javafx.scene.control.Label;
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

    private MainHandler mainHandler;

    private Mode active;

    boolean isEditorActive() {
        return active == Mode.EDITOR;
    }

    boolean isEvaluatorActive(){
        return active == Mode.EVALUATOR;
    }

    enum Mode{
        EDITOR,
        EVALUATOR
    }

    MainScene(){
        super(new Region(), 800,600);
        initComponents();
        layoutComponents();
        getRoot().getStylesheets().add("style.css");
    }

    void stop(){
        mainHandler.stop();
    }

    Mode getActiveMode(){
        return active;
    }

    private void initComponents(){
        root = new BorderPane();
        topContainer = new VBox();

        editorHandler = new EditorHandler();
        editor = new EditorView(editorHandler);
        evaluatorHandler = new EvaluatorHandler();
        evaluator = new EvaluatorView(evaluatorHandler);

        mainHandler = new MainHandler(evaluatorHandler,editorHandler);
        mainHandler.setMainScene(this);
        menuManager.setMenuHandler(mainHandler);

        setActive(Mode.EVALUATOR);
        menuManager.disableGroupNeeded();
    }

    private void layoutComponents(){
        setRoot(root);
        root.setTop(topContainer);
        topContainer.getChildren().add(menuManager.getMenuBar() );
        root.setBottom(new Label("Status (COMING SOON)"));
        mainHandler.checkGroupsPresent();
    }

    void setActive(Mode mode){
        switch (mode){
            case EDITOR:
                root.setCenter(editor);
                editor.refresh();
                menuManager.disableEvaluatorItems();
                break;
            case EVALUATOR:
                root.setCenter(evaluator);
                // Should reload catalogue also since may Reqs have changed
                menuManager.disableEditorItems();
                break;
        }
        active = mode;
    }


    @Override
    public String getTitle() {
        String mode = active.equals(Mode.EDITOR) ? "Editor" : "Evaluator";
        return String.format("ReqMan %s (%s)", mode, Version.getInstance().getVersion());
    }
}
