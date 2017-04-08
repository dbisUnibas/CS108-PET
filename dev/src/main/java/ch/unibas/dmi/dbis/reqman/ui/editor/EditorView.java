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
    private MilestonesView msView;
    private CatalogueInfoPane catInfo;


    private EditorController controller;

    private String title = "Editor";

    private static int counter = 0;
    private static volatile  boolean info = false;

    public EditorView(EditorController controller){
        super();
        this.controller = controller;
        initComponents();
        layoutComponents();

        /* === EXPERIMENTAL === */
        reqTableView.setOnAdd(event -> {
            catInfo.setMaxPoints(counter++);
        });

        reqTableView.setOnRemove(event -> {
            if(info){
                catInfo.setCatName("");
                catInfo.setCatLecture("");
                catInfo.setCatSemester("");
            }else{
                catInfo.setCatName("Programmierprojekt");
                catInfo.setCatLecture("Programmier-Projekt");
                catInfo.setCatSemester("FS17");
            }
            info = !info;
            LOGGER_UI.debug("Style class: "+catInfo.debugStyle());
        });
    }

    private void initComponents(){
        reqTableView = new RequirementTableView();
        msView = new MilestonesView(controller);
        catInfo = new CatalogueInfoPane();


        splitter = new SplitPane();

        splitter.prefWidthProperty().bind(widthProperty() );
        splitter.prefHeightProperty().bind(heightProperty() );
    }

    private void layoutComponents(){
        splitter.getItems().addAll(msView, reqTableView);
        splitter.setDividerPositions(0.33);
        setTop(catInfo);
        setCenter(splitter);
    }

    void enableAll(){
        reqTableView.setDisable(false);
        msView.setDisable(false);
    }

    void disableAll(){
        reqTableView.setDisable(true);
        msView.setDisable(true);
    }



    @Override
    public String getTitle() {
        return title;
    }
}
