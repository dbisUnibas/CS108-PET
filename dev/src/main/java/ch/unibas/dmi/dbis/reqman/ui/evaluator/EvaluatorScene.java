package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The class {@link EvaluatorScene} is the class which is directly the scene of the assessment mode.
 *
 * It initializes the components and lets its root lay them out.
 *
 * @author loris.sauter
 */
public class EvaluatorScene extends Scene{

    private BorderPane root;

    private SplitPane horizontalSplitter;
    private HBox leftContent = new HBox();
    private HBox rightContent = new HBox();

    private String title = "Evaluator";

    public EvaluatorScene(String title, int width, int height){
        this(width, height);
        this.title = title;
    }

    public EvaluatorScene(int width, int height){
        super(new BorderPane(), width, height);
        root = (BorderPane) getRoot();
        initComponents();
    }

    private void initComponents(){
        horizontalSplitter = new SplitPane();
        horizontalSplitter.prefWidthProperty().bind(widthProperty() );
        horizontalSplitter.prefHeightProperty().bind(heightProperty() );

        Button bleft = new Button("LEFT");
        Button bright = new Button("RIGHT");

        HBox hbox = new HBox();
        hbox.getChildren().addAll(bleft);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(bright);

        leftContent.getChildren().addAll(hbox);
        rightContent.getChildren().addAll(vbox);

        horizontalSplitter.getItems().addAll(leftContent, rightContent);

        root.setTop(createMenuBar() );
        root.setCenter(horizontalSplitter);

        horizontalSplitter.setDividerPositions(0.3);
    }

    private MenuBar createMenuBar(){
        MenuBar bar = new MenuBar();

        Menu file = new Menu("File");
        MenuItem itemLoad = new MenuItem("Load");

        file.getItems().addAll(itemLoad);


        bar.getMenus().addAll(file);
        return bar;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
