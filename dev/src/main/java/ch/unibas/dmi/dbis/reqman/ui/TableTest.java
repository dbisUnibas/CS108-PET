package ch.unibas.dmi.dbis.reqman.ui;/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TableTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    final ObservableList<MetaKeyValuePair> tableData = FXCollections.observableArrayList(
            new MetaKeyValuePair("key", "value"),
            new MetaKeyValuePair("category", "dump"),
            new MetaKeyValuePair("foo", "bar")
    );

    @Override
    public void start(Stage primaryStage) {
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane);


        TableView<MetaKeyValuePair> table = new TableView<>();
        table.setEditable(true);

        TableColumn<MetaKeyValuePair, String> firstCol = new TableColumn<>("Key");
        firstCol.setCellValueFactory(
                new PropertyValueFactory<>("key")
        );
        firstCol.setCellFactory(TextFieldTableCell.forTableColumn() );
        firstCol.setOnEditCommit((TableColumn.CellEditEvent<MetaKeyValuePair, String> t)->{
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setKey(t.getNewValue());
        });
        TableColumn<MetaKeyValuePair, String> secondCol = new TableColumn<>("Value");
        secondCol.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );
        secondCol.setCellFactory(TextFieldTableCell.forTableColumn());
        secondCol.setOnEditCommit((TableColumn.CellEditEvent<MetaKeyValuePair, String> t) ->{
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue(t.getNewValue());
        });

        table.setItems(tableData);
        table.getColumns().addAll(firstCol,secondCol);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pane.setCenter(table);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Testing TableView");
        primaryStage.show();
    }

    public static class MetaKeyValuePair{
        private final SimpleStringProperty key;

        public MetaKeyValuePair(String key, String value) {
            this.key = new SimpleStringProperty(key);
            this.value = new SimpleStringProperty(value);
        }

        public String getKey() {
            return key.get();
        }

        public SimpleStringProperty keyProperty() {
            return key;
        }

        public void setKey(String key) {
            this.key.set(key);
        }

        public String getValue() {
            return value.get();
        }

        public SimpleStringProperty valueProperty() {
            return value;
        }

        public void setValue(String value) {
            this.value.set(value);
        }

        private final SimpleStringProperty value;
    }


}
