package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.Callback;
import ch.unibas.dmi.dbis.reqman.common.ThrowingCallback;
import ch.unibas.dmi.dbis.reqman.core.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EntityManager {

    private static final Logger LOGGER = LogManager.getLogger(EntityManager.class);
    private static EntityManager instance = null;
    private final SimpleDoubleProperty maxSumProperty = new SimpleDoubleProperty();
    /* === COMMON === */
    private Catalogue catalogue = null;
    private File catalogueFile = null;
    private File lastOpenLocation = null;
    private File lastSaveLocation = null;
    private File lastExportLocation = null;
    /* === CATALOGUE / EDITOR  RELATED === */
    private ObservableList<Requirement> observableRequirements;
    private ObservableList<Milestone> observableMilestones;
    /* === EVALUATOR RELATED === */
    private ObservableList<Group> groups = FXCollections.observableArrayList();
    private HashMap<String, File> groupFileMap = new HashMap<>();
    private Group lastOpenedGroup = null;
    private int lastOrdinal = -1;


    private EntityManager() {

    }

    public static EntityManager getInstance() {
        LOGGER.trace(":getInstance");
        if (instance == null) {
            LOGGER.trace(":getInstance - creating new");
            instance = new EntityManager();
        }
        return instance;
    }

    private static void runTask(ManagementTask task, Callback internalCallback, Callback doneCallback){
        Thread th = createDeamon(task);


        task.setOnFailed(event -> {
            throw new RuntimeException("Opening failed: ", task.getException());
        });

        if (internalCallback != null) {
            task.setOnSucceeded(event -> {
                internalCallback.apply(null);


                if (doneCallback != null) {
                    doneCallback.apply(null);
                }

            });
        }
        th.start();
    }

    private static Thread createDeamon(ManagementTask task){
        Thread thread = new Thread(task);
        thread.setDaemon(true); // silently shutsdown upon exiting

        task.setOnFailed(event -> {
            throw new RuntimeException("Opening failed: ", task.getException());
        });
        return thread;
    }

    private static void runTaskThrowing(ThrowingManagementTask task, ThrowingCallback internalCallback, Callback doneCallback){
        Thread deamon = createDeamon(task);
        if (internalCallback != null) {
            task.setThrowingOnSucceeded(event -> {
                internalCallback.callThrowing();

                if (doneCallback != null) {
                    doneCallback.apply(null);
                }

            });
        }
        deamon.start();
    }

    private static void runTask(ManagementTask task){
        runTask(task, null, null);
    }

    private static void runTask(ManagementTask task, Callback internal){
        runTask(task, internal, null);
    }

    public ObservableList<Group> groupList() {
        return groups;
    }

    public ObservableList<Requirement> getObservableRequirements() {
        return observableRequirements;
    }

    public ObservableList<Milestone> getObservableMilestones() {
        return observableMilestones;
    }

    /**
     * @param file nontnull
     */
    public void openCatalogue(File file, Callback doneCallback){
        LOGGER.trace(":openCat");

        OpenCatalogueTask openTask = new OpenCatalogueTask(file);

        try {
            runTask(openTask, () -> {
                setCatalogue(openTask.getValue());
                catalogueFile = file;
                lastOpenLocation = ensureDirectory(file);
                LOGGER.trace(":openCatalogue - Finished");

            }, doneCallback);
        } catch (Exception e) {
            e.printStackTrace(); // TODO handle
        }

    }

    public boolean isCatalogueFilePresent() {
        return catalogueFile != null;
    }

    /**
     * caller must ensure via isCatalogueFilePresent if catalogue can be saved
     */
    public void saveCatalogue() {
        saveCatalogue(catalogueFile);
    }

    private void saveCatalogue(File file) {
        SaveCatalogueTask saveTask = new SaveCatalogueTask(catalogue, file);

        try {
            runTask(saveTask, () -> {
                LOGGER.info("Saved catalogue to: " + file.getPath());
                catalogueFile = file;
            });
        } catch (Exception e) {
            e.printStackTrace(); // TODO Handle
        }
    }

    public void saveAsCatalogue(File file) {
        saveCatalogue(file);
    }

    public void exportCatalogue(File file) {
        ExportCatalogueTask task = new ExportCatalogueTask(catalogue, file);
        try {
            runTask(task, () -> {
                LOGGER.info("Export done");
                lastExportLocation = ensureDirectory(file);
            });
        } catch (Exception e) {
            e.printStackTrace(); // TODO handle
        }
    }

    public String getLecture() {
        return catalogue.getLecture();
    }

    public String getCatalogueName() {
        return catalogue.getName();
    }

    public String getDescription() {
        return catalogue.getDescription();
    }

    public String getSemester() {
        return catalogue.getSemester();
    }

    public boolean addMilestone(Milestone milestone) {
        milestone.setOrdinal(++lastOrdinal);
        return observableMilestones.add(milestone);
    }

    public boolean removeMilestone(Milestone milestone) {
        return observableMilestones.remove(milestone);
    }

    List<Milestone> getMilestones() {
        return catalogue.getMilestones();
    }

    public boolean addRequirement(Requirement requirement) {
        return observableRequirements.add(requirement);
    }

    public boolean removeRequirement(Requirement requirement) {
        boolean result = observableRequirements.remove(requirement);
        LOGGER.debug(String.format("Removing %s, success: %b. CatReq.size: %d, Mng.size: %d", requirement.getName(), result, catalogue.getRequirements().size(), observableRequirements.size()));
        return result;
    }

    public List<Requirement> getRequirements() {
        return catalogue.getRequirements();
    }

    public Milestone getMilestoneByOrdinal(int ordinal) {
        return catalogue.getMilestoneByOrdinal(ordinal);
    }

    public List<Requirement> getRequirementsByMilestone(int ordinal) {
        return catalogue.getRequirementsByMilestone(ordinal);
    }

    public List<Requirement> getRequirementsWithMinMS(int ordinal) {
        return catalogue.getRequirementsWithMinMS(ordinal);
    }

    @JsonIgnore
    public double getSum(int msOrdinal) {
        return catalogue.getSum(msOrdinal);
    }

    @JsonIgnore
    public double getSum() {
        return catalogue.getSum();
    }

    @JsonIgnore
    public Requirement getRequirementByName(String name) {
        return catalogue.getRequirementByName(name);
    }

    @JsonIgnore
    public boolean containsRequirement(String name) {
        return catalogue.containsRequirement(name);
    }

    @JsonIgnore
    public Requirement getRequirementForProgress(Progress progress) {
        return catalogue.getRequirementForProgress(progress);
    }

    @JsonIgnore
    public Milestone getMilestoneForProgress(Progress progress) {
        return catalogue.getMilestoneForProgress(progress);
    }

    public SimpleDoubleProperty sumProperty() {
        return maxSumProperty;
    }

    public boolean isCatalogueLoaded() {
        return catalogue != null;
    }

    public void replaceMilestone(Milestone oldMS, Milestone newMS) {
        if (observableMilestones.remove(oldMS)) {
            newMS.setOrdinal(oldMS.getOrdinal());
            observableMilestones.add(newMS);
        }
    }

    private List<Requirement> getSuccessors(Requirement requirement) {
        ArrayList<Requirement> list = new ArrayList<>();

        observableRequirements.forEach(r -> {
            if (r.getPredecessorNames().contains(requirement.getName())) {
                list.add(r);
            }
        });

        return list;
    }

    public void replaceRequirement(Requirement oldReq, Requirement newReq) {
        // Case name changed: have to update all reqs, which use oldReq as predecessor
        if (!oldReq.getName().equals(newReq.getName())) {
            getSuccessors(oldReq).forEach(r -> {
                r.removePredecessorName(oldReq.getName());
                r.addPredecessorName(newReq.getName());
            });
        }
        if (observableRequirements.remove(oldReq)) {
            observableRequirements.add(newReq);
        }
    }

    public void modifyCatalogue(Catalogue mod) {
        catalogue.setName(mod.getName());
        catalogue.setDescription(mod.getDescription());
        catalogue.setLecture(mod.getLecture());
        catalogue.setSemester(mod.getSemester());
    }

    public Catalogue getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(Catalogue catalogue) {
        this.catalogue = catalogue;
        observableRequirements = FXCollections.observableList(catalogue.requirementList());
        observableMilestones = FXCollections.observableList(catalogue.milestoneList());
        lastOrdinal = catalogue.getLastOrdinal();

        maxSumProperty.set(catalogue.getSum());

        observableRequirements.addListener(new ListChangeListener<Requirement>() {
            @Override
            public void onChanged(Change<? extends Requirement> c) {
                LOGGER.trace(":changed");
                while (c.next()) {
                    if (c.wasPermutated()) {
                        // Permutation
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {

                        }
                    } else if (c.wasUpdated()) {
                        // Update
                        LOGGER.error("UPDATED"); // TODO Handle
                    } else {
                        for (Requirement removeItem : c.getRemoved()) {
                            if (removeItem == null) {
                                continue;
                            }
                            double sumPre = catalogue.getSum();
                            double change = removeItem.getMaxPoints();
                            double sumPost = sumPre - change;
                            LOGGER.trace(String.format(":onChanged - Remove: pre:%g, change:%g, post%g", sumPre, change, sumPost));
                            maxSumProperty.set(catalogue.getSum() - removeItem.getMaxPoints());
                        }
                        for (Requirement addItem : c.getAddedSubList()) {
                            if (addItem == null) {
                                continue;
                            }
                            maxSumProperty.set(catalogue.getSum() + addItem.getMaxPoints());
                        }
                    }

                }
                c.reset();
            }
        });
    }

    public boolean isGroupNameUnique(String name) {
        for (Group g : groups) {
            if (g.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public void replaceGroup(Group gr, Group mod) {
        // DONT FORGET TO UPDATE **ALL** REFERENCES WHEN NAME CHANGED
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean removeGroup(Group del) {
        return groups.remove(del);
    }

    public void addGroup(Group gr) {
        groups.add(gr);
    }


    public boolean hasLastOpenLocation() {
        return lastOpenLocation != null;
    }

    public File getLastOpenLocation() {
        return lastOpenLocation;
    }

    public boolean hasLastSaveLocation() {
        return lastSaveLocation != null;
    }

    public File getLastSaveLocation() {
        return lastSaveLocation;
    }

    public boolean hasLastExportLocation() {
        return lastExportLocation != null;
    }

    public File getLastExportLocation() {
        return lastExportLocation;
    }

    private File ensureDirectory(File f) {
        if (f.isDirectory()) {
            return f;
        } else if (f.isFile()) {
            return f.getParentFile();
        }
        throw new IllegalArgumentException("File is neither directory nor file - symbolik link?");
    }

    public File getCatalogueFile() {
        return catalogueFile;
    }

    private Exception lastOpenException = null;

    public void openGroup(File file, Consumer<Group> done) {
        LOGGER.entry(file);
        OpenGroupTask task = new OpenGroupTask(file);
            runTaskThrowing(task, () -> {
                if(task.getLastException() != null){ // NOT WORKING
                    lastOpenException = task.getLastException();
                }
                openedGroup(file, task.getValue());
            }, () -> {
                done.accept(task.getValue());
            } );
    }

    public void openGroups(List<File> files, Consumer<List<Group>> callback){
        LOGGER.entry(files);
        OpenMultipleGroupsTask task = new OpenMultipleGroupsTask(files);
        runTask(task, () -> {
            openedGroups(files, task.getValue());
        },() -> {
            callback.accept(task.getValue());
        });
    }

    public Exception getLastOpenException(){
        return lastOpenException;
    }

    private void openedGroup(File file, Group group) throws CatalogueNameMismatchException, NonUniqueGroupNameException {
        LOGGER.trace(":openedGroup");
        LOGGER.entry(file, group);
        if(group == null || file == null){
            throw new NullPointerException("Group or file null");
        }
        /*if (!group.getCatalogueName().equals(catalogue.getName())) {
            throw LOGGER.throwing(new CatalogueNameMismatchException(catalogue.getName(), group.getCatalogueName(), group.getName(), file) );
        }
        if(!isGroupNameUnique(group.getName()) ){
            throw LOGGER.throwing(new NonUniqueGroupNameException(group.getName() ) );
        }*/
        groupFileMap.put(group.getName(), file);
        LOGGER.trace(":openedGroup - stored file");
        groups.add(group);
        LOGGER.trace(":openedGroup - Added group");
        lastOpenLocation = ensureDirectory(file);
        LOGGER.trace(":openedGroup - stored last location");
        LOGGER.info("Successfully loaded group "+String.format("(%s)", group.getName())+" to workspace.");
    }

    /**
     * Contract: files.size()==groups.size();
     * @param files
     * @param groups
     */
    private void openedGroups(List<File> files, List<Group> groups){
        LOGGER.trace(":openedGroups");
        for(int i=0; i<files.size(); ++i){
            openedGroup(files.get(i), groups.get(i));
        }
        LOGGER.trace("Finished loading of groups");
    }

    public Group getLastOpenedGroup() {
        if(lastOpenedGroup == null){
            throw new NullPointerException();
        }
        return lastOpenedGroup;
    }

    public ObservableList<Progress> getObservableProgress(Group provider){
        return FXCollections.observableList(provider.progressList());
    }

    public boolean hasGroupFile(Group active) {
        LOGGER.trace(":hasGroupFile");
        LOGGER.entry(active);
        return groupFileMap.containsKey(active.getName() );
    }

    public void saveGroup(Group group) {
        File f = groupFileMap.get(group.getName());
        SaveGroupTask task = new SaveGroupTask(f, group);
        runTask(task, () -> {
            LOGGER.info("Saved group ("+group.getName()+") to "+f.getPath());
        });
    }

    public void saveGroupAs(Group group, File file){
        SaveGroupTask task = new SaveGroupTask(file, group);
        runTask(task, () -> {
            LOGGER.info("SavedAs group ("+group.getName()+") to "+file.getPath());
            groupFileMap.put(group.getName(), file);
        });
    }

    public void exportAllGroups(File dir) {
        ExportMultipleGroupTask task = new ExportMultipleGroupTask(dir, groups, catalogue);
        runTask(task, () -> {
            lastExportLocation = ensureDirectory(dir);
            LOGGER.info("All export done");
        });
    }
}
