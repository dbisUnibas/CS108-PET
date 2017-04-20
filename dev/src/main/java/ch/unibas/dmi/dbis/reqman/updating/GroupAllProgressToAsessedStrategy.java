package ch.unibas.dmi.dbis.reqman.updating;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Progress;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupAllProgressToAsessedStrategy implements GroupUpdatingStrategy {

    private Group g;
    private Catalogue catalogue;

    @Override
    public boolean open(File file) {
        try {
            g = JSONUtils.readGroupJSONFile(file);
            System.out.println("Successfully loaded " + file.getPath());
            System.out.println("Group: " + g.getName());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean update() {
        // Removing all with default percentage
        List<Progress> progressList = g.getProgressList();
        List<Progress> out = new ArrayList<>(progressList);
        progressList.forEach(progress -> {
            if (progress.hasDefaultPercentage()) {
                out.remove(progress);
            }
        });

        // Setting assessment date to milestone date
        out.forEach(p -> {
            Milestone ms = catalogue.getMilestoneByOrdinal(p.getMilestoneOrdinal());
            p.setDate(ms.getDate());
        });
        g.setProgressList(out);
        return true;
    }

    @Override
    public void save() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean save(File file) throws IOException {
        JSONUtils.writeToJSONFile(g, file);
        System.out.println("Successfully wrote " + file.getPath());
        return true;
    }

    @Override
    public void setCatalogue(File file) {
        try {
            catalogue = JSONUtils.readCatalogueJSONFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not read catalogue: ", e);
        }
    }

    @Override
    public String fromVersion() {
        return "<=0.4.10";
    }

    @Override
    public String toVersion() {
        return "0.4.11";
    }
}
