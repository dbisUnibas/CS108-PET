package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.ui.ReqmanApplication;
import ch.unibas.dmi.dbis.reqman.ui.common.TitledScene;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ConcurrentModificationException;
import java.util.TreeMap;

/**
 * The class {@link EvaluatorScene} is the class which is directly the scene of the assessment mode.
 * <p>
 * It initializes the components and lets its root lay them out.
 *
 * @author loris.sauter
 */
public class EvaluatorScene extends TitledScene {

    private BorderPane root;

    private SplitPane horizontalSplitter = new SplitPane();
    private SplitPane verticalSplitter = new SplitPane();

    private HBox leftContent = new HBox();
    private VBox rightContent = new VBox();
    private TabPane tabPane = new TabPane();

    private String title = "Evaluator";

    private EvaluatorController controller;

    private GroupView groupView;
    private CatalogueInfoView catInfoView;
    private TreeMap<Group, Tab> groupTabMap = new TreeMap<>();
    private EventHandler<ReqmanApplication.ChangeEvent> changeHandler = null;

    private final ToggleGroup toggleMilestone = new ToggleGroup();
    private Menu menuGlobalMilestone;

    private final Logger LOGGER = LogManager.getLogger(getClass());

    public EvaluatorScene(Stage parent, String title, int width, int height) {
        this(parent, width, height);
        this.title = title;

    }
    private Stage parent;

    public EvaluatorScene(Stage parent, int width, int height) {
        super(new BorderPane(), width, height);
        this.parent = parent;
        root = (BorderPane) getRoot();
        controller = new EvaluatorController(this);
        groupView = new GroupView(controller);
        catInfoView = new CatalogueInfoView();
        initComponents();
        disableAll();
        controller.openBackupsIfExistent();
    }

    public CatalogueInfoView getCatalogueInfoView() {
        return catInfoView;
    }

    void addGroupTab(AssessmentView av, boolean unsafed){
        Tab tab = new Tab();
        tab.setText(av.getActiveGroup().getName());
        av.bindToParentSize(rightContent);
        tab.setContent(av);
        tabPane.getTabs().add(tab);
        groupTabMap.put(av.getActiveGroup(), tab);
        if(unsafed){
            markDirty(av.getActiveGroup() );
        }
    }

    public boolean isGroupTabbed(Group group) {
        for (Tab t : tabPane.getTabs()) {
            if (t.getText().equals(group.getName())) {
                return true;
            }
        }
        return false;
    }

    public Group getActiveGroup() {
        Tab selected = tabPane.getSelectionModel().getSelectedItem();
        Node content = selected.getContent();
        if (content instanceof AssessmentView) {
            AssessmentView av = (AssessmentView) content;
            return av.getActiveGroup();
        } else {
            throw new RuntimeException("This should definitively not happen (Non-AV-tab?)");
        }
    }

    public void removeTab(Group group) {
        try {
            for (Tab tab : tabPane.getTabs()) {
                Node content = tab.getContent();
                if (content instanceof AssessmentView) {
                    AssessmentView av = (AssessmentView) content;
                    if (group.equals(av.getActiveGroup())) {
                        tabPane.getTabs().remove(tab);
                        groupTabMap.remove(av.getActiveGroup());
                    }
                }
            }
        } catch (ConcurrentModificationException ex) {
            // Silently catching this exception, since 1 thread environment
        }

    }

