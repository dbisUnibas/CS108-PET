package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.core.Group;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class OpenMultipleGroupsTask extends ManagementTask<List<Group>> {

    private final List<File> files;

    public OpenMultipleGroupsTask(List<File> files) {
        this.files = new ArrayList<>(files);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    protected List<Group> call() throws Exception {
        return null;
    }
}
