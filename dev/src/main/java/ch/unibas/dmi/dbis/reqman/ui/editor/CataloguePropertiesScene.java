package ch.unibas.dmi.dbis.reqman.ui.editor;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CataloguePropertiesScene extends AbstractPopulatedGridScene {

    public CataloguePropertiesScene() {
        super();
    }

    @Override
    protected void populateScene(){
        Label lblLecture = new Label("Lecture");
        Label lblName = new Label("Name");
        Label lblDescription = new Label("Description");
        // Milestones and Labels added via different scene
        Label lblSemester = new Label("Semester");

        TextField tfLecture = new TextField();
        TextField tfName = new TextField();
        TextArea taDesc = new TextArea();
        TextField tfSemester = new TextField();

        SaveCancelPane buttonWrapper = new SaveCancelPane();

        int rowIndex = 0;

        grid.add(lblLecture, 0, rowIndex);
        grid.add(tfLecture, 1, rowIndex++);
        grid.add(lblSemester, 0, rowIndex);
        grid.add(tfSemester, 1, rowIndex++);
        grid.add(lblName, 0, rowIndex);
        grid.add(tfName, 1, rowIndex++);
        grid.add(lblDescription, 0, rowIndex);
        grid.add(taDesc, 1, rowIndex, 1, 3);
        rowIndex += 3;
        grid.add(buttonWrapper, 0, ++rowIndex, 2, 1);
    }
}
