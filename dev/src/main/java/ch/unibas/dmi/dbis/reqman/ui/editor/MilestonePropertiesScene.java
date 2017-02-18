package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.ZoneId;
import java.util.Date;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class MilestonePropertiesScene extends AbstractVisualCreator<Milestone> {

    private Milestone milestone = null;
    private TextField tfName = new TextField("Milestone"); // Default name
    private Label lblOrdinal = new Label("N/A");
    private DatePicker inputDate = new DatePicker();

    public MilestonePropertiesScene() {
        super();
        populateScene();
    }
    public MilestonePropertiesScene(Milestone milestone) {
        this();
        this.milestone = milestone;
        loadMilestone();
    }

    private void loadMilestone() {
        if (milestone != null) {
            if (milestone.getName() != null && !milestone.getName().isEmpty()) {
                tfName.setText(milestone.getName());
            }
            if (milestone.getDate() != null) {
                inputDate.setValue(milestone.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            lblOrdinal.setText(String.valueOf(milestone.getOrdinal()));
        }
    }

    public void handleSaving(ActionEvent event) {
        String name = (tfName.getText() == null || tfName.getText().isEmpty()) ? "Milestone" : tfName.getText(); // Default name
        Date d = null;
        if (inputDate.getValue() != null) {
            d = Date.from(inputDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        milestone = new Milestone(name, 0, d);// ordinal is handled later
        getWindow().hide();
    }

    @Override
    protected void populateScene() {
        Label lblName = new Label("Name");
        Label lblDate = new Label("Date");
        Label lblOrdinalLabel = new Label("Ordinal");

        loadMilestone();


        int rowIndex = 0;

        grid.add(lblName, 0, rowIndex);
        grid.add(tfName, 1, rowIndex++);

        grid.add(lblOrdinalLabel, 0, rowIndex);
        grid.add(lblOrdinal, 1, rowIndex++);

        grid.add(lblDate, 0, rowIndex);
        grid.add(inputDate, 1, rowIndex++);

        grid.add(buttons, 0, ++rowIndex, 2, 1);
    }

    @Override
    public Milestone create() throws IllegalStateException {
        if (!isCreatorReady()) {
            throw new IllegalStateException("Creation of Milestone failed: Creator not ready");
        }
        return milestone;
    }

    @Override
    public boolean isCreatorReady() {
        return milestone != null;
    }

    @Override
    public String getPromptTitle() {
        return "Milestone Properties";
    }
}
