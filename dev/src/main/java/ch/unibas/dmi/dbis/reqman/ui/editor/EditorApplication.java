package ch.unibas.dmi.dbis.reqman.ui.editor;


import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListController;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Editor");
        BorderPane wrapperPane = new BorderPane();
        Scene scene = new Scene(wrapperPane, 800, 600);

        // The menu bar is the menu-containing node
        MenuBar menuBar = new MenuBar();

        // Classic menus: file, edit, view
        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuView = new Menu("View");

        // TODO: Add MenuItem s

        // In order from left to right
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

        // Set the menubar to the top
        wrapperPane.setTop(menuBar);

        GridPane main = new GridPane();
        // Setting the main node.
        wrapperPane.setCenter(main);

        ListView<String> bottomList = new ListView<>();
        ObservableList<String> bottomItems = FXCollections.observableArrayList("Antoher", "List", "Item", "Definition", "Superlongnamethatgoeson");
        bottomList.setItems(bottomItems);

        ModifiableListView<String> modifiableListView = new ModifiableListView<>("TITLE", new ModifiableListController<String>() {
            private int counter = 0;
            @Override
            protected String createNew() {
                return "New element"+(counter++);
            }
        });
        BorderPane topSide = modifiableListView.getView();

        BorderPane bottomSide = createPrototype("BOTTOM", bottomList);

        // TODO Make width *relative* to total width: Use property
        topSide.setPrefWidth(scene.getWidth()/3.0);
        bottomSide.setPrefWidth(scene.getWidth()/3.0);

        main.add(topSide, 0,0);
        //GridPane.setHgrow(topSide, Priority.SOMETIMES);
        GridPane.setVgrow(topSide, Priority.SOMETIMES);
        GridPane.setFillWidth(topSide, true);
        main.add(bottomSide, 0,1);
        //GridPane.setHgrow(bottomSide, Priority.SOMETIMES);
        GridPane.setVgrow(bottomSide, Priority.SOMETIMES);
        GridPane.setFillWidth(bottomSide, true);

        main.add(new Label("Center"), 1,1,1,1);


        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("Effective. "+topSide.getWidth());
    }



    private BorderPane createPrototype(String title, Region content){
        BorderPane pane = new BorderPane(); // May replace with borderpane
        pane.setStyle("-fx-border-width: 1; -fx-border-color: gray");
        // TitleBar:
        AnchorPane titleBar = new AnchorPane();

        titleBar.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: gray;");
        // Buttons
        HBox buttons = new HBox();
        buttons.setPadding(new Insets(10, 10, 10, 10));
        buttons.setSpacing(10);
        Button add = new Button("+");
        Button remove = new Button("-");
        Font buttonFont = Font.font("sans-serif", FontWeight.EXTRA_BOLD, 12);
        add.setFont(buttonFont);
        remove.setFont(buttonFont);
        buttons.getChildren().addAll(add, remove);
        // Title
        Label titleText = new Label(title);
        titleText.setStyle("-fx-font-size: 12pt");

        titleBar.getChildren().addAll(titleText, buttons);
        AnchorPane.setLeftAnchor(titleText, 10.0);
        AnchorPane.setTopAnchor(titleText, 10.0);
        AnchorPane.setRightAnchor(buttons, 0.0);

        // Content
        pane.setTop(titleBar);
        pane.setCenter(content);

        return pane;
    }
}
