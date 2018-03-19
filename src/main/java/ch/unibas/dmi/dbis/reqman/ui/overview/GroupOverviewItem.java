package ch.unibas.dmi.dbis.reqman.ui.overview;

import ch.unibas.dmi.dbis.reqman.analysis.GroupAnalyser;
import ch.unibas.dmi.dbis.reqman.data.ProgressSummary;

import java.util.Map;
import java.util.UUID;

/**
 * A simple container item which holds a name and a map of id-points pairs for {@link ch.unibas.dmi.dbis.reqman.data.Group}s.
 *
 * In particular, this object represents a row in a {@link javafx.scene.control.TreeTableView}
 * where the 'name' stands for the entry (see below) and the id-points pairs stand for the columns,
 * where the point-number is the actual cell content and the id the column identifier.
 *
 * By design, there are two cases for this object:
 * <ul>
 *   <li>
 *     The name represents a {@link ch.unibas.dmi.dbis.reqman.data.Catalogue}.
 *     In this case the id-points pairs are a tuples of a
 *     {@link ch.unibas.dmi.dbis.reqman.data.Group}'s {@link java.util.UUID} and the sum of this group,
 *     as calculated by {@link GroupAnalyser#getSum()}
 *   </li>
 *   <li>
 *     The name reprsents a {@link ch.unibas.dmi.dbis.reqman.data.Milestone}.
 *     In this case, the id-points pair are a tuples of a
 *     {@link ch.unibas.dmi.dbis.reqman.data.Group}'s {@link java.util.UUID} and the sum of this group,
 *     for this milestone's {@link ch.unibas.dmi.dbis.reqman.data.ProgressSummary} as calculated by
 *     {@link GroupAnalyser#getSumFor(ProgressSummary)}
 *   </li>
 * </ul>
 *
 * @author loris.sauter
 */
public class GroupOverviewItem {
  
  private final String name;
  private final UUID uuid;
  
  private final Map<UUID, Double> tuples;
  
  /**
   *
   * @param name
   * @param uuid of the 'row' e.g. catalogue's or milestone's uuid
   * @param tuples
   */
  GroupOverviewItem(String name, UUID uuid, Map<UUID, Double> tuples) {
    this.name = name;
    this.uuid = uuid;
    this.tuples = tuples;
  }
  
  public double getPoints(UUID uuid){
    return tuples.get(uuid);
  }
  
  public String getName() {
    return name;
  }
  
  public UUID getUuid() {
    return uuid;
  }
}
