package ch.unibas.dmi.dbis.reqman.storage;

import javafx.stage.FileChooser;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.core.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;

/**
 * The {@link ReqmanFile} is a tuple of {@link File} and {@link ReqmanFile.Type}.
 * <p>
 * This class is used to type a certain file.
 *
 * @author loris.sauter
 */
public class ReqmanFile {
  
  private final File file;
  
  private final Type type;
  
  public ReqmanFile(@NotNull File file, @NotNull Type type) {
    this.file = file;
    this.type = type;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    ReqmanFile that = (ReqmanFile) o;
    
    if (!getFile().equals(that.getFile())) return false;
    return getType() == that.getType();
  }
  
  @Override
  public int hashCode() {
    int result = getFile().hashCode();
    result = 31 * result + getType().hashCode();
    return result;
  }
  
  public File getFile() {
    return file;
  }
  
  public Type getType() {
    return type;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("ReqmanFile{");
    sb.append("file=").append(file);
    sb.append(", type=").append(type);
    sb.append('}');
    return sb.toString();
  }
  
  /**
   * Denotes a file's type.
   */
  public enum Type {
    /**
     * A {@link ReqmanFile} with this type represents a save file of the entity {@link
     * ch.unibas.dmi.dbis.reqman.data.Catalogue}
     */
    CATALOGUE,
    /**
     * A {@link ReqmanFile} with this type represents a save file of the entity {@link
     * ch.unibas.dmi.dbis.reqman.data.Course}
     */
    COURSE,
    /**
     * A {@link ReqmanFile} with this type represents a save file of the entity {@link
     * ch.unibas.dmi.dbis.reqman.data.Group}
     */
    GROUP,
    /**
     * A {@link ReqmanFile} with this type represents a config file
     */
    CONFIG,
    /**
     * A {@link ReqmanFile} with this type represents a template file
     */
    TEMPLATE,
    /**
     * A {@link ReqmanFile} with this type represents a backup file
     */
    BACKUP,
    /**
     * A {@link ReqmanFile} with this type represents a session file
     */
    SESSION;
    
    public String getExtension(){
      return name().toLowerCase();
    }
    
    public final FileFilter getFileFilter = pathname -> FileUtils.getFileExtension(pathname).equals(getExtension());
    public final FileChooser.ExtensionFilter getExtensionFilter(){
      return new FileChooser.ExtensionFilter(StringUtils.capitalize(getExtension() )+" files", "*."+getExtension());
    }
  }
}
