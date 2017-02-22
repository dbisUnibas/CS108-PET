package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Progress;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
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
    private VBox rightContent = new VBox();

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


        Button bdown = new Button("DOWN");

        VBox upper = new VBox();
        upper.getChildren().add(new CatalogueInfoView(null));

        VBox lower = new VBox();
        lower.getChildren().add(bdown);

        verticalSplitter.getItems().addAll(upper, lower);

        verticalSplitter.setDividerPositions(0.3);

        leftContent.getChildren().add(verticalSplitter);

        // TEST CODE
        Requirement binary = new Requirement("Requirement","Description", 0,0,10,true,true, false);
        Requirement partial = new Requirement("Requierement","Description", 0,0,5,false,true,false);

        ProgressView pv1 = new ProgressView(null, binary);

        ProgressView pv2 = new ProgressView(null, partial);

        rightContent.setStyle("-fx-padding: 10px;-fx-spacing: 10px;");
        rightContent.getChildren().addAll(pv1, pv2);

        horizontalSplitter.getItems().addAll(leftContent, rightContent);


        root.setTop(createMenuBar() );

        VBox box = new VBox();
        box.getChildren().add(horizontalSplitter);
        root.setCenter(box);

        horizontalSplitter.setDividerPositions(0.3);
    }

    private MenuBar createMenuBar(){
        MenuBar bar = new MenuBar();

        Menu menuFile = new Menu("File");

        MenuItem itemLoad = new MenuItem("Load Catalogue");
        MenuItem itemNew = new MenuItem("New Group");
        MenuItem itemOpen = new MenuItem("Open Group");
        MenuItem itemSave = new MenuItem("Save Group");
        MenuItem itemSaveAs = new MenuItem("Save Group As");
        MenuItem itemExit = new MenuItem("Quit");


        menuFile.getItems().addAll(itemLoad, itemNew, itemOpen, itemSave, itemSaveAs, new SeparatorMenuItem(), itemExit);

        Menu menuEdit = new Menu("Edit");
        MenuItem itemModify = new MenuItem("Modify Group");

        menuEdit.getItems().addAll(itemModify);

        Menu menuView = new Menu("View");

        Menu menuHelp = new Menu("Help");



        bar.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);
        return bar;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
