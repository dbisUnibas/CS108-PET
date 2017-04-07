package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.SortingUtils;
import ch.unibas.dmi.dbis.reqman.core.*;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class AssessmentView extends BorderPane implements PointsChangeListener, DirtyListener {

    private HBox titleBar;
    private AnchorPane titleAnchor;
    private Label lblChoice;
    private ComboBox<Milestone> cbMilestones;
    @Deprecated // Added changelistener to cbMilestones
    private Button btnRefresh;
    private Button btnSummary;
    private HBox statusWrapper;
    private AnchorPane statusBar;
    private Label lblSum;
    private TextField tfSum;
    private VBox content;
    private ScrollPane scrollPane;

    private final Logger LOGGER = LogManager.getLogger(getClass() );

    private EvaluatorController controller;

    private Group group;
    private Milestone activeMS = null;
    /**
     * Maps MS ordinal to a map of Req.name <-> Progress (obj)
     */
    private Map<Integer, Map<String, Progress>> progressMap = new TreeMap<>();
    private List<ProgressSummary> summaries = new ArrayList<>();
    private List<ProgressView> activeProgressViews = new ArrayList<>();
    private Set<Milestone> visitedMilestones = new HashSet<>();

    AssessmentView(EvaluatorController controller, Group active) {
        this(controller, active, null);
    }

    AssessmentView(EvaluatorController controller, Group activeGroup, Milestone activeMS){
        super();
        LOGGER.debug("Initializing for group "+activeGroup.getName() );

        this.controller = controller;
        this.group = activeGroup;
        this.activeMS = activeMS;

        LOGGER.debug("Active MS: "+(this.activeMS != null ? this.activeMS.getName() : "null"));

        initComponents();
        layoutComponents();
        loadGroup();

        updateProgressViews(this.activeMS);
    }

    public List<ProgressSummary> getSummaries() {
        return summaries;
    }

    public void bindToParentSize(Region parent) {
        prefWidthProperty().bind(parent.widthProperty());
        prefHeightProperty().bind(parent.heightProperty());
    }

    @Override
    public void pointsChanged(double newValue) {
        calcActiveSum();
        controller.markDirty(getActiveGroup());
    }

    public Group getActiveGroup() {
        return group;
    }

    /**
     * ONLY if group has to be saved. Grabs ALL progress objects
     *
     * @return
     */
    public List<Progress> getProgressListForSaving() {
        return getProgressListForSaving(false);
    }

    /**
     * Grabs all progress' objects and returns the list of them.
     * @param trim It true, only those progresses with !hasDefaultPercentage are grabbed
     * @return
     */
    public List<Progress> getProgressListForSaving(boolean trim){
        List<Progress> list = new ArrayList<>();
        if(trim){
            progressMap.values().forEach(consumer-> consumer.values().forEach( p -> {
                if(!p.hasDefaultPercentage() ){
                    list.add(p);
                }
            }));
        }else{
            progressMap.values().forEach(consumer -> consumer.values().forEach(list::add));
        }
        activeProgressViews.forEach(pv->pv.markSaved());
        return list;
    }

    public void setActiveMilestone(Milestone activeMilestone) {
        this.activeMS = activeMilestone;
    }

    void replaceGroup(Group newGroup) {
        this.group = newGroup;
    }

    private void loadGroup() {
        if (summaries != null) {
            summaries.addAll(group.getProgressSummaries());
        }

        List<Progress> progressList = group.getProgressList();
        if (progressList == null || progressList.isEmpty()) {
            progressMap = setupProgressMap();
        } else {
            progressMap = loadProgress(progressList);
            mergeCatalogueProgress(setupProgressMap(), progressMap);
        }

    }

    private void mergeCatalogueProgress(Map<Integer, Map<String, Progress>> catalogueMap, Map<Integer, Map<String, Progress>> groupMap) {
        catalogueMap.keySet().forEach(ordinal -> {
            LOGGER.debug(":mergeCatalogueProgress - Merging for MS: "+ordinal);
            Map<String, Progress> groupProgress = groupMap.get(ordinal);
            Map<String, Progress> catalogueProgress = catalogueMap.get(ordinal);
            Set<String> tempSet = new HashSet<String>(catalogueProgress.keySet());
            if(groupProgress != null){ // may be null if the group was stored to disk and a milestone was not yet tracked.
                tempSet.removeAll(groupProgress.keySet());
                for (String reqName : tempSet) {
                    progressMap.get(ordinal).put(reqName, catalogueProgress.get(reqName));
                }
            }else{
                LOGGER.debug(":mergeCatalogueProgress - "+String.format("%s", catalogueProgress));
                LOGGER.debug(":mergeCatalogueProgress - "+String.format("%s", progressMap.get(ordinal)));
                progressMap.put(ordinal, new TreeMap<>(catalogueProgress));
            }

        });
    }

    private Map<Integer, Map<String, Progress>> loadProgress(List<Progress> list) {
        Map<Integer, Map<String, Progress>> progressMap = new TreeMap<>();
        for (Progress p : list) {
            int ordinal = p.getMilestoneOrdinal();
            String reqName = p.getRequirementName();

            if (progressMap.containsKey(ordinal)) {
                // MS entry exists already
                Map<String, Progress> rpMap = progressMap.get(ordinal);
                if (rpMap == null || rpMap.containsKey(reqName)) {
                    // no map, but ordinal OR requirement is already existing. THIS IS A SEVERE ERROR
                } else {
                    rpMap.put(reqName, p);
                }
            } else {
                // FIRST time this MS occurs:
                TreeMap<String, Progress> rpMap = new TreeMap<>();
                rpMap.put(reqName, p);
                progressMap.put(ordinal, rpMap);
            }
        }
        return progressMap;
    }

    /**
     * Sets up the map as if the group was newly created
     */
    private Map<Integer, Map<String, Progress>> setupProgressMap() {
        Map<Integer, Map<String, Progress>> progressMap = new TreeMap<>();
        controller.getMilestones().forEach(ms -> {
            TreeMap<String, Progress> reqProgMap = new TreeMap<String, Progress>();
            controller.getRequirementsByMilestone(ms.getOrdinal()).forEach(r -> {
                reqProgMap.put(r.getName(), new Progress(r.getName(), ms.getOrdinal(), 0));
            });
            progressMap.put(ms.getOrdinal(), reqProgMap);
        });
        return progressMap;
    }

    private void initComponents() {
        titleBar = new HBox();
        titleAnchor = new AnchorPane();
        lblChoice = new Label("Current Milestone: ");
        cbMilestones = new ComboBox<>();
        //btnRefresh = new Button("Update");
        btnSummary = new Button("Comments");
        statusWrapper = new HBox();
        statusBar = new AnchorPane();
        lblSum = new Label("Sum:");
        tfSum = new TextField();
        tfSum.setEditable(false);
        content = new VBox();
        scrollPane = new ScrollPane();
    }

    private void layoutComponents() {
        // Forge top aka title bar:
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.getChildren().addAll(lblChoice, cbMilestones, btnSummary);
        titleBar.setStyle(titleBar.getStyle() + "-fx-spacing: 10px; -fx-padding: 10px;");

        if (controller != null) {
            cbMilestones.setItems(FXCollections.observableList(controller.getMilestones()));
            cbMilestones.setCellFactory(param -> new Utils.MilestoneCell());
            cbMilestones.setButtonCell(new Utils.MilestoneCell());

            cbMilestones.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                LOGGER.trace("Handling milestone choice");
                this.activeMS = cbMilestones.getSelectionModel().getSelectedItem();
                updateProgressViews(cbMilestones.getSelectionModel().getSelectedItem());
            });
        }

        if (controller != null) {
            btnSummary.setOnAction(this::handleComments);
        }

        VBox titleWrapper = new VBox();
        Separator sep = new Separator();
        titleWrapper.getChildren().addAll(titleBar, sep);
        setTop(titleWrapper);

        // Forge center aka ProgressView list
        scrollPane.setContent(content);
        setCenter(scrollPane);

        // Forge bottom aka status bar:
        statusWrapper.setAlignment(Pos.CENTER_LEFT);
        statusWrapper.getChildren().addAll(lblSum, tfSum);
        statusWrapper.setStyle("-fx-padding: 10px; -fx-spacing: 10px;");
        Separator sep2 = new Separator();
        statusBar.getChildren().add(statusWrapper);
        statusBar.getChildren().add(sep2);
        AnchorPane.setTopAnchor(sep2, 0d);
        AnchorPane.setRightAnchor(statusWrapper, 10d);
        setBottom(statusBar);

    }

    private void handleComments(ActionEvent event) {
        Milestone ms = cbMilestones.getSelectionModel().getSelectedItem();
        if (ms != null) {
            ProgressSummary ps = null;
            boolean replace = false;
            if (hasSummaryForMilestone(ms)) {
                //ps = EvaluatorPromptFactory.promptSummary(ms, group.getName(), getSummaryForMilestone(ms));
                EvaluatorPromptFactory.showSummary(ms, group.getName(), summary -> {handleSummaryReceiving(summary, true);}, getSummaryForMilestone(ms));
                replace = true;
            } else {
                //ps = EvaluatorPromptFactory.promptSummary(ms, group.getName());
                EvaluatorPromptFactory.showSummary(ms, group.getName(), progressSummary -> handleSummaryReceiving(progressSummary, false));
            }
            if (ps != null) {
                if (replace) {
                    summaries.remove(getSummaryForMilestone(ms));
                }
                summaries.add(ps);
            }
        }
    }

    private void handleSummaryReceiving(ProgressSummary ps, boolean replace){
        if(ps != null){
            if(replace){
                summaries.remove(getSummaryForMilestone(controller.getActiveCatalogue().getMilestoneByOrdinal(ps.getMilestoneOrdinal())));
            }
            summaries.add(ps);
            markDirty();
        }
    }

    private boolean hasSummaryForMilestone(Milestone ms) {
        if (summaries.isEmpty()) {
            return false;
        } else {
            for (ProgressSummary ps : summaries) {
                if (ps.getMilestoneOrdinal() == ms.getOrdinal()) {
                    return true;
                }
            }
        }
        return false;
    }

    private ProgressSummary getSummaryForMilestone(Milestone ms) {
        if (summaries.isEmpty()) {
            return null;
        } else {
            for (ProgressSummary ps : summaries) {
                if (ps.getMilestoneOrdinal() == ms.getOrdinal()) {
                    return ps;
                }
            }
        }
        return null;
    }

    private void loadActiveProgressViews(Milestone activeMS){
        LOGGER.trace(":loadActiveProgressViews - MS: "+activeMS.getName());
        LOGGER.trace(":loadActiveProgressViews - this.activeMS: "+this.activeMS.getName());
        visitedMilestones.add(this.activeMS);
        activeProgressViews.clear();
        List<Requirement> reqs = controller.getRequirementsByMilestone(this.activeMS.getOrdinal());
        reqs.sort(SortingUtils.REQUIREMENT_COMPARATOR);
        reqs.forEach(r -> {
            Progress p = progressMap.get(activeMS.getOrdinal()).get(r.getName());

            ProgressView pv = new ProgressView(p, r);

            pv.setActiveMilestone(activeMS);
            verifyPredecessorsAchieved(pv);
            pv.addPointsChangeListener(this);

            pv.addDirtyListener(this);

            // Filter the ones that are not really on this milestone and are already assessed
            if(p.getDate() == null){
                // progress was not yet tracked.
                activeProgressViews.add(pv);
            }else if(p.getDate().before(activeMS.getDate() ) ){
                // progress was made STRICTLY before active milestone
                // this ProgressView must not be shown now
            }else if(p.getDate().equals(activeMS.getDate() )){
                // comparison possible - always a ms date is used.
                // the progress date matches the current milestone -> show pv
                activeProgressViews.add(pv);
            }else{
                activeProgressViews.add(pv);
            }
            //activeProgressViews.add(pv);
        });

    }

    private void verifyPredecessorsAchieved(ProgressView pv){
        pv.setDisable(!group.isProgressUnlocked(controller.getActiveCatalogue(), pv.getProgress()));
    }


    /**
     *
     * @param activeMS If {@code == null}, the first entry in the choice is set.
     */
    private void updateProgressViews(Milestone activeMS){
        LOGGER.trace(":updateProgressViews - MS: "+ (activeMS != null ? activeMS.getName() : "null") );
        detachProgressViews();
        tfSum.setText("0");
        if(activeMS == null){
            LOGGER.trace(":updateProgressViews - Setting default active ms");
            this.activeMS = cbMilestones.getItems().get(0);
        }
        loadActiveProgressViews(this.activeMS);
        attachProgressViews();
        calcActiveSum();
        if(cbMilestones.getSelectionModel().getSelectedItem() == null){
            cbMilestones.getSelectionModel().select(this.activeMS);
        }
    }

    /**
     * Also updates the view
     * @param ms
     */
    public void selectMilestone(Milestone ms){
        this.activeMS = ms;
        cbMilestones.getSelectionModel().select(ms);
    }

    private void calcActiveSum() {
        double sum = group.getSumForMilestone(activeMS, controller.getActiveCatalogue());
        tfSum.setText(String.valueOf(sum));
    }

    private void attachProgressViews() {
        activeProgressViews.forEach(pv -> {
            addProgressView(pv);
        });
    }

    private void detachProgressViews() {
        activeProgressViews.forEach(pv -> removeProgressView(pv));
    }

    private void addProgressView(ProgressView pv) {
        content.getChildren().add(pv);
        pv.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    private void removeProgressView(ProgressView pv) {
        content.getChildren().remove(pv);
    }

    private void bindContent() {
        content.prefWidthProperty().bind(widthProperty());
        content.prefHeightProperty().bind(heightProperty());
    }


    @Override
    public void markDirty() {
        calcActiveSum();
        controller.markDirty(getActiveGroup());
    }

    @Override
    public void unmarkDirty() {
        calcActiveSum();
        controller.unmarkDirty(getActiveGroup());
    }
}
