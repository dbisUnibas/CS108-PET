package ch.unibas.dmi.dbis.reqman.ui;

import javafx.application.Application;
<<<<<<< HEAD:dev/src/main/java/ch/unibas/dmi/dbis/reqman/ui/SandboxUI.java
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
=======
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
>>>>>>> 5800484e2b667283e9d85dd30238f8175c3fcf4a:dev/src/main/java/ch/unibas/dmi/dbis/reqman/ui/GraphicalUI.java
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

<<<<<<< HEAD:dev/src/main/java/ch/unibas/dmi/dbis/reqman/ui/SandboxUI.java
public class SandboxUI extends Application {
=======
/**
 * A sandbox
 */
public class GraphicalUI extends Application {
>>>>>>> 5800484e2b667283e9d85dd30238f8175c3fcf4a:dev/src/main/java/ch/unibas/dmi/dbis/reqman/ui/GraphicalUI.java

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
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1,1);
        Label info = new Label("Bounds: ");
        grid.add(info, 0, 2);
        TextField infoBounds = new TextField();
        infoBounds.setEditable(false);
        Screen primary = Screen.getPrimary();
        infoBounds.setText(primary.getVisualBounds().toString() );
        grid.add(infoBounds, 1, 2);
        Button button = new Button("Exit");
        grid.add(button, 1, 3);
        button.setOnAction((event) -> {
            System.exit(0);
        });



        primaryStage.show();
    }

    private Scene createRequirementPropertiesScene(){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Scene scene = new Scene(grid);

        Label lblName = new Label("Name");
        Label lblDesc = new Label("Description");
        Label lblMinMS = new Label("Minimal Milestone");
        Label lblMaxMS = new Label("Maximal Milestone");
        Label lblMaxPoints = new Label("Maximal Points");
        Label lblBinary = new Label("Binary");
        Label lblMandatory = new Label("Mandaotry");
        Label lblMalus = new Label("Malus");
        Label lblPredecessors = new Label("Predecessors");
        Label lblProps = new Label("Properties");

        TextField tfName = new TextField();
        TextArea taDesc = new TextArea();

        ChoiceBox<String> cbMinMS = new ChoiceBox<>(); // May be ComboBox
        ObservableList<String> list = FXCollections.observableArrayList("Create new...", "Milestone1", "Milestone2");
        cbMinMS.setItems(list);
        ChoiceBox<String> cbMaxMS = new ChoiceBox<>();
        cbMaxMS.setItems(list); // NOTE: This is intentionally the same list

        TextField tfPoints = new TextField();


        Button saveButton = new Button("Save");
        Button cancleButton = new Button("Cancle");

        grid.add(lblName, 0,0);
        grid.add(tfName, 1,0);
        grid.add(lblDesc, 0,1,0,1);
        // Skip one row
        

        return scene;
    }
}
