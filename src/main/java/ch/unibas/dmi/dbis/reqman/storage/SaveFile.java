package ch.unibas.dmi.dbis.reqman.storage;

import ch.unibas.dmi.dbis.reqman.common.IOUtils;
import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.common.VersionedEntity;
import org.apache.logging.log4j.core.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * The {@link SaveFile} class represents a save file of an entity.
 * <p>
 * The save file is used to read and write the entity it is typed for, using {@link
 * ch.unibas.dmi.dbis.reqman.common.JSONUtils}.
 * Furthermore, it keeps track of the on disk storage location, so that it could be written to disk without the need
 * of getting a location for it.
 *
 * @param <T> The entity type.
 */
public class SaveFile<T extends VersionedEntity> {
  
  private T entity;
  private Class<T> typeClass;
  
  private File file = null;
  
  private File dir = null;
  
  public SaveFile(File file, Class<T> typeClass) {
    this.typeClass = typeClass;
    if (file.isDirectory()) {
      this.dir = file;
    } else if (file.isFile() && hasCorrectExtension(file)) {
      this.file = file;
    } else {
      this.file = new File(file.getPath() + IOUtils.EXTENSION_SEPARATOR + getDesignatedExtension());
    }
    if (dir == null) {
      dir = file.getParentFile();
    }
    
  }
  
  public SaveFile(T entity) {
    this.entity = entity;
  }
  
  public void setStorageFile(File file) {
    // TODO Check extension
    this.file = file;
    this.dir = file.getParentFile();
  }
  
  public void setSaveDirectory(File dir){
    // TODO Check dir
    this.dir = dir;
  }
  
  public void setEntity(T entity){
    this.entity = entity;
  }
  
  public void save() throws IOException {
    // No file set:
    if (dir == null) {
      throw new RuntimeException("No directory given");
    }
    
    if (file == null) {
      file = new File(dir.getPath() + File.separator + getSaveFileName());
    }
    JSONUtils.writeToJSONFile(entity, file);
  }
  
  public File getSaveFile() {
    return file;
  }
  
  public File getSaveDir() {
    return dir;
  }
  
  // TODO reduce visibility
  public String getSaveFileName() {
    return entity.getName() + IOUtils.EXTENSION_SEPARATOR + getDesignatedExtension();
  }
  
  protected boolean hasCorrectExtension(File file) {
    return FileUtils.getFileExtension(file).equals(getDesignatedExtension());
  }
  
  protected String getDesignatedExtension() {
    if(entity == null){
      return typeClass.getTypeName().substring(typeClass.getTypeName().lastIndexOf(IOUtils.EXTENSION_SEPARATOR)+1).toLowerCase();
    }else{
      return entity.getClass().getSimpleName().toLowerCase();
    }
  }
  
}
