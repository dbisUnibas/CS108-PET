package ch.unibas.dmi.dbis.reqman.ui;/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.ui.common.TitledScene;
import ch.unibas.dmi.dbis.reqman.ui.editor.EditorScene;
import ch.unibas.dmi.dbis.reqman.ui.evaluator.EvaluatorScene;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static Logger LOGGER;
    private static Version version;

    private int currentView = -1;

    {
        editor.setOnChangeEvent(this::handleChangeView);
        evaluator.setOnChangeEvent(this::handleChangeView);
    }

    public static void main(String[] args) {
        Log4J2Fix.applyHotFix();
        version = Version.getInstance();
        LOGGER = LogManager.getLogger(ReqmanApplication.class);
        LOGGER.info("Starting reqman @ v"+ version.getFullVersion() );
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showScene(evaluator);
        currentView = EVALUATOR_VIEW;
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
        currentView = view;
    }

    private void showScene(TitledScene scene) {
        primaryStage.setScene(scene);
        primaryStage.setTitle(scene.getTitle());
        primaryStage.show();
    }

    @Override
    public void stop(){
        if(currentView == EVALUATOR_VIEW ){
            evaluator.stop();
        }
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
