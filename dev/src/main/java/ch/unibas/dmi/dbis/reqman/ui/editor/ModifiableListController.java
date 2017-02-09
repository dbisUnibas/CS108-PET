package ch.unibas.dmi.dbis.reqman.ui.editor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.util.ArrayList;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ModifiableListController<T> {

    private ObservableList<T> items = FXCollections.observableList(new ArrayList<T>() );

    public ModifiableListController(){

    }

    public ObservableList<T> getItems(){
        return items;
    }

    public void setItems(ObservableList<T> items){
        this.items = items;
    }

    public void addItem(T item){
        items.add(item);
    }

    public void removeItem(T item){
        items.remove(item);
    }

    public void addAll(T... items){
        this.items.addAll(items);
    }

    public void onRemove(ModifiableListView.RemoveEvent<T> event){
        System.out.println("REMOVE");
    }

    public void onAdd(ActionEvent event){
        System.out.println("ADD");
    }
}
