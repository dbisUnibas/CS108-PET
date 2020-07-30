package ch.unibas.dmi.dbis.cs108pet.management;

import ch.unibas.dmi.dbis.cs108pet.common.JSONUtils;
import ch.unibas.dmi.dbis.cs108pet.data.Group;

import java.io.File;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SaveGroupTask extends ManagementTask<Boolean> {
  
  private final File file;
  private final Group group;
  
  public SaveGroupTask(File file, Group group) {
    this.file = file;
    this.group = group;
  }
  
  @Override
  protected Boolean call() throws Exception {
    updateAll("Start writing " + group.getName() + " to disk", 0.2);
    JSONUtils.writeToJSONFile(group, file);
    updateAll("Done writing " + group.getName() + " to " + file.getPath(), 1.0);
    return true;
  }
}
