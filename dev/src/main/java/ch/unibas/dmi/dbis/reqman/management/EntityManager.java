package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.Callback;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Progress;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class EntityManager {

    private static final Logger LOGGER = LogManager.getLogger(EntityManager.class);
    private static EntityManager instance = null;
    private Catalogue catalogue = null;
    private File catalogueFile = null;
    private ObservableList<Requirement> observableRequirements;
    private ObservableList<Milestone> observableMilestones;
    private final SimpleDoubleProperty sumProperty = new SimpleDoubleProperty();

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

        sumProperty.set(catalogue.getSum() );

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
                            sumProperty.set(catalogue.getSum()-removeItem.getMaxPoints());
                        }
                        for (Requirement addItem : c.getAddedSubList()) {
                            if(addItem == null){
                                continue;
                            }
                            sumProperty.set(catalogue.getSum()+addItem.getMaxPoints() );
                        }
                    }

                }
                c.reset();
            }
        });
    }

    private static void runTask(ManagementTask task){
        Thread th = new Thread(task);
        th.setDaemon(true); // silently shutsdown upon exiting
        th.start();
    }
    /**
     * @param file nontnull
     */
    public void openCatalogue(File file, Callback doneCallback) {
        LOGGER.trace(":openCat");

        OpenCatalogueTask openTask = new OpenCatalogueTask(file);

        openTask.setOnFailed(event -> {
            throw new RuntimeException("Opening failed: ", openTask.getException());
        });
        openTask.setOnSucceeded(event -> {
            setCatalogue(openTask.getValue() );
            catalogueFile = file;
            LOGGER.trace(":openCatalogue finished");
            doneCallback.apply(null);
        });

        runTask(openTask);

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
        saveTask.setOnFailed(event -> {
            throw new RuntimeException("Failed saving catalogue.",saveTask.getException());
        });

        saveTask.setOnSucceeded(event -> {
            LOGGER.info("Saved catalogue to: "+file.getPath() );
            catalogueFile = file;
        });

        runTask(saveTask);
    }

    public void saveAsCatalogue(File file){
        saveCatalogue(file);
    }

    public void exportCatalogue(File file){
        ExportCatalogueTask task = new ExportCatalogueTask(catalogue, file);
        task.setOnFailed(event -> {
            throw new RuntimeException("Failed exporting catalgoue", task.getException());
        });
        task.setOnSucceeded(event -> {
            LOGGER.info("Export done");
        });
        runTask(task);
    }

    public String getLecture() {
        return catalogue.getLecture();
    }

    public String getName() {
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
        return sumProperty;
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
}
