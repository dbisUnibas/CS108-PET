package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.Callback;
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

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EntityManager {

    private static final Logger LOGGER = LogManager.getLogger(EntityManager.class);
    private static EntityManager instance = null;

    /* === COMMON === */
    private Catalogue catalogue = null;
    private File catalogueFile = null;
    
    private File lastOpenLocation = null;
    
    private File lastSaveLocation = null;
    
    private File lastExportLocation = null;

    /* === CATALOGUE / EDITOR  RELATED === */
    private ObservableList<Requirement> observableRequirements;
    private ObservableList<Milestone> observableMilestones;
    private final SimpleDoubleProperty maxSumProperty = new SimpleDoubleProperty();

    /* === EVALUATOR RELATED === */
    private ObservableList<Group> groups = FXCollections.observableArrayList();
    private HashMap<String, File> groupFileMap = new HashMap<>();
    private Group lastOpenedGroup = null;


    public ObservableList<Group> groupList(){
        return groups;
    }


    public ObservableList<Requirement> getObservableRequirements() {
        return observableRequirements;
    }

    public ObservableList<Milestone> getObservableMilestones() {
        return observableMilestones;
    }

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

    private int lastOrdinal = -1;

    public void setCatalogue(Catalogue catalogue) {
        this.catalogue = catalogue;
        observableRequirements = FXCollections.observableList(catalogue.requirementList());
        observableMilestones = FXCollections.observableList(catalogue.milestoneList());
        lastOrdinal = catalogue.getLastOrdinal();

        maxSumProperty.set(catalogue.getSum() );

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
                            if(removeItem == null){
                                continue;
                            }
                            double sumPre = catalogue.getSum();
                            double change = removeItem.getMaxPoints();
                            double sumPost = sumPre - change;
                            LOGGER.trace(String.format(":onChanged - Remove: pre:%g, change:%g, post%g", sumPre, change, sumPost));
                            maxSumProperty.set(catalogue.getSum()-removeItem.getMaxPoints());
                        }
                        for (Requirement addItem : c.getAddedSubList()) {
                            if(addItem == null){
                                continue;
                            }
                            maxSumProperty.set(catalogue.getSum()+addItem.getMaxPoints() );
                        }
                    }

                }
                c.reset();
            }
        });
    }

    private static void runTask(ManagementTask task, Callback internalCallback, Callback doneCallback){
        Thread th = new Thread(task);
        th.setDaemon(true); // silently shutsdown upon exiting
        th.start();

        task.setOnFailed(event -> {
            throw new RuntimeException("Opening failed: ", task.getException());
        });

        if(internalCallback != null){
            task.setOnSucceeded(event -> {
                internalCallback.apply(null);

                if(doneCallback != null){
                    doneCallback.apply(null);
                }

            });
        }
    }

    private static void runTask(ManagementTask task){
        runTask(task, null, null);
    }

    private static void runTask(ManagementTask task, Callback internal){
        runTask(task, internal, null);
    }

    /**
     * @param file nontnull
     */
    public void openCatalogue(File file, Callback doneCallback) {
        LOGGER.trace(":openCat");

        OpenCatalogueTask openTask = new OpenCatalogueTask(file);

        runTask(openTask, () -> {
            setCatalogue(openTask.getValue() );
            catalogueFile = file;
            LOGGER.trace(":openCatalogue - Finished");

        }, doneCallback);

    }

    public boolean isCatalogueFilePresent(){
        return catalogueFile != null;
    }

    /**
     * caller must ensure via isCatalogueFilePresent if catalogue can be saved
     */
    public void saveCatalogue() {
        saveCatalogue(catalogueFile);
    }


    private void saveCatalogue(File file){
        SaveCatalogueTask saveTask = new SaveCatalogueTask(catalogue, file);

        runTask(saveTask, () -> {
            LOGGER.info("Saved catalogue to: "+file.getPath() );
            catalogueFile = file;
        });
    }

    public void saveAsCatalogue(File file){
        saveCatalogue(file);
    }

    public void exportCatalogue(File file){
        ExportCatalogueTask task = new ExportCatalogueTask(catalogue, file);
        runTask(task, () -> {
            LOGGER.info("Export done");
            lastExportLocation = ensureDirectory(file);
        });
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
        if(observableMilestones.remove(oldMS) ){
            newMS.setOrdinal(oldMS.getOrdinal());
            observableMilestones.add(newMS);
        }
    }

    private List<Requirement> getSuccessors(Requirement requirement){
        ArrayList<Requirement> list = new ArrayList<>();

        observableRequirements.forEach( r -> {
            if(r.getPredecessorNames().contains(requirement.getName() )){
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
        catalogue.setName(mod.getName() );
        catalogue.setDescription(mod.getDescription() );
        catalogue.setLecture(mod.getLecture() );
        catalogue.setSemester(mod.getSemester() );
    }

    public Catalogue getCatalogue() {
        return catalogue;
    }

    public boolean isGroupNameUnique(String name){
        for(Group g : groups){
            if(g.getName().equals(name)){
                return false;
            }
        }
        return true;
    }

    public void replaceGroup(Group gr, Group mod) {
        // DONT FORGET TO UPDATE **ALL** REFERENCES WHEN NAME CHANGED
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void removeGroup(Group del) {
        throw new UnsupportedOperationException("Not implmented yet");
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
    
    public boolean hasLastSaveLocation(){
        return lastSaveLocation != null;
    }
    
    public File getLastSaveLocation(){
        return lastSaveLocation;
    }
    
    public boolean hasLastExportLocation(){
        return lastExportLocation != null;
    }
    
    public File getLastExportLocation(){
        return lastExportLocation;
    }
    
    private File ensureDirectory( File f){
        if(f.isDirectory() ){
            return f;
        }else if(f.isFile() ){
            return f.getParentFile();
        }
        throw new IllegalArgumentException("File is neither directory nor file - symbolik link?");
    }

    public File getCatalogueFile() {
        return catalogueFile;
    }

    public void openGroup(File file, Callback done){
        LOGGER.entry(file);
        OpenGroupTask task = new OpenGroupTask(file);
        runTask(task, () -> {
            lastOpenLocation = ensureDirectory(file);
            lastOpenedGroup = task.getValue();
            openedGroup(file, lastOpenedGroup);
        }, done);
    }

    private void openedGroup(File file, Group lastOpenedGroup) {
        LOGGER.trace("Creating stuff for group");
    }

    public Group getLastOpenedGroup() {
        return lastOpenedGroup;
    }
}
