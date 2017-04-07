package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Abstract controller for the {@link ModifiableListView}
 * <p>
 * Handles the adding and removing of elements.
 *
 * @author loris.sauter
 */
public abstract class ModifiableListController<T> implements ModifiableListHandler<T> {

    private ObservableList<T> items = FXCollections.observableList(new ArrayList<T>());

    public ModifiableListController() {

    }

    public ObservableList<T> getItems() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items = items;
    }

    public void addItem(T item) {
        items.add(item);
    }

    public void removeItem(T item) {
        items.remove(item);
    }

    public void addAll(T... items) {
        this.items.addAll(items);
    }

    @Override
    public void onRemove(ModifiableListView.RemoveEvent<T> event) {
        T toRemove = event.getSelected();
        if (toRemove == null) {
            return; // Cannot remove a non selected item.
        }
        int index = items.indexOf(toRemove);
        items.remove(index);
    }

    @Override
    public void onAdd(ModifiableListView.AddEvent<T> event) {
        items.add(createNew());
    }


    protected abstract T createNew();

}
