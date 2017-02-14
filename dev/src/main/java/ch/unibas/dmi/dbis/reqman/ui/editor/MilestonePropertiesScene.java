package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractPopulatedGridScene;
import ch.unibas.dmi.dbis.reqman.ui.common.Creator;
import ch.unibas.dmi.dbis.reqman.ui.common.SaveCancelPane;
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
public class MilestonePropertiesScene extends AbstractPopulatedGridScene implements Creator<Milestone>{

    public MilestonePropertiesScene(){
        super();
    }

    public MilestonePropertiesScene(Milestone milestone){
        this();
        this.milestone = milestone;
    }

    private Milestone milestone = null;

    private void loadMilestone(){
        if(milestone != null){
            // TODO Add proper handling if values exist!
            tfName.setText(milestone.getName() );
            inputDate.setValue(milestone.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() );
        }
    }

    private TextField tfName = new TextField();
    private DatePicker inputDate = new DatePicker();



    @Override
    protected void populateScene() {
        Label lblName = new Label("Name");
        Label lblDate = new Label("Date");


        SaveCancelPane buttonWrapper = new SaveCancelPane();
        // TODO Add SaveCancelHandler

        buttonWrapper.setOnSave(event -> {
            milestone = new Milestone(
                    tfName.getText(),
                    0,
                    Date.from(inputDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant() )
            );
            getWindow().hide();
        });

        buttonWrapper.setOnCancel(event -> getWindow().hide());

        int rowIndex = 0;

        grid.add(lblName, 0, rowIndex);
        grid.add(tfName, 1, rowIndex++);

        grid.add(lblDate, 0, rowIndex);
        grid.add(inputDate, 1, rowIndex++);

        grid.add(buttonWrapper, 0, ++rowIndex, 2, 1);
    }

    @Override
    public Milestone create() throws IllegalStateException {
        if(!isCreatorReady() ){
            throw new IllegalStateException("Creation of Milestone failed: Creator not ready");
        }
        return milestone;
    }

    @Override
    public boolean isCreatorReady() {
        return milestone != null;
    }
}
