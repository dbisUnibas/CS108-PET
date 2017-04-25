package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.Callback;
import ch.unibas.dmi.dbis.reqman.common.ThrowingCallback;
import ch.unibas.dmi.dbis.reqman.core.*;
import ch.unibas.dmi.dbis.reqman.ui.StatusBar;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
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

    public static final String BACKUP_EXTENSION = "backup";
    public static final String GROUP_KEY = "group";
    public static final String CATALOGUE_KEY = "catalogue";
    private static final Logger LOGGER = LogManager.getLogger(EntityManager.class);
    public static final FileFilter BACKUP_FILTER = (file) -> {
        if (file.isDirectory()) {
            return false;
        } else {
            String name = file.getName();
            LOGGER.debug("Filtering... " + name);
            int index = name.lastIndexOf(".");
            if (index != -1) {
                String extension = name.substring(name.lastIndexOf("."));
                LOGGER.debug(" ... with extension: " + extension);
                if (BACKUP_EXTENSION.equals(extension.substring(1))) { // substring(1) so "." is gone
                    return true;
                }
            } else {
                LOGGER.debug("... extension-less files are ignored");
            }
            return false;
        }
    };
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
    private Exception lastOpenException = null;
    @Deprecated
    private StatusBar statusBar;

    private EntityManager() {

    }

    public static EntityManager getInstance() {
        LOGGER.traceEntry();
        if (instance == null) {
            LOGGER.trace(":getInstance - creating new");
            instance = new EntityManager();
        }
        return instance;
    }

    private static Thread createDeamon(ManagementTask task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true); // silently shutsdown upon exiting

        task.setOnFailed(event -> {
            throw new RuntimeException("Opening failed: ", task.getException());
        });
        return thread;
    }

    private static void runTaskThrowing(ThrowingManagementTask task, ThrowingCallback internalCallback, Callback doneCallback) {
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
    public void openCatalogue(File file, Consumer<Catalogue> doneProcessor) throws IllegalStateException {
        LOGGER.traceEntry();

        if (catalogue != null) {
            throw new IllegalStateException("There is already a catalogue open");
        }

        CheckedAsynchronousOperation<Catalogue> operation = OperationFactory.createLoadCatalogueOperation(file, doneProcessor);

        operation.addProcessor(c -> {
            setCatalogue(c);
            catalogueFile = file;
            lastOpenLocation = ensureDirectory(file);
            LOGGER.trace(":openCatalogue - Finished");
        }, 0);

        operation.start();


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

    @Deprecated // Remove when fully async implemented
    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    public void saveAsCatalogue(File file) {
        saveCatalogue(file);
    }

    public void exportCatalogue(File file) {
        LOGGER.traceEntry();
        CheckedAsynchronousOperation<Boolean> op = OperationFactory.createExportCatalogueOperation(catalogue, file);
        op.addProcessor(b -> {
            LOGGER.info("Export done");
            lastExportLocation = ensureDirectory(file);
        });
        op.start();
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
        LOGGER.traceEntry();
        milestone.setOrdinal(++lastOrdinal);
        return observableMilestones.add(milestone);
    }

    public boolean removeMilestone(Milestone milestone) {
        return observableMilestones.remove(milestone);
    }

    public boolean addRequirement(Requirement requirement) {
        return observableRequirements.add(requirement);
    }

    public boolean removeRequirement(Requirement requirement) {
        LOGGER.traceEntry();
        boolean result = observableRequirements.remove(requirement);
        LOGGER.debug(String.format("Removing %s, success: %b. CatReq.size: %d, Mng.size: %d", requirement.getName(), result, catalogue.getRequirements().size(), observableRequirements.size()));
        return result;
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

    public double getSum(int msOrdinal) {
        return catalogue.getSum(msOrdinal);
    }

    public double getSum() {
        return catalogue.getSum();
    }

    public Requirement getRequirementByName(String name) {
        return catalogue.getRequirementByName(name);
    }

    public boolean containsRequirement(String name) {
        return catalogue.containsRequirement(name);
    }

    public Requirement getRequirementForProgress(Progress progress) {
        return catalogue.getRequirementForProgress(progress);
    }

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
        LOGGER.traceEntry();
        if (observableMilestones.remove(oldMS)) {
            newMS.setOrdinal(oldMS.getOrdinal());
            observableMilestones.add(newMS);
        }
    }

    public void replaceRequirement(Requirement oldReq, Requirement newReq) {
        LOGGER.traceEntry("Params: {}, {}");
        // Case name changed: have to update all reqs, which use oldReq as predecessor
        if (!oldReq.getName().equals(newReq.getName())) {
            getSuccessors(oldReq).forEach(r -> {
                r.removePredecessorName(oldReq.getName());
                r.addPredecessorName(newReq.getName());
            });
            // Also, update the progress
            updateProgress(oldReq, newReq);
        }
        if (observableRequirements.remove(oldReq)) {
            observableRequirements.add(newReq);
        }
    }

    private void updateProgress(Requirement oldReq, Requirement newReq) {
        groups.forEach(g -> {
            Progress p = getProgressForRequirement(g, oldReq);
            if(p != null){
                p.setRequirementName(newReq.getName() );
            }
        });
    }

    public void modifyCatalogue(Catalogue mod) {
        LOGGER.traceEntry();
        catalogue.setName(mod.getName());
        catalogue.setDescription(mod.getDescription());
        catalogue.setLecture(mod.getLecture());
        catalogue.setSemester(mod.getSemester());
    }

    public Catalogue getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(Catalogue catalogue) {
        LOGGER.debug("Setting catalogue %s", catalogue);
        this.catalogue = catalogue;
        observableRequirements = FXCollections.observableList(catalogue.requirementList());
        observableMilestones = FXCollections.observableList(catalogue.milestoneList());
        lastOrdinal = catalogue.getLastOrdinal();

        maxSumProperty.set(catalogue.getSum());

        observableRequirements.addListener(new ListChangeListener<Requirement>() {
            @Override
            public void onChanged(Change<? extends Requirement> c) {
                LOGGER.traceEntry();
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
        groupFileMap.remove(del.getName());
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

    public File getCatalogueFile() {
        return catalogueFile;
    }

    public void openGroup(File file, Consumer<Group> done, Consumer<Exception> exHandler) {
        LOGGER.traceEntry("param: %s", file);

        CheckedAsynchronousOperation<Group> op = OperationFactory.createOpenGroupOperation(file);
        op.addProcessor(group -> this.openedGroup(file, group), -10);
        op.addProcessor(done);

        op.addValidator(group -> checkGroupConstraints(op, group));
        op.setExceptionHandler(exHandler);

        op.start();
    }

    public void openGroups(List<File> files, Consumer<List<Group>> callback, Consumer<Exception> exHandler) {
        LOGGER.traceEntry("Param: {}",files);

        CheckedAsynchronousOperation<List<Group>> op = OperationFactory.createOpenMultipleGroupOperation(files);

        op.addProcessor(groups -> openedGroups(files, groups), -10);
        op.addProcessor(callback);

        op.addValidator(groups -> {
            for (Group g : groups) {
                return checkGroupConstraints(op, g);
            }
            return true; // Actually unreachable?
        });

        op.start();
    }

    public void exportOverview(File exportFile){
        LOGGER.traceEntry("Params: {}", exportFile);
        CheckedAsynchronousOperation<Boolean> op = OperationFactory.createExportOverviewOperation(new OverviewSnapshot(catalogue, groups.toArray(new Group[0])), exportFile);
        op.setExceptionHandler(ex -> LOGGER.catching(Level.ERROR, ex));
        op.start();
    }

    public Exception getLastOpenException() {
        return lastOpenException;
    }

    public Group getLastOpenedGroup() {
        if (lastOpenedGroup == null) {
            throw new NullPointerException();
        }
        return lastOpenedGroup;
    }

    public ObservableList<Progress> getObservableProgress(Group provider) {
        return FXCollections.observableList(provider.progressList());
    }

    public Progress getProgressForRequirement(Group group, Requirement requirement) {
        return group.getProgressForRequirement(requirement);
    }

    public boolean hasGroupFile(Group group) {
        LOGGER.traceEntry("Param: %s", group);
        return groupFileMap.containsKey(group.getName());
    }

    public void saveGroup(Group group) {
        File f = groupFileMap.get(group.getName());
        saveGroup(group, f);
    }

    public void saveGroupAs(Group group, File file) {
        saveGroup(group, file);
    }

    public void exportAllGroups(File dir) {
        CheckedAsynchronousOperation<Boolean> op = OperationFactory.createExportMultipleGroupsOperation(dir, groups, catalogue);
        op.addProcessor(b -> {
            lastExportLocation = ensureDirectory(dir);
            LOGGER.info("All export done");
        });
        op.start();
    }

    public void saveAsBackup(Group g) {
        OperationFactory.createSaveAsBackupOperation(g, catalogueFile).start();
    }

    public void openBackupsIfExistent(Consumer<List<OpenBackupsTask.BackupObject>> callback) {
        CheckedAsynchronousOperation<List<OpenBackupsTask.BackupObject>> op = OperationFactory.createOpenBackupsOperation();
        op.addProcessor(list -> list.forEach(o -> {
            if (o.isCatalogue() && o.getCatalogue() != null) {

                setCatalogue(o.getCatalogue());
                catalogueFile = o.getLocation();
            } else {
                openedGroup(null, o.getGroup());
            }
        }), -10);

        op.addProcessor(callback);
        op.start();
    }

    public List<File> isAnyGroupFilePresent(List<File> files) {
        List<File> out = new ArrayList<>();
        for (File file : files) {
            for (File f : groupFileMap.values()) {
                if (f.equals(file)) {
                    out.add(file);
                }
            }
        }
        return out;
    }

    private void saveCatalogue(File file) {
        OperationFactory.createSaveCatalogueOperation(catalogue, file).start();
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

    private File ensureDirectory(File f) {
        if (f.isDirectory()) {
            return f;
        } else if (f.isFile()) {
            return f.getParentFile();
        }
        throw new IllegalArgumentException("File is neither directory nor file - symbolik link?");
    }

    private <T> boolean checkGroupConstraints(CheckedAsynchronousOperation<T> op, Group group) {
        if (!group.getCatalogueName().equals(catalogue.getName())) {
            op.setExceptionMessage("Invalid catalogue signature\n" +
                    "Expected: " + catalogue.getName() + "\n" +
                    "Found:    " + group.getCatalogueName());
            return false;
        }
        if (!isGroupNameUnique(group.getName())) {
            op.setExceptionMessage("Group name not unique");
            return false;
        }
        return true;
    }

    private void openedGroup(File file, Group group) throws CatalogueNameMismatchException, NonUniqueGroupNameException {
        LOGGER.traceEntry("File: %s, Group: %s", file, group);
        if (group == null) {
            throw new NullPointerException("Group null");
        }
        /*if (!group.getCatalogueName().equals(catalogue.getName())) {
            throw LOGGER.throwing(new CatalogueNameMismatchException(catalogue.getName(), group.getCatalogueName(), group.getName(), file) );
        }
        if(!isGroupNameUnique(group.getName()) ){
            throw LOGGER.throwing(new NonUniqueGroupNameException(group.getName() ) );
        }*/

        groups.add(group);
        LOGGER.trace(":openedGroup - Added group");

        if (file != null) {
            groupFileMap.put(group.getName(), file);
            LOGGER.trace(":openedGroup - stored file");
            lastOpenLocation = ensureDirectory(file);
            LOGGER.trace(":openedGroup - stored last location");
        }

        LOGGER.info("Successfully loaded group " + String.format("(%s)", group.getName()) + " to workspace.");
    }

    /**
     * Contract: files.size()==groups.size();
     *
     * @param files
     * @param groups
     */
    private void openedGroups(List<File> files, List<Group> groups) {
        LOGGER.traceEntry();
        for (int i = 0; i < files.size(); ++i) {
            openedGroup(files.get(i), groups.get(i));
        }
        LOGGER.trace("Finished loading of groups");
    }

    private void saveGroup(Group group, File file) {
        CheckedAsynchronousOperation<Boolean> op = OperationFactory.createSaveGroupOperation(file, group);
        op.addProcessor(b -> {
            LOGGER.info("SavedAs group (" + group.getName() + ") to " + file.getPath());
            groupFileMap.put(group.getName(), file);
        });
        op.start();
    }
}