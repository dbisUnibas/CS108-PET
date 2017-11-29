package ch.unibas.dmi.dbis.reqman.storage;

import ch.unibas.dmi.dbis.reqman.common.IOUtils;
import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.common.Version;
import ch.unibas.dmi.dbis.reqman.common.VersionedEntity;
import org.apache.logging.log4j.core.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The {@link SaveFile} class represents a save file of an entity.
 * <p>
 * The save file is used to open and write the entity it is typed for, using {@link
 * ch.unibas.dmi.dbis.reqman.common.JSONUtils}.
 * Furthermore, it keeps track of the on disk storage location, so that it could be written to disk without the need
 * of getting a location for it.
 *
 * @param <T> The entity type.
 */
public class SaveFile<T extends VersionedEntity> {
  
  private T entity;
  private Class<T> typeClass = null;
  
  private File file = null;
  
  private File dir = null;
  
  private SaveFile(T entity) {
    this.entity = entity;
  }
  
  private SaveFile(Class<T> typeClass) {
    this.typeClass = typeClass;
  }
  
  public static <T extends VersionedEntity> SaveFile createForEntity(@NotNull T entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Cannot create SaveFile for null entity");
    }
    return new SaveFile<>(entity);
  }
  
  public static <T extends VersionedEntity> SaveFile createForSaveDir(@NotNull File dir, @NotNull Class<T> typeClass) {
    // TODO no way to check if directory, if not existent
    SaveFile<T> save = new SaveFile<>(typeClass);
    save.setSaveDirectory(dir);
    return save;
  }
  
  public static <T extends VersionedEntity> SaveFile createForSaveFile(@NotNull File file, @NotNull Class<T> typeClass) {
    SaveFile<T> save = new SaveFile<>(typeClass);
    save.setSaveFile(file);
    return save;
  }
  
  public void setSaveDirectory(File dir) {
    // TODO Check dir
    if(!dir.isDirectory() ){
      throw new IllegalArgumentException("Cannot set save directory for non-directory file "+dir.toString());
    }
    this.dir = dir;
  }
  
  public void save() throws IOException {
    // No file set:
    if (dir == null) {
      throw new RuntimeException("No directory given");
    }
    
    if (file == null) {
      file = new File(getSaveFilePath());
    }
    entity.setVersion(Version.getInstance().getVersion());
    JSONUtils.writeToJSONFile(entity, file);
  }
  
  public void open() throws IOException {
    if (file != null) {
      // Case 1: File directly specified (e.g. per user dialog)
      // do nothing
    } else if (dir != null) {
      // Case 2: Directory specified
      // search file with matching extension in that dir
      File[] files = dir.listFiles(f -> FileUtils.getFileExtension(f).equals(getDesignatedExtension()));
      if(files == null){
        throw new RuntimeException("For some reason, files is null");
      }
      if (files.length >= 1) {
        file = files[0];
      } else {
        throw new FileNotFoundException(String.format("Could not find a %s file in %s", getDesignatedExtension(), dir.getAbsolutePath()));
      }
    } else {
      throw new IllegalStateException("Cannot open a savefile, if neither savedir nor savefile is set");
    }
    entity = JSONUtils.readFromJSONFile(file, typeClass);
  }
  
  public T getEntity() {
    return entity;
  }
  
  public void setEntity(T entity) {
    this.entity = entity;
  }
  
  public File getSaveFile() {
    return file;
  }
  
  public void setSaveFile(File file) {
    // TODO Check extension
    if (IOUtils.hasExpectedExtension(file, getDesignatedExtension())) {
      this.file = file;
      this.dir = this.file.getParentFile();
    } else {
      this.file = new File(file.getPath() + IOUtils.EXTENSION_SEPARATOR + getDesignatedExtension());
      dir = this.file.getParentFile();
    }
  }
  
  public File getSaveDir() {
    return dir;
  }
  
  // TODO reduce visibility
  public String getSaveFileName() {
    return entity.getName().replace(' ', '_') + IOUtils.EXTENSION_SEPARATOR + getDesignatedExtension();
  }
  
  // TODO reduce visiblity
  public String getSaveFilePath() {
    return dir.getPath() + IOUtils.FILE_SEPARATOR + getSaveFileName();
  }
  
  private String getDesignatedExtension() {
    if (entity == null) {
      return typeClass.getTypeName().substring(typeClass.getTypeName().lastIndexOf(IOUtils.EXTENSION_SEPARATOR) + 1).toLowerCase();
    } else {
      return entity.getClass().getSimpleName().toLowerCase();
    }
  }
  
}
