package ch.unibas.dmi.dbis.cs108pet.management;

import ch.unibas.dmi.dbis.cs108pet.common.JSONUtils;
import ch.unibas.dmi.dbis.cs108pet.data.Group;

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
  }
  
  @Override
  protected List<Group> call() throws Exception {
    List<Group> list = new ArrayList<>();
    for (int i = 1; i <= files.size(); i++) {
      updateAll("Opening group " + files.get(i - 1).getName() + "..." + String.format(" (%d/%d)", i, files.size()), calcFirstProgress(i));
      Group gr = JSONUtils.readGroupJSONFile(files.get(i - 1));
      list.add(gr);
      updateAll("Succsessfully opened group " + gr.getName() + "." + String.format(" (%d/%d)", i, files.size()), calcSecondProgress(i));
    }
    return list;
  }
  
  /**
   * @param i Must be that first stage: i==1
   * @return
   */
  private double calcFirstProgress(double i) {
    return i * (0.2 / (double) files.size()) + (i - 1) * (0.8 / (double) files.size());
  }
  
  private double calcSecondProgress(double i) {
    return i * (0.2 / (double) files.size()) + (i) * (0.8 / (double) files.size());
  }
}
