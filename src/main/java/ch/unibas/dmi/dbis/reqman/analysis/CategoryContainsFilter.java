package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Requirement;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class CategoryContainsFilter implements Filter {
  
  private final String category;
  
  public CategoryContainsFilter(@NotNull String category) {
    this.category = category.toLowerCase();
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("CategoryContainsFilter{");
    sb.append("category='").append(category).append('\'');
    sb.append('}');
    return sb.toString();
  }
  
  @Override
  public boolean test(Requirement requirement) {
    try {
      return requirement.getCategory().toLowerCase().contains(category);
    } catch (NullPointerException ex) {
      return false; // Probably categroy is null
    }
  }
  
  @Override
  public String getDisplayRepresentation() {
    return "Category contains " + category;
  }
}
