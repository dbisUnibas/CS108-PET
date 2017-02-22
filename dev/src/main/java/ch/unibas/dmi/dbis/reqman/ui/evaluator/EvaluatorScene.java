package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
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

    private HBox root;

    private SplitPane horizontalSplitter;
    private HBox leftContent = new HBox();
    private HBox rightContent = new HBox();

    public EvaluatorScene(int width, int height){
        super(new HBox(), width, height);
        root = (HBox) getRoot();

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

        root.getChildren().add(horizontalSplitter);
        horizontalSplitter.setDividerPositions(0.3);
    }

}
