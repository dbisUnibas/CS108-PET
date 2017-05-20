package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

/**
 * Represents a component which has a save and cancel button.
 * <p>
 * The buttons are laid out to be right-aligned. To handle the {@link javafx.event.ActionEvent} fired
 * by the buttons, add a {@link SaveCancelHandler} to an instance of this class.
 * <p>
 * The class is derived from {@link AnchorPane} to directly add it to its parent(s). Although technical possible
 * it is not recommended to add other children to this pane.
 *
 * @author loris.sauter
 */
public class SaveCancelPane extends AnchorPane {

    /**
     * The save button instance
     */
    protected Button saveButton = new Button("Save");
    /**
     * The cancel button instance
     */
    protected Button cancelButton = new Button("Cancel");

    private ArrayList<SaveCancelHandler> handlers = new ArrayList<>();

    private EventHandler<ActionEvent> saveHandler = null;
    private EventHandler<ActionEvent> cancelHandler = null;

    /**
     * Creates a new {@link SaveCancelPane} with its components laid out.
     */
    public SaveCancelPane() {
        super();
        initComponents();
    }

    /**
     * Registers a {@link SaveCancelHandler} to the buttons of this instance.
     * May more than one handler is registered, they will be notified respecting FIFO principle.
     *
     * @param handler The handler which handles the events fired by the buttons
     */
    public void addSaveCancelHandler(SaveCancelHandler handler) {
        handlers.add(handler);
    }

    /**
     * Removes a {@link SaveCancelHandler} from the list of registered handlers.
     *
     * @param handler The handler to remove.
     */
    public void removeSaveCancelHandler(SaveCancelHandler handler) {
        handlers.remove(handler);
    }

    /**
     * Sets the handler for the save button.
     * <p>
     * This replaces any pre-registered {@link SaveCancelHandler}s.
     *
     * @param handler The handler to handle the save {@link ActionEvent}
     */
    public void setOnSave(EventHandler<ActionEvent> handler) {
        this.saveHandler = handler;
    }

    /**
     * Sets the handler for the cancel button.
     * <p>
     * This replaces any pre-registered {@link SaveCancelHandler}s.
     *
     * @param handler The handler to handle the cancel {@link ActionEvent}
     */
    public void setOnCancel(EventHandler<ActionEvent> handler) {
        this.cancelHandler = handler;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    /**
     * Initializes the components.
     */
    protected void initComponents() {
        // Declare roles
        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);

        // Init behavior
        saveButton.setOnAction(event -> {
            handleSave(event);
            event.consume();
        });
        cancelButton.setOnAction(event -> {
            handleCancel(event);
            event.consume();
        });

        // Init layout of components
        HBox btnBox = new HBox();
        btnBox.getChildren().addAll(saveButton, cancelButton);
        getChildren().add(btnBox);
        setRightAnchor(btnBox, 0d);

        btnBox.setStyle("-fx-spacing: inherit;-fx-padding: inherit");
    }

    private void handleSave(ActionEvent event) {
        if (saveHandler != null) {
            saveHandler.handle(event);
        } else {
            handlers.forEach(handler -> {
                handler.save(event);
            });
        }
    }

    private void handleCancel(ActionEvent event) {
        if (cancelHandler != null) {
            cancelHandler.handle(event);
        } else {
            handlers.forEach(handler -> {
                handler.cancel(event);
            });
        }
    }


}
