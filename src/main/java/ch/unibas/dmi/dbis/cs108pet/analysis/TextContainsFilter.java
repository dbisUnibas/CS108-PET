package ch.unibas.dmi.dbis.cs108pet.analysis;

import ch.unibas.dmi.dbis.cs108pet.data.Requirement;
import org.jetbrains.annotations.NotNull;

/**
 * Filter for text contains.
 *
 * @author loris.sauter
 */
public class TextContainsFilter implements Filter {
  
  private final String pattern;
  
  public TextContainsFilter(@NotNull String pattern) {
    this.pattern = pattern.toLowerCase();
  }
  
  @Override
  public boolean test(Requirement requirement) {
    boolean inName = requirement.getName() != null && requirement.getName().toLowerCase().contains(pattern);
    boolean inExcerpt = requirement.getExcerpt() != null && requirement.getExcerpt().toLowerCase().contains(pattern);
    boolean inDesc = requirement.getDescription() != null && requirement.getDescription().toLowerCase().contains(pattern);
    boolean inCategory = requirement.getCategory() != null && requirement.getCategory().toLowerCase().contains(pattern);
    
    return inName || inExcerpt || inDesc || inCategory;
  }
  
  @Override
  public String getDisplayRepresentation() {
    return "Text contains " + pattern;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("TextContainsFilter{");
    sb.append("pattern='").append(pattern).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
