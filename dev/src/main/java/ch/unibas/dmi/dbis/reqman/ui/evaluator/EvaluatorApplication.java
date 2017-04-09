package ch.unibas.dmi.dbis.reqman.ui.evaluator;


import ch.unibas.dmi.dbis.reqman.common.Log4J2Fix;
import ch.unibas.dmi.dbis.reqman.common.Version;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorApplication extends Application {

    private EvaluatorScene scene;

    private EvaluatorView view;
    private static volatile boolean exp = false;

    public static void main(String[] args) {
        Log4J2Fix.applyHotFix();

        if(args.length >= 1){
            if("--exp".equals(args[0]) || "--experimental".equals(args[0]) ){
                exp = true;
            }
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if(exp){
            startExperimental(primaryStage);
        }else {
            startClassic(primaryStage);
        }
    }

    private void startExperimental(Stage primaryStage) {
        EvaluatorHandler handler = new EvaluatorHandler();
        view = new EvaluatorView(handler);
        // TEMP
        BorderPane root = new BorderPane();
        root.setCenter(view);
        root.setTop(createExpMenu(handler));
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("ReqMan: " + view.getTitle()+ " ("+ Version.getInstance().getVersion()+"-EXPERIMENTAL)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Node createExpMenu(EvaluatorHandler handler) {
        MenuBar bar = new MenuBar();
        Menu menu = new Menu("Exp");
        MenuItem miOpenCat = new MenuItem("Open Cat");
        miOpenCat.setOnAction(handler::handleOpenCatalogue);
        MenuItem miOpenGr = new MenuItem("Open gr");
        miOpenGr.setOnAction(handler::handleOpenGroups);
        MenuItem miSaveGr = new MenuItem("Save gr");
        miSaveGr.setOnAction(handler::handleSaveGroup);

        menu.getItems().addAll(miOpenCat,miOpenGr,miSaveGr);

        bar.getMenus().add(menu);
        return bar;
    }

    private void startClassic(Stage primaryStage){
        scene = new EvaluatorScene(primaryStage, 800, 600);
        primaryStage.setTitle(scene.getTitle());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop(){
        scene.stop();
    }
}
