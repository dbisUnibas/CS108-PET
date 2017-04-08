package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Progress;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
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
        observableMilestones = FXCollections.observableList(catalogue.getMilestones());
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
                            double sumPre = catalogue.getSum();
                            double change = removeItem.getMaxPoints();
                            double sumPost = sumPre - change;
                            LOGGER.trace(String.format(":onChanged - Remove: pre:%g, change:%g, post%g", sumPre, change, sumPost));
                            sumProperty.set(catalogue.getSum()-removeItem.getMaxPoints());
                        }
                        for (Requirement addItem : c.getAddedSubList()) {
                            sumProperty.set(catalogue.getSum()+addItem.getMaxPoints() );
                        }
                    }

                }
                c.reset();
            }
        });
    }

    /**
     * @param file nontnull
     */
    public void openCatalogue(File file) {
        LOGGER.trace(":openCat");
        //Platform.runLater(() -> {
            LOGGER.trace(":openCatalogue - started");
            try {
                Catalogue cat = JSONUtils.readCatalogueJSONFile(file);
                setCatalogue(cat);
                catalogueFile = file;
                LOGGER.info("Successfully opened catalogue from " + file.getPath());
            } catch (IOException e) {
                e.printStackTrace(); // TODO handling
            }
        //});
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
        Platform.runLater(() -> {
            try {
                JSONUtils.writeToJSONFile(catalogue, file);
                LOGGER.info("Saved catalogue to: "+file.getPath());
            } catch (IOException e) {
                e.printStackTrace(); // TODO Handling
            }
        });
    }

    public void saveAsCatalogue(File file){
        saveCatalogue(file);
    }

    public void exportCatalogue(File file){
        LOGGER.error("Not implemented yet");
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
}
