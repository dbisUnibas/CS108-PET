package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Class to represent a {@link ListView} which has a title and buttons to add / remove items.
 *
 * @author loris.sauter
 */
public class ModifiableListView<T> extends BorderPane{

    // TODO Allow custom styling
    // TODO derive from panel or similar.

    private ModifiableListController<T> controller;

    public ModifiableListView(String title, ModifiableListController<T> controller){
        super();
        createView(title, listView);
        this.controller = controller;
        listView.setItems(controller.getItems() );

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private ListView<T> listView = new ListView();

    public void setItems(ObservableList<T> items){
        listView.setItems(items);
    }

    public ObservableList<T> getItems(){
        return listView.getItems();
    }



    protected void createView(String title, Region content){

        // Border style
        this.setStyle("-fx-border-width: 1; -fx-border-color: silver");

        // TitleBar with Add / Remove Buttons
        AnchorPane titleBar = new AnchorPane();
        // TitleBar border style
        titleBar.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: silver;");
        // Button group
        HBox buttons = new HBox();
        buttons.setPadding(new Insets(10));
        buttons.setSpacing(10);

        Button buttonAdd = new Button("+");
        buttonAdd.setOnAction((event) -> {
            controller.onAdd(event);
        });
        Button buttonRemove = new Button("-");
        buttonRemove.setOnAction((event) -> {
            T selected = listView.getSelectionModel().getSelectedItem();
            System.out.println("Selected: "+selected);
            RemoveEvent<T> removeEvent = new RemoveEvent<T>(event, selected);
            controller.onRemove(removeEvent);
        });

        Font fontButton = Font.font("sans-serif", FontWeight.EXTRA_BOLD, 12);
        buttonAdd.setFont(fontButton);
        buttonRemove.setFont(fontButton);
        buttons.getChildren().addAll(buttonAdd, buttonRemove);

        // Title
        Label titleText = new Label(title);

        titleBar.getChildren().addAll(titleText, buttons);
        AnchorPane.setLeftAnchor(titleText, 10.0);
        AnchorPane.setTopAnchor(titleText, 10.0);
        AnchorPane.setRightAnchor(buttons, 0.0);

        // Content
        this.setTop(titleBar);
        this.setCenter(content);

    }


    public static class RemoveEvent<T> extends ActionEvent{
        private T selected;

        public static EventType<RemoveEvent> REMOVE = new EventType(ActionEvent.ACTION, "remove");

        public RemoveEvent(ActionEvent source, T selected){
            super(source.getSource(), source.getTarget());
            this.selected = selected;
        }

        @Override
        public EventType<? extends ActionEvent> getEventType() {
            return REMOVE;
        }

        public T getSelected() {
            return selected;
        }
    }

}