    void removeGroupTab(Group oldGroup){
        Tab oldTab = groupTabMap.get(oldGroup);
        tabPane.getTabs().remove(oldTab);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void disableAll() {
        groupView.setDisable(true);
        catInfoView.setDisable(true);
    }

    public void enableAll() {
        groupView.setDisable(false);
        catInfoView.setDisable(false);
    }

    public void markDirty(Group modified) {
        Tab tab = groupTabMap.get(modified);
        if (tab.getText().indexOf("*") < 0) {
            tab.setText(tab.getText() + "*");
        }
        if (!tab.getStyleClass().contains("modified")) {
            tab.getStyleClass().add("modified");
        }

    }

    public void unmarkDirty(Group modified) {
        Tab tab = groupTabMap.get(modified);
        tab.getStyleClass().remove("modified");
        if (tab.getText().indexOf("*") >= 0) {
            String text = tab.getText().substring(0, tab.getText().indexOf("*"));
            tab.setText(text);
        }

    }

    boolean isDirty(Group group){
        Tab tab = groupTabMap.get(group);
        return tab.getStyleClass().contains("modified");
    }

    public void setOnChangeEvent(EventHandler<ReqmanApplication.ChangeEvent> handler) {
        changeHandler = handler;
    }

    public void setActiveTab(AssessmentView assessmentView) {
        for(int i=0; i<tabPane.getTabs().size(); i++){
            if(tabPane.getTabs().get(i).getText().equals(assessmentView.getActiveGroup().getName())){
                tabPane.getSelectionModel().select(i);
            }
        }
    }

    private void initComponents() {
        horizontalSplitter.prefWidthProperty().bind(widthProperty());
        horizontalSplitter.prefHeightProperty().bind(heightProperty());

        verticalSplitter.setOrientation(Orientation.VERTICAL);

        verticalSplitter.prefWidthProperty().bind(widthProperty());
        verticalSplitter.prefHeightProperty().bind(heightProperty());


        VBox upper = new VBox();
        upper.getChildren().add(catInfoView);

        VBox lower = new VBox();
        lower.getChildren().add(groupView);

        verticalSplitter.getItems().addAll(upper, lower);

        verticalSplitter.setDividerPositions(0.3);

        leftContent.getChildren().add(verticalSplitter);


        rightContent.setStyle("-fx-padding: 10px;-fx-spacing: 10px;");

        tabPane.setStyle("-fx-pading: 10px; -fx-spacing: 10px;");
        tabPane.getStylesheets().add("style.css");
        rightContent.getChildren().addAll(tabPane);// TODO Iff no catalogue loaded / all tabs closed: Plachoolder with info

        horizontalSplitter.getItems().addAll(leftContent, rightContent);


        root.setTop(createMenuBar());

        VBox box = new VBox();
        box.getChildren().add(horizontalSplitter);
        root.setCenter(box);

        horizontalSplitter.setDividerPositions(0.3);
    }

    private MenuBar createMenuBar() {
        MenuBar bar = new MenuBar();

        Menu menuFile = new Menu("File");

        MenuItem itemLoad = new MenuItem("Load Catalogue");
        itemLoad.setOnAction(controller::handleLoadCatalogue);
        itemLoad.setAccelerator(KeyCombination.keyCombination("Ctrl+L"));
        MenuItem itemNew = new MenuItem("New Group");
        itemNew.setOnAction(controller::handleAddGroup);
        itemNew.setAccelerator(KeyCombination.keyCombination("Ctrl+G"));
        MenuItem itemOpen = new MenuItem("Open Group");
        itemOpen.setOnAction(controller::handleOpenGroup);
        itemOpen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        MenuItem itemSave = new MenuItem("Save Group");
        itemSave.setOnAction(controller::handleSaveGroup);
        itemSave.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        MenuItem itemSaveAs = new MenuItem("Save Group As");
        itemSaveAs.setOnAction(controller::handleSaveAsGroup);
        MenuItem itemExportGroup = new MenuItem("Export Group(s)");
        itemExportGroup.setOnAction(controller::handleExportGroup);

        MenuItem itemExit = new MenuItem("Quit");


        menuFile.getItems().addAll(itemLoad, itemNew, itemOpen, itemSave, itemSaveAs, new SeparatorMenuItem(), itemExportGroup, new SeparatorMenuItem(), itemExit);

        Menu menuEdit = new Menu("Edit");
        MenuItem itemModify = new MenuItem("Modify Group");
        itemModify.setOnAction(controller::handleModifyGroup);
        menuGlobalMilestone = new Menu("Set milestone forall groups");


        // The set-milestone-forall-groups menu related handling
        toggleMilestone.selectedToggleProperty().addListener( (ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle ) -> {
            /*
            Lots of trace logging to understand what is going on
             */

            if(ov != null){
                LOGGER.trace("OV: "+ov.toString() );
                if(ov.getValue() instanceof RadioMenuItem){
                    RadioMenuItem item = (RadioMenuItem) ov.getValue();
                    if(item != null && item.getUserData() != null){
                        if(item.getUserData() instanceof String && ((String)item.getUserData()).equals("clear")){
                            controller.resetGlobalMilestoneChoice();
                            return;
                        }
                    }
                }
            }
            if(newToggle != null){
                if(newToggle.getUserData() instanceof Milestone){
                    Milestone newMS = (Milestone) newToggle.getUserData();
                    LOGGER.trace("newMS: "+newMS.getName());
                }else{
                    LOGGER.trace("newToggle: "+newToggle.toString() );
                }

            }
            if(oldToggle != null){
                if(oldToggle.getUserData() instanceof  Milestone){
                    Milestone oldMS = (Milestone) oldToggle.getUserData();
                    LOGGER.trace("oldMS: "+oldMS.getName() );
                }else{
                    LOGGER.trace("oldToggle: "+oldToggle.toString());
                }

            }

            /*
            Conclusion: Only if *newly* selected the event is fired and thus handled in here.
             */
            if(toggleMilestone.getSelectedToggle() != null && toggleMilestone.getSelectedToggle().getUserData() instanceof Milestone){
                Milestone ms = (Milestone) toggleMilestone.getSelectedToggle().getUserData();
                LOGGER.debug("Selected: "+ms.getName());
                controller.setGlobalMilestoneChoice(ms);
            }
        } );


        menuEdit.getItems().addAll(itemModify, menuGlobalMilestone);

        Menu menuView = new Menu("View");
        MenuItem itemChangeToEditor = new MenuItem("Editor");
        itemChangeToEditor.setOnAction(this::handleChangeView);
        MenuItem itemOverview = new MenuItem("Show Overview");
        itemOverview.setOnAction(controller::handleOverview);
        menuView.getItems().addAll(itemChangeToEditor, new SeparatorMenuItem(), itemOverview);

        Menu menuHelp = new Menu("Help");


        bar.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);
        return bar;
    }

    private void handleChangeView(ActionEvent event) {
        if (changeHandler != null) {
            ReqmanApplication.ChangeEvent evt = new ReqmanApplication.ChangeEvent(event, ReqmanApplication.EDITOR_VIEW, getWidth(), getHeight(), parent.isMaximized());
            changeHandler.handle(evt);
        }
    }

    void setupGlobalMilestoneMenu(){
        for(Milestone ms : controller.getMilestones()){
            RadioMenuItem itemMilestone = new RadioMenuItem(ms.getName() );
            itemMilestone.setUserData(ms);
            itemMilestone.setToggleGroup(toggleMilestone);
            menuGlobalMilestone.getItems().add(itemMilestone);
        }
        RadioMenuItem itemClearMilestone = new RadioMenuItem("Clear Global Milestone");
        itemClearMilestone.setUserData("clear");
        itemClearMilestone.setToggleGroup(toggleMilestone);
        menuGlobalMilestone.getItems().add(0, itemClearMilestone);
    }

    public void stop() {
        controller.stop();
    }
}
