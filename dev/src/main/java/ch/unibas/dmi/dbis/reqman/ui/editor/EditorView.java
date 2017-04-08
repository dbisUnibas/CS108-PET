package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.ui.common.TitleProvider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorView extends BorderPane implements TitleProvider{

    static final Logger LOGGER_UI = LogManager.getLogger(EditorView.class);


    private SplitPane splitter;


    private RequirementTableView reqTableView;
    private MilestonesListView msView;
    private CatalogueInfoPane catInfo;


    @Deprecated
    private EditorController controller;

    private final EditorHandler handler;

    private String title = "Editor";

    private static int counter = 0;
    private static volatile  boolean info = false;

    public EditorView(EditorHandler handler){
        super();
        LOGGER_UI.trace("<init>");
        this.handler = handler;
        this.handler.setEditorView(this);
        initComponents();
        layoutComponents();
    }

    private void initComponents(){
        LOGGER_UI.trace(":initComps");
        reqTableView = new RequirementTableView();
        reqTableView.setOnAdd(handler::handleCreation);
        reqTableView.setOnRemove(handler::handleDeletion);

        msView = new MilestonesListView(handler);

        catInfo = new CatalogueInfoPane();


        splitter = new SplitPane();

        splitter.prefWidthProperty().bind(widthProperty() );
        splitter.prefHeightProperty().bind(heightProperty() );
    }

    private void layoutComponents(){
        LOGGER_UI.trace(":layoutComps");
        splitter.getItems().addAll(msView, reqTableView);
        splitter.setDividerPositions(0.33);
        setTop(catInfo);
        setCenter(splitter);
        disableAll();
    }

    void enableAll(){
        reqTableView.setDisable(false);
        msView.setDisable(false);
    }

    void disableAll(){
        reqTableView.setDisable(true);
        msView.setDisable(true);
    }

    RequirementTableView getRequirementsView() {
        return reqTableView;
    }

    MilestonesListView getMilestoneView(){
        return msView;
    }

    CatalogueInfoPane getCatalogueView(){
        return catInfo;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
