package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.templating.RenderManager;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class EditorController  {

    private Catalogue catalogue;

    private Stage controlledStage;

    private EditorApplication editor;

    private ObservableList<Requirement> observableReqs = FXCollections.observableArrayList();
    private ObservableList<Milestone> observableMs = FXCollections.observableArrayList();

    public EditorController(EditorApplication editor, Stage controlledStage){
        this.editor = editor;
        this.controlledStage = controlledStage;
    }

    public void openCatalogue(Catalogue catalogue){
        this.catalogue = catalogue;
        observableReqs = FXCollections.observableArrayList(catalogue.getRequirements() );
        observableMs = FXCollections.observableArrayList(catalogue.getMilestones() );
        editor.passRequirementsToView(observableReqs);
        editor.passMilestonesToView(observableMs);
        updateTitle();
    }

    public Catalogue getCatalogue(){
        catalogue.setRequirements(observableReqs);
        catalogue.setMilestones(observableMs);
        return catalogue;
    }

    public ObservableList<Requirement> getObservableRequirements(){
        return observableReqs;
    }

    public ObservableList<Milestone> getObservableMilestones(){
        return observableMs;
    }

    public void handleAddRequirement(ActionEvent event){
        if(!isCatalogueSet() ){
            return; // Prevent open prompt from accelerator even if no catalogue is set
        }
        Requirement r = EditorPromptFactory.promptNewRequirement(this);
        if(r != null){ // user may cancelled the prompt
            observableReqs.add(r );
        }

    }

    public void handleRemoveRequirement(ModifiableListView.RemoveEvent<Requirement> event){
        if(event.getSelected() != null){ // If nothing selected - don't remove it!
            observableReqs.remove(event.getSelectedIndex() );
        }

    }

    public void handleAddMilestone(ActionEvent event){
        if(!isCatalogueSet() ){
            return; // Prevent open prompt from accelerator even if no catalogue is set
        }
        Milestone m = EditorPromptFactory.promptNewMilestone();
        if(m != null){
            m.setOrdinal(getNextMsOrdinal() );
            observableMs.add(m);
        }
    }

    private boolean isCatalogueSet(){
        return catalogue != null;
    }

    private int getNextMsOrdinal(){
        ArrayList<Milestone> temp = new ArrayList<>(observableMs);
        temp.sort((ms1, ms2) -> {
            // TODO nullcheck
            if(ms1.getOrdinal() < ms2.getOrdinal() ){
                return -1;
            }else if(ms1.getOrdinal() == ms2.getOrdinal() ){
                return 0;
            }else{
                return 1;
            }
        });
        if(temp.isEmpty() ){
            return 1;
        }
        return temp.get(temp.size()-1).getOrdinal()+1;
    }

    public void handleRemoveMilestone(ModifiableListView.RemoveEvent<Milestone> event){
        if(event.getSelected() != null){ // If nothing selected - don't remove it!
            observableMs.remove(event.getSelectedIndex());
        }

    }

    public void handleNewCatalogue(ActionEvent event){
        Catalogue cat = EditorPromptFactory.promptNewCatalogue();
        if(cat != null){
            this.catalogue = cat;
            updateTitle();
            editor.enableAll();
        }
    }

    public void handleModifyCatalogue(ActionEvent event){
        Catalogue updated = EditorPromptFactory.promptCatalogue(catalogue);
        if(updated != null){
            this.catalogue = updated;
            updateTitle();
        }
    }

    private File catalogueFile = null;

    public void handleSaveAsCatalogue(ActionEvent event){
        FileChooser saveChooser = createCatalogueFileChooser("Save As");
        catalogueFile = saveChooser.showSaveDialog(controlledStage);
        try {
            JSONUtils.writeToJSONFile(getCatalogue(), catalogueFile); // Important to use getCatalogue as the reqs and ms are set there
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSaveCatalogue(ActionEvent event){
        if(catalogueFile == null){
            handleSaveAsCatalogue(event);
        }

        try {
            JSONUtils.writeToJSONFile(getCatalogue(), catalogueFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileChooser createCatalogueFileChooser(String action){
        FileChooser fc = new FileChooser();
        fc.setTitle(action+" Catalogue");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("Any", "*.*")
        );
        return fc;
    }

    public void handleExportCatalogue(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Catalogue");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HTML", "*.html"));
        File f = fc.showSaveDialog(controlledStage);
        if(f != null){
            // user did not abort file choose
            try {
                hardcodeExport(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        /*
        // OLD code:
        SimpleCatalogueExporter exporter = new SimpleCatalogueExporter(getCatalogue() );
        String html = exporter.exportHTML();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(html);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
         */

    }

    public void handleOpenCatalogue(ActionEvent event){
        FileChooser openChooser = createCatalogueFileChooser("Open");
        catalogueFile = openChooser.showOpenDialog(controlledStage);
        try {
            openCatalogue(JSONUtils.readCatalogueJSONFile(catalogueFile));
            System.out.println("===");
            System.out.println(catalogue.getSum(1));
            System.out.println(catalogue.getSum(2));
            System.out.println(catalogue.getSum(3));
            System.out.println(catalogue.getSum(4));
            System.out.println(catalogue.getSum(5));
            System.out.println(catalogue.getSum());
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.enableAll();
    }


    private void updateTitle(){
        StringBuffer sb = new StringBuffer("ReqMan: Editor");
        sb.append(" - ");
        sb.append(catalogue.getName() != null ? catalogue.getName() : "N/A");
        sb.append(" (");
        sb.append(catalogue.getLecture() != null ? catalogue.getLecture() : "N/A" );
        sb.append(" @ ");
        sb.append(catalogue.getSemester() != null ? catalogue.getSemester() : "N/A" );
        sb.append(")");
        controlledStage.setTitle(sb.toString());
    }


    public void handleModifyRequirement(Requirement sel) {
        Requirement update = EditorPromptFactory.promptRequirement(this, sel);
        int index = observableReqs.indexOf(sel);
        if(update != null){
            observableReqs.remove(index);
            observableReqs.add(index, update);
        }
    }

    public void handleModifyMilestone(Milestone sel){
        Milestone update = EditorPromptFactory.promptMilestone(sel);
        int index = observableMs.indexOf(sel);
        if(update != null){
            observableMs.remove(index);
            observableMs.add(index, update);
        }
    }

    public Milestone getMilestoneByOrdinal(int ordinal) {
        Milestone result = null;
        for(Milestone ms : observableMs){
            if(ms.getOrdinal() == ordinal){
                result = ms;
            }
        }
        return result;
    }

    public Requirement findRequirementByName(String name){
        Requirement result = null;
        for(Requirement req : observableReqs){
            if(req.getName().equals(name)){
                result = req;
            }
        }
        return result;
    }

    private void hardcodeExport(File f) throws FileNotFoundException {
        //RenderManager manager = new RenderManager(getCatalogue());//get catalogue assembles the catalogue
        RenderManager manager = new RenderManager(catalogue);
        String catHTML = "  <!DOCTYPE html>\n" +
                "  <html>\n" +
                "\t<head>\n" +
                "\t\t<link href=\"http://fonts.googleapis.com/icon?family=Material+Icons\" rel=\"stylesheet\">\n" +
                "\t\t<link type=\"text/css\" rel=\"stylesheet\" href=\"css/materialize.min.css\"  media=\"screen,projection\"/>\n" +
                "\t\t<link type=\"text/css\" rel=\"stylesheet\" href=\"css/achievements.css\"/>\n" +
                "\t\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "\t</head>\n" +
                "    <body>\n" +
                "\t\n" +
                "\t<br><br><br>\n" +
                "\t\n" +
                "\t<div class=\"container\">\n" +
                "        \n" +
                "\t\t${catalogue.requirements}\n" +
                "<br><br>\n" +
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
                "<td>${catalogue.sumMS[1]}</td>\n" +
                "<td>${catalogue.sumMS[2]}</td>\n" +
                "<td>${catalogue.sumMS[3]}</td>\n" +
                "<td>${catalogue.sumMS[4]}</td>\n" +
                "<td>${catalogue.sumMS[5]}</td>\n" +
                "<td>${catalogue.sumTotal}</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "\t\n" +
                "\t\n" +
                "      <!--Import jQuery before materialize.js-->\n" +
                "      <script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-2.1.1.min.js\"></script>\n" +
                "      <script type=\"text/javascript\" src=\"js/materialize.min.js\"></script>\n" +
                "    </body>\n" +
                "  </html>";

        String reqHTML = "<div class=\"achievement ${requirement.meta[category]} ${requirement.mandatory[][bonus]} z-depth-2 hoverable\">\n" +
                "\t<div class=\"achievement-img-container\">\n" +
                "\t\t<img src=\"img/${requirement.meta[image]}\">\n" +
                "\t</div>\n" +
                "\t<div class=\"achievement-content-container\">\n" +
                "\t\t<div class=\"achievement-header\">\n" +
                "\t\t\t<span class=\"achievement-title\">${requirement.name}</span>\n" +
                "\t\t\t<span class=\"achievement-points\">${requirement.malus[-][]}${requirement.maxPoints}</span>\n" +
                "\t\t\t<span class=\"achievement-date\">${requirement.minMS.name}</span>\n" +
                "\t\t</div>\n" +
                "\t\t<span class=\"achievement-description\">${requirement.description}</span>\n" +
                "\t</div>\n" +
                "</div>";

        manager.parseRequirementTemplate(reqHTML);
        manager.parseCatalogueTemplate(catHTML);

        String export = manager.renderCatalogue();

        PrintWriter pw = new PrintWriter(f);
        pw.write(export);
        pw.close();
        pw.flush();
    }
}
