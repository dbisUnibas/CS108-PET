package ch.unibas.dmi.dbis.reqman.updating;

import java.io.File;
import java.io.IOException;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public interface GroupUpdatingStrategy {

    boolean open(File file);

    boolean update();

    void save() throws IOException;

    boolean save(File file) throws IOException;

    void setCatalogue(File file);

    String fromVersion();

    String toVersion();
}
