package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
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

    private SplitPane horizontalSplitter = new SplitPane();
    private SplitPane verticalSplitter = new SplitPane();

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
        horizontalSplitter.prefWidthProperty().bind(widthProperty() );
        horizontalSplitter.prefHeightProperty().bind(heightProperty() );

        verticalSplitter.setOrientation(Orientation.VERTICAL);

        verticalSplitter.prefWidthProperty().bind(widthProperty());
        verticalSplitter.prefHeightProperty().bind(heightProperty() );


        Button bup = new Button("UP");
        Button bdown = new Button("DOWN");

        VBox upper = new VBox();
        upper.getChildren().add(bup);

        VBox lower = new VBox();
        lower.getChildren().add(bdown);

        Button bright = new Button("RIGHT");

        verticalSplitter.getItems().addAll(upper, lower);

        verticalSplitter.setDividerPositions(0.3);

        leftContent.getChildren().add(verticalSplitter);

        rightContent.getChildren().addAll(bright);

        horizontalSplitter.getItems().addAll(leftContent, rightContent);


        root.setTop(createMenuBar() );

        VBox box = new VBox();
        box.getChildren().add(horizontalSplitter);
        root.setCenter(box);

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
