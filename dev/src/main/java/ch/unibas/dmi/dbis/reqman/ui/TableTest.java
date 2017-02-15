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

    @Override
    public void start(Stage primaryStage) {
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane);

        ObservableList<KeyValuePropertyPair> tableData = FXCollections.observableArrayList();
        tableData.add(new KeyValuePropertyPair("key", "value"));

        TableView table = new TableView<>();
        table.setEditable(true);

        TableColumn keyCol = new TableColumn("Key");
        keyCol.setCellValueFactory(new PropertyValueFactory<>("key"));
        keyCol.setCellFactory(TextFieldTableCell.<KeyValuePropertyPair>forTableColumn() );

        TableColumn valCol = new TableColumn("Value");
        valCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        table.setItems(tableData);
        table.getColumns().addAll(keyCol, valCol);

        pane.setCenter(table);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Testing TableView");
        primaryStage.show();
    }

    public static class KeyValuePropertyPair {
        private SimpleStringProperty key;
        private SimpleStringProperty value;

        public KeyValuePropertyPair(String key, String value) {
            this.key = new SimpleStringProperty(key);
            this.value = new SimpleStringProperty(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            KeyValuePropertyPair that = (KeyValuePropertyPair) o;

            if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) {
                return false;
            }
            return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
        }

        @Override
        public int hashCode() {
            int result = getKey() != null ? getKey().hashCode() : 0;
            result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
            return result;
        }

        public KeyValuePropertyPair() {
        }

        public String getKey() {
            return key.get();
        }

        public void setKey(String key) {
            this.key.set(key);
        }

        public String getValue() {
            return value.get();
        }

        public void setValue(String value) {
            this.value.set(value);
        }
    }


}
