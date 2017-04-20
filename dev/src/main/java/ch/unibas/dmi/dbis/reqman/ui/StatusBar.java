package ch.unibas.dmi.dbis.reqman.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class StatusBar extends HBox {

    private final SimpleStringProperty messageProperty = new SimpleStringProperty();

    private Label messageLabel;
    private Label statusLabel;


    public StatusBar(){
        initComponents();
        layoutComponents();
    }

    private void initComponents(){
        messageLabel = new Label();
        statusLabel = new Label("Status: ");
        messageLabel.textProperty().bindBidirectional(messageProperty);
    }

    private void layoutComponents(){
        getChildren().addAll(statusLabel, messageLabel);
    }

    public StringProperty messageProperty(){
        return messageProperty;
    }

    public void setMessage(String message){
        messageProperty.set(message);
    }

    public String getMessage(){
        return messageProperty.get();
    }
}
