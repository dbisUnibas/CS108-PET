package ch.unibas.dmi.dbis.reqman.ui;
/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GraphicalUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Requirement Manager");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        // TODO: Reslution and stuff
        Scene scene = new Scene(grid, 800, 600);

        primaryStage.setScene(scene);
        Text scenetitle = new Text("Test");
        grid.add(scenetitle, 0,0,2,1);
        Label userName = new Label("User Name: ");
        TextField userTextField = new TextField();
        grid.add(userTextField, 1,1);


        primaryStage.show();
    }
}
