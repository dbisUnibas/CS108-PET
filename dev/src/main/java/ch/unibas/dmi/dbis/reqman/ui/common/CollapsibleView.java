package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CollapsibleView extends VBox {
    private ToggleButton collapseButton = new ToggleButton(Utils.ARROW_DOWN);
    private Node collapsible;
    private HBox content;

    public CollapsibleView() {
        super();

        initComponents();
        initCollapsible();
    }

    private void initCollapsible(){
        collapseButton.setOnAction(this::handleCollapse);
        //collapsible.setVisible(false);
        collapsible.setStyle("-fx-background-color: white;-fx-padding: 10px; -fx-spacing: 10px;-fx-border-width: 1px;-fx-border-color: silver");

    }

    private void handleCollapse(ActionEvent event){
        if(collapsible == null){
            return;
        }
        if(collapseButton.isSelected() ){
            collapseButton.setText(Utils.ARROW_UP);
            getChildren().add(collapsible);
            //collapsible.setVisible(true);
        }else{
            collapseButton.setText(Utils.ARROW_DOWN);

            getChildren().remove(collapsible);
            //collapsible.setVisible(false);
        }
        event.consume();
    }

    private void initComponents(){
        content = new HBox();

        content.prefHeightProperty().bind(prefWidthProperty() );

        getChildren().add(content);
        content.setStyle("-fx-spacing: 10px;-fx-padding: 10px;-fx-border-color: silver;-fx-border-width: 1px;");
    }

    public void setContent(Node node){
        content.getChildren().addAll(node);
    }

    public void removeContent(Node node){
        content.getChildren().remove(node);
    }

    public void setCollapsible(Node node){
        collapsible = node;
    }

    public void removeCollapsible(){
        collapsible = null;
    }
}
