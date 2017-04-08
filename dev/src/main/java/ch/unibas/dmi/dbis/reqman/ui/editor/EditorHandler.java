package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.management.EntityManager;
import ch.unibas.dmi.dbis.reqman.ui.editor.event.EditorEvent;
import ch.unibas.dmi.dbis.reqman.ui.editor.event.TargetEntity;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Layer between (G)UI and internal logic.
 *
 * @author loris.sauter
 */
public class EditorHandler implements EventHandler<EditorEvent> {

    private static final Logger LOGGER = LogManager.getLogger(EditorHandler.class);

    private EntityManager manager = EntityManager.getInstance();

    private EditorView editor = null;

    void setEditorView(EditorView view){
        this.editor = view;
    }

    @Override
    public void handle(EditorEvent event) {
        if (event != null) {
            if (EditorEvent.CREATION.equals(event.getEventType())) {
                handleCreation(event);
            } else if (EditorEvent.DELETION.equals(event.getEventType())) {
                handleDeletion(event);
            } else if (EditorEvent.MODIFICATION.equals(event.getEventType())) {
                handleModification(event);
            } else {
                throw new IllegalArgumentException("Cannot handle unknown event type: " + event.getEventType().toString());
            }
        }
        // Silently ignoring null events

    }

    /**
     * It must be guaranteed that the passed event is of type CREATION
     *
     * @param evt
     */
    public void handleCreation(EditorEvent evt) {
        switch (evt.getTargetEntity()) {
            case CATALOGUE:
                Catalogue cat = EditorPromptFactory.promptNewCatalogue();
                manager.setCatalogue(cat);
                setupEditor();
                break;
            case REQUIREMENT:
                Requirement req = EditorPromptFactory.promptNewRequirement(this);
                manager.addRequirement(req);
                break;
            case MILESTONE:
                Milestone ms = EditorPromptFactory.promptNewMilestone();
                manager.addMilestone(ms);
                break;
            default:
                throwInappropriateTargetEntity(evt.getTargetEntity());
        }
    }

    public void handleDeletion(EditorEvent evt) {
        switch(evt.getTargetEntity()){
            case CATALOGUE:
                throw new IllegalArgumentException("Cannot delete the catalogue");
            case REQUIREMENT:
                if(evt.getDeletion() instanceof RequirementTableView.ObservableRequirement){
                    RequirementTableView.ObservableRequirement obsReq = (RequirementTableView.ObservableRequirement)evt.getDeletion();
                    Requirement req = manager.getRequirementByName(obsReq.getName());
                    LOGGER.debug(String.format("Deletion Requirement: selected=%s, index=%d, result=%s", obsReq, evt.getIndex(), req));
                    manager.removeRequirement(req);
                    LOGGER.debug("size: "+editor.getRequirementsView().getRequirementsSize() );
                }else{
                    throw new RuntimeException("Something went really bad.");
                }

                break;
            case MILESTONE:
                if(evt.getDeletion() instanceof  Milestone){
                    Milestone milestone = (Milestone)evt.getDeletion();
                    manager.removeMilestone(milestone);
                }
                break;
                default:
                    throwInappropriateTargetEntity(evt.getTargetEntity());
        }
    }

    public void handleModification(EditorEvent evt) {
        switch(evt.getTargetEntity()){
            case CATALOGUE:
                break;
            case REQUIREMENT:

                break;
            case MILESTONE:

                break;
            default:
                throwInappropriateTargetEntity(evt.getTargetEntity());
        }
    }

    private final static void throwInappropriateTargetEntity(TargetEntity entity){
        throw new IllegalArgumentException("EditorHandler can only handle TargetEntity.CATALOGUE, TargetEntity.REQUIREMENT, TargetEntity.MILESTONE, but "+entity.toString()+" was given");
    }

    public void openCatalogue(File file) {
        LOGGER.trace(":openCatalogue "+String.format("File: %s",file.getPath()));
        manager.openCatalogue(file);

        setupEditor();
    }

    private void setupEditor(){
        editor.getRequirementsView().setRequirements(manager.getObservableRequirements());
        editor.getMilestoneView().setItems(manager.getObservableMilestones() );


        editor.getCatalogueView().setCatName(manager.getName());
        editor.getCatalogueView().setCatLecture(manager.getLecture());
        editor.getCatalogueView().setCatSemester(manager.getSemester());
        editor.getCatalogueView().maxPointsProperty().bind(manager.sumProperty() );

        editor.enableAll();
    }

    public void saveCatalogue() {
        manager.saveCatalogue();
    }

    public void saveAsCatalogue(File file) {
        manager.saveAsCatalogue(file);
    }

    public void exportCatalogue(File file) {
        manager.exportCatalogue(file);
    }

    public boolean isCatalogueFilePresent() {
        return manager.isCatalogueFilePresent();
    }

    public boolean isCatalogueLoaded() {
        return manager.isCatalogueLoaded();
    }

    public Milestone getMilestoneByOrdinal(int ordinal) {
        return manager.getMilestoneByOrdinal(ordinal);
    }

    public Requirement getRequirementByName(String name) {
        return manager.getRequirementByName(name);
    }

    public ObservableList<Milestone> getObservableMilestones() {
        return manager.getObservableMilestones();
    }

    public ObservableList<Requirement> getObservableRequirements() {
        return manager.getObservableRequirements();
    }
}
