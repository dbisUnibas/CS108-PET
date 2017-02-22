package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueInfoView extends HBox{

    private GridPane root = new GridPane();

    private Label lblName, lblLecture, lblSemester, lblDescription, lblSum;

    private TextField tfName, tfLecture, tfSemester, tfSum;
    private TextArea taDesc;

    private Catalogue catalogue;

    public CatalogueInfoView(Catalogue catalogue){
        super();
        this.catalogue = catalogue;

        initComponents();

        getChildren().add(root);

    }

    private void initComponents(){
        setStyle("-fx-padding: 10px; -fx-spacing: 10px");
        lblName = new Label("Name");
        lblLecture = new Label("Lecture");
        lblSemester = new Label("Semester");
        lblDescription= new Label("Description");
        lblSum = new Label("Maximal Points");

        tfName = new TextField();
        tfName.setEditable(false);
        tfLecture = new TextField();
        tfLecture.setEditable(false);
        tfSemester = new TextField();
        tfSemester.setEditable(false);
        tfSum = new TextField();
        tfSum.setEditable(false);

        taDesc = new TextArea();
        taDesc.setEditable(false);

        int rowIndex = 0;

        root.add(lblName, 0, rowIndex);
        root.add(tfName, 1, rowIndex++);
        root.add(lblLecture, 0, rowIndex);
        root.add(tfLecture, 1, rowIndex++);
        root.add(lblSemester, 0, rowIndex);
        root.add(tfSemester, 1,rowIndex++);
        root.add(lblSum, 0, rowIndex);
        root.add(tfSum, 1, rowIndex++);
        root.add(lblDescription, 0, rowIndex);
        root.add(taDesc, 1, rowIndex++);
    }

    public void displayData(){
        if(catalogue != null){
            tfName.setText(catalogue.getName());
            tfLecture.setText(catalogue.getLecture() );
            tfSemester.setText(catalogue.getSemester() );
            taDesc.setText(catalogue.getDescription() );
        }
    }





}
