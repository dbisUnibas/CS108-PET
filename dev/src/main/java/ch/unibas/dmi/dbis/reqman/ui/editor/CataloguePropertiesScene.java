package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.reqman.ui.common.SaveCancelPane;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * A scene which contains all of the properties a catalogue needs to have.
 * <p>
 * This scene then must be handed safely to its stage. This stage most probably is a pop up window.
 *
 * @author loris.sauter
 */
public class CataloguePropertiesScene extends AbstractVisualCreator<Catalogue> {

    private Catalogue catalogue = null;
    private TextField tfLecture = new TextField();
    private TextField tfName = new TextField();
    private TextArea taDesc = new TextArea();
    private TextField tfSemester = new TextField();

    public CataloguePropertiesScene() {
        super();
        populateScene();
    }

    public CataloguePropertiesScene(Catalogue catalogue) {
        this();
        this.catalogue = catalogue;
        loadCatalogue();
    }

    @Override
    public Catalogue create() {
        if (!isCreatorReady()) {
            throw new IllegalStateException("Creation failed: Was not ready");
        }

        return catalogue;
    }

    @Override
    public boolean isCreatorReady() {
        return catalogue != null;
    }

    @Override
    public String getPromptTitle() {
        return "Catalogue Properties";
    }

    public void handleSaving(ActionEvent event) {
        String name = tfName.getText();
        String lecture = tfLecture.getText();

        if (name == null) {
            throw new IllegalArgumentException("[Catalogue] Name MUST not be null");
        }

        catalogue = new Catalogue(
                name,
                lecture,
                taDesc.getText(),
                tfSemester.getText()
        );
        getWindow().hide();

    }

    private void loadCatalogue() {
        if (catalogue != null) {
            tfLecture.setText(catalogue.getLecture());
            tfName.setText(catalogue.getName());
            taDesc.setText(catalogue.getDescription());
            tfSemester.setText(catalogue.getSemester());
        }

    }

    @Override
    protected void populateScene() {
        Label lblLecture = new Label("Lecture");
        Label lblName = new Label("Name*");
        Label lblDescription = new Label("Description");

        // Milestones and Labels added via different scene
        Label lblSemester = new Label("Semester");

        SaveCancelPane buttonWrapper = new SaveCancelPane();

        buttonWrapper.setOnCancel(event -> {
            getWindow().hide();
        });

        buttonWrapper.setOnSave(this::handleSaving);

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
