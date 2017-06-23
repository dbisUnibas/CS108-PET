package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.ui.common.TitleProvider;
import ch.unibas.dmi.dbis.reqman.ui.event.CUDEvent;
import ch.unibas.dmi.dbis.reqman.ui.event.TargetEntity;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorView extends BorderPane implements TitleProvider {

    static final Logger LOGGER_UI = LogManager.getLogger(EditorView.class);
    private final EditorHandler handler;
    private SplitPane splitter;
    private RequirementTableView reqTableView;
    private MilestonesListView msView;
    private CatalogueInfoPane catInfo;
    private String title = "Editor";

    public EditorView(EditorHandler handler) {
        super();
        LOGGER_UI.trace("<init>");
        this.handler = handler;
        this.handler.setEditorView(this);
        initComponents();
        layoutComponents();
        if (handler.isCatalogueLoaded()) {
            handler.setupEditor();
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void indicateWaiting(boolean waiting) {
        getScene().getRoot().setCursor(waiting ? Cursor.WAIT : Cursor.DEFAULT);
    }

    public void refresh() {
        handler.setupEditor();
    }

    void enableAll() {
        reqTableView.setDisable(false);
        msView.setDisable(false);
    }

    void disableAll() {
        reqTableView.setDisable(true);
        msView.setDisable(true);
    }

    RequirementTableView getRequirementsView() {
        return reqTableView;
    }

    MilestonesListView getMilestoneView() {
        return msView;
    }

    CatalogueInfoPane getCatalogueView() {
        return catInfo;
    }

    private void initComponents() {
        LOGGER_UI.trace(":initComps");
        reqTableView = new RequirementTableView();
        reqTableView.setOnAdd(handler::handleCreation);
        reqTableView.setOnRemove(handler::handleDeletion);
        reqTableView.setOnModify(handler::handleModification);

        msView = new MilestonesListView(handler);

        catInfo = new CatalogueInfoPane();


        splitter = new SplitPane();

        splitter.prefWidthProperty().bind(widthProperty());
        splitter.prefHeightProperty().bind(heightProperty());
    }

    private void layoutComponents() {
        LOGGER_UI.trace(":layoutComps");
        splitter.getItems().addAll(msView, reqTableView);
        splitter.setDividerPositions(0.33);
        setTop(catInfo);
        setCenter(splitter);
        disableAll();

        // TEMP
        catInfo.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2) {
                CUDEvent event = CUDEvent.generateModificationEvent(new ActionEvent(evt.getSource(), evt.getTarget()), TargetEntity.CATALOGUE, null);
                handler.handleModification(event);
            }
        });
    }
}