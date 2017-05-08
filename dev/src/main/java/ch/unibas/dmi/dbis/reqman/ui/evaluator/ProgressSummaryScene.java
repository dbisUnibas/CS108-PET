package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.ProgressSummary;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.apache.commons.lang.StringUtils;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ProgressSummaryScene extends AbstractVisualCreator<ProgressSummary> {

    private ProgressSummary summary = null;

    private TextArea taInternal;
    private TextArea taExternal;

    private Milestone milestone;
    private String groupName;


    public ProgressSummaryScene(Milestone milestone, String groupName) {
        super();
        this.milestone = milestone;
        this.groupName = groupName;
        populateScene();
    }

    public ProgressSummaryScene(Milestone milestone, String groupName, ProgressSummary progressSummary) {
        this(milestone, groupName);
        this.summary = progressSummary;
        loadSummary();
    }

    @Override
    public String getPromptTitle() {
        return "Progress Summary";
    }

    @Override
    public void handleSaving(ActionEvent event) {
        boolean externalExists = StringUtils.isNotEmpty(taExternal.getText());
        boolean internalExists = StringUtils.isNotEmpty(taInternal.getText());

        if (externalExists || internalExists) {
            summary = new ProgressSummary(milestone.getOrdinal(), taInternal.getText(), taExternal.getText());
        }

        dismiss();
    }

    @Override
    public ProgressSummary create() throws IllegalStateException {
        if (!isCreatorReady()) {
            throw new IllegalStateException("Cannot create ProgressSummary since the Creator is not ready");
        }
        return summary;
    }

    @Override
    public boolean isCreatorReady() {
        return summary != null;
    }

    @Override
    protected void populateScene() {
        Label lblGroup = new Label("Group");
        Label lblGroupName = new Label(groupName);
        Label lblMilestone = new Label("Milestone");
        Label lblMilestoneName = new Label(milestone.getName());
        Label lblExternal = new Label("External comment");
        Label lblInternal = new Label("Internal comment");

        taExternal = new TextArea();
        taInternal = new TextArea();

        int rowIndex = 0;

        grid.add(lblGroup, 0, rowIndex);
        grid.add(lblGroupName, 1, rowIndex++);

        grid.add(lblMilestone, 0, rowIndex);
        grid.add(lblMilestoneName, 1, rowIndex++);

        grid.add(lblExternal, 0, rowIndex);
        grid.add(taExternal, 1, rowIndex, 1, 2);
        rowIndex += 2;

        grid.add(lblInternal, 0, rowIndex);
        grid.add(taInternal, 1, rowIndex, 1, 2);
        rowIndex += 2;

        grid.add(buttons, 0, ++rowIndex, 2, 1);
    }

    private void loadSummary() {
        if (summary != null) {
            taInternal.setText(summary.getInternalComment());
            taExternal.setText(summary.getExternalComment());
        }
    }
}
