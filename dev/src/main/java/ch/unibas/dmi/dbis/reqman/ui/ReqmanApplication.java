package ch.unibas.dmi.dbis.reqman.ui;/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import ch.unibas.dmi.dbis.reqman.common.LoggingUtils;
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
    private static Logger LOGGER;
    private static Version version;
    private Stage primaryStage;
    private EditorScene editor;
    private EvaluatorScene evaluator;
    private int currentView = -1;

    private static volatile boolean exp = false;

    public static void main(String[] args) {
        Log4J2Fix.applyHotFix();
        version = Version.getInstance();
        LOGGER = LogManager.getLogger(ReqmanApplication.class);
        LOGGER.info(LoggingUtils.REQMAN_MARKER, "Starting reqman @ v" + version.getFullVersion());
        if(args.length >= 1){
            if("--exp".equals(args[0]) || "--experimental".equals(args[0] ) ){
                exp = true;
            }
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if(exp){
            startExp(primaryStage);
        }else{
            startOld(primaryStage);
        }
    }
    MainScene scene;
    private void startExp(Stage primaryStage) {
        scene = new MainScene();
        primaryStage.setScene(scene );
        primaryStage.setTitle(scene.getTitle());
        primaryStage.show();
    }

    public void startOld(Stage primaryStage) {
        editor = new EditorScene(primaryStage, 800, 600);
        evaluator = new EvaluatorScene(primaryStage, 800, 600);

        editor.setOnChangeEvent(this::handleChangeView);
        evaluator.setOnChangeEvent(this::handleChangeView);

        this.primaryStage = primaryStage;
        showScene(evaluator);
        currentView = EVALUATOR_VIEW;
    }

    public void handleChangeView(ActionEvent event) {
        if (event instanceof ChangeEvent) {
            ChangeEvent evt = (ChangeEvent) event;
            changeView(evt);
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

    private void changeView(ChangeEvent evt) {
        switch (evt.getView()) {
            case EDITOR_VIEW:
                showScene(editor, evt);
                break;
            case EVALUATOR_VIEW:

                showScene(evaluator, evt);
                break;
            default:
                // DO nothing
        }
        currentView = evt.getView();
    }


    private void showScene(TitledScene scene) {
        primaryStage.setScene(scene);
        primaryStage.setTitle(setupTitle(scene.getTitle()));
        primaryStage.show();
    }

    private void showScene(TitledScene scene, ChangeEvent evt) {

        if (evt != null) {
            primaryStage.setWidth(evt.getWidth());
            primaryStage.setHeight(evt.getHeight());
            primaryStage.setMaximized(evt.isMaximized());
        }
        primaryStage.setScene(scene);
        primaryStage.setTitle(scene.getTitle());
        primaryStage.show();
    }

    private String setupTitle(String title){
        return "ReqMan: "+title + " ("+version.getVersion()+")";
    }

    @Override
    public void stop() {
        if(scene != null){
            scene.stop();
        }else if(evaluator != null){
            if (currentView == EVALUATOR_VIEW) {
                evaluator.stop();
            }
        }

    }


    public static class ChangeEvent extends ActionEvent {
        private int view = -1;
        private double width = -1;
        private double height = -1;
        private boolean maximized = false;

        @Deprecated // Window size is passed by in newer versions of reqman.
        public ChangeEvent(ActionEvent source, int view) {
            super(source, source.getTarget());
            this.view = view;
        }

        public ChangeEvent(ActionEvent source, int view, double width, double height, boolean maximized) {
            super(source, source.getTarget());
            this.view = view;
            this.width = width;
            this.height = height;
            this.maximized = maximized;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public boolean isMaximized() {
            return maximized;
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
