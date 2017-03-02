package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.templating.RenderManager;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EvaluatorController {

    private EvaluatorScene evaluator;

    private Catalogue catalogue;

    private ObservableList<Group> groups;
    private Map<String, AssessmentView> groupAVMap = new TreeMap<>();
    private Map<String, File> groupFileMap = new TreeMap<>();
    private File lastLocation = null;


    public EvaluatorController(EvaluatorScene evaluator) {
        this.evaluator = evaluator;
        init();
    }

    public Catalogue getActiveCatalogue(){
        return catalogue;
    }

    private void init() {
        // groups etc
        groups = FXCollections.observableArrayList();
    }

    public List<Milestone> getMilestones() {
        return catalogue.getMilestones();
    }

    public Requirement getRequirementByName(String name) {
        Requirement r = null;
        for (Requirement req : catalogue.getRequirements()) {
            if (req.getName().equals(name)) {
                r = req;
            }
        }
        return r;
    }

    public void handleLoadCatalogue(ActionEvent event) {
        FileChooser fc = Utils.createCatalogueFileChooser("Load");
        File f = fc.showOpenDialog(evaluator.getWindow());
        if (f != null) {
            try {
                catalogue = JSONUtils.readCatalogueJSONFile(f);
                evaluator.getCatalogueInfoView().displayData(catalogue);
                evaluator.enableAll();
            } catch(UnrecognizedPropertyException ex){
                Utils.showErrorDialog("Failed loading catalogue", "The provided file could not be read as a catalogue.\nTry again with a catalogue file.");
            } catch (IOException e) {
                // TODO Handle exception
                e.printStackTrace();
            }
        }
    }

    public void handleAddGroup(ActionEvent event) {
        if (!isCatalogueSet()) {
            return;
        }
        Group group = EvaluatorPromptFactory.promptNewGroup(this);

        if (group != null) {
            addGroupToInternalStorage(group);
            addGroupTab(group);
        }
    }

    public boolean isGroupNameUnique(String name) {
        for (Group g : groups) {
            if (g.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    private void addGroupToInternalStorage(Group group) {
        groups.add(group);
        groupAVMap.put(group.getName(), new AssessmentView(this, group));
    }

    public void addGroupTab(Group active) {
        if (evaluator.isGroupTabbed(active)) {
            // Dont add another tab of the same group
        } else {
            evaluator.addGroupTab(groupAVMap.get(active.getName()));
        }


    }

    public List<Requirement> getRequirementsByMilestone(int ordinal) {
        return catalogue.getRequirementsByMilestone(ordinal);
    }

    public void handleRemoveGroup(ModifiableListView.RemoveEvent<Group> event) {
        Group selected = event.getSelected();
        if (selected != null) {
            groups.remove(selected);
            evaluator.removeTab(selected);
        }
    }

    private File lastOpenLocation = null;



    public void handleOpenGroup(ActionEvent event) {
        if (!isCatalogueSet()) {
            return;
        }
        FileChooser fc = Utils.createGroupFileChooser("Open");
        if(lastOpenLocation != null){
            fc.setInitialDirectory(lastOpenLocation);
        }
        File f = fc.showOpenDialog(evaluator.getWindow());
        if (f != null) {
            try {
                Group group = JSONUtils.readGroupJSONFile(f);
                if (!group.getCatalogueName().equals(catalogue.getName())) {
                    Utils.showErrorDialog("Catalogue signature failure", "The group loaded has a different catalogue name stored than currently active:\nGroups's catalgue name: " + group.getCatalogueName() + ", Currentl catalogue: " + catalogue.getName());
                    return;
                }
                if(!isGroupNameUnique(group.getName())){
                    Utils.showErrorDialog("Opening group failed", "There already exists a group with name:\n\n"+group.getName()+"\n\nYou have to rename the group manually if both groups are needed.");
                    return;
                }
                addGroupToInternalStorage(group);
                groupFileMap.put(group.getName(), f);
                lastOpenLocation = f.getParentFile();
                addGroupTab(group);
            } catch(UnrecognizedPropertyException ex){
                Utils.showErrorDialog("Failed opening group", "The provided file could not be read as a group.\nTry again with a group file.");
            }  catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleSaveGroup(ActionEvent event) {
        if (!isCatalogueSet()) {
            return;
        }
        Group active = evaluator.getActiveGroup();
        AssessmentView av = groupAVMap.get(active.getName());
        File f = groupFileMap.get(active.getName());
        if (f == null) {
            handleSaveAsGroup(event);
        } else {
            try {
                saveGroup(active, av, f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveGroup(Group group, AssessmentView av, File f) throws IOException {
        group.setProgressList(av.getProgressList());
        group.setProgressSummaryList(av.getSummaries());
        JSONUtils.writeToJSONFile(group, f);
        evaluator.unmarkDirty(group);
    }

    public void handleSaveAsGroup(ActionEvent event) {
        if (!isCatalogueSet()) {
            return;
        }
        Group active = evaluator.getActiveGroup();
        AssessmentView av = groupAVMap.get(active.getName());
        FileChooser fc = Utils.createGroupFileChooser("Save As");
        setupFileChooser(fc, active.getName());
        File f = fc.showSaveDialog(evaluator.getWindow());
        if (f != null) {
            try {
                saveGroup(active, av, f);
                groupFileMap.put(active.getName(), f);
                lastLocation = f.getParentFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupLastLocation(FileChooser fc) {
        if (lastLocation != null) {
            fc.setInitialDirectory(lastLocation);
        }
    }

    private void setupFileChooser(FileChooser fc, String proposedName) {
        setupLastLocation(fc);
        fc.setInitialFileName(proposedName);
    }

    public void handleModifyGroup(ActionEvent event) {
        if (!isCatalogueSet()) {
            return;
        }
        // NEED TO GET THE ACTIVE GROUP
    }

    public ObservableList<Group> getObservableGroups() {
        return groups;
    }

    public void handleExportGroup(ActionEvent event) {
        if (!isCatalogueSet()) {
            return;
        }
        for (Group g : groups) {
            if (!StringUtils.isNotEmpty(g.getExportFileName())) {
                g.setExportFileName(g.getName() + ".html");// TODO Handle correctly, with warning or so
            }
        }
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choose an export folder");
        File dir = dc.showDialog(evaluator.getWindow());

        RenderManager manager = createAndPrepareRenderManager();

        for (Group g : groups) {
            manager.setGroup(g);
            String html = manager.renderGroup(g);
            try {
                PrintWriter pw = new PrintWriter(dir.getPath() + System.getProperty("file.separator") + g.getExportFileName());
                pw.write(html);
                pw.flush();
                pw.close();
                System.out.println("============================");
                System.out.println(" FINISHED : " + g.getName());
                System.out.println("============================");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private RenderManager createAndPrepareRenderManager() {
        RenderManager manager = new RenderManager(catalogue);

        String groupTempate = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<link href=\"http://fonts.googleapis.com/icon?family=Material+Icons\" rel=\"stylesheet\">\n" +
                "\t<link type=\"text/css\" rel=\"stylesheet\" href=\"css/materialize.min.css\"  media=\"screen,projection\"/>\n" +
                "\t<link type=\"text/css\" rel=\"stylesheet\" href=\"css/achievements.css\"/>\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "\t<title>${group.name}</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<br><br><br>\n" +
                "\n" +
                "<h1>${group.name}</h1>\n" +
                "<div class=\"container\">\n" +
                "\t\n" +
                "\t${group.milestones}\n" +
                "\n" +
                "\n" +"<br><br>\n" +
                "<table class=\"bordered responsive-table\">\n" +
                "<thead>\n" +
                "<tr>\n" +
                "<th>${catalogue.milestoneName[1]}</th>\n" +
                "<th>${catalogue.milestoneName[2]}</th>\n" +
                "<th>${catalogue.milestoneName[3]}</th>\n" +
                "<th>${catalogue.milestoneName[4]}</th>\n" +
                "<th>${catalogue.milestoneName[5]}</th>\n" +
                "<th>Total</th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>${group.sumMS[1]}</td>\n" +
                "<td>${group.sumMS[2]}</td>\n" +
                "<td>${group.sumMS[3]}</td>\n" +
                "<td>${group.sumMS[4]}</td>\n" +
                "<td>${group.sumMS[5]}</td>\n" +
                "<td>${group.sumTotal}</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "  <!--Import jQuery before materialize.js-->\n" +
                "  <script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-2.1.1.min.js\"></script>\n" +
                "  <script type=\"text/javascript\" src=\"js/materialize.min.js\"></script>\n" +
                "</body>\n" +
                "</html>";

        String groupMilestoneTemplate = "<div class=\"milestone-content-container\">\n" +
                "\t<div class=\"milestone-summary z-depth-3 hoverable\">\n" +
                "\t\t<div class=\"milestone-summary-header\">${groupMilestone.name}: Summary</div>\n" +
                "\t\t<div class=\"milestone-summary-text\">${groupMilestone.comment}</div>\n" +
                "\t\t<div class=\"milestone-summary-progress\">\n" +
                "\t\t\t<div class=\"milestone-summary-bar\">\n" +
                "\t\t\t\t<div class=\"progress\">\n" +
                "\t\t\t\t\t<div class=\"determinate\" style=\"width: ${groupMilestone.percentage}%\"></div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div><!-- .milestone-summary-bar -->\n" +
                "\t\t\t<div class=\"milestone-summary-points\">${groupMilestone.sum} / ${milestone.sumMax}</div>\n" +
                "\t\t</div>\n" +
                "\t</div><!-- .milestone-summary -->\n" +
                "\t<div class=\"milestone-achievements-list\">\n" +
                "\t\t${groupMilestone.progressList}\n" +
                "\t</div><!-- .milestone-achievements-list -->\n" +
                "</div><!-- .milestone-content-container -->\n";

        String progressTemplate = "<div class=\"achievement ${requirement.meta[category]} ${requirement.mandatory[][bonus]} ${progress.hasPoints[achieved][]} ${requirement.malus[malus][]} z-depth-2 hoverable\">\n" +
                "\t<div class=\"achievement-img-container\">\n" +
                "\t\t<img src=\"${requirement.meta[image]}\">\n" +
                "\t</div>\n" +
                "\t<div class=\"achievement-content-container\">\n" +
                "\t\t<div class=\"achievement-header\">\n" +
                "\t\t\t<span class=\"achievement-title\">${requirement.name}</span>\n" +
                "\t\t\t<span class=\"achievement-points\">${progress.points} of ${requirement.maxPoints}</span>\n" +
                "\t\t\t<span class=\"achievement-date\">${requirement.minMS.name}</span>\n" +
                "\t\t</div><!-- achievement-header -->\n" +
                "\t\t<span class=\"achievement-description\">${requirement.description}</span>\n" +
                "\t</div><!-- .achievement-content-container -->\n" +
                "</div><!-- .achievement -->";


        manager.parseGroupMilestoneTemplate(groupMilestoneTemplate);
        manager.parseGroupTemplate(groupTempate);
        manager.parseProgressTemplate(progressTemplate);

        return manager;
    }

    public boolean isCatalogueSet() {
        return catalogue != null;
    }

    public void markDirty(Group modified) {
        evaluator.markDirty(modified);
    }
}
