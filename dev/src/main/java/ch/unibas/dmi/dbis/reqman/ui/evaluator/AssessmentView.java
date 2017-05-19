package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.SortingUtils;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
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

    private final Logger LOGGER = LogManager.getLogger(getClass());
    private final EvaluatorHandler handler;
    private HBox titleBar;
    private AnchorPane titleAnchor;
    private Label lblChoice;
    private ComboBox<Milestone> cbMilestones;
    private Button btnSummary;
    private HBox statusWrapper;
    private AnchorPane statusBar;
    private Label lblSum;
    private TextField tfSum;
    private VBox content;
    private ScrollPane scrollPane;
    private Group group;
    private Milestone activeMS = null;
    /**
     * Maps MS ordinal to a map of Req.name <-> Progress (obj)
     */
    private Map<Integer, Map<String, Progress>> progressMap;
    private List<ProgressSummary> summaries = new ArrayList<>();
    private List<ProgressView> activeProgressViews = new ArrayList<>();
    private Set<Milestone> visitedMilestones = new HashSet<>();

    AssessmentView(EvaluatorHandler handler, Group activeGroup) {
        this(handler, activeGroup, null);
    }

    AssessmentView(EvaluatorHandler handler, Group activeGroup, Milestone activeMS) {
        super();
        LOGGER.debug("Initializing for group " + activeGroup.getName());

        this.handler = handler;
        this.group = activeGroup;
        this.activeMS = activeMS;

        LOGGER.debug("Active MS: " + (this.activeMS != null ? this.activeMS.getName() : "null"));

        initComponents();
        layoutComponents();
        loadGroup();

        updateProgressViews(this.activeMS);
    }

    private static Map<Integer, Map<String, Progress>> generateOriginMap(List<Requirement> requirements) {
        Map<Integer, Map<String, Progress>> map = new HashMap<>();
        for (Requirement r : requirements) {
            if (!map.containsKey(r.getMinMilestoneOrdinal())) {
                // case 1 r's ms nonexistent:
                Map<String, Progress> msMap = new HashMap<>();
                msMap.put(r.getName(), new Progress(r));
                map.put(r.getMinMilestoneOrdinal(), msMap);
            } else {
                map.get(r.getMinMilestoneOrdinal()).put(r.getName(), new Progress(r));
            }
        }
        return map;
    }

    private static Map<Integer, Map<String, Progress>> generateMap(List<Progress> progressList, Catalogue cat) {
        Map<Integer, Map<String, Progress>> map = new HashMap<>();

        for (Progress p : progressList) {
            Requirement r = cat.getRequirementByName(p.getRequirementName());
            if (r == null) {
                // Group has progress of a req that is not existent anymore. just ignore it (progress still tracked in file)
                continue;
            }
            if (!map.containsKey(r.getMinMilestoneOrdinal())) {
                Map<String, Progress> msMap = new HashMap<>();
                msMap.put(r.getName(), p);
                map.put(r.getMinMilestoneOrdinal(), msMap);
            } else {
                map.get(r.getMinMilestoneOrdinal()).put(r.getName(), p);
            }
        }

        return map;
    }

    private static Map<Integer, Map<String, Progress>> mergeMaps(Map<Integer, Map<String, Progress>> origin, Map<Integer, Map<String, Progress>> loaded) {
        Map<Integer, Map<String, Progress>> map = new HashMap<>();
        for (int ordinal : origin.keySet()) {
            Map<String, Progress> oMS = origin.get(ordinal);
            Map<String, Progress> lMS = loaded.get(ordinal);
            if (lMS == null || lMS.isEmpty()) {
                // CASE 0: Iff group has no progress entries for that milestone: add generated reqname-progress-map
                map.put(ordinal, oMS);
                continue;
            }

            Map<String, Progress> working = new HashMap<>();

            for (String reqName : oMS.keySet()) {
                /*
                If loaded does not contain entry to requirement -> catalogue has new requirement which
                probably needs to be assessed. Thus->
                 */
                if (!lMS.containsKey(reqName)) {
                    working.put(reqName, oMS.get(reqName));
                } else {
                    working.put(reqName, lMS.get(reqName));
                }
            }
            map.put(ordinal, working);
        }
        return map;
    }

    public List<ProgressSummary> getSummaries() {
        return summaries;
    }

    public void bindToParentSize(Region parent) {
        prefWidthProperty().bind(parent.widthProperty());
        prefHeightProperty().bind(parent.heightProperty());
        scrollPane.prefWidthProperty().bind(widthProperty());
        scrollPane.prefHeightProperty().bind(heightProperty());
        bindContent();
    }

    @Override
    public void pointsChanged(double newValue) {
        LOGGER.trace("Points changed");
        calcActiveSum();
        handler.markDirty(getActiveGroup());
    }

    public Group getActiveGroup() {
        return group;
    }

    /**
     * Grabs all progress' objects and returns the list of them.
     *
     * @param trim It true, only those progresses with !hasDefaultPercentage are grabbed
     * @return
     */
    public List<Progress> getProgressListForSaving(boolean trim) {
        List<Progress> list = new ArrayList<>();
        if (trim) {
            progressMap.values().forEach(consumer -> consumer.values().forEach(p -> {
                if (!p.hasDefaultPercentage()) {
                    list.add(p);
                }
            }));
        } else {
            progressMap.values().forEach(consumer -> consumer.values().forEach(list::add));
        }
        activeProgressViews.forEach(pv -> pv.markSaved());
        handler.unmarkDirty(group);
        return list;
    }

    public void setActiveMilestone(Milestone activeMilestone) {
        this.activeMS = activeMilestone;
    }

    /**
     * Also updates the view
     *
     * @param ms
     */
    public void selectMilestone(Milestone ms) {
        this.activeMS = ms;
        cbMilestones.getSelectionModel().select(ms);
    }

    @Override
    public void markDirty() {
        LOGGER.trace("Dirty");
        calcActiveSum();
        handler.markDirty(getActiveGroup());
    }

    @Override
    public void unmarkDirty() {
        LOGGER.trace("Undirty");
        calcActiveSum();
        handler.unmarkDirty(getActiveGroup());
    }

    public void reloadRequirements(boolean refreshAll) {
        List<Progress> progressList = group.getProgressList();
        Map<Integer, Map<String, Progress>> origin = generateOriginMap(handler.getCatalogue().getRequirements());
        if (progressList == null || progressList.isEmpty()) {
            progressMap = origin;
        } else {
            Map<Integer, Map<String, Progress>> loaded = generateMap(progressList, handler.getCatalogue());
            progressMap = mergeMaps(origin, loaded);
        }
        if (refreshAll) {
            LOGGER.debug(":reloadReqs:refreshAll");
            updateProgressViews(activeMS);
        }
    }

    void replaceGroup(Group newGroup) {
        this.group = newGroup;
    }

    private void loadGroup() {
        if (summaries != null) {
            summaries.addAll(group.getProgressSummaries());
        }

        reloadRequirements(false);
        syncProgressList();
    }

    private void syncProgressList() {
        LOGGER.trace(":syncProgressList");
        handler.progressList(group).clear(); // Ensure empty list
        progressMap.values().forEach(map -> {
            map.values().forEach(p -> handler.progressList(group).add(p));
        });
        LOGGER.debug("Presenting the progress list: " + handler.progressList(getActiveGroup()));
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

        if (handler != null) {
            cbMilestones.setItems(FXCollections.observableList(handler.getMilestones()));
            cbMilestones.setCellFactory(param -> new Utils.MilestoneCell());
            cbMilestones.setButtonCell(new Utils.MilestoneCell());

            cbMilestones.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                LOGGER.trace("Handling milestone choice");
                this.activeMS = cbMilestones.getSelectionModel().getSelectedItem();
                updateProgressViews(cbMilestones.getSelectionModel().getSelectedItem());
            });
        }

        if (handler != null) {
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
                //ps = EvaluatorPromptFactory.promptSummary(ms, group.getCatalogueName(), getSummaryForMilestone(ms));
                EvaluatorPromptFactory.showSummary(ms, group.getName(), summary -> {
                    handleSummaryReceiving(summary, true);
                }, getSummaryForMilestone(ms));
                replace = true;
            } else {
                //ps = EvaluatorPromptFactory.promptSummary(ms, group.getCatalogueName());
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

    private void handleSummaryReceiving(ProgressSummary ps, boolean replace) {
        LOGGER.debug(String.format(":handleSummaryReceiving - Received summary: %s", ps));
        if (ps != null) {
            // Case receiving non-null summary
            ProgressSummary sent = getSummaryForMilestone(handler.getMilestoneByOrdinal(ps.getMilestoneOrdinal()));
            if (replace) {
                // Case have to replace summary
                summaries.remove(getSummaryForMilestone(handler.getMilestoneByOrdinal(ps.getMilestoneOrdinal())));
            }
            summaries.add(ps);
            if (sent == ps) { // if it is *the same object*
                // No changes, since exactly the same as before
                LOGGER.debug(":handleSummaryReceiving - Received is same as sent");
                boolean equalExternal = ps.getExternalComment().equals(sent.getExternalComment());
                boolean equalInternal = ps.getInternalComment().equals(sent.getInternalComment());
                LOGGER.debug(":handleSummaryReceiving - " + String.format("Equal external=%s and internal=%s", equalExternal, equalInternal));
                if (equalExternal && equalInternal) {
                    // no changes
                    LOGGER.debug(":handleSummaryReceiving - No changes");
                } else {
                    // changes
                    LOGGER.debug(":handleSummaryReceiving - Changes detected");
                    markDirty();
                }
            } else {
                // first time recevining summary for this ms
                LOGGER.debug(":handleSummaryReceiving - Received differs form sent");
                markDirty();
            }

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

    private void loadActiveProgressViews(Milestone activeMS) {
        LOGGER.trace(":loadActiveProgressViews - MS: " + activeMS.getName());
        LOGGER.trace(":loadActiveProgressViews - this.activeMS: " + this.activeMS.getName());
        visitedMilestones.add(this.activeMS);
        activeProgressViews.clear();
        List<Progress> actives = getActiveProgresses(activeMS);
        LOGGER.debug("Active Progresses: " + actives.toString());
        for (Progress p : actives) {
            activeProgressViews.add(new ProgressView(p, handler.getCatalogue().getRequirementForProgress(p), handler.getCatalogue()));
            LOGGER.debug("Added PV for " + p.getRequirementName());
        }
        activeProgressViews.forEach(pv -> {
            pv.setActiveMilestone(activeMS);
            verifyPredecessorsAchieved(pv);
            pv.addPointsChangeListener(this);
            pv.addDirtyListener(this);
        });

    }

    private void verifyPredecessorsAchieved(ProgressView pv) {
        pv.setDisable(!group.isProgressUnlocked(handler.getCatalogue(), pv.getProgress()));
    }

    /**
     * @param activeMS If {@code == null}, the first entry in the choice is set.
     */
    private void updateProgressViews(Milestone activeMS) {
        LOGGER.trace(":updateProgressViews - MS: " + (activeMS != null ? activeMS.getName() : "null"));
        detachProgressViews();
        tfSum.setText("0");
        if (activeMS == null) {
            LOGGER.trace(":updateProgressViews - Setting default active ms");
            this.activeMS = cbMilestones.getItems().get(0);
        }
        loadActiveProgressViews(this.activeMS);

        attachProgressViews();
        calcActiveSum();
        if (cbMilestones.getSelectionModel().getSelectedItem() == null) {
            cbMilestones.getSelectionModel().select(this.activeMS);
        }
    }

    private void calcActiveSum() {
        double sum = group.getSumForMilestone(activeMS, handler.getCatalogue());
        tfSum.setText(StringUtils.prettyPrint(sum));
        activeProgressViews.forEach(this::verifyPredecessorsAchieved);
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

    private boolean isProgressActive(Progress p) {
        if (p.hasDefaultPercentage() || p.getDate() == null) {
            return true;
        } else if (p.getDate().before(activeMS.getDate())) {
            return false;
        } else {
            return true;
        }
    }

    private List<Progress> getActiveProgresses(Milestone activeMS) {
        LOGGER.trace(":getActiveProgresses - MS: " + activeMS.toString());
        List<Progress> active = new ArrayList<>();

        List<Requirement> activeReqs = handler.getCatalogue().getRequirementsByMilestone(activeMS.getOrdinal());
        LOGGER.trace(String.format("Active reqs: %s", activeReqs));
        Map<String, Progress> map = progressMap.get(activeMS.getOrdinal());
        LOGGER.trace(String.format("Found progresses: %s", map));
        for (Requirement r : activeReqs) {
            boolean progressExistent = map.containsKey(r.getName());
            LOGGER.debug(String.format("Scanning for %s", r));
            if (progressExistent) {
                Progress p = map.get(r.getName());
                boolean activeProgress = isProgressActive(p);
                LOGGER.debug(String.format("Checking: %s 's date: %b", p, activeProgress));
                if (activeProgress) {
                    active.add(p);
                }
            } else {
                Progress p = group.getProgressForRequirement(r);
                if (p == null) {
                    LOGGER.error(String.format("Could not find a progress for %s", r.getName()));
                    LOGGER.error(String.format("Dumping all known progress:\n%s", group.progressList().toString()));
                } else {
                    boolean activeProgress = isProgressActive(p);
                    LOGGER.debug(String.format("Checking: %s 's date: %b", p, activeProgress));
                    if (activeProgress) {
                        active.add(p);
                    }
                }


            }
        }
        active.sort(SortingUtils.getProgressComparator(handler.getCatalogue()));
        return active;
    }

}
