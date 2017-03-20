package ch.unibas.dmi.dbis.reqman.ui;/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import ch.unibas.dmi.dbis.reqman.ui.common.TitledScene;
import ch.unibas.dmi.dbis.reqman.ui.editor.EditorScene;
import ch.unibas.dmi.dbis.reqman.ui.evaluator.EvaluatorScene;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.stage.Stage;

public class ReqmanApplication extends Application {

    /**
     * Temporary
     */
    public static final int EDITOR_VIEW = 1000;
    /**
     * Temporary
     */
    public static final int EVALUATOR_VIEW = 2000;
    private Stage primaryStage;
    private EditorScene editor = new EditorScene(800, 600);
    private EvaluatorScene evaluator = new EvaluatorScene(800, 600);

    {
        editor.setOnChangeEvent(this::handleChangeView);
        evaluator.setOnChangeEvent(this::handleChangeView);
    }

    public static void main(String[] args) {
        Log4J2Fix.applyHotFix();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showScene(evaluator);

    }

    public void handleChangeView(ActionEvent event) {
        if (event instanceof ChangeEvent) {
            ChangeEvent evt = (ChangeEvent) event;
            changeView(evt.getView());
        }
    }

    private void changeView(int view) {
        switch (view) {
            case EDITOR_VIEW:
                showScene(editor);
                break;
            case EVALUATOR_VIEW:
                showScene(evaluator);
                break;
            default:
                // Do nothing, unknown view

        }
    }

    private void showScene(TitledScene scene) {
        primaryStage.setScene(scene);
        primaryStage.setTitle(scene.getTitle());
        primaryStage.show();
    }


    public static class ChangeEvent extends ActionEvent {
        private int view = -1;

        public ChangeEvent(ActionEvent source, int view) {
            super(source, source.getTarget());
            this.view = view;
        }

        @Override
        public EventType<ChangeEvent> getEventType() {
            return new EventType<>(ACTION, "CHANGE");
        }

        int getView() {
            return view;
        }
    }
}
